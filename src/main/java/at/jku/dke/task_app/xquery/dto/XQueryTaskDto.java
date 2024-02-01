package at.jku.dke.task_app.xquery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * DTO for {@link at.jku.dke.task_app.xquery.data.entities.XQueryTask}
 *
 * @param solution The solution.
 * @param sorting  The sorting.
 */
public record XQueryTaskDto(@Schema(example = "return for $b in //bezirke return $b/name") @NotNull String solution,
                            @Schema(example = "//name") String sorting) implements Serializable {
}
