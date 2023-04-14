package io.schram.jwebassembly;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CompilerTest {

    @Test
    public void testIfAbleToOverrideDefaultValues() {
        Compiler compiler = new Compiler();

        compiler.setArtifactId("some-artifact-id");
        compiler.setGroupId("some-group-id");
        compiler.setVersion("0.42");

        assertThat(compiler.getArtifactId()).isEqualTo("some-artifact-id");
        assertThat(compiler.getGroupId()).isEqualTo("some-group-id");
        assertThat(compiler.getVersion()).isEqualTo("0.42");
    }

    @Test
    public void testIfAbleToGetDefaultValues() {
        Compiler compiler = new Compiler();

        assertThat(compiler.getArtifactId()).isEqualTo("jwebassembly-compiler");
        assertThat(compiler.getGroupId()).isEqualTo("de.inetsoftware");
        assertThat(compiler.getVersion()).isEqualTo("0.4");
    }
}