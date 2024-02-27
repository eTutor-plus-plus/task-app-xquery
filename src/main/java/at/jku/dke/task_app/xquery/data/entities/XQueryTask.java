package at.jku.dke.task_app.xquery.data.entities;

import at.jku.dke.etutor.task_app.data.entities.BaseTaskInGroup;
import at.jku.dke.etutor.task_app.dto.TaskStatus;
import at.jku.dke.task_app.xquery.data.converters.GradingStrategyConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.List;

/**
 * Represents a XQuery task.
 */
@Entity
@Table(name = "task")
public class XQueryTask extends BaseTaskInGroup<XQueryTaskGroup> {
    @NotNull
    @Column(name = "solution", nullable = false)
    private String solution;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(name = "sorting")
    private List<String> sorting;

    @NotNull
    @Column(name = "missing_node_penalty", nullable = false, precision = 5, scale = 2)
    private BigDecimal missingNodePenalty;

    @NotNull
    @Column(name = "superfluous_node_penalty", nullable = false, precision = 5, scale = 2)
    private BigDecimal superfluousNodePenalty;

    @NotNull
    @Column(name = "incorrect_text_penalty", nullable = false, precision = 5, scale = 2)
    private BigDecimal incorrectTextPenalty;

    @NotNull
    @Column(name = "displaced_node_penalty", nullable = false, precision = 5, scale = 2)
    private BigDecimal displacedNodePenalty;

    @NotNull
    @Column(name = "missing_attribute_penalty", nullable = false, precision = 5, scale = 2)
    private BigDecimal missingAttributePenalty;

    @NotNull
    @Column(name = "superfluous_attribute_penalty", nullable = false, precision = 5, scale = 2)
    private BigDecimal superfluousAttributePenalty;

    @NotNull
    @Column(name = "incorrect_attribute_value_penalty", nullable = false, precision = 5, scale = 2)
    private BigDecimal incorrectAttributeValuePenalty;

    @Convert(converter = GradingStrategyConverter.class)
    @Column(name = "missing_node_strategy", columnDefinition = "grading_strategy not null")
    private GradingStrategy missingNodeStrategy;

    @Convert(converter = GradingStrategyConverter.class)
    @Column(name = "superfluous_node_strategy", columnDefinition = "grading_strategy not null")
    private GradingStrategy superfluousNodeStrategy;

    @Convert(converter = GradingStrategyConverter.class)
    @Column(name = "incorrect_text_strategy", columnDefinition = "grading_strategy not null")
    private GradingStrategy incorrectTextStrategy;

    @Convert(converter = GradingStrategyConverter.class)
    @Column(name = "displaced_node_strategy", columnDefinition = "grading_strategy not null")
    private GradingStrategy displacedNodeStrategy;

    @Convert(converter = GradingStrategyConverter.class)
    @Column(name = "missing_attribute_strategy", columnDefinition = "grading_strategy not null")
    private GradingStrategy missingAttributeStrategy;

    @Convert(converter = GradingStrategyConverter.class)
    @Column(name = "superfluous_attribute_strategy", columnDefinition = "grading_strategy not null")
    private GradingStrategy superfluousAttributeStrategy;

    @Convert(converter = GradingStrategyConverter.class)
    @Column(name = "incorrect_attribute_value_strategy", columnDefinition = "grading_strategy not null")
    private GradingStrategy incorrectAttributeValueStrategy;

    /**
     * Creates a new instance of class {@link XQueryTask}.
     */
    public XQueryTask() {
    }

    /**
     * Creates a new instance of class {@link XQueryTask}.
     *
     * @param solution The solution.
     * @param sorting  The sorted nodes.
     */
    public XQueryTask(String solution, List<String> sorting) {
        this.solution = solution;
        this.sorting = sorting;
    }

    /**
     * Creates a new instance of class {@link XQueryTask}.
     *
     * @param maxPoints The maximum points.
     * @param status    The status.
     * @param taskGroup The task group.
     * @param solution  The solution.
     * @param sorting   The sorted nodes.
     */
    public XQueryTask(BigDecimal maxPoints, TaskStatus status, XQueryTaskGroup taskGroup, String solution, List<String> sorting) {
        super(maxPoints, status, taskGroup);
        this.solution = solution;
        this.sorting = sorting;
    }

    /**
     * Creates a new instance of class {@link XQueryTask}.
     *
     * @param id        The identifier.
     * @param maxPoints The maximum points.
     * @param status    The status.
     * @param taskGroup The task group.
     * @param solution  The solution.
     * @param sorting   The sorting.
     */
    public XQueryTask(Long id, BigDecimal maxPoints, TaskStatus status, XQueryTaskGroup taskGroup, String solution, List<String> sorting) {
        super(id, maxPoints, status, taskGroup);
        this.solution = solution;
        this.sorting = sorting;
    }

    /**
     * Gets the solution.
     *
     * @return The solution.
     */
    public String getSolution() {
        return solution;
    }

    /**
     * Sets the solution.
     *
     * @param solution The solution.
     */
    public void setSolution(String solution) {
        this.solution = solution;
    }

    /**
     * Gets the XPath expressions which denotes XML nodes, which have to be in
     * certain order in the submitted query.
     *
     * @return The sorting.
     */
    public List<String> getSorting() {
        return sorting;
    }

    /**
     * Sets the XPath expressions which denotes XML nodes, which have to be in
     * certain order in the submitted query.
     *
     * @param sorting The sorting.
     */
    public void setSorting(List<String> sorting) {
        this.sorting = sorting;
    }

    /**
     * Gets the missing node penalty.
     *
     * @return The missing node penalty.
     */
    public BigDecimal getMissingNodePenalty() {
        return missingNodePenalty;
    }

    /**
     * Sets the missing node penalty.
     *
     * @param missingNodePenalty The missing node penalty.
     */
    public void setMissingNodePenalty(BigDecimal missingNodePenalty) {
        this.missingNodePenalty = missingNodePenalty;
    }

    /**
     * Gets the superfluous node penalty.
     *
     * @return The superfluous node penalty.
     */
    public BigDecimal getSuperfluousNodePenalty() {
        return superfluousNodePenalty;
    }

    /**
     * Sets the superfluous node penalty.
     *
     * @param superfluousNodePenalty The superfluous node penalty.
     */
    public void setSuperfluousNodePenalty(BigDecimal superfluousNodePenalty) {
        this.superfluousNodePenalty = superfluousNodePenalty;
    }

    /**
     * Gets the incorrect text penalty.
     *
     * @return The incorrect text penalty.
     */
    public BigDecimal getIncorrectTextPenalty() {
        return incorrectTextPenalty;
    }

    /**
     * Sets the incorrect text penalty.
     *
     * @param incorrectTextPenalty The incorrect text penalty.
     */
    public void setIncorrectTextPenalty(BigDecimal incorrectTextPenalty) {
        this.incorrectTextPenalty = incorrectTextPenalty;
    }

    /**
     * Gets the displaced node penalty.
     *
     * @return The displaced node penalty.
     */
    public BigDecimal getDisplacedNodePenalty() {
        return displacedNodePenalty;
    }

    /**
     * Sets the displaced node penalty.
     *
     * @param displacedNodePenalty The displaced node penalty.
     */
    public void setDisplacedNodePenalty(BigDecimal displacedNodePenalty) {
        this.displacedNodePenalty = displacedNodePenalty;
    }

    /**
     * Gets the missing attribute penalty.
     *
     * @return The missing attribute penalty.
     */
    public BigDecimal getMissingAttributePenalty() {
        return missingAttributePenalty;
    }

    /**
     * Sets the missing attribute penalty.
     *
     * @param missingAttributePenalty The missing attribute penalty.
     */
    public void setMissingAttributePenalty(BigDecimal missingAttributePenalty) {
        this.missingAttributePenalty = missingAttributePenalty;
    }

    /**
     * Gets the superfluous attribute penalty.
     *
     * @return The superfluous attribute penalty.
     */
    public BigDecimal getSuperfluousAttributePenalty() {
        return superfluousAttributePenalty;
    }

    /**
     * Sets the superfluous attribute penalty.
     *
     * @param superfluousAttributePenalty The superfluous attribute penalty.
     */
    public void setSuperfluousAttributePenalty(BigDecimal superfluousAttributePenalty) {
        this.superfluousAttributePenalty = superfluousAttributePenalty;
    }

    /**
     * Gets the incorrect attribute value penalty.
     *
     * @return The incorrect attribute value penalty.
     */
    public BigDecimal getIncorrectAttributeValuePenalty() {
        return incorrectAttributeValuePenalty;
    }

    /**
     * Sets the incorrect attribute value penalty.
     *
     * @param incorrectAttributeValuePenalty The incorrect attribute value penalty.
     */
    public void setIncorrectAttributeValuePenalty(BigDecimal incorrectAttributeValuePenalty) {
        this.incorrectAttributeValuePenalty = incorrectAttributeValuePenalty;
    }

    /**
     * Gets the missing node grading strategy.
     *
     * @return The missing node strategy.
     */
    public GradingStrategy getMissingNodeStrategy() {
        return missingNodeStrategy;
    }

    /**
     * Sets the missing node grading strategy.
     *
     * @param missingNodeStrategy The missing node strategy.
     */
    public void setMissingNodeStrategy(GradingStrategy missingNodeStrategy) {
        this.missingNodeStrategy = missingNodeStrategy;
    }

    /**
     * Gets the superfluous node grading strategy.
     *
     * @return The superfluous node strategy.
     */
    public GradingStrategy getSuperfluousNodeStrategy() {
        return superfluousNodeStrategy;
    }

    /**
     * Sets the superfluous node grading strategy.
     *
     * @param superfluousNodeStrategy The superfluous node strategy.
     */
    public void setSuperfluousNodeStrategy(GradingStrategy superfluousNodeStrategy) {
        this.superfluousNodeStrategy = superfluousNodeStrategy;
    }

    /**
     * Gets the incorrect text grading strategy.
     *
     * @return The incorrect text strategy.
     */
    public GradingStrategy getIncorrectTextStrategy() {
        return incorrectTextStrategy;
    }

    /**
     * Sets the incorrect text grading strategy.
     *
     * @param incorrectTextStrategy The incorrect text strategy.
     */
    public void setIncorrectTextStrategy(GradingStrategy incorrectTextStrategy) {
        this.incorrectTextStrategy = incorrectTextStrategy;
    }

    /**
     * Gets the displaced node grading strategy.
     *
     * @return The displaced node strategy.
     */
    public GradingStrategy getDisplacedNodeStrategy() {
        return displacedNodeStrategy;
    }

    /**
     * Sets the displaced node grading strategy.
     *
     * @param displacedNodeStrategy The displaced node strategy.
     */
    public void setDisplacedNodeStrategy(GradingStrategy displacedNodeStrategy) {
        this.displacedNodeStrategy = displacedNodeStrategy;
    }

    /**
     * Gets the missing attribute grading strategy.
     *
     * @return The missing attribute strategy.
     */
    public GradingStrategy getMissingAttributeStrategy() {
        return missingAttributeStrategy;
    }

    /**
     * Sets the missing attribute grading strategy.
     *
     * @param missingAttributeStrategy The missing attribute strategy.
     */
    public void setMissingAttributeStrategy(GradingStrategy missingAttributeStrategy) {
        this.missingAttributeStrategy = missingAttributeStrategy;
    }

    /**
     * Gets the superfluous attribute grading strategy.
     *
     * @return The superfluous attribute strategy.
     */
    public GradingStrategy getSuperfluousAttributeStrategy() {
        return superfluousAttributeStrategy;
    }

    /**
     * Sets the superfluous attribute grading strategy.
     *
     * @param superfluousAttributeStrategy The superfluous attribute strategy.
     */
    public void setSuperfluousAttributeStrategy(GradingStrategy superfluousAttributeStrategy) {
        this.superfluousAttributeStrategy = superfluousAttributeStrategy;
    }

    /**
     * Gets the incorrect attribute value grading strategy.
     *
     * @return The incorrect attribute value strategy.
     */
    public GradingStrategy getIncorrectAttributeValueStrategy() {
        return incorrectAttributeValueStrategy;
    }

    /**
     * Sets the incorrect attribute value grading strategy.
     *
     * @param incorrectAttributeValueStrategy The incorrect attribute value strategy.
     */
    public void setIncorrectAttributeValueStrategy(GradingStrategy incorrectAttributeValueStrategy) {
        this.incorrectAttributeValueStrategy = incorrectAttributeValueStrategy;
    }
}
