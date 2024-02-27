package at.jku.dke.task_app.xquery.evaluation.grading;

import at.jku.dke.task_app.xquery.data.entities.GradingStrategy;
import at.jku.dke.task_app.xquery.data.entities.XQueryTask;
import at.jku.dke.task_app.xquery.evaluation.analysis.Analysis;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Grades the XQuery analysis.
 */
public class XQueryGrading {
    private final XQueryTask task;
    private final Analysis analysis;
    private final BigDecimal points;
    private final boolean isCorrect;
    private final List<GradingEntry> details;

    /**
     * Creates a new instance of class {@link XQueryGrading} and grades the analysis.
     *
     * @param task     The task containing the information about the grading strategy and penalties.
     * @param analysis The analysis object containing information about the analyzed query and detected errors.
     */
    public XQueryGrading(XQueryTask task, Analysis analysis) {
        this.task = task;
        this.analysis = analysis;
        this.isCorrect = analysis.isCorrect();
        if (analysis.isCorrect()) {
            this.points = task.getMaxPoints();
            this.details = new ArrayList<>();
        } else if (analysis.isSchemaValid()) {
            this.details = calculateMinusPoints(analysis, task);
            BigDecimal deduction = details.stream().map(GradingEntry::minusPoints).reduce(BigDecimal.ZERO, BigDecimal::add);
            this.points = task.getMaxPoints().subtract(deduction).max(BigDecimal.ZERO);
        } else {
            this.details = List.of(new GradingEntry(GradingEntry.INVALID_SCHEMA, task.getMaxPoints()));
            this.points = BigDecimal.ZERO;
        }
    }

    /**
     * Gets the task.
     *
     * @return The task.
     */
    public XQueryTask getTask() {
        return task;
    }

    /**
     * Gets the analysis.
     *
     * @return The analysis.
     */
    public Analysis getAnalysis() {
        return analysis;
    }

    /**
     * Gets the total points.
     *
     * @return The points.
     */
    public BigDecimal getPoints() {
        return points;
    }

    /**
     * Returns whether the solution is fully correct.
     *
     * @return {@code true} if the solution is correct; {@code false} otherwise.
     */
    public boolean isCorrect() {
        return isCorrect;
    }

    /**
     * Gets the details of the grading.
     *
     * @return The details.
     */
    public List<GradingEntry> getDetails() {
        return details;
    }

    /**
     * Gets the details of the grading for a specific category.
     *
     * @param category The category name.
     * @return The details, if present.
     */
    public Optional<GradingEntry> getDetails(String category) {
        return this.details.stream().filter(x -> x.errorCategory().equals(category)).findFirst();
    }

    /**
     * Calculates all minus points with regard to the configured penalties and grading strategies.
     *
     * @param analysis The analysis object containing information about the analyzed query and detected errors.
     * @param task     The task containing the information about the grading strategy and penalties.
     * @return The calculated minus points.
     */
    private static List<GradingEntry> calculateMinusPoints(Analysis analysis, XQueryTask task) {
        // TODO: check redundancies

        List<GradingEntry> entries = new ArrayList<>();

        calculateMinusPoints(GradingEntry.MISSING_NODE, task.getMaxPoints(), task.getMissingNodePenalty(), task.getMissingNodeStrategy(), analysis.getMissingNodes()).ifPresent(entries::add);
        calculateMinusPoints(GradingEntry.SUPERFLUOUS_NODE, task.getMaxPoints(), task.getSuperfluousNodePenalty(), task.getSuperfluousNodeStrategy(), analysis.getSuperfluousNodes()).ifPresent(entries::add);
        calculateMinusPoints(GradingEntry.DISPLACED_NODE, task.getMaxPoints(), task.getDisplacedNodePenalty(), task.getDisplacedNodeStrategy(), analysis.getDisplacedNodes()).ifPresent(entries::add);
        calculateMinusPoints(GradingEntry.INCORRECT_TEXT, task.getMaxPoints(), task.getIncorrectTextPenalty(), task.getIncorrectTextStrategy(), analysis.getIncorrectTextValues()).ifPresent(entries::add);
        calculateMinusPoints(GradingEntry.MISSING_ATTRIBUTE, task.getMaxPoints(), task.getMissingAttributePenalty(), task.getMissingAttributeStrategy(), analysis.getMissingAttributes()).ifPresent(entries::add);
        calculateMinusPoints(GradingEntry.SUPERFLUOUS_ATTRIBUTE, task.getMaxPoints(), task.getSuperfluousAttributePenalty(), task.getSuperfluousAttributeStrategy(), analysis.getSuperfluousAttributes()).ifPresent(entries::add);
        calculateMinusPoints(GradingEntry.INCORRECT_ATTRIBUTE_VALUE, task.getMaxPoints(), task.getIncorrectAttributeValuePenalty(), task.getIncorrectAttributeValueStrategy(), analysis.getIncorrectAttributeValues()).ifPresent(entries::add);

        return entries;
    }

    /**
     * Calculates the minus points for a specific category.
     *
     * @param category  The category.
     * @param maxPoints The maximum points.
     * @param penalty   The penalty for the category.
     * @param strategy  The grading strategy for the category.
     * @param data      The data to check.
     * @return The calculated grading entry. If data is empty, an empty optional is returned.
     */
    private static Optional<GradingEntry> calculateMinusPoints(String category, BigDecimal maxPoints, BigDecimal penalty, GradingStrategy strategy, List<?> data) {
        if (data.isEmpty())
            return Optional.empty();

        BigDecimal deduction = switch (strategy) {
            case KO -> maxPoints;
            case GROUP -> penalty;
            case EACH -> penalty.multiply(BigDecimal.valueOf(data.size()));
        };

        return Optional.of(new GradingEntry(category, deduction));
    }
}
