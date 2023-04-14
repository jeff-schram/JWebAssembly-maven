package io.schram.jwebassembly.test;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.nio.file.Files.find;

public class IntegrationTestFunctions {

    static final Path TARGET_DIRECTORY = Paths.get("target");

    public static File actualWebAssemblyFile() throws IOException {
        return findFileWithExtension("wasm");
    }

    public static File actualWebAssemblyTextFile() throws IOException {
        return findFileWithExtension("wat");
    }

    private static File findFileWithExtension(final String extension) throws IOException {
        try (Stream<Path> stream = find(TARGET_DIRECTORY, 1, (path, attributes) -> path.getFileName().toString().endsWith(extension))) {
            return stream.findFirst()
                    .map(Path::toFile)
                    .orElseThrow(() -> new IllegalStateException(format("No .%s file found", extension)));
        }
    }

    public static File expectedWebAssemblyFile() throws URISyntaxException {
        return findResource("expected_result.wasm");
    }

    public static File expectedWebAssemblyTextFile() throws URISyntaxException {
        return findResource("expected_result.wat");
    }

    private static File findResource(final String name) throws URISyntaxException {
        return Paths.get(IntegrationTestFunctions.class.getClassLoader().getResource(name).toURI()).toFile();
    }

}