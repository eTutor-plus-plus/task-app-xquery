package at.jku.dke.task_app.xquery.data.entities;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class XQuerySubmissionTest {

    @Test
    void testConstructor() {
        // Arrange
        var expected = "test";

        // Act
        var submission = new XQuerySubmission(expected);
        var actual = submission.getSubmission();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void testGetSetSubmission() {
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
