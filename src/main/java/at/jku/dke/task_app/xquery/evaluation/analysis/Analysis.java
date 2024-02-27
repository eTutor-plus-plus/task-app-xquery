package at.jku.dke.task_app.xquery.evaluation.analysis;

import org.w3c.dom.Document;

import java.util.List;

/**
 * Represents the result of an analysis.
 */
public interface Analysis {
    /**
     * Gets the submission result.
     *
     * @return The submission result.
     */
    XQResult getSubmissionResult();

    /**
     * Gets the solution result.
     *
     * @return The solution result.
     */
    XQResult getSolutionResult();

    /**
     * Returns whether the submission is fully correct.
     *
     * @return {@code true} if the submission is correct; otherwise {@code false}.
     */
    boolean isCorrect();

    /**
     * Returns whether the schema is valid, i.e. the schema of the submission result is valid with regard to the solution result.
     *
     * @return {@code true} if the schema is valid; otherwise {@code false}.
     */
    boolean isSchemaValid();

    /**
     * Returns the diff document.
     *
     * @return The document with diff annotations.
     */
    Document getDiffDocument();

    /**
     * Returns the missing nodes.
     * <p>
     * Missing nodes are nodes that are contained in the solution result, but not in the submission result.
     *
     * @return The missing nodes.
     */
    List<NodeModel> getMissingNodes();

    /**
     * Returns the superfluous nodes.
     * <p>
     * Superfluous nodes are nodes that are contained in the submission result, but not in the solution result.
     *
     * @return The superfluous nodes.
     */
    List<NodeModel> getSuperfluousNodes();

    /**
     * Returns the incorrect text values.
     * <p>
     * Incorrect text values are elements that are contained in the submission result, but have a different value than in the solution result.
     *
     * @return The incorrect text values.
     */
    List<IncorrectTextValueModel> getIncorrectTextValues();

    /**
     * Returns the displaced nodes.
     * <p>
     * Displaced nodes are nodes that are contained in the submission result, but not at the expected position.
     *
     * @return The displaced nodes.
     */
    List<NodeModel> getDisplacedNodes();

    /**
     * Returns the missing attributes.
     * <p>
     * Missing attributes are attributes that are contained in the solution result, but not in the submission result.
     *
     * @return The missing attributes.
     */
    List<AttributeModel> getMissingAttributes();

    /**
     * Returns the superfluous attributes.
     * <p>
     * Superfluous attributes are attributes that are contained in the submission result, but not in the solution result.
     *
     * @return The superfluous attributes.
     */
    List<AttributeModel> getSuperfluousAttributes();

    /**
     * Returns the incorrect attribute values.
     * <p>
     * Incorrect attribute values are attributes that are contained in the submission result, but have a different value than in the solution result.
     *
     * @return The incorrect attribute values.
     */
    List<IncorrectAttributeValueModel> getIncorrectAttributeValues();
}
