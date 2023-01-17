package io.schram.jwebassembly;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.MojoExecutionException;
import org.codehaus.plexus.logging.Logger;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newOutputStream;

public class JWebAssemblyCompiler {

    private final Object compiler;
    private final Method addFile;
    private final Method compileToBinary;
    private final Method compileToText;
    private final Method setProperty;
    private final Method addLibrary;
    private final Logger log;

    /**
     * Facade class for interacting with a (reflectively accessed) JWebAssembly instance
     */
    JWebAssemblyCompiler(final Artifact compilerDependency, final Logger log) throws MojoExecutionException {
        this.log = log;
        try {
            log.debug("Getting reference to: " + compilerDependency);
            Class<?> compiler = getReferenceToCompilerFor(compilerDependency);
            this.compiler = compiler.getDeclaredConstructor().newInstance();

            log.debug("Retrieving references to compiler methods");
            addFile = getMethod(compiler, "addFile", File.class);
            compileToBinary = getMethod(compiler, "compileToBinary", File.class);
            compileToText = getMethod(compiler, "compileToText", Appendable.class);
            setProperty = getMethod(compiler, "setProperty", String.class, String.class);
            addLibrary = getMethod(compiler, "addLibrary", File.class);
        } catch (final Throwable throwable) {
            log.error("Unable to access JWebAssembly compiler", throwable);
            throw new MojoExecutionException(throwable);
        }
    }

    private static Class<?> getReferenceToCompilerFor(final Artifact compilerDependency) throws MojoExecutionException {
        try {
            URLClassLoader classLoader = new URLClassLoader( new URL[]{ compilerDependency.getFile().toURI().toURL()});
            return classLoader.loadClass("de.inetsoftware.jwebassembly.JWebAssembly");
        } catch (final ClassNotFoundException | IOException exception) {
            throw new MojoExecutionException(exception);
        }
    }

    private static Method getMethod(final Class<?> clazz, final String name, final Class<?>... parameterTypes ) throws Exception {
        Method method = clazz.getMethod( name, parameterTypes );
        method.setAccessible(true);
        return method;
    }

    void addFile(@Nonnull final File file) throws MojoExecutionException {
        try {
            log.debug("Adding " + file.getAbsolutePath());
            addFile.invoke(compiler, file);
        } catch (final Exception exception) {
            throw new MojoExecutionException(exception);
        }
    }

    void addLibrary(@Nonnull final File file) throws MojoExecutionException {
        try {
            log.debug("Adding " + file.getAbsolutePath());
            addLibrary.invoke(compiler, file);
        } catch (final Exception exception) {
            throw new MojoExecutionException(exception);
        }
    }

    void setProperty(final String key, final String value) throws MojoExecutionException {
        try {
            log.debug(String.format("Adding property '%s' with value '%s'", key, value));
            setProperty.invoke(compiler, key, value);
        } catch (final Exception exception) {
            throw new MojoExecutionException(exception);
        }
    }

    void compileToBinary(final File target) throws MojoExecutionException {
        log.info("Compiling to Binary format");

        try {
            compileToBinary.invoke(compiler, target);
        } catch (final InvocationTargetException | IllegalAccessException exception) {
            log.error("Unable to compile to binary format", exception);
            throw new MojoExecutionException(exception);
        }
    }

    void compileToText(final File target) throws MojoExecutionException {
        log.info("Compiling to Text format");

        try (final OutputStream outputStream = newOutputStream(target.toPath());
             final OutputStreamWriter writer = new OutputStreamWriter(outputStream, UTF_8)) {

            compileToText.invoke(compiler, writer);
        } catch (final InvocationTargetException | IllegalAccessException | IOException exception) {
            log.error("Unable to compile to text format", exception);
            throw new MojoExecutionException(exception);
        }
    }
}
