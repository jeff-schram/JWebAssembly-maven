package io.schram.jwebassembly;

/**
 * Represents the intended JWebAssembly compiler implementation
 */
public class Compiler {

    static final String DEFAULT_ARTIFACT_ID = "jwebassembly-compiler";
    static final String DEFAULT_GROUP_ID = "de.inetsoftware";
    static final String DEFAULT_VERSION = "0.4";

    private String artifactId;
    private String groupId;
    private String version;

    /**
     * @return the artifactId as specified in the configuration.compiler tag, or the default ({@link #DEFAULT_ARTIFACT_ID})
     */
    public String getArtifactId() {
        return this.artifactId != null ? this.artifactId : DEFAULT_ARTIFACT_ID;
    }

    void setArtifactId(final String artifactId) {
        this.artifactId = artifactId;
    }

    /**
     * @return the groupId as specified in the configuration.compiler tag, or the default ({@link #DEFAULT_GROUP_ID})
     */
    public String getGroupId() {
        return this.groupId != null ? this.groupId : DEFAULT_GROUP_ID;
    }

    void setGroupId(final String groupId) {
        this.groupId = groupId;
    }

    /**
     * @return the version as specified in the configuration.compiler tag, or the default ({@link #DEFAULT_VERSION})
     */
    public String getVersion() {
        return this.version != null ? this.version : DEFAULT_VERSION;
    }

    void setVersion(final String version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + getGroupId() + ":" + getArtifactId() + ":" + getVersion() + ")";
    }
}
