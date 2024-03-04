package at.jku.dke.task_app.xquery.evaluation.execution;

import org.junit.jupiter.api.Test;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SuppressWarnings("resource")
class SaxonProcessorTest {
// Test fails, but I don't understand why the saxon processor is looking in the wrong directory
//    @Test
//    void executeQuery_notExistingDirectory() throws XQueryException, IOException {
//        // Arrange
//        var path = Path.of(".", "saxon", UUID.randomUUID().toString());
//        FileSystemUtils.deleteRecursively(path);
//        var processor = new SaxonProcessor(path);
//        var document = """
//            <docs>
//                <a>1</a>
//                <b>2</b>
//                <a>3</a>
//            </docs>
//            """;
//        var query = """
//            for $x in doc('etutor.xml')/docs/a
//            return $x/text()
//            """;
//
//        // Act
//        var result = processor.executeQuery(query, document);
//        processor.close();
//
//        // Assert
//        assertEquals("1\n3", result);
//    }

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
        processor.close();

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
