package at.jku.dke.task_app.xquery.evaluation.grading;

import at.jku.dke.task_app.xquery.data.entities.GradingStrategy;
import at.jku.dke.task_app.xquery.data.entities.XQueryTask;
import at.jku.dke.task_app.xquery.evaluation.analysis.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class XQueryGradingTest {

    @Test
    void getTask() throws AnalysisException {
        // Arrange
        XQueryTask task = new XQueryTask();
        task.setMaxPoints(BigDecimal.TEN);
        Analysis analysis = new AnalysisImpl(new XQResult(""), new XQResult(""), null);
        XQueryGrading grading = new XQueryGrading(task, analysis);

        // Act
        XQueryTask result = grading.getTask();

        // Assert
        assertEquals(task, result);
    }

    @Test
    void getAnalysis() throws AnalysisException {
        // Arrange
        XQueryTask task = new XQueryTask();
        task.setMaxPoints(BigDecimal.TEN);
        Analysis analysis = new AnalysisImpl(new XQResult(""), new XQResult(""), null);
        XQueryGrading grading = new XQueryGrading(task, analysis);

        // Act
        Analysis result = grading.getAnalysis();

        // Assert
        assertEquals(analysis, result);
    }

    @Test
    void isCorrect_false() {
        // Arrange
        XQueryTask task = new XQueryTask();
        task.setMaxPoints(BigDecimal.TEN);
        Analysis analysis = mock(AnalysisImpl.class);
        when(analysis.isCorrect()).thenReturn(false);
        XQueryGrading grading = new XQueryGrading(task, analysis);

        // Act
        boolean result = grading.isCorrect();

        // Assert
        assertFalse(result);
    }

    @Test
    void isCorrect_true() {
        // Arrange
        XQueryTask task = new XQueryTask();
        task.setMaxPoints(BigDecimal.TEN);
        Analysis analysis = mock(AnalysisImpl.class);
        when(analysis.isCorrect()).thenReturn(true);
        XQueryGrading grading = new XQueryGrading(task, analysis);

        // Act
        boolean result = grading.isCorrect();

        // Assert
        assertTrue(result);
    }

    @Test
    void getPoints_valid() {
        // Arrange
        XQueryTask task = new XQueryTask();
        task.setMaxPoints(BigDecimal.TEN);
        task.setMissingNodePenalty(BigDecimal.ZERO);
        task.setMissingNodeStrategy(GradingStrategy.KO);

        Analysis analysis = mock(AnalysisImpl.class);
        when(analysis.isCorrect()).thenReturn(true);

        XQueryGrading grading = new XQueryGrading(task, analysis);

        // Act
        BigDecimal points = grading.getPoints();
        List<GradingEntry> details = grading.getDetails();

        // Assert
        assertEquals(task.getMaxPoints(), points);
        assertThat(details).isEmpty();
    }

    @Test
    void getPoints_invalid() {
        // Arrange
        XQueryTask task = new XQueryTask();
        task.setMaxPoints(BigDecimal.TEN);
        task.setMissingNodePenalty(BigDecimal.ZERO);
        task.setMissingNodeStrategy(GradingStrategy.KO);

        Analysis analysis = mock(AnalysisImpl.class);
        when(analysis.isCorrect()).thenReturn(false);
        when(analysis.isSchemaValid()).thenReturn(false);

        XQueryGrading grading = new XQueryGrading(task, analysis);

        // Act
        BigDecimal points = grading.getPoints();
        List<GradingEntry> details = grading.getDetails();

        // Assert
        assertEquals(BigDecimal.ZERO, points);
        assertThat(details)
            .containsExactly(new GradingEntry(GradingEntry.INVALID_SCHEMA, task.getMaxPoints()));
    }

    @Test
    void getPointsAndDetails_missingNode() {
        // Arrange
        XQueryTask task = new XQueryTask();
        task.setMaxPoints(BigDecimal.TEN);
        task.setMissingNodePenalty(BigDecimal.ZERO);
        task.setMissingNodeStrategy(GradingStrategy.KO);

        Analysis analysis = mock(AnalysisImpl.class);
        when(analysis.isCorrect()).thenReturn(false);
        when(analysis.isSchemaValid()).thenReturn(true);
        when(analysis.getMissingNodes()).thenReturn(List.of(new NodeModel("missingNode", "missingNode")));

        XQueryGrading grading = new XQueryGrading(task, analysis);

        // Act
        BigDecimal points = grading.getPoints();
        List<GradingEntry> details = grading.getDetails();

        // Assert
        assertEquals(BigDecimal.ZERO, points);
        assertThat(details)
            .containsExactly(new GradingEntry(GradingEntry.MISSING_NODE, task.getMaxPoints()));
    }

    @Test
    void getPointsAndDetails_superfluousNode() {
        // Arrange
        XQueryTask task = new XQueryTask();
        task.setMaxPoints(BigDecimal.TEN);
        task.setSuperfluousNodePenalty(BigDecimal.TWO);
        task.setSuperfluousNodeStrategy(GradingStrategy.GROUP);

        Analysis analysis = mock(AnalysisImpl.class);
        when(analysis.isCorrect()).thenReturn(false);
        when(analysis.isSchemaValid()).thenReturn(true);
        when(analysis.getSuperfluousNodes()).thenReturn(List.of(new NodeModel("missingNode", "missingNode"), new NodeModel("missingNode2", "missingNode")));

        XQueryGrading grading = new XQueryGrading(task, analysis);

        // Act
        BigDecimal points = grading.getPoints();
        List<GradingEntry> details = grading.getDetails();

        // Assert
        assertEquals(BigDecimal.valueOf(8), points);
        assertThat(details)
            .containsExactly(new GradingEntry(GradingEntry.SUPERFLUOUS_NODE, task.getSuperfluousNodePenalty()));
    }

    @Test
    void getPointsAndDetails_displacedNode() {
        // Arrange
        XQueryTask task = new XQueryTask();
        task.setMaxPoints(BigDecimal.TEN);
        task.setDisplacedNodePenalty(BigDecimal.TWO);
        task.setDisplacedNodeStrategy(GradingStrategy.EACH);

        Analysis analysis = mock(AnalysisImpl.class);
        when(analysis.isCorrect()).thenReturn(false);
        when(analysis.isSchemaValid()).thenReturn(true);
        when(analysis.getDisplacedNodes()).thenReturn(List.of(new NodeModel("missingNode", "missingNode"), new NodeModel("missingNode2", "missingNode")));

        XQueryGrading grading = new XQueryGrading(task, analysis);

        // Act
        BigDecimal points = grading.getPoints();
        List<GradingEntry> details = grading.getDetails();

        // Assert
        assertEquals(BigDecimal.valueOf(6), points);
        assertThat(details)
            .containsExactly(new GradingEntry(GradingEntry.DISPLACED_NODE, task.getDisplacedNodePenalty().multiply(BigDecimal.valueOf(2))));
    }

    @Test
    void getPointsAndDetails_incorrectText() {
        // Arrange
        XQueryTask task = new XQueryTask();
        task.setMaxPoints(BigDecimal.TEN);
        task.setIncorrectTextPenalty(BigDecimal.TEN);
        task.setIncorrectTextStrategy(GradingStrategy.EACH);

        Analysis analysis = mock(AnalysisImpl.class);
        when(analysis.isCorrect()).thenReturn(false);
        when(analysis.isSchemaValid()).thenReturn(true);
        when(analysis.getIncorrectTextValues()).thenReturn(List.of(new IncorrectTextValueModel("missingNode", "missingNode"), new IncorrectTextValueModel("missingNode2", "missingNode")));

        XQueryGrading grading = new XQueryGrading(task, analysis);

        // Act
        BigDecimal points = grading.getPoints();
        List<GradingEntry> details = grading.getDetails();

        // Assert
        assertEquals(BigDecimal.ZERO, points);
        assertThat(details)
            .containsExactly(new GradingEntry(GradingEntry.INCORRECT_TEXT, task.getIncorrectTextPenalty().multiply(BigDecimal.valueOf(2))));
    }

    @Test
    void getPointsAndDetails_missingAttribute() {
        // Arrange
        XQueryTask task = new XQueryTask();
        task.setMaxPoints(BigDecimal.TEN);
        task.setMissingAttributePenalty(BigDecimal.ZERO);
        task.setMissingAttributeStrategy(GradingStrategy.KO);

        Analysis analysis = mock(AnalysisImpl.class);
        when(analysis.isCorrect()).thenReturn(false);
        when(analysis.isSchemaValid()).thenReturn(true);
        when(analysis.getMissingAttributes()).thenReturn(List.of(new AttributeModel("missingAttribute", "missingAttribute", "a")));

        XQueryGrading grading = new XQueryGrading(task, analysis);

        // Act
        BigDecimal points = grading.getPoints();
        List<GradingEntry> details = grading.getDetails();

        // Assert
        assertEquals(BigDecimal.ZERO, points);
        assertThat(details)
            .containsExactly(new GradingEntry(GradingEntry.MISSING_ATTRIBUTE, task.getMaxPoints()));
    }

    @Test
    void getPointsAndDetails_superfluousAttribute() {
        // Arrange
        XQueryTask task = new XQueryTask();
        task.setMaxPoints(BigDecimal.TEN);
        task.setSuperfluousAttributePenalty(BigDecimal.TWO);
        task.setSuperfluousAttributeStrategy(GradingStrategy.GROUP);

        Analysis analysis = mock(AnalysisImpl.class);
        when(analysis.isCorrect()).thenReturn(false);
        when(analysis.isSchemaValid()).thenReturn(true);
        when(analysis.getSuperfluousAttributes()).thenReturn(List.of(new AttributeModel("missingAttribute", "missingAttribute", "a"), new AttributeModel("missingAttribute2", "missingAttribute", "a")));

        XQueryGrading grading = new XQueryGrading(task, analysis);

        // Act
        BigDecimal points = grading.getPoints();
        List<GradingEntry> details = grading.getDetails();

        // Assert
        assertEquals(BigDecimal.valueOf(8), points);
        assertThat(details)
            .containsExactly(new GradingEntry(GradingEntry.SUPERFLUOUS_ATTRIBUTE, task.getSuperfluousAttributePenalty()));
    }

    @Test
    void getPointsAndDetails_incorrectAttributeValue() {
        // Arrange
        XQueryTask task = new XQueryTask();
        task.setMaxPoints(BigDecimal.TEN);
        task.setIncorrectAttributeValuePenalty(BigDecimal.TEN.multiply(BigDecimal.valueOf(2)));
        task.setIncorrectAttributeValueStrategy(GradingStrategy.EACH);

        Analysis analysis = mock(AnalysisImpl.class);
        when(analysis.isCorrect()).thenReturn(false);
        when(analysis.isSchemaValid()).thenReturn(true);
        when(analysis.getIncorrectAttributeValues()).thenReturn(List.of(new IncorrectAttributeValueModel("missingAttribute", "missingAttribute", "a")));

        XQueryGrading grading = new XQueryGrading(task, analysis);

        // Act
        BigDecimal points = grading.getPoints();
        List<GradingEntry> details = grading.getDetails();

        // Assert
        assertEquals(BigDecimal.ZERO, points);
        assertThat(details)
            .containsExactly(new GradingEntry(GradingEntry.INCORRECT_ATTRIBUTE_VALUE, task.getIncorrectAttributeValuePenalty()));
    }
}
