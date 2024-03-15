package at.jku.dke.task_app.xquery.services;

import at.jku.dke.etutor.task_app.dto.GradingDto;
import at.jku.dke.etutor.task_app.dto.ModifyTaskDto;
import at.jku.dke.etutor.task_app.dto.TaskStatus;
import at.jku.dke.task_app.xquery.data.entities.XQueryTask;
import at.jku.dke.task_app.xquery.dto.ModifyXQueryTaskDto;
import at.jku.dke.task_app.xquery.evaluation.EvaluationServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class XQueryTaskServiceTest {

    //#region --- createTask ---
    @Test
    void createTask() {
        // Arrange
        var evalService = mock(EvaluationServiceImpl.class);
        var dto = new ModifyTaskDto<>(3L, BigDecimal.TEN, "xquery", TaskStatus.APPROVED, new ModifyXQueryTaskDto("/person", "//person\n//address/*"));
        var service = new XQueryTaskService(null, null, evalService);
        when(evalService.evaluate(any())).thenReturn(new GradingDto(BigDecimal.TEN, BigDecimal.TEN, "", List.of()));

        // Act
        var result = service.createTask(3, dto);

        // Assert
        assertEquals(dto.additionalData().solution(), result.getSolution());
        assertEquals(List.of("//person", "//address/*"), result.getSorting());
    }

    @Test
    void afterCreate_invalidSyntax() {
        // Arrange
        var evalService = mock(EvaluationServiceImpl.class);
        var service = new XQueryTaskService(null, null, evalService);
        when(evalService.evaluate(any())).thenReturn(new GradingDto(BigDecimal.ZERO, BigDecimal.TEN, "invalid syntax", List.of()));

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> service.afterCreate(new XQueryTask()));
    }

    @Test
    void createTask_invalidType() {
        // Arrange
        var dto = new ModifyTaskDto<>(3L, BigDecimal.TEN, "datalog", TaskStatus.APPROVED, new ModifyXQueryTaskDto("/person", "//person\n//address/*"));
        var service = new XQueryTaskService(null, null, null);

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> service.createTask(3, dto));
    }
    //#endregion

    //#region --- updateTask ---
    @Test
    void updateTask() {
        // Arrange
        var evalService = mock(EvaluationServiceImpl.class);
        var dto = new ModifyTaskDto<>(3L, BigDecimal.TEN, "xquery", TaskStatus.APPROVED, new ModifyXQueryTaskDto("/people", "//person\n//address/*"));
        var service = new XQueryTaskService(null, null, evalService);
        var task = new XQueryTask("/person", null);
        when(evalService.evaluate(any())).thenReturn(new GradingDto(BigDecimal.TEN, BigDecimal.TEN, "", List.of()));

        // Act
        service.updateTask(task, dto);

        // Assert
        assertEquals(dto.additionalData().solution(), task.getSolution());
        assertEquals(List.of("//person", "//address/*"), task.getSorting());
    }

    @Test
    void afterUpdate_invalidSyntax() {
        // Arrange
        var evalService = mock(EvaluationServiceImpl.class);
        var service = new XQueryTaskService(null, null, evalService);
        var task = new XQueryTask("/person", null);
        when(evalService.evaluate(any())).thenReturn(new GradingDto(BigDecimal.ZERO, BigDecimal.TEN, "", List.of()));

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> service.afterUpdate(task));
    }

    @Test
    void updateTask_invalidType() {
        // Arrange
        var dto = new ModifyTaskDto<>(3L, BigDecimal.TEN, "datalog", TaskStatus.APPROVED, new ModifyXQueryTaskDto("/people", "//person\n//address/*"));
        var service = new XQueryTaskService(null, null, null);
        var task = new XQueryTask("/person", null);

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> service.updateTask(task, dto));
    }
    //#endregion

    @Test
    void mapToReturnData() {
        // Arrange
        var task = new XQueryTask("//person", List.of("//person", "//address/*"));
        var service = new XQueryTaskService(null, null, null);

        // Act
        var result = service.mapToReturnData(task, true);

        // Assert
        assertNotNull(result);
    }

}
