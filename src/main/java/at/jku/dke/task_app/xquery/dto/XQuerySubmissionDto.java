package at.jku.dke.task_app.xquery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

/**
 * This class represents a data transfer object for submitting a solution.
 *
 * @param input The user input.
 */
public record XQuerySubmissionDto(@Schema(example = "return for $b in //bezirke return $b/name") @NotNull String input) {
}
