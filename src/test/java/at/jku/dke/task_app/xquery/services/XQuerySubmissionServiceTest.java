package at.jku.dke.task_app.xquery.services;

import at.jku.dke.etutor.task_app.dto.SubmissionMode;
import at.jku.dke.etutor.task_app.dto.SubmitSubmissionDto;
import at.jku.dke.task_app.xquery.data.entities.XQuerySubmission;
import at.jku.dke.task_app.xquery.dto.XQuerySubmissionDto;
import at.jku.dke.task_app.xquery.evaluation.EvaluationService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class XQuerySubmissionServiceTest {
    @Test
    void createSubmissionEntity() {
        // Arrange
        var service = new XQuerySubmissionService(null, null, null);
        var dto = new SubmitSubmissionDto<>("test-user", "test-quiz", 7L, "de", SubmissionMode.SUBMIT, 3, new XQuerySubmissionDto("test-input"));

        // Act
        var submission = service.createSubmissionEntity(dto);

        // Assert
        assertEquals(dto.submission().input(), submission.getSubmission());
    }

    @Test
    void mapSubmissionToSubmissionData() {
        // Arrange
        var service = new XQuerySubmissionService(null, null, null);
        var submission = new XQuerySubmission("test-input");

        // Act
        var dto = service.mapSubmissionToSubmissionData(submission);

        // Assert
        assertEquals(submission.getSubmission(), dto.input());
    }

    @Test
    void evaluate() {
        // Arrange
        var evalService = mock(EvaluationService.class);
        var dto = new SubmitSubmissionDto<>("test-user", "test-quiz", 7L, "de", SubmissionMode.SUBMIT, 3, new XQuerySubmissionDto("test-input"));
        var service = new XQuerySubmissionService(null, null, evalService);

        // Act
        service.evaluate(dto);

        // Assert
        verify(evalService).evaluate(dto);
    }
}
