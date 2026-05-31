package com.aws.outposts;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class BuildInfoTest {

    private InputStream stream(String contents) {
        return new ByteArrayInputStream(contents.getBytes(StandardCharsets.UTF_8));
    }

    @Test
    public void parsesVersionAndRevision() {
        BuildInfo info = BuildInfo.fromStream(stream("app.version=1.0.2\ngit.revision=abc1234\n"));
        assertEquals("1.0.2", info.version());
        assertEquals("abc1234", info.revision());
    }

    @Test
    public void nullStreamFallsBackToDefaults() {
        BuildInfo info = BuildInfo.fromStream(null);
        assertEquals("dev", info.version());
        assertEquals("unknown", info.revision());
        // negative guard: defaults must be distinct, not both the same sentinel
        assertFalse(info.version().equals(info.revision()));
    }

    @Test
    public void unresolvedMavenTokensTreatedAsMissing() {
        // If resource filtering is off, the raw ${...} tokens leak through.
        // They must NOT be shown to users as if they were a real version.
        BuildInfo info = BuildInfo.fromStream(
                stream("app.version=${project.version}\ngit.revision=${git.revision}\n"));
        assertEquals("dev", info.version());
        assertEquals("unknown", info.revision());
    }

    @Test
    public void blankValuesFallBackToDefaults() {
        BuildInfo info = BuildInfo.fromStream(stream("app.version=\ngit.revision=   \n"));
        assertEquals("dev", info.version());
        assertEquals("unknown", info.revision());
    }
}
