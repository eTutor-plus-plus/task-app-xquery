package at.jku.dke.task_app.xquery.evaluation.execution;

import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.health.Status;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class XqHealthIndicatorTest {

    @Test
    void health_up() throws XQueryException {
        // Arrange
        var executor = mock(XQProcessor.class);
        when(executor.getVersion()).thenReturn("xq-version");
        var indicator = new XqHealthIndicator(executor);

        // Act
        var result = indicator.health();

        // Assert
        assertEquals(Status.UP, result.getStatus());
        assertEquals("xq-version", result.getDetails().get("version"));
    }

    @Test
    void health_exception() throws XQueryException {
        // Arrange
        var executor = mock(XQProcessor.class);
        when(executor.getVersion()).thenThrow(new XQueryException("xq-error"));
        var indicator = new XqHealthIndicator(executor);

        // Act
        var result = indicator.health();

        // Assert
        assertEquals(Status.DOWN, result.getStatus());
        assertEquals("at.jku.dke.task_app.xquery.evaluation.execution.XQueryException: xq-error", result.getDetails().get("error"));
    }

    @Test
    void health_cache() throws XQueryException {
        // Arrange
        var executor = mock(XQProcessor.class);
        when(executor.getVersion()).thenReturn("xq-version");
        var indicator = new XqHealthIndicator(executor);

        // Act
        var result = indicator.health();
        var result2 = indicator.health();

        // Assert
        assertEquals(result, result2);
        verify(executor, times(1)).getVersion();
    }

}
