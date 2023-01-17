package io.schram.jwebassembly.util;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;

import java.util.Objects;

import static java.util.Objects.nonNull;

public final class DependencyResolver {
    private final ArtifactRepository localRepository;
    private final MavenProject project;
    private final RepositorySystem repositorySystem;

    public DependencyResolver(final MavenProject project,
                              final ArtifactRepository localRepository,
                              final RepositorySystem repositorySystem) {

        this.localRepository = localRepository;
        this.project = project;
        this.repositorySystem = repositorySystem;
    }

    public Artifact getCompilerArtifact() {
        Dependency dependency = getCompilerDependencyFrom(project);
        Artifact artifact = nonNull(dependency) ? toArtifact(dependency) : getDefaultCompilerArtifact();

        return localRepository.find(artifact);
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
        return localRepository.find(artifact);
    }

    private Artifact toArtifact(final Dependency dependency) {
        return repositorySystem.createArtifact(
                dependency.getGroupId(),
                dependency.getArtifactId(),
                dependency.getVersion(),
                dependency.getType());
    }
}
