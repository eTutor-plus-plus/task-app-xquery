package at.jku.dke.task_app.xquery.services;

import at.jku.dke.etutor.task_app.dto.ModifyTaskGroupDto;
import at.jku.dke.etutor.task_app.dto.TaskStatus;
import at.jku.dke.task_app.xquery.data.entities.XQueryTaskGroup;
import at.jku.dke.task_app.xquery.dto.ModifyXQueryTaskGroupDto;
import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class XQueryTaskGroupServiceTest {

    //#region --- createTaskGroup ---
    @Test
    void createTaskGroup() {
        // Arrange
        var dto = new ModifyTaskGroupDto<>("xquery", TaskStatus.APPROVED, new ModifyXQueryTaskGroupDto("<root><a>1</a></root>", "<root><a>2</a></root>"));
        var service = new XQueryTaskGroupService(null, null);

        // Act
        var result = service.createTaskGroup(1, dto);

        // Assert
        assertEquals(dto.additionalData().diagnoseDocument(), result.getDiagnoseDocument());
        assertEquals(dto.additionalData().submitDocument(), result.getSubmitDocument());
    }

    @Test
    void createTaskGroup_invalidType() {
        // Arrange
        var dto = new ModifyTaskGroupDto<>("datalog", TaskStatus.APPROVED, new ModifyXQueryTaskGroupDto("<root><a>1</a></root>", "<root><a>2</a></root>"));
        var service = new XQueryTaskGroupService(null, null);

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> service.createTaskGroup(1, dto));
    }

    @Test
    void createTaskGroup_invalidDiagnoseSyntax() {
        // Arrange
        var dto = new ModifyTaskGroupDto<>("xquery", TaskStatus.APPROVED, new ModifyXQueryTaskGroupDto("<root><a>1</a><root>", "<root><a>2</a></root>"));
        var service = new XQueryTaskGroupService(null, null);

        // Act & Assert
        assertThrows(ValidationException.class, () -> service.createTaskGroup(1, dto));
    }

    @Test
    void createTaskGroup_invalidSubmitSyntax() {
        // Arrange
        var dto = new ModifyTaskGroupDto<>("xquery", TaskStatus.APPROVED, new ModifyXQueryTaskGroupDto("<root><a>1</a></root>", "<root></a></root>"));
        var service = new XQueryTaskGroupService(null, null);

        // Act & Assert
        assertThrows(ValidationException.class, () -> service.createTaskGroup(1, dto));
    }
    //#endregion

    //#region --- updateTaskGroup ---
    @Test
    void updateTaskGroup() {
        // Arrange
        var dto = new ModifyTaskGroupDto<>("xquery", TaskStatus.APPROVED, new ModifyXQueryTaskGroupDto("<root><a>1</a></root>", "<root><a>2</a></root>"));
        var service = new XQueryTaskGroupService(null, null);
        var taskGroup = new XQueryTaskGroup("<root><a>3</a></root>", "<root><a>4</a></root>");

        // Act
        service.updateTaskGroup(taskGroup, dto);

        // Assert
        assertEquals(dto.additionalData().diagnoseDocument(), taskGroup.getDiagnoseDocument());
        assertEquals(dto.additionalData().submitDocument(), taskGroup.getSubmitDocument());
    }

    @Test
    void updateTaskGroup_invalidType() {
        // Arrange
        var dto = new ModifyTaskGroupDto<>("datalog", TaskStatus.APPROVED, new ModifyXQueryTaskGroupDto("<root><a>1</a></root>", "<root><a>2</a></root>"));
        var service = new XQueryTaskGroupService(null, null);
        var taskGroup = new XQueryTaskGroup("diagnose", "submit");

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> service.updateTaskGroup(taskGroup, dto));
    }

    @Test
    void updateTaskGroup_invalidDiagnoseSyntax() {
        // Arrange
        var dto = new ModifyTaskGroupDto<>("xquery", TaskStatus.APPROVED, new ModifyXQueryTaskGroupDto("root><a>1</a></root>", "<root><a>2</a></root>"));
        var service = new XQueryTaskGroupService(null, null);
        var taskGroup = new XQueryTaskGroup("diagnose", "submit");

        // Act & Assert
        assertThrows(ValidationException.class, () -> service.updateTaskGroup(taskGroup, dto));
    }

    @Test
    void updateTaskGroup_invalidSubmitSyntax() {
        // Arrange
        var dto = new ModifyTaskGroupDto<>("xquery", TaskStatus.APPROVED, new ModifyXQueryTaskGroupDto("<root><a>1</a></root>", "<root><a>2/a></root>"));
        var service = new XQueryTaskGroupService(null, null);
        var taskGroup = new XQueryTaskGroup("diagnose", "submit");

        // Act & Assert
        assertThrows(ValidationException.class, () -> service.updateTaskGroup(taskGroup, dto));
    }
    //#endregion

    @Test
    void mapToReturnData() {
        // Arrange
        MessageSource ms = mock(MessageSource.class);
        var service = new XQueryTaskGroupService(null, ms);
        var taskGroup = new XQueryTaskGroup("<root><a>1</a></root>", "<root><a>2</a></root>");
        taskGroup.setId(55L);
        when(ms.getMessage(anyString(), any(), any(Locale.class))).thenAnswer(i -> i.getArgument(1, Object[].class)[1] + ":::" + i.getArgument(1, Object[].class)[0]);

        // Act
        var result = service.mapToReturnData(taskGroup, true);

        // Assert
        assertNotNull(result);
        assertThat(result.descriptionDe())
            .contains(":::")
            .contains("&lt;root&gt;&lt;a&gt;1&lt;/a&gt;&lt;/root&gt;");
        var id = Arrays.stream(result.descriptionDe().split(":::")).findFirst();
        assertEquals(55L, HashIds.decode(id.orElseThrow()));
    }

}
