package at.jku.dke.task_app.xquery.services;

import at.jku.dke.etutor.task_app.dto.ModifyTaskGroupDto;
import at.jku.dke.etutor.task_app.dto.TaskStatus;
import at.jku.dke.task_app.xquery.data.entities.XQueryTaskGroup;
import at.jku.dke.task_app.xquery.dto.ModifyXQueryTaskGroupDto;
import org.junit.jupiter.api.Test;
import org.springframework.context.MessageSource;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class XQueryTaskGroupServiceTest {

//    @Test
//    void createTaskGroup() {
//        // Arrange
//        ModifyTaskGroupDto<ModifyXQueryTaskGroupDto> dto = new ModifyTaskGroupDto<>("xquery", TaskStatus.APPROVED, new ModifyXQueryTaskGroupDto(1, 2));
//        XQueryTaskGroupService service = new XQueryTaskGroupService(null, null);
//
//        // Act
//        var taskGroup = service.createTaskGroup(3, dto);
//
//        // Assert
//        assertEquals(dto.additionalData().minNumber(), taskGroup.getMinNumber());
//        assertEquals(dto.additionalData().maxNumber(), taskGroup.getMaxNumber());
//    }
//
//    @Test
//    void createTaskGroupInvalidType() {
//        // Arrange
//        ModifyTaskGroupDto<ModifyXQueryTaskGroupDto> dto = new ModifyTaskGroupDto<>("sql", TaskStatus.APPROVED, new ModifyXQueryTaskGroupDto(1, 2));
//        XQueryTaskGroupService service = new XQueryTaskGroupService(null, null);
//
//        // Act & Assert
//        assertThrows(ResponseStatusException.class, () -> service.createTaskGroup(3, dto));
//    }
//
//    @Test
//    void updateTaskGroup() {
//        // Arrange
//        ModifyTaskGroupDto<ModifyXQueryTaskGroupDto> dto = new ModifyTaskGroupDto<>("xquery", TaskStatus.APPROVED, new ModifyXQueryTaskGroupDto(1, 2));
//        XQueryTaskGroupService service = new XQueryTaskGroupService(null, null);
//        var taskGroup = new XQueryTaskGroup(3, 4);
//
//        // Act
//        service.updateTaskGroup(taskGroup, dto);
//
//        // Assert
//        assertEquals(dto.additionalData().minNumber(), taskGroup.getMinNumber());
//        assertEquals(dto.additionalData().maxNumber(), taskGroup.getMaxNumber());
//    }
//
//    @Test
//    void updateTaskGroupInvalidType() {
//        // Arrange
//        ModifyTaskGroupDto<ModifyXQueryTaskGroupDto> dto = new ModifyTaskGroupDto<>("sql", TaskStatus.APPROVED, new ModifyXQueryTaskGroupDto(1, 2));
//        XQueryTaskGroupService service = new XQueryTaskGroupService(null, null);
//        var taskGroup = new XQueryTaskGroup(3, 4);
//
//        // Act & Assert
//        assertThrows(ResponseStatusException.class, () -> service.updateTaskGroup(taskGroup, dto));
//    }
//
//    @Test
//    void mapToReturnData() {
//        // Arrange
//        MessageSource ms = mock(MessageSource.class);
//        XQueryTaskGroupService service = new XQueryTaskGroupService(null, ms);
//        var taskGroup = new XQueryTaskGroup(3, 4);
//
//        // Act
//        var result = service.mapToReturnData(taskGroup, true);
//
//        // Assert
//        assertNotNull(result);
//        verify(ms).getMessage("defaultTaskGroupDescription", new Object[]{taskGroup.getMinNumber(), taskGroup.getMaxNumber()}, Locale.GERMAN);
//        verify(ms).getMessage("defaultTaskGroupDescription", new Object[]{taskGroup.getMinNumber(), taskGroup.getMaxNumber()}, Locale.ENGLISH);
//    }

}
