package at.jku.dke.task_app.xquery.data.entities;

import at.jku.dke.etutor.task_app.dto.TaskStatus;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class XQueryTaskGroupTest {

    @Test
    void testConstructor1() {
        // Arrange
        final String diagnose = "<db><diagnose>42</diagnose></db>";
        final String submit = "<db><submit>42</submit></db>";

        // Act
        XQueryTaskGroup xqueryTaskGroup = new XQueryTaskGroup(diagnose, submit);
        String actualDiagnose = xqueryTaskGroup.getDiagnoseDocument();
        String actualSubmit = xqueryTaskGroup.getSubmitDocument();

        // Assert
        assertEquals(diagnose, actualDiagnose);
        assertEquals(submit, actualSubmit);
    }

    @Test
    void testConstructor2() {
        // Arrange
        final TaskStatus status = TaskStatus.APPROVED;
        final String diagnose = "<db><diagnose>42</diagnose></db>";
        final String submit = "<db><submit>42</submit></db>";

        // Act
        XQueryTaskGroup xqueryTaskGroup = new XQueryTaskGroup(status, diagnose, submit);
        TaskStatus actualStatus = xqueryTaskGroup.getStatus();
        String actualDiagnose = xqueryTaskGroup.getDiagnoseDocument();
        String actualSubmit = xqueryTaskGroup.getSubmitDocument();

        // Assert
        assertEquals(status, actualStatus);
        assertEquals(diagnose, actualDiagnose);
        assertEquals(submit, actualSubmit);
    }

    @Test
    void testConstructor3() {
        // Arrange
        final long expectedId = 21;
        final TaskStatus status = TaskStatus.APPROVED;
        final String diagnose = "<db><diagnose>42</diagnose></db>";
        final String submit = "<db><submit>42</submit></db>";

        // Act
        XQueryTaskGroup xqueryTaskGroup = new XQueryTaskGroup(expectedId, status, diagnose, submit);
        long actualId = xqueryTaskGroup.getId();
        TaskStatus actualStatus = xqueryTaskGroup.getStatus();
        String actualDiagnose = xqueryTaskGroup.getDiagnoseDocument();
        String actualSubmit = xqueryTaskGroup.getSubmitDocument();

        // Assert
        assertEquals(expectedId, actualId);
        assertEquals(status, actualStatus);
        assertEquals(diagnose, actualDiagnose);
        assertEquals(submit, actualSubmit);
    }

    @Test
    void testGetSetDiagnoseDocument() {
        // Arrange
        XQueryTaskGroup xqueryTaskGroup = new XQueryTaskGroup();
        final String expected = "<db><diagnose>42</diagnose></db>";

        // Act
        xqueryTaskGroup.setDiagnoseDocument(expected);
        String actual = xqueryTaskGroup.getDiagnoseDocument();

        // Assert
        assertEquals(expected, actual);
    }

    @Test
    void testGetSetSubmitDocument() {
        // Arrange
        XQueryTaskGroup xqueryTaskGroup = new XQueryTaskGroup();
        final String expected = "<db><submit>42</submit></db>";

        // Act
        xqueryTaskGroup.setSubmitDocument(expected);
        String actual = xqueryTaskGroup.getSubmitDocument();

        // Assert
        assertEquals(expected, actual);
    }

}
