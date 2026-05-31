# Release pipeline: the full journey

How a single release travels from `git tag v1.2.7` on your laptop all the way to a
running, scanned, and signed container, with every value traced from its source.

Throughout, the example version is `1.2.7` and the example commit hash is `a1b2c3d4`.

---

## Key idea up front

Every value in a release traces back to **one source**:

- the **version** (`1.2.7`) comes from the **git tag** you push, and
- the **revision** (`a1b2c3d4`) is the **commit** that tag points at, handed to the build by GitHub.

Nothing is hand-typed into files. Nothing is duplicated.

Two facts that resolve the most common confusions:

1. **`src/main/resources/app-version.properties` on disk never changes.** It holds
   placeholders (`${project.version}`) forever. The real values are substituted into a
   throwaway *copy* during the build on GitHub's runner, and that copy is what ships in
   the WAR.
2. **`github.sha` is not produced by the checkout step.** GitHub already knows it before
   any step runs. Checkout *uses* it to pull the right code; it does not generate it.

---

## What a version tag actually is

- A **commit** is a snapshot of every file, identified by a 40-char hash (`a1b2c3d4...`).
  Change one byte anywhere and the hash changes. The hash is a fingerprint of the whole codebase.
- A **tag** is a 41-byte sticky note: a human name (`v1.2.7`) pointing at a commit hash.
  It is not a copy of code.

```
   refs/tags/v1.2.7  ──────►  commit a1b2c3d4  ──────►  snapshot of all your files
   (the bookmark)             (the fingerprint)          (the actual code)
```

So `1.2.7` (human name) and `a1b2c3d4` (precise fingerprint) are two IDs for the same thing.
The pipeline carries both.

---

## Phase A — You, on your laptop

```
STEP 1.  Finish code, commit to main. The commit gets a sha: a1b2c3d4e5...(40 chars).

STEP 2.  Create a version tag pointing at that commit:  git tag v1.2.7
         A tag is just a named bookmark on commit a1b2c3d4.

STEP 3.  Push the tag:  git push origin v1.2.7
         This upload is what wakes up GitHub Actions.
```

Nothing else happens on your machine. Your job is done after step 3.

---

## Phase B — GitHub decides to run (BEFORE any YAML step executes)

This is not a step in the workflow file. It is what GitHub does *before* the runner
executes a single line.

```
STEP 4.  GitHub sees a new ref "refs/tags/v1.2.7" was pushed.

STEP 5.  It checks workflow triggers. In release.yaml:
             on: push: tags: ['v*.*.*']
         "v1.2.7" matches v*.*.*  →  the workflow starts.

STEP 6.  GitHub resolves the tag and pre-loads the run context BEFORE step 1:
             github.ref_type = "tag"
             github.ref_name = "v1.2.7"
             github.sha      = "a1b2c3d4e5..."   ← already known here, not computed later
```

`github.sha` is an **input** GitHub hands the run, available to every step from the start.

---

## Phase C — The runner sets up (steps run top-to-bottom, in file order)

```
STEP 7.  actions/checkout@v4        → clones your repo at commit a1b2c3d4 onto the runner.
                                       (Uses the sha; does NOT create it. Brings the WHOLE
                                        commit + code, not just the 41-byte tag.)

STEP 8.  configure-aws-credentials  → runner assumes the AWS IAM role via the OIDC
                                       id-token (permissions: id-token: write). Now it
                                       can talk to your AWS account.

STEP 9.  amazon-ecr-login (id: ecr) → logs docker into ECR, outputs the registry URL as
                                       steps.ecr.outputs.registry

STEP 10. setup-buildx-action        → installs Docker buildx (the builder).
```

---

## Phase D — The `meta` step: turn the tag into a version (the heart)

```
STEP 11. The meta step runs shell logic:
             if ref_type == "tag":
                 version = ${GITHUB_REF_NAME#v}    # strip leading "v"
                 release = true

         GITHUB_REF_NAME = "v1.2.7"
         ${GITHUB_REF_NAME#v} = "1.2.7"            # POSIX parameter expansion:
                                                   #   ${VAR#pat} removes pat from the front
         Outputs saved for later steps:
             steps.meta.outputs.version = "1.2.7"
             steps.meta.outputs.release = "true"
```

`meta` only needs `github.ref_name` (already in context), so its position relative to
checkout does not matter. Checkout is first only because the *build* later needs the
source files on disk.

---

## Phase E — Build the image locally (NOT pushed yet)

```
STEP 12. build-local calls docker buildx with:
             build-args:
               APP_VERSION=1.2.7              ← from steps.meta.outputs.version
               GIT_REVISION=a1b2c3d4e5...     ← from github.sha
             load: true   push: false         ← build into the runner, don't upload
             tags: <registry>/aws-movie-theatre:1.2.7
```

