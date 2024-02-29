package at.jku.dke.task_app.xquery.evaluation.execution;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XQueryExceptionTest {
    @Test
    void constructor1() {
        // Arrange
        var expected = "message";

        // Act
        var result = new XQueryException(expected);

        // Assert
        assertEquals(expected, result.getMessage());
    }

    @Test
    void constructor2() {
        // Arrange
        var expected = "message";
        var cause = new Throwable();

        // Act
        var result = new XQueryException(expected, cause);

        // Assert
        assertEquals(expected, result.getMessage());
        assertEquals(cause, result.getCause());
    }

    @Test
    void constructor3() {
        // Arrange
        var cause = new Throwable();

        // Act
        var result = new XQueryException(cause);

        // Assert
        assertEquals(cause, result.getCause());
    }
}
