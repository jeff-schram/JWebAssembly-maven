package io.schram.jwebassembly.util;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;

import java.util.Objects;

import static java.util.Objects.nonNull;

public final class DependencyResolver {

    private final ArtifactRepository artifactRepository;
    private final ArtifactRetriever artifactRetriever;
    private final MavenProject mavenProject;
    private final RepositorySystem repositorySystem;

    public DependencyResolver(final ArtifactRepository artifactRepository,
                              final ArtifactRetriever artifactRetriever,
                              final MavenProject mavenProject,
                              final RepositorySystem repositorySystem) {

        this.artifactRepository = artifactRepository;
        this.artifactRetriever = artifactRetriever;
        this.mavenProject = mavenProject;
        this.repositorySystem = repositorySystem;
    }

    public Artifact getCompilerArtifact() throws MojoExecutionException {
        Dependency dependency = getCompilerDependencyFrom(mavenProject);
        Artifact artifact = nonNull(dependency) ? toArtifact(dependency) : getDefaultCompilerArtifact();

        retrieve(artifact);

        return artifactRepository.find(artifact);
    }

    private static Dependency getCompilerDependencyFrom(final MavenProject project) {
        for (final Plugin plugin : project.getBuildPlugins()) {
            if (isJWebAssemblyMavenPlugin(plugin)) {
                return getCompilerDependencyFrom(plugin);
            }
        }

        return null;
    }

    private static boolean isJWebAssemblyMavenPlugin(final Plugin plugin) {
        return Objects.equals(plugin.getGroupId(), "io.schram") && Objects.equals(plugin.getArtifactId(), "jwebassembly-maven");
    }

    private static Dependency getCompilerDependencyFrom(final Plugin plugin) {
        for (final Dependency dependency : plugin.getDependencies()) {
            if (isJWebAssemblyCompilerDependency(dependency)) {
                return dependency;
            }
        }

        return null;
    }

    private static boolean isJWebAssemblyCompilerDependency(final Dependency dependency) {
        return Objects.equals(dependency.getGroupId(), "de.inetsoftware") && Objects.equals(dependency.getArtifactId(), "jwebassembly-compiler");
    }

    private Artifact getDefaultCompilerArtifact() {
        return repositorySystem.createArtifact("de.inetsoftware", "jwebassembly-compiler", "0.4", "jar");
    }

    public Artifact resolve(final Dependency dependency) {
        Artifact artifact = toArtifact(dependency);
        return artifactRepository.find(artifact);
    }

    private Artifact toArtifact(final Dependency dependency) {
        return repositorySystem.createArtifact(
                dependency.getGroupId(),
                dependency.getArtifactId(),
                dependency.getVersion(),
                dependency.getType());
    }

    private void retrieve(final Artifact artifact) throws MojoExecutionException {
        if (nonNull(artifact.getFile()) && artifact.getFile().exists()) {
            return;
        }

        artifactRetriever.retrieve(artifact);
    }
}
