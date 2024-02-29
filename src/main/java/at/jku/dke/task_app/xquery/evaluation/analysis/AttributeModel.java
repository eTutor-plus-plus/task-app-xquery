package at.jku.dke.task_app.xquery.evaluation.analysis;

/**
 * Represents a missing or superfluous attribute.
 */
public class AttributeModel {
    private String path;
    private String name;
    private String value;

    /**
     * Creates a new instance of class {@link AttributeModel}.
     */
    public AttributeModel() {
    }

    /**
     * Creates a new instance of class {@link AttributeModel}.
     *
     * @param path  The path to the parent of the node containing the attribute.
     * @param name  The name of the attribute.
     * @param value The attribute value.
     */
    public AttributeModel(String path, String name, String value) {
        this.path = path;
        this.name = name;
        this.value = value;
    }

    /**
     * Gets the path to the parent of the node containing the attribute.
     *
     * @return The path to the parent of the node containing the attribute.
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the path to the parent of the node containing the attribute.
     *
     * @param path The path to the parent of the node containing the attribute.
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
     * Sets the attribute value.
     *
     * @return The attribute value.
     */
    public String getValue() {
        return value;
    }

    /**
     * Gets the attribute value.
     *
     * @param value The attribute value.
     */
    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.path + "@" + this.name + "=\"" + this.value + "\"";
    }
}
