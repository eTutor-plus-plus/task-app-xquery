package at.jku.dke.task_app.xquery.evaluation.execution;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("resource")
class SaxonProcessorTest {

    @Test
    void executeQuery_valid() throws XQueryException {
        // Arrange
        var processor = new SaxonProcessor(Path.of("./saxon"));
        var document = """
            <docs>
                <a>1</a>
                <b>2</b>
                <a>3</a>
            </docs>
            """;
        var query = """
            for $x in doc('etutor.xml')/docs/a
            return $x/text()
            """;

        // Act
        var result = processor.executeQuery(query, document);

        // Assert
        assertEquals("1\n3", result);
    }

    @Test
    void executeQuery_invalidFile() {
        // Arrange
        var processor = new SaxonProcessor(Path.of("./saxon"));
        var query = """
            for $x in doc('etutor.xml')/docs/a
            return $x/text()
            """;

        // Act & Assert
        assertThrows(XQueryException.class, () -> processor.executeQuery(query, "invalid"));
    }

    @Test
    void executeQuery_invalidQuery() {
        // Arrange
        var processor = new SaxonProcessor(Path.of("./saxon"));
        var document = """
            <docs>
                <a>1</a>
                <b>2</b>
                <a>3</a>
            </docs>
            """;
        var query = """
            for $x in doc('etutor.xml')/docs/a
            return $x/text(
            """;

        // Act & Assert
        assertThrows(XQueryException.class, () -> processor.executeQuery(query, document));
    }

    @Test
    void executeQuery_update() {
        // Arrange
        var processor = new SaxonProcessor(Path.of("./saxon"));
        var document = """
            <docs>
                <a>1</a>
                <b>2</b>
                <a>3</a>
            </docs>
            """;
        var query = """
            let $doc := doc('etutor.xml')
            insert node (attribute { 'a' } { 5 }, 'text', <e/>) into $doc/
            """;

        // Act & Assert
        assertThrows(XQueryException.class, () -> processor.executeQuery(query, document));
    }

}
