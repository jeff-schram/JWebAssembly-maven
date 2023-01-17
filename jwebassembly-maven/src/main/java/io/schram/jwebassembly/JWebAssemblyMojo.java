package io.schram.jwebassembly;

import io.schram.jwebassembly.util.JavaVersion;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static io.schram.jwebassembly.OutputFormat.Binary;
import static java.util.Objects.nonNull;
import static org.apache.maven.plugins.annotations.LifecyclePhase.PREPARE_PACKAGE;

@Mojo(name="build", defaultPhase = PREPARE_PACKAGE)
public class JWebAssemblyMojo extends BaseMojo {

    Artifact compilerDependency;
    final List<File> classesToCompile = new ArrayList<>();
    final List<Dependency> dependencies = new ArrayList<>();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        compilerDependency = dependencyResolver().getCompilerArtifact();

        checkIfJavaVersionIsSupported();
        checkIfCompilerVersionIsSupported();
        findCodeToCompile();

        final JWebAssemblyCompiler compiler = getCompiler();

        passPropertiesTo(compiler);
        passCodeTo(compiler);
        run(compiler);
    }

    JWebAssemblyCompiler getCompiler() throws MojoExecutionException {
        return new JWebAssemblyCompiler(compilerDependency, getLogger());
    }

    private void checkIfJavaVersionIsSupported() throws MojoFailureException {
        if (JavaVersion.getVersion() != 8) {
            throw new MojoFailureException("JWebAssembly (currently) only works with JDK 8");
        }
    }

    private void checkIfCompilerVersionIsSupported() throws MojoFailureException {
        if ("0.1".equals(compilerDependency.getVersion())) {
            throw new MojoFailureException("Unsupported version of jwebassembly-compiler, use 0.2 or higher");
        }
    }

    private void findCodeToCompile() {
        dependencies.addAll(project.getDependencies());
        findClassesToCompileIn(new File(project.getBuild().getOutputDirectory()));
    }

    private void findClassesToCompileIn(final File file) {
        if (file.getName().endsWith(".class")) {
            classesToCompile.add(file);
        } else if (file.isDirectory()) {
            final File[] files = file.listFiles();
            if (nonNull(files)) {
                for (final File content : files) {
                    findClassesToCompileIn(content);
                }
            }
        }
    }

    private void passPropertiesTo(final JWebAssemblyCompiler compiler) throws MojoExecutionException {
        if (nonNull(getProperties())) {
            for (final String key : getProperties().stringPropertyNames()) {
                final String value = getProperties().getProperty(key);

                compiler.setProperty(key, value);
            }
        }
    }

    private void passCodeTo(final JWebAssemblyCompiler compiler) throws MojoExecutionException {
        for (final File classToCompile : classesToCompile) {
            compiler.addFile(classToCompile);
        }

        for (final Dependency dependency : dependencies) {
            compiler.addLibrary(dependencyResolver().resolve(dependency).getFile());
        }
    }

    private void run(final JWebAssemblyCompiler compiler) throws MojoExecutionException {
        if (getOutputFormat().equals(Binary)) {
            compiler.compileToBinary(getOutputFile());
        } else {
            compiler.compileToText(getOutputFile());
        }
    }
}
