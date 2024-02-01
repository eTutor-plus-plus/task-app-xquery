package at.jku.dke.task_app.xquery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.io.Serializable;

/**
 * DTO for {@link at.jku.dke.task_app.xquery.data.entities.XQueryTaskGroup}
 *
 * @param diagnoseDocument The diagnose document.
 * @param submitDocument   The submit document.
 */
public record XQueryTaskGroupDto(@Schema(example = "<db><x>1</x></db>")  @NotNull String diagnoseDocument,
                                 @Schema(example = "<db><x>2</x></db>")  @NotNull String submitDocument) implements Serializable {
}
