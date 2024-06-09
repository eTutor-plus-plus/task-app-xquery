package at.jku.dke.task_app.xquery.evaluation.analysis;

/**
 * Represents a missing or superfluous node.
 */
public class NodeModel {
    private String path;
    private String name;

    /**
     * Creates a new instance of class {@link NodeModel}.
     */
    public NodeModel() {
    }

    /**
     * Creates a new instance of class {@link NodeModel}.
     *
     * @param path The path to the parent of the node.
     * @param name The name of the node.
     */
    public NodeModel(String path, String name) {
        this.path = path.replaceAll("/xquery-result\\[1\\]", "");
        this.name = name;
    }

    /**
     * Gets the path to the parent of the node.
     *
     * @return The path to the parent of the node.
     */
    public String getPath() {
        return path;
    }

    /**
     * Sets the path to the parent of the node.
     *
     * @param path The path to the parent of the node.
     */
    public void setPath(String path) {
        this.path = path.replaceAll("/xquery-result\\[1\\]", "");
    }

    /**
     * Gets the name of the node.
     *
     * @return The name of the node.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the node.
     *
     * @param name The name of the node.
     */
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.path + ": " + this.name;
    }
}
