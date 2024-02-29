package at.jku.dke.task_app.xquery.dto;

import at.jku.dke.task_app.xquery.data.entities.GradingStrategy;
import at.jku.dke.task_app.xquery.data.entities.XQueryTask;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * DTO for {@link XQueryTask}
 *
 * @param solution                        The solution.
 * @param sorting                         The XPath sorting expressions (separated by new line).
 * @param missingNodePenalty              The penalty for missing nodes.
 * @param missingNodeStrategy             The grading strategy for missing nodes.
 * @param superfluousNodePenalty          The penalty for superfluous nodes.
 * @param superfluousNodeStrategy         The grading strategy for superfluous nodes.
 * @param incorrectTextPenalty            The penalty for incorrect text.
 * @param incorrectTextStrategy           The grading strategy for incorrect text.
 * @param displacedNodePenalty            The penalty for displaced nodes.
 * @param displacedNodeStrategy           The grading strategy for displaced nodes.
 * @param missingAttributePenalty         The penalty for missing attributes.
 * @param missingAttributeStrategy        The grading strategy for missing attributes.
 * @param superfluousAttributePenalty     The penalty for superfluous attributes.
 * @param superfluousAttributeStrategy    The grading strategy for superfluous attributes.
 * @param incorrectAttributeValuePenalty  The penalty for incorrect attribute values.
 * @param incorrectAttributeValueStrategy The grading strategy for incorrect attribute values.
 */
public record XQueryTaskDto(@Schema(example = "return for $b in //bezirke return $b/name") @NotNull String solution,
                            @Schema(example = "//name") String sorting,
                            @NotNull @PositiveOrZero BigDecimal missingNodePenalty,
                            @NotNull GradingStrategy missingNodeStrategy,
                            @NotNull @PositiveOrZero BigDecimal superfluousNodePenalty,
                            @NotNull GradingStrategy superfluousNodeStrategy,
                            @NotNull @PositiveOrZero BigDecimal incorrectTextPenalty,
                            @NotNull GradingStrategy incorrectTextStrategy,
                            @NotNull @PositiveOrZero BigDecimal displacedNodePenalty,
                            @NotNull GradingStrategy displacedNodeStrategy,
                            @NotNull @PositiveOrZero BigDecimal missingAttributePenalty,
                            @NotNull GradingStrategy missingAttributeStrategy,
                            @NotNull @PositiveOrZero BigDecimal superfluousAttributePenalty,
                            @NotNull GradingStrategy superfluousAttributeStrategy,
                            @NotNull @PositiveOrZero BigDecimal incorrectAttributeValuePenalty,
                            @NotNull GradingStrategy incorrectAttributeValueStrategy) implements Serializable {
    /**
     * Creates a new instance of {@link XQueryTaskDto}.
     *
     * @param solution The solution.
     * @param sorting  The sorting.
     */
    public XQueryTaskDto(@NotNull String solution, String sorting) {
        this(solution, sorting, BigDecimal.ZERO, GradingStrategy.KO, BigDecimal.ZERO, GradingStrategy.KO, BigDecimal.ZERO, GradingStrategy.KO, BigDecimal.ZERO,
            GradingStrategy.KO, BigDecimal.ZERO, GradingStrategy.KO, BigDecimal.ZERO, GradingStrategy.KO, BigDecimal.ZERO, GradingStrategy.KO);
    }

    /**
     * Creates a new instance of {@link XQueryTaskDto} based on a {@link XQueryTask}.
     *
     * @param task The task.
     */
    public XQueryTaskDto(XQueryTask task) {
        this(task.getSolution(), String.join("\n", task.getSorting()), task.getMissingNodePenalty(), task.getMissingNodeStrategy(),
            task.getSuperfluousNodePenalty(), task.getSuperfluousNodeStrategy(), task.getIncorrectTextPenalty(), task.getIncorrectTextStrategy(),
            task.getDisplacedNodePenalty(), task.getDisplacedNodeStrategy(), task.getMissingAttributePenalty(), task.getMissingAttributeStrategy(),
            task.getSuperfluousAttributePenalty(), task.getSuperfluousAttributeStrategy(), task.getIncorrectAttributeValuePenalty(), task.getIncorrectAttributeValueStrategy());
    }
}
