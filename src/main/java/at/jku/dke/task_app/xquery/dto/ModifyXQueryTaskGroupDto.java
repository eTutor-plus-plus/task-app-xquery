package at.jku.dke.task_app.xquery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;

import java.io.Serializable;

/**
 * This class represents a data transfer object for modifying a XQuery task group.
 *
 * @param diagnoseDocument The diagnose document.
 * @param submitDocument   The submit document.
 */
public record ModifyXQueryTaskGroupDto(@Schema(example = "<db><x>1</x></db>") @NotEmpty String diagnoseDocument,
                                       @Schema(example = "<db><x>2</x></db>") @NotEmpty String submitDocument) implements Serializable {
}
