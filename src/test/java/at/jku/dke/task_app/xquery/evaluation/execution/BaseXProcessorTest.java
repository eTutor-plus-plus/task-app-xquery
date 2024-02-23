package at.jku.dke.task_app.xquery.evaluation.execution;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BaseXProcessorTest {

    @Test
    void executeQuery_valid() throws XQueryException {
        try (var processor = new BaseXProcessor(Path.of("./basex"))) {
            // Arrange
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
    }

    @Test
    void executeQuery_invalidFile() {
        try (var processor = new BaseXProcessor(Path.of("./basex"))) {
            // Arrange
            var document = """
                <invalid>
                """;
            var query = """
                for $x in doc('etutor.xml')/docs/a
                return $x/text()
                """;

            // Act & Assert
            assertThrows(XQueryException.class, () -> processor.executeQuery(query, document));
        }
    }

    @Test
    void executeQuery_invalidQuery() {
        try (var processor = new BaseXProcessor(Path.of("./basex"))) {
            // Arrange
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
    }

}
