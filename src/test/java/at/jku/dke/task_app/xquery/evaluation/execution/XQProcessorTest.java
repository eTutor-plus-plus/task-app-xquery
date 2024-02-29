package at.jku.dke.task_app.xquery.evaluation.execution;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class XQProcessorTest {

    @Test
    void replaceXmlFileName_valid() throws InvalidDocumentLoadException {
        // Arrange
        var query = """
            for $x in doc('etutor.xml')/doc/a
            return $x/text()
            """;

        // Act
        var result = XQProcessor.replaceXmlFileName(query, "test.xml");

        // Assert
        assertEquals("""
            for $x in doc('test.xml')/doc/a
            return $x/text()
            """, result);
    }

    @Test
    void replaceXmlFileName_multiple() throws InvalidDocumentLoadException {
        // Arrange
        var query = """
            for $x in doc('etutor.xml')/doc/a
            return $x/text()
            for $x in collection("etutor.xml")/doc/b
            return $x/text()
            """;

        // Act
        var result = XQProcessor.replaceXmlFileName(query, "./dir/test.xml");

        // Assert
        assertEquals("""
            for $x in doc('./dir/test.xml')/doc/a
            return $x/text()
            for $x in collection("./dir/test.xml")/doc/b
            return $x/text()
            """, result);
    }

    @Test
    void replaceXmlFileName_invalidDoc() {
        // Arrange
        var query = """
            for $x in doc('etutor2.xml')/doc/a
            return $x/text()
            """;

        // Act & Assert
        assertThrows(InvalidDocumentLoadException.class, () -> XQProcessor.replaceXmlFileName(query, "test.xml"));
    }

    @Test
    void replaceXmlFileName_invalidCollection() {
        // Arrange
        var query = """
            for $x in collection('etutor2.xml')/doc/a
            return $x/text()
            """;

        // Act & Assert
        assertThrows(InvalidDocumentLoadException.class, () -> XQProcessor.replaceXmlFileName(query, "test.xml"));
    }

}
