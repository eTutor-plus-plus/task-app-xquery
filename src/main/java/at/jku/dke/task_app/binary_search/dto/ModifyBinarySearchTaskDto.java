package at.jku.dke.task_app.xquery.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * This class represents a data transfer object for modifying a XQuery task.
 *
 * @param solution The solution.
 */
public record ModifyXQueryTaskDto(@NotNull Integer solution) implements Serializable {
}
