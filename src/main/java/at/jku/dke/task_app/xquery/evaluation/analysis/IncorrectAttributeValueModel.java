package at.jku.dke.task_app.xquery.evaluation.analysis;

/**
 * Represents an incorrect attribute value.
 */
public final class IncorrectAttributeValueModel {
    private String path;
    private String name;
    private String expectedValue;

    /**
     * Creates a new instance of class {@link IncorrectAttributeValueModel}.
     */
    public IncorrectAttributeValueModel() {
    }

    /**
     * Creates a new instance of class {@link IncorrectAttributeValueModel}.
     *
     * @param path          The path to the node containing the attribute.
     * @param name          The name of the attribute.
     * @param expectedValue The expected value.
     */
    public IncorrectAttributeValueModel(String path, String name, String expectedValue) {
        this.path = path;
        this.name = name;
        this.expectedValue = expectedValue;
    }

    /**
     * Gets the path to the node containing the attribute.
     *
     * @return The path to the node containing the attribute.
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the path to the node containing the attribute.
     *
     * @param path The path to the node containing the attribute.
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * Gets the name of the attribute.
     *
     * @return The name of the attribute.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the attribute.
     *
     * @param name The name of the attribute.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the expected value.
     *
     * @return The expected value.
     */
    public String getExpectedValue() {
        return expectedValue;
    }

    /**
     * Sets the expected value.
     *
     * @param expectedValue The expected value.
     */
    public void setExpectedValue(String expectedValue) {
        this.expectedValue = expectedValue;
    }
}
