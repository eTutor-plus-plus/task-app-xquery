package at.jku.dke.task_app.xquery.dto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * DTO for {@link at.jku.dke.task_app.xquery.data.entities.XQueryTask}
 *
 * @param solution The solution.
 */
public record XQueryTaskDto(@NotNull Integer solution) implements Serializable {
}
