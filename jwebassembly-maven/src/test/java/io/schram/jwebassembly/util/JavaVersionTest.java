package io.schram.jwebassembly.util;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.assertThat;

@SuppressWarnings("JUnitMalformedDeclaration")
@RunWith(JUnitParamsRunner.class)
public class JavaVersionTest {

    @Test
    @Parameters({
            "1.6.0_23, 6",
            "1.7.0, 7",
            "1.7.0_80, 7",
            "1.8.0_211, 8",
            "9.0.1, 9",
            "11.0.4, 11",
            "12, 12",
            "12.0.1, 12" })
    public void testIfAbleToGetJavaVersion(final String property, final int expectedVersion) {
        System.setProperty("java.version", property);

        assertThat(JavaVersion.getVersion()).isEqualTo(expectedVersion);
    }
}