package io.schram.jwebassembly;

import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;
import org.codehaus.plexus.logging.Logger;

import java.io.File;
import java.util.List;
import java.util.Properties;

import static io.schram.jwebassembly.OutputFormat.Binary;
import static io.schram.jwebassembly.OutputFormat.Text;

/**
 * Base class that serves to wire injected dependencies to utility classes
 */
abstract class BaseMojo extends AbstractMojo {

    @Parameter(property = "format")
    String format;

    @Parameter(property = "compiler", required = true)
    Compiler compiler;

    @Parameter( defaultValue = "${project}", readonly = true )
    MavenProject mavenProject;

    @Component
    Logger logger;

    @Parameter
    Properties properties;

    @Component
    RepositorySystem repositorySystem;

    @Parameter(defaultValue = "${project.remoteArtifactRepositories}", readonly = true, required = true)
    private List<MavenArtifactRepository> remoteRepositories;

    Logger getLogger() {
        return logger;
    }

    @Override
    public Log getLog() {
        throw new UnsupportedOperationException();
    }

    Properties getProperties() {
        return properties;
    }

    DependencyResolver dependencyResolver;

    DependencyResolver dependencyResolver() {
        if (dependencyResolver == null) {
            dependencyResolver = new DependencyResolver(remoteRepositories, repositorySystem);
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
