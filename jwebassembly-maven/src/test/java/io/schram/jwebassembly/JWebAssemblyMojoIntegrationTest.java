package io.schram.jwebassembly;

import org.apache.maven.plugin.MojoExecutionException;
import org.mockito.ArgumentCaptor;
import org.mockito.stubbing.Answer;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;

import static io.schram.jwebassembly.OutputFormat.Binary;
import static io.schram.jwebassembly.OutputFormat.Text;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

public class JWebAssemblyMojoIntegrationTest extends AbstractMojoIntegrationTest {

    final ArgumentCaptor<File> passedFile = ArgumentCaptor.forClass(File.class);
    final ArgumentCaptor<File> passedLibrary = ArgumentCaptor.forClass(File.class);
    final ArgumentCaptor<File> targetFile = ArgumentCaptor.forClass(File.class);

    public void testIfAbleToBuildWithoutJWebAssemblyDependencyDeclared() throws Exception {
        File baseDirectory = createBaseDirectoryFor("use-plugin-without-explicit-compiler-dependency");
        JWebAssemblyMojo jWebAssemblyMojo = getJWebAssemblyMojoFor(baseDirectory);

        jWebAssemblyMojo.execute();

        assertThat(jWebAssemblyMojo.compilerDependency).isNotNull();
        assertThat(jWebAssemblyMojo.compilerDependency.getVersion()).isEqualTo("0.4");
    }

    public void testIfAbleToBuildWithExplicitJWebAssemblyDependencyDeclared() throws Exception {
        File baseDirectory = createBaseDirectoryFor("use-plugin-with-explicit-compiler-dependency");
        JWebAssemblyMojo jWebAssemblyMojo = getJWebAssemblyMojoFor(baseDirectory);

        jWebAssemblyMojo.execute();

        assertThat(jWebAssemblyMojo.compilerDependency).isNotNull();
        assertThat(jWebAssemblyMojo.compilerDependency.getVersion()).isEqualTo("0.2");
    }

    public void testIfAbleToCompileToTextFormat() throws Exception {
        File baseDirectory = createBaseDirectoryFor("compile-to-text-format");
        JWebAssemblyMojo jWebAssemblyMojo = spy(getJWebAssemblyMojoFor(baseDirectory));
        AtomicReference<JWebAssemblyCompiler> compiler = getReferenceToCompilerFrom(jWebAssemblyMojo);

        jWebAssemblyMojo.execute();

        assertThat(jWebAssemblyMojo.getOutputFormat()).isEqualTo(Text);
        verify(compiler.get(), never()).compileToBinary(any(File.class));
        verify(compiler.get()).compileToText(targetFile.capture());

        assertThat(targetFile.getValue().getName()).isEqualTo("test-0.0.0.wat");
        assertThat(new File(baseDirectory, "target")).isDirectoryContaining("glob:**test-0.0.0.wat");
    }

    public void testIfAbleToCompileToBinaryFormat() throws Exception {
        File baseDirectory = createBaseDirectoryFor("compile-to-binary-format");
        JWebAssemblyMojo jWebAssemblyMojo = spy(getJWebAssemblyMojoFor(baseDirectory));
        AtomicReference<JWebAssemblyCompiler> compiler = getReferenceToCompilerFrom(jWebAssemblyMojo);

        jWebAssemblyMojo.execute();

        assertThat(jWebAssemblyMojo.getOutputFormat()).isEqualTo(Binary);
        verify(compiler.get()).compileToBinary(targetFile.capture());
        verify(compiler.get(), never()).compileToText(any(File.class));

        assertThat(targetFile.getValue().getName()).isEqualTo("test-0.0.0.wasm");
        assertThat(new File(baseDirectory, "target")).isDirectoryContaining("glob:**test-0.0.0.wasm");
        assertThat(new File(baseDirectory, "target")).isDirectoryContaining("glob:**test-0.0.0.wasm.js");
    }

    public void testIfAbleToGetCodeToCompile() throws Exception {
        File baseDirectory = createBaseDirectoryFor("compile-to-binary-format");
        copyExampleCodeTo(baseDirectory);
        JWebAssemblyMojo jWebAssemblyMojo = spy(getJWebAssemblyMojoFor(baseDirectory));
        AtomicReference<JWebAssemblyCompiler> compiler = getReferenceToCompilerFrom(jWebAssemblyMojo);

        jWebAssemblyMojo.execute();

        verify(compiler.get()).addFile(passedFile.capture());
        verify(compiler.get()).addLibrary(passedLibrary.capture());

        assertThat(passedFile.getValue().getName()).isEqualTo("HelloWorld.class");
        assertThat(passedLibrary.getValue().getName()).isEqualTo("jwebassembly-api-0.4.jar");
    }

    public void testIfPassingAlongProperties() throws Exception {
        File baseDirectory = createBaseDirectoryFor("use-plugin-with-properties");
        JWebAssemblyMojo jWebAssemblyMojo = spy(getJWebAssemblyMojoFor(baseDirectory));
        AtomicReference<JWebAssemblyCompiler> compiler = getReferenceToCompilerFrom(jWebAssemblyMojo);

        jWebAssemblyMojo.execute();

        verify(compiler.get()).setProperty("some.property", "some-value");
        verify(compiler.get()).setProperty("another.property", "another value");
    }

    static AtomicReference<JWebAssemblyCompiler> getReferenceToCompilerFrom(final JWebAssemblyMojo jWebAssemblyMojo) throws MojoExecutionException {
        AtomicReference<JWebAssemblyCompiler> compilerReference = new AtomicReference<>(null);

        doAnswer((Answer<JWebAssemblyCompiler>) invocation -> {
            JWebAssemblyCompiler compiler = (JWebAssemblyCompiler) invocation.callRealMethod();
            compilerReference.set(spy(compiler));
            return compilerReference.get();
        }).when(jWebAssemblyMojo).getCompiler();

        return compilerReference;
    }
}