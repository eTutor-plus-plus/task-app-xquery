package at.jku.dke.task_app.xquery.evaluation.analysis;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AnalysisExceptionTest {
    @Test
    void constructor1() {
        // Arrange
        var message = "message";

        // Act
        var result = new AnalysisException(message);

        // Assert
        assertEquals(message, result.getMessage());
    }

    @Test
    void constructor2() {
        // Arrange
        var message = "message";
        var cause = new Throwable();

        // Act
        var result = new AnalysisException(message, cause);

        // Assert
        assertEquals(message, result.getMessage());
        assertEquals(cause, result.getCause());
    }

    @Test
    void constructor3() {
        // Arrange
        var cause = new Throwable();

        // Act
        var result = new AnalysisException(cause);

        // Assert
        assertEquals(cause, result.getCause());
    }
}
