# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Commands

```bash
# Build WAR (copies images from images/ into webapp, then runs Maven)
./build.sh

# Local dev server at http://localhost:8080/
mvn tomcat7:run

# Build only (no image copy)
mvn clean package

# Regenerate AI movie posters (requires AWS CLI + Bedrock access in us-west-2)
./generate-images.sh
```

## Architecture

Single-servlet Java WAR app — no framework, no templating engine.

- **`AwsMovieTheatreServlet.java`** — the entire application. One `doGet` method that writes a full HTML page inline using `PrintWriter`. Images are served as static files by the servlet container.
- **`pom.xml`** — packages as `war`, `finalName=ROOT` so the WAR deploys to the root context (`/`) of Tomcat. Java 25 source compatibility. Only runtime dependency is `javax.servlet-api` (provided).
- **Image pipeline** — source images live in `images/`. `build.sh` copies them into `src/main/webapp/images/` before Maven packages them into the WAR. If you skip `build.sh` and run `mvn` directly, the images must already be in `src/main/webapp/images/`.

## Docker

Multi-stage build: Maven builds the WAR, then `tomcat:9.0-jdk25` serves it. Tomcat 9 is required — the app uses `javax.servlet` (Servlet 3.1), which is incompatible with Tomcat 10+ (jakarta.servlet). Runtime stage runs as non-root user `app`.

```bash
docker build -t movie-theatre:local .
docker run --rm -p 8080:8080 movie-theatre:local
```

## Deployment (non-Docker)

```bash
sudo cp target/ROOT.war /opt/tomcat/latest/webapps/ROOT.war
sudo systemctl restart tomcat
```
