package io.schram.jwebassembly.util;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.RepositorySystem;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DependencyResolverTest {

    @Mock ArtifactRepository localRepository;
    @Mock MavenProject mavenProject;
    @Mock RepositorySystem repositorySystem;

    @InjectMocks
    DependencyResolver dependencyResolver;

    @Test
    public void testIfAbleToGetCompilerArtifactFromPOM() {
        Plugin otherPlugin = mock(Plugin.class);
        when(otherPlugin.getGroupId()).thenReturn("io.schram");
        when(otherPlugin.getArtifactId()).thenReturn("other-maven-plugin");

        Plugin jwebassemblyPlugin = mock(Plugin.class);
        when(jwebassemblyPlugin.getGroupId()).thenReturn("io.schram");
        when(jwebassemblyPlugin.getArtifactId()).thenReturn("jwebassembly-maven");

        when(mavenProject.getBuildPlugins()).thenReturn(asList(otherPlugin, jwebassemblyPlugin));

        Dependency irrelevantDependency = mock(Dependency.class);
        when(irrelevantDependency.getGroupId()).thenReturn("de.inetsoftware");
        when(irrelevantDependency.getArtifactId()).thenReturn("irrelevant-dependency");

        Dependency jwebassemblyDependency = mock(Dependency.class);
        when(jwebassemblyDependency.getGroupId()).thenReturn("de.inetsoftware");
        when(jwebassemblyDependency.getArtifactId()).thenReturn("jwebassembly-compiler");
        when(jwebassemblyDependency.getVersion()).thenReturn("0.42");
        when(jwebassemblyDependency.getType()).thenReturn("jar");
        when(jwebassemblyPlugin.getDependencies()).thenReturn(asList(irrelevantDependency, jwebassemblyDependency));

        Artifact expectedCompilerArtifact = mock(Artifact.class);
        when(repositorySystem.createArtifact("de.inetsoftware", "jwebassembly-compiler", "0.42", "jar")).thenReturn(expectedCompilerArtifact);
        when(localRepository.find(expectedCompilerArtifact)).thenReturn(expectedCompilerArtifact);

        Artifact result = dependencyResolver.getCompilerArtifact();

        assertThat(result).isEqualTo(expectedCompilerArtifact);
        verify(repositorySystem).createArtifact("de.inetsoftware", "jwebassembly-compiler", "0.42", "jar");
        verify(localRepository).find(expectedCompilerArtifact);
    }

    @Test
    public void testIfGettingDefaultWhenCompilerArtifactIsNotDeclaredInPOM() {
        Artifact expectedCompilerArtifact = mock(Artifact.class);
        when(repositorySystem.createArtifact("de.inetsoftware", "jwebassembly-compiler", "0.4", "jar")).thenReturn(expectedCompilerArtifact);
        when(localRepository.find(expectedCompilerArtifact)).thenReturn(expectedCompilerArtifact);

        Artifact result = dependencyResolver.getCompilerArtifact();

        assertThat(result).isEqualTo(expectedCompilerArtifact);
        verify(repositorySystem).createArtifact("de.inetsoftware", "jwebassembly-compiler", "0.4", "jar");
        verify(localRepository).find(expectedCompilerArtifact);
    }

    @Test
    public void testIfAbleToResolveDependency() {
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
        when(localRepository.find(expectedCompilerArtifact)).thenReturn(expectedCompilerArtifact);

        Artifact result = dependencyResolver.resolve(dependency);

        assertThat(result).isEqualTo(expectedCompilerArtifact);
        verify(repositorySystem).createArtifact(groupId, artifactId, version, type);
        verify(localRepository).find(expectedCompilerArtifact);
    }
}