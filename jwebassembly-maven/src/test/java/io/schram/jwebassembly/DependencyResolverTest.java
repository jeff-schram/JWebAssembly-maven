package io.schram.jwebassembly;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.artifact.resolver.ArtifactResolutionResult;
import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.repository.RepositorySystem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.util.Sets.set;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DependencyResolverTest {

    RepositorySystem repositorySystem;
    DependencyResolver dependencyResolver;

    @BeforeEach
    void setupDependencyResolver() {
        repositorySystem = mock(RepositorySystem.class);
        dependencyResolver = new DependencyResolver(emptyList(), repositorySystem);
    }

    @Test
    public void testIfAbleToResolveCompiler() throws MojoExecutionException {
        String groupId = "some.de.inetsoftware";
        String artifactId = "some-jwebassembly-compiler";
        String version = "0.42";

        Compiler compiler = mock(Compiler.class);
        when(compiler.getArtifactId()).thenReturn(artifactId);
        when(compiler.getGroupId()).thenReturn(groupId);
        when(compiler.getVersion()).thenReturn(version);

        Artifact expectedCompilerArtifact = mock(Artifact.class);
        when(repositorySystem.createArtifact(groupId, artifactId, version, "jar")).thenReturn(expectedCompilerArtifact);

        ArtifactResolutionResult resolutionResult = mock(ArtifactResolutionResult.class);
        when(resolutionResult.getArtifacts()).thenReturn(set(expectedCompilerArtifact));
        when(repositorySystem.resolve(any(ArtifactResolutionRequest.class))).thenReturn(resolutionResult);

        Artifact result = dependencyResolver.resolve(compiler);

        assertThat(result).isEqualTo(expectedCompilerArtifact);
        verify(repositorySystem).createArtifact(groupId, artifactId, version, "jar");
        verify(repositorySystem).resolve(any(ArtifactResolutionRequest.class));
    }

    @Test
    public void testIfAbleToResolveDependency() throws MojoExecutionException {
        String groupId = "some.group";
        String artifactId = "artifact-id";
        String version = "1.2.3";
        String type = "jar";

        Dependency dependency = mock(Dependency.class);
        when(dependency.getGroupId()).thenReturn(groupId);
        when(dependency.getArtifactId()).thenReturn(artifactId);
        when(dependency.getVersion()).thenReturn(version);
        when(dependency.getType()).thenReturn(type);

        Artifact expectedCompilerArtifact = mock(Artifact.class);
        when(repositorySystem.createArtifact(groupId, artifactId, version, type)).thenReturn(expectedCompilerArtifact);

        ArtifactResolutionResult resolutionResult = mock(ArtifactResolutionResult.class);
        when(resolutionResult.getArtifacts()).thenReturn(set(expectedCompilerArtifact));
        when(repositorySystem.resolve(any(ArtifactResolutionRequest.class))).thenReturn(resolutionResult);

        Artifact result = dependencyResolver.resolve(dependency);

        assertThat(result).isEqualTo(expectedCompilerArtifact);
        verify(repositorySystem).createArtifact(groupId, artifactId, version, type);
        verify(repositorySystem).resolve(any(ArtifactResolutionRequest.class));
    }
}