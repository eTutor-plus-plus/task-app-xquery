package at.jku.dke.task_app.xquery.data.entities;

import at.jku.dke.etutor.task_app.data.entities.BaseTaskInGroup;
import at.jku.dke.etutor.task_app.dto.TaskStatus;
import jakarta.persistence.Column;
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
}
