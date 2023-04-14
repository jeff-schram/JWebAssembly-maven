package io.schram.jwebassembly;

/**
 * Format of the output file created by the {@code JWebAssemblyCompiler}.
 */
public enum OutputFormat {
    /** Compiled .wasm file */
    Binary,

    /** Human-readable .wat file */
    Text
}