Control passes INTO the Dockerfile:

```
STEP 13. Dockerfile build stage:
             ARG APP_VERSION=1.0.0     ← default "1.0.0" OVERRIDDEN to 1.2.7
             ARG GIT_REVISION=unknown  ← default OVERRIDDEN to a1b2c3d4
             RUN mvn package -Drevision=1.2.7 -Dgit.revision=a1b2c3d4

STEP 14. Maven runs inside the container:

   14a. -Drevision=1.2.7 overrides the pom property <revision>1.0.0</revision>.
        Since <version>${revision}</version>, the project version becomes 1.2.7.
        Same for git.revision → a1b2c3d4.

   14b. RESOURCE FILTERING (pom has <filtering>true</filtering>):
        Maven COPIES app-version.properties into the build, replacing ${...} as it copies:

           template on disk (UNCHANGED):     filled copy in target/classes (NEW):
           app.version=${project.version}  →  app.version=1.2.7
           git.revision=${git.revision}    →  git.revision=a1b2c3d4

        The template on disk is never modified. Only the copy gets values.

STEP 15. Maven compiles BuildInfo + the two servlets, packages everything, including the
         FILLED copy of app-version.properties, into ROOT.war.

STEP 16. Dockerfile runtime stage:
             COPY --from=build ROOT.war into Tomcat
             LABEL org.opencontainers.image.version="1.2.7"   ← also stamped as image metadata
                   org.opencontainers.image.revision="a1b2c3d4"
         Finished image lives in the runner's local docker. 1.2.7 is baked in TWO places:
         inside the WAR, and as OCI labels on the image.
```

---

## Phase F — Scan before anything leaves the runner

```
STEP 17. trivy-action scans the local image for HIGH/CRITICAL vulnerabilities:
             - found one?  exit-code 1  → job FAILS here. Nothing is pushed. STOP.
             - clean?      → continue.
         Security gate: a vulnerable image never reaches ECR.
```

(This step also runs on plain `main` pushes as a CI gate; the push/sign steps below do not.)

---

## Phase G — Push to ECR (only because release == true)

```
STEP 18. push step: if steps.meta.outputs.release == 'true'  → runs (it's a tag).
         Same build-args (1.2.7 / a1b2c3d4), now push: true.
         buildx reuses cached layers from step 12 (no real rebuild) and uploads.
         tags pushed:  :1.2.7  (and :latest, if kept)

STEP 19. ECR computes the DIGEST (sha256 of the pushed bytes): sha256:d2dca6...
         docker/build-push-action captures it as steps.push.outputs.digest.
         This push creates the ECR objects: index + image + attestation (+ signature next).
```

---

## Phase H — Sign the digest (only because release == true)

```
STEP 20. cosign-installer installs cosign on the runner.

STEP 21. cosign sign --yes <registry>/aws-movie-theatre@sha256:d2dca6...
         Signs the @DIGEST, not the :1.2.7 tag → binds the signature to exact immutable bytes.
         Keyless: GitHub OIDC identity → Sigstore Fulcio mints a short-lived cert → signs →
         records in the Rekor transparency log. The .sig artifact lands in ECR by the image.

STEP 22. Job succeeds. Release v1.2.7 is now: built, scanned, pushed, and signed.
```

---

## Phase I — Runtime, when someone visits the app

```
STEP 23. Cluster pulls and runs the image (pinned by :1.2.7 or @digest). Tomcat deploys ROOT.war.

STEP 24. First request to "/" hits AwsMovieTheatreServlet.
         BuildInfo.load() reads /app-version.properties FROM THE WAR
             → app.version=1.2.7, git.revision=a1b2c3d4 (the values baked at step 14b)
         Page footer prints:  v1.2.7 (a1b2c3d4)

STEP 25. A request to "/version" hits VersionServlet → returns
             {"version":"1.2.7","revision":"a1b2c3d4","status":"ok"}
```

---

## One-line summary of the whole data flow

```
  git tag v1.2.7
      │  (you)
      ▼
  github.ref_name="v1.2.7"  +  github.sha="a1b2c3d4"   (GitHub provides both, before step 1)
      │  meta step strips "v"
      ▼
  version=1.2.7
      │  passed as --build-arg into docker
      ▼
  ARG APP_VERSION (default 1.0.0 overridden) → mvn -Drevision=1.2.7
      │  Maven resource filtering replaces ${project.version} in a COPY
      ▼
  app-version.properties inside the WAR:  app.version=1.2.7
      │  BuildInfo.load() reads it at runtime
      ▼
  footer "v1.2.7"  +  /version JSON  +  OCI label  +  cosigned digest
```

The single weak link worth knowing: the revision is `github.sha`, the commit GitHub
checked out. As long as you tag the commit you actually want to ship (which `git tag` on
your current HEAD does), it is exact.
```
