package io.schram.jwebassembly;

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
import static org.apache.maven.plugins.annotations.LifecyclePhase.PROCESS_CLASSES;

/**
 * Simple Maven plugin (i.e., {@link Mojo} for
 * 1. retrieving the JWebAssembly compiler from Maven, and
 * 2. executing the JWebAssembly compiler (through Reflection)
 */
@Mojo(name="compile", defaultPhase = PROCESS_CLASSES)
public class JWebAssemblyMojo extends BaseMojo {

    Artifact compilerDependency;
    final List<File> classesToCompile = new ArrayList<>();
    final List<Dependency> dependencies = new ArrayList<>();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        compilerDependency = dependencyResolver().resolve(compiler);

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

    private void checkIfCompilerVersionIsSupported() throws MojoFailureException {
        if ("0.1".equals(compilerDependency.getVersion())) {
            throw new MojoFailureException("Unsupported version of jwebassembly-compiler, use 0.2 or higher");
        }
    }

    private void findCodeToCompile() {
        List<Dependency> dependencies = new ArrayList<>();
        for (final Dependency dependency : mavenProject.getDependencies()) {
            if (! dependency.getScope().equalsIgnoreCase("test")) {
                dependencies.add(dependency);
            }
        }

        this.dependencies.addAll(dependencies);
        findClassesToCompileIn(new File(mavenProject.getBuild().getOutputDirectory()));
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
