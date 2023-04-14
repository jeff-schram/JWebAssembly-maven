package io.schram.jwebassembly.test;

import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;

import static io.schram.jwebassembly.test.IntegrationTestFunctions.actualWebAssemblyFile;
import static io.schram.jwebassembly.test.IntegrationTestFunctions.expectedWebAssemblyFile;
import static org.apache.commons.io.IOUtils.contentEquals;
import static org.assertj.core.api.Assertions.assertThat;

public class HelloWorldTest {

    @Test
    public void shouldHaveCreateWebAssemblyFile() throws IOException, URISyntaxException {
        assertThat(contentEquals(new FileReader(actualWebAssemblyFile()), new FileReader(expectedWebAssemblyFile()))).isTrue();
    }

}