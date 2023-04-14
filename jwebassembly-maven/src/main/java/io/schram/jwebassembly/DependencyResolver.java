package io.schram.jwebassembly;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.MavenArtifactRepository;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.repository.RepositorySystem;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Utility class for retrieving Maven dependencies
 */
final class DependencyResolver {

    private final RepositorySystem repositorySystem;
    private final List<MavenArtifactRepository> remoteRepositories;

    DependencyResolver(final List<MavenArtifactRepository> remoteRepositories, final RepositorySystem repositorySystem) {

        this.remoteRepositories = remoteRepositories;
        this.repositorySystem = repositorySystem;
    }

    /**
     * @param compiler to resolve to {@code Artifact}
     * @return resolved artifact
     * @throws MojoExecutionException when unable to retrieve artifact through {@code ArtifactRetriever}
     */
    Artifact resolve(final Compiler compiler) throws MojoExecutionException {
        Artifact artifact = repositorySystem.createArtifact(compiler.getGroupId(), compiler.getArtifactId(), compiler.getVersion(), "jar");
        return resolve(artifact);
    }

    /**
     * @param dependency to resolve to {@code Artifact}
     * @return resolved artifact
     */
    Artifact resolve(final Dependency dependency) throws MojoExecutionException {
        Artifact artifact = toArtifact(dependency);
        return resolve(artifact);
    }

    private Artifact toArtifact(final Dependency dependency) {
        return repositorySystem.createArtifact(
                dependency.getGroupId(),
                dependency.getArtifactId(),
                dependency.getVersion(),
                dependency.getType());
    }

    private Artifact resolve(final Artifact artifact) throws MojoExecutionException {
        ArtifactResolutionRequest request = new ArtifactResolutionRequest();
        request.setArtifact(artifact);
        request.setRemoteRepositories(new ArrayList<>(remoteRepositories));
        ArtifactResolutionResult result = repositorySystem.resolve(request);

        Iterator<Artifact> artifacts = result.getArtifacts().iterator();
        if (artifacts.hasNext()) {
            return artifacts.next();
        } else {
            throw new MojoExecutionException("Unable to retrieve " + artifact);
        }
    }
}
