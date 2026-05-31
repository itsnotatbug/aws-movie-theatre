package com.aws.outposts;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Build identity (version + git revision) baked into the artifact at build time
 * via Maven resource filtering of {@code /app-version.properties}.
 *
 * <p>This is deliberately NOT read from an environment variable: the version is an
 * immutable property of the built bytes, not per-environment configuration. Baking it
 * in keeps the cosigned image self-describing.
 */
public final class BuildInfo {

    static final String DEFAULT_VERSION = "dev";
    static final String DEFAULT_REVISION = "unknown";

    private final String version;
    private final String revision;

    BuildInfo(String version, String revision) {
        this.version = version;
        this.revision = revision;
    }

    public String version() {
        return version;
    }

    public String revision() {
        return revision;
    }

    /** Loads from the classpath resource produced by Maven filtering. */
    public static BuildInfo load() {
        return fromStream(BuildInfo.class.getResourceAsStream("/app-version.properties"));
    }

    /** Parses a properties stream; tolerant of null, blank, and unresolved Maven tokens. */
    public static BuildInfo fromStream(InputStream in) {
        if (in == null) {
            return new BuildInfo(DEFAULT_VERSION, DEFAULT_REVISION);
        }
        Properties props = new Properties();
        try {
            props.load(in);
        } catch (IOException e) {
            return new BuildInfo(DEFAULT_VERSION, DEFAULT_REVISION);
        }
        return new BuildInfo(
                clean(props.getProperty("app.version"), DEFAULT_VERSION),
                clean(props.getProperty("git.revision"), DEFAULT_REVISION));
    }

    private static String clean(String value, String fallback) {
        if (value == null) {
            return fallback;
        }
        String trimmed = value.trim();
        // Empty, or an unresolved Maven placeholder (filtering disabled) -> treat as missing.
        if (trimmed.isEmpty() || trimmed.startsWith("${")) {
            return fallback;
        }
        return trimmed;
    }
}
