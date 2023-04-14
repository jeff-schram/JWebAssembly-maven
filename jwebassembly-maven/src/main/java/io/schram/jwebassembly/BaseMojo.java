package io.schram.jwebassembly;

import io.schram.jwebassembly.util.ArtifactRetriever;
import io.schram.jwebassembly.util.DependencyResolver;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.BuildPluginManager;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;
import org.codehaus.plexus.logging.Logger;

import java.io.File;
import java.util.Properties;

import static io.schram.jwebassembly.OutputFormat.Binary;
import static io.schram.jwebassembly.OutputFormat.Text;

/**
 * Base class that serves to wire injected dependencies to utility classes
 */
abstract class BaseMojo extends AbstractMojo {

    @Parameter(property = "format")
    String format;

    @Parameter( defaultValue = "${localRepository}", required = true, readonly = true )
    ArtifactRepository artifactRepository;

    @Component
    RepositorySystem repositorySystem;

    @Component
    MavenProject mavenProject;

    @Component
    MavenSession mavenSession;

    @Component
    BuildPluginManager pluginManager;

    @Component
    Logger logger;

    @Parameter
    Properties properties;

    public Logger getLogger() {
        return logger;
    }

    @Override
    public Log getLog() {
        throw new UnsupportedOperationException();
    }

    public Properties getProperties() {
        return properties;
    }

    DependencyResolver dependencyResolver;

    DependencyResolver dependencyResolver() {
        if (dependencyResolver == null) {
            ArtifactRetriever artifactRetriever = new ArtifactRetriever(mavenProject, mavenSession, pluginManager);
            dependencyResolver = new DependencyResolver(artifactRepository, artifactRetriever, mavenProject, repositorySystem);
        }

        return dependencyResolver;
    }

    OutputFormat getOutputFormat() {
        return Text.toString().equalsIgnoreCase(format) ? Text : Binary;
    }

    File getOutputFile() {
        String targetPath = String.format("%s/%s.%s",
                mavenProject.getBuild().getDirectory(),
                mavenProject.getBuild().getFinalName(),
                getOutputFormat().equals(Binary) ? "wasm" : "wat");

        return new File(targetPath);
    }
}
