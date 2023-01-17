package io.schram.jwebassembly;

import org.apache.maven.artifact.InvalidRepositoryException;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.DefaultMavenExecutionResult;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecution;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.codehaus.plexus.component.repository.exception.ComponentLookupException;
import org.eclipse.aether.DefaultRepositorySystemSession;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;

public abstract class AbstractMojoIntegrationTest extends AbstractMojoTestCase {

    File createBaseDirectoryFor(final String testProjectDirectory) throws IOException {
        File baseDirectory = new File("target/test-classes/" + testProjectDirectory);
        File targetDirectory = new File(baseDirectory, "target");

        if (targetDirectory.exists()) {
            try (Stream<Path> stream = Files.walk(targetDirectory.toPath())) {
                stream.sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
            }
        }

        Files.createDirectory(targetDirectory.toPath());

        return baseDirectory;
    }

    void copyExampleCodeTo(final File baseDirectory) throws IOException {
        final File targetDirectory = Paths.get(baseDirectory.getAbsolutePath(), "target", "classes").toFile();
        assertTrue(targetDirectory.mkdirs());

        try (final Stream<Path> stream = Files.find(Paths.get("target"), 10, (path, attributes) -> path.endsWith("HelloWorld.class"))) {
            Path helloWorld = stream.findFirst().get();
            Files.copy(helloWorld, Paths.get(targetDirectory.getAbsolutePath(), helloWorld.getFileName().toString()));
        }
    }

    JWebAssemblyMojo getJWebAssemblyMojoFor(final File baseDirectory) throws Exception {
        MavenSession session = getMavenSessionFor(baseDirectory);
        MojoExecution execution = newMojoExecution("build");
        return (JWebAssemblyMojo) lookupConfiguredMojo(session, execution);
    }

    protected MavenSession getMavenSessionFor(final File baseDirectory) throws Exception {
        MavenExecutionRequest mavenExecutionRequest = createMavenExecutionRequestFor(baseDirectory);
        MavenProject mavenProject = createMavenProjectFor(mavenExecutionRequest);
        MavenExecutionResult result = new DefaultMavenExecutionResult();

        MavenSession session = new MavenSession( getContainer(), MavenRepositorySystemUtils.newSession(), mavenExecutionRequest, result );
        session.setCurrentProject( mavenProject );
        session.setProjects(singletonList(mavenProject));

        return session;
    }

    private MavenExecutionRequest createMavenExecutionRequestFor(final File baseDirectory) throws ComponentLookupException, InvalidRepositoryException {
        ArtifactRepository artifactRepository = lookup(RepositorySystem.class).createDefaultLocalRepository();
        MavenExecutionRequest request = new DefaultMavenExecutionRequest();

        request.setBaseDirectory(baseDirectory);
        request.setLocalRepository(artifactRepository);

        return request;
    }

    private MavenProject createMavenProjectFor(final MavenExecutionRequest mavenExecutionRequest) throws ComponentLookupException, ProjectBuildingException {
        ProjectBuildingRequest configuration = mavenExecutionRequest.getProjectBuildingRequest();
        configuration.setRepositorySession(new DefaultRepositorySystemSession());

        File pom = new File(mavenExecutionRequest.getBaseDirectory(), "pom.xml");
        return lookup(ProjectBuilder.class).build(pom, configuration).getProject();
    }
}
