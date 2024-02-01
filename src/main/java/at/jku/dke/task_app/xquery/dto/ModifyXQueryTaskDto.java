package at.jku.dke.task_app.xquery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;

/**
 * This class represents a data transfer object for modifying a XQuery task.
 *
 * @param solution The solution.
 * @param sorting  The sorting.
 */
public record ModifyXQueryTaskDto(@Schema(example = "return for $b in //bezirke return $b/name") @NotEmpty String solution,
                                  @Schema(example = "//name") String sorting) implements Serializable {
}
