package at.jku.dke.task_app.xquery.evaluation.grading;

import java.math.BigDecimal;

/**
 * Represents a grading entry.
 *
 * @param errorCategory The error category (must be one of the error category constant values).
 * @param minusPoints   The minus points (must not be negative).
 */
public record GradingEntry(String errorCategory, BigDecimal minusPoints) {
    public GradingEntry {
        if (minusPoints == null || minusPoints.compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Minus points must not be negative.");
        if (errorCategory == null || errorCategory.isBlank())
            throw new IllegalArgumentException("Error category must not be null or blank.");
    }

    /**
     * Error Category: Invalid Schema
     */
    public static final String INVALID_SCHEMA = "schema";

    /**
     * Error Category: Missing Node
     */
    public static final String MISSING_NODE = "missingNode";

    /**
     * Error Category: Superfluous Node
     */
    public static final String SUPERFLUOUS_NODE = "superfluousNode";

    /**
     * Error Category: Incorrect Text
     */
    public static final String INCORRECT_TEXT = "incorrectText";

    /**
     * Error Category: Displaced Node
     */
    public static final String DISPLACED_NODE = "displacedNode";

    /**
     * Error Category: Missing Attribute
     */
    public static final String MISSING_ATTRIBUTE = "missingAttribute";

    /**
     * Error Category: Superfluous Attribute
     */
    public static final String SUPERFLUOUS_ATTRIBUTE = "superfluousAttribute";

    /**
     * Error Category: Incorrect Attribute Value
     */
    public static final String INCORRECT_ATTRIBUTE_VALUE = "incorrectAttributeValue";
}
