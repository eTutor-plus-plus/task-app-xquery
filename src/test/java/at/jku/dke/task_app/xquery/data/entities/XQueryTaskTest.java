package at.jku.dke.task_app.xquery.data.entities;

import at.jku.dke.etutor.task_app.dto.TaskStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class XQueryTaskTest {

    @Test
    void constructor1() {
        // Arrange
        final String solution = "//country/name";
        final List<String> sorting = List.of("//path");

        // Act
        var task = new XQueryTask(solution, sorting);
        String actualSolution = task.getSolution();
        List<String> actualSorting = task.getSorting();

        // Assert
        assertEquals(solution, actualSolution);
        assertEquals(sorting, actualSorting);
    }

    @Test
    void constructor2() {
        // Arrange
        final String solution = "//country/name";
        final List<String> sorting = List.of("//path");
        final BigDecimal maxPoints = BigDecimal.TEN;
        final TaskStatus status = TaskStatus.APPROVED;
        final XQueryTaskGroup taskGroup = new XQueryTaskGroup();
        taskGroup.setId(55L);

        // Act
        var task = new XQueryTask(maxPoints, status, taskGroup, solution, sorting);
        String actualSolution = task.getSolution();
        List<String> actualSorting = task.getSorting();
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
    void constructor3() {
        // Arrange
        final String solution = "//country/name";
        final List<String> sorting = List.of("//path");
        final BigDecimal maxPoints = BigDecimal.TEN;
        final TaskStatus status = TaskStatus.APPROVED;
        final XQueryTaskGroup taskGroup = new XQueryTaskGroup();
        taskGroup.setId(55L);
        final long id = 1L;

        // Act
        var task = new XQueryTask(id, maxPoints, status, taskGroup, solution, sorting);
        long actualId = task.getId();
        String actualSolution = task.getSolution();
        List<String> actualSorting = task.getSorting();
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
    void getSetSolution() {
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
    void getSetSorting() {
        // Arrange
        var task = new XQueryTask();
        final List<String> expected = List.of("//path");

        // Act
        task.setSorting(expected);
        final List<String> actual = task.getSorting();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void getSetMissingNodePenalty(){
        // Arrange
        var task = new XQueryTask();
        final BigDecimal expected = BigDecimal.TEN;

        // Act
        task.setMissingNodePenalty(expected);
        final BigDecimal actual = task.getMissingNodePenalty();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void getSetSuperfluousNodePenalty(){
        // Arrange
        var task = new XQueryTask();
        final BigDecimal expected = BigDecimal.TEN;

        // Act
        task.setSuperfluousNodePenalty(expected);
        final BigDecimal actual = task.getSuperfluousNodePenalty();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void getSetIncorrectTextPenalty(){
        // Arrange
        var task = new XQueryTask();
        final BigDecimal expected = BigDecimal.TEN;

        // Act
        task.setIncorrectTextPenalty(expected);
        final BigDecimal actual = task.getIncorrectTextPenalty();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void getSetDisplacedNodePenalty(){
        // Arrange
        var task = new XQueryTask();
        final BigDecimal expected = BigDecimal.TEN;

        // Act
        task.setDisplacedNodePenalty(expected);
        final BigDecimal actual = task.getDisplacedNodePenalty();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void getSetMissingAttributePenalty(){
        // Arrange
        var task = new XQueryTask();
        final BigDecimal expected = BigDecimal.TEN;

        // Act
        task.setMissingAttributePenalty(expected);
        final BigDecimal actual = task.getMissingAttributePenalty();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void getSetSuperfluousAttributePenalty(){
        // Arrange
        var task = new XQueryTask();
        final BigDecimal expected = BigDecimal.TEN;

        // Act
        task.setSuperfluousAttributePenalty(expected);
        final BigDecimal actual = task.getSuperfluousAttributePenalty();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void getSetIncorrectAttributeValuePenalty(){
        // Arrange
        var task = new XQueryTask();
        final BigDecimal expected = BigDecimal.TEN;

        // Act
        task.setIncorrectAttributeValuePenalty(expected);
        final BigDecimal actual = task.getIncorrectAttributeValuePenalty();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void getSetMissingNodeStrategy(){
        // Arrange
        var task = new XQueryTask();
        final GradingStrategy expected = GradingStrategy.KO;

        // Act
        task.setMissingNodeStrategy(expected);
        final GradingStrategy actual = task.getMissingNodeStrategy();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void getSetSuperfluousNodeStrategy(){
        // Arrange
        var task = new XQueryTask();
        final GradingStrategy expected = GradingStrategy.KO;

        // Act
        task.setSuperfluousNodeStrategy(expected);
        final GradingStrategy actual = task.getSuperfluousNodeStrategy();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void getSetIncorrectTextStrategy(){
        // Arrange
        var task = new XQueryTask();
        final GradingStrategy expected = GradingStrategy.KO;

        // Act
        task.setIncorrectTextStrategy(expected);
        final GradingStrategy actual = task.getIncorrectTextStrategy();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void getSetDisplacedNodeStrategy(){
        // Arrange
        var task = new XQueryTask();
        final GradingStrategy expected = GradingStrategy.KO;

        // Act
        task.setDisplacedNodeStrategy(expected);
        final GradingStrategy actual = task.getDisplacedNodeStrategy();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void getSetMissingAttributeStrategy(){
        // Arrange
        var task = new XQueryTask();
        final GradingStrategy expected = GradingStrategy.KO;

        // Act
        task.setMissingAttributeStrategy(expected);
        final GradingStrategy actual = task.getMissingAttributeStrategy();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void getSetSuperfluousAttributeStrategy(){
        // Arrange
        var task = new XQueryTask();
        final GradingStrategy expected = GradingStrategy.KO;

        // Act
        task.setSuperfluousAttributeStrategy(expected);
        final GradingStrategy actual = task.getSuperfluousAttributeStrategy();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void getSetIncorrectAttributeValueStrategy(){
        // Arrange
        var task = new XQueryTask();
        final GradingStrategy expected = GradingStrategy.KO;

        // Act
        task.setIncorrectAttributeValueStrategy(expected);
        final GradingStrategy actual = task.getIncorrectAttributeValueStrategy();

        // Assert
        assertEquals(expected, actual);
    }
}
