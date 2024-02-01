package at.jku.dke.task_app.xquery.data.entities;

import at.jku.dke.etutor.task_app.dto.TaskStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XQueryTaskTest {

    @Test
    void testConstructor1() {
        // Arrange
        final String solution = "//country/name";
        final String sorting = "//path";

        // Act
        var task = new XQueryTask(solution, sorting);
        String actualSolution = task.getSolution();
        String actualSorting = task.getSorting();

        // Assert
        assertEquals(solution, actualSolution);
        assertEquals(sorting, actualSorting);
    }

    @Test
    void testConstructor2() {
        // Arrange
        final String solution = "//country/name";
        final String sorting = "//path";
        final BigDecimal maxPoints = BigDecimal.TEN;
        final TaskStatus status = TaskStatus.APPROVED;
        final XQueryTaskGroup taskGroup = new XQueryTaskGroup();
        taskGroup.setId(55L);

        // Act
        var task = new XQueryTask(maxPoints, status, taskGroup, solution, sorting);
        String actualSolution = task.getSolution();
        String actualSorting = task.getSorting();
        BigDecimal actualMaxPoints = task.getMaxPoints();
        TaskStatus actualStatus = task.getStatus();
        XQueryTaskGroup actualTaskGroup = task.getTaskGroup();

        // Assert
        assertEquals(solution, actualSolution);
        assertEquals(sorting, actualSorting);
        assertEquals(maxPoints, actualMaxPoints);
        assertEquals(status, actualStatus);
        assertEquals(taskGroup, actualTaskGroup);
    }

    @Test
    void testConstructor3() {
        // Arrange
        final String solution = "//country/name";
        final String sorting = "//path";
        final BigDecimal maxPoints = BigDecimal.TEN;
        final TaskStatus status = TaskStatus.APPROVED;
        final XQueryTaskGroup taskGroup = new XQueryTaskGroup();
        taskGroup.setId(55L);
        final long id = 1L;

        // Act
        var task = new XQueryTask(id, maxPoints, status, taskGroup, solution, sorting);
        long actualId = task.getId();
        String actualSolution = task.getSolution();
        String actualSorting = task.getSorting();
        BigDecimal actualMaxPoints = task.getMaxPoints();
        TaskStatus actualStatus = task.getStatus();
        XQueryTaskGroup actualTaskGroup = task.getTaskGroup();

        // Assert
        assertEquals(id, actualId);
        assertEquals(solution, actualSolution);
        assertEquals(sorting, actualSorting);
        assertEquals(maxPoints, actualMaxPoints);
        assertEquals(status, actualStatus);
        assertEquals(taskGroup, actualTaskGroup);
    }

    @Test
    void testGetSetSolution() {
        // Arrange
        var task = new XQueryTask();
        final String expected = "//country/name";

        // Act
        task.setSolution(expected);
        final String actual = task.getSolution();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void testGetSetSorting() {
        // Arrange
        var task = new XQueryTask();
        final String expected = "//path";

        // Act
        task.setSorting(expected);
        final String actual = task.getSorting();

        // Assert
        assertEquals(expected, actual);
    }

}
