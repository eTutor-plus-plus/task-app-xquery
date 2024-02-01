package at.jku.dke.task_app.xquery.data.entities;

import at.jku.dke.etutor.task_app.data.entities.BaseTaskInGroup;
import at.jku.dke.etutor.task_app.dto.TaskStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * Represents a XQuery task.
 */
@Entity
@Table(name = "task")
public class XQueryTask extends BaseTaskInGroup<XQueryTaskGroup> {
    @NotNull
    @Column(name = "solution", nullable = false)
    private String solution;

    @Column(name = "sorting")
    private String sorting;

    /**
     * Creates a new instance of class {@link XQueryTask}.
     */
    public XQueryTask() {
    }

    /**
     * Creates a new instance of class {@link XQueryTask}.
     *
     * @param solution The solution.
     * @param sorting  The sorting.
     */
    public XQueryTask(String solution, String sorting) {
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
     * @param sorting   The sorting.
     */
    public XQueryTask(BigDecimal maxPoints, TaskStatus status, XQueryTaskGroup taskGroup, String solution, String sorting) {
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
    public XQueryTask(Long id, BigDecimal maxPoints, TaskStatus status, XQueryTaskGroup taskGroup, String solution, String sorting) {
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
     * Gets the sorting.
     *
     * @return The sorting.
     */
    public String getSorting() {
        return sorting;
    }

    /**
     * Sets the sorting.
     *
     * @param sorting The sorting.
     */
    public void setSorting(String sorting) {
        this.sorting = sorting;
    }
}
