package at.jku.dke.task_app.xquery.evaluation.analysis;

/**
 * Represents an incorrect node text value.
 */
public final class IncorrectTextValueModel {
    private String path;
    private String expectedValue;

    /**
     * Creates a new instance of class {@link IncorrectTextValueModel}.
     */
    public IncorrectTextValueModel() {
    }

    /**
     * Gets the path to the node.
     *
     * @return The path to the node.
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the path to the node .
     *
     * @param path The path to the node.
     */
    public void setPath(String path) {
        this.path = path;
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
