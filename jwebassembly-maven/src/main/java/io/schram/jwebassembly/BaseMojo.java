package io.schram.jwebassembly;

import io.schram.jwebassembly.util.DependencyResolver;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
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
    ArtifactRepository localRepository;

    @Parameter(defaultValue = "${project}", required = true, readonly = true)
    MavenProject project;

    @Component
    RepositorySystem repositorySystem;

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
            dependencyResolver = new DependencyResolver(project, localRepository, repositorySystem);
        }

        return dependencyResolver;
    }

    OutputFormat getOutputFormat() {
        return Text.toString().equalsIgnoreCase(format) ? Text : Binary;
    }

    File getOutputFile() {
        String targetPath = String.format("%s/%s.%s",
                project.getBuild().getDirectory(),
                project.getBuild().getFinalName(),
                getOutputFormat().equals(Binary) ? "wasm" : "wat");

        return new File(targetPath);
    }
}
