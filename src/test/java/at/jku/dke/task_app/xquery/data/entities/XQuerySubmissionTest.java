package at.jku.dke.task_app.xquery.data.entities;

import at.jku.dke.etutor.task_app.dto.SubmissionMode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class XQuerySubmissionTest {

    @Test
    void constructor1() {
        // Arrange
        var expected = "test";

        // Act
        var submission = new XQuerySubmission(expected);
        var actual = submission.getSubmission();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void contructor2(){
        // Arrange
        var expected = "test";
        var task = new XQueryTask();
        var userId = "1";
        var assignmentId = "1";
        var language = "en";
        var feedbackLevel = 1;
        var mode = SubmissionMode.DIAGNOSE;

        // Act
        var submission = new XQuerySubmission(userId, assignmentId, task, language, feedbackLevel, mode, expected);

        // Assert
        assertEquals(expected, submission.getSubmission());
        assertEquals(userId, submission.getUserId());
        assertEquals(assignmentId, submission.getAssignmentId());
        assertEquals(task, submission.getTask());
        assertEquals(language, submission.getLanguage());
        assertEquals(feedbackLevel, submission.getFeedbackLevel());
        assertEquals(mode, submission.getMode());
    }

    @Test
    void getSetSubmission() {
        // Arrange
        var submission = new XQuerySubmission();
        var expected = "test";

        // Act
        submission.setSubmission(expected);
        var actual = submission.getSubmission();

        // Assert
        assertEquals(expected, actual);
    }

}
