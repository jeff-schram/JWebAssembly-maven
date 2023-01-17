package io.schram.jwebassembly.util;

public final class JavaVersion {

    public static int getVersion() {
        String version = System.getProperty("java.version");

        if (version.startsWith("1.")) {
            version = version.substring(2, 3);
        } else {
            int dot = version.indexOf(".");
            if (dot != -1) {
                version = version.substring(0, dot);
            }
        }

        return Integer.parseInt(version);
    }
}
