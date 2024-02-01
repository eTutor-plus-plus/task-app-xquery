package at.jku.dke.task_app.xquery.data.entities;

import at.jku.dke.etutor.task_app.dto.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class XQueryTaskGroupTest {

    @Test
    void testConstructor1() {
        // Arrange
        final int expectedMinNumber = 21;
        final int expectedMaxNumber = 42;

        // Act
        XQueryTaskGroup binarySearchTaskGroup = new XQueryTaskGroup(expectedMinNumber, expectedMaxNumber);
        int actualMinNumber = binarySearchTaskGroup.getMinNumber();
        int actualMaxNumber = binarySearchTaskGroup.getMaxNumber();

        // Assert
        assertEquals(expectedMinNumber, actualMinNumber);
        assertEquals(expectedMaxNumber, actualMaxNumber);
    }

    @Test
    void testConstructor2() {
        // Arrange
        final TaskStatus status = TaskStatus.APPROVED;
        final int expectedMinNumber = 21;
        final int expectedMaxNumber = 42;

        // Act
        XQueryTaskGroup binarySearchTaskGroup = new XQueryTaskGroup(status, expectedMinNumber, expectedMaxNumber);
        TaskStatus actualStatus = binarySearchTaskGroup.getStatus();
        int actualMinNumber = binarySearchTaskGroup.getMinNumber();
        int actualMaxNumber = binarySearchTaskGroup.getMaxNumber();

        // Assert
        assertEquals(status, actualStatus);
        assertEquals(expectedMinNumber, actualMinNumber);
        assertEquals(expectedMaxNumber, actualMaxNumber);
    }

    @Test
    void testConstructor3() {
        // Arrange
        final long expectedId = 21;
        final TaskStatus status = TaskStatus.APPROVED;
        final int expectedMinNumber = 21;
        final int expectedMaxNumber = 42;

        // Act
        XQueryTaskGroup binarySearchTaskGroup = new XQueryTaskGroup(expectedId, status, expectedMinNumber, expectedMaxNumber);
        long actualId = binarySearchTaskGroup.getId();
        TaskStatus actualStatus = binarySearchTaskGroup.getStatus();
        int actualMinNumber = binarySearchTaskGroup.getMinNumber();
        int actualMaxNumber = binarySearchTaskGroup.getMaxNumber();

        // Assert
        assertEquals(expectedId, actualId);
        assertEquals(status, actualStatus);
        assertEquals(expectedMinNumber, actualMinNumber);
        assertEquals(expectedMaxNumber, actualMaxNumber);
    }

    @Test
    void testGetSetMinNumber() {
        // Arrange
        XQueryTaskGroup binarySearchTaskGroup = new XQueryTaskGroup();
        final int expected = 21;

        // Act
        binarySearchTaskGroup.setMinNumber(expected);
        int actual = binarySearchTaskGroup.getMinNumber();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void testGetSetMaxNumber() {
        // Arrange
        XQueryTaskGroup binarySearchTaskGroup = new XQueryTaskGroup();
        final int expected = 21;

        // Act
        binarySearchTaskGroup.setMaxNumber(expected);
        int actual = binarySearchTaskGroup.getMaxNumber();

        // Assert
        assertEquals(expected, actual);
    }

}
