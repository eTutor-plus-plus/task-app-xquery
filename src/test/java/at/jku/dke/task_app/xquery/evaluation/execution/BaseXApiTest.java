package at.jku.dke.task_app.xquery.evaluation.execution;

import org.basex.core.BaseXException;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class BaseXApiTest {

    @Test
    void getContext() {
        try (var basex = new BaseXApi(Path.of("basex"))) {
            // Act
            var result = basex.getContext();

            // Assert
            assertNotNull(result);
        }
    }

    @Test
    void createDatabase() throws BaseXException {
        try (var basex = new BaseXApi(Path.of("basex"))) {
            // Arrange
            var name = "test";
            var document = "<test></test>";

            // Act
            basex.createDatabase(name, document);

            // Assert
            var dbs = basex.getContext().listDBs();
            assertTrue(dbs.contains(name));
        }
    }

    @Test
    void dropDatabase() throws BaseXException {
        try (var basex = new BaseXApi(Path.of("basex"))) {
            // Arrange
            var name = "test";
            var document = "<test></test>";
            basex.createDatabase(name, document);

            var dbs = basex.getContext().listDBs();
            assertTrue(dbs.contains(name));

            // Act
            basex.dropDatabase(name);

            // Assert
            dbs = basex.getContext().listDBs();
            assertFalse(dbs.contains(name));
        }
    }

    @Test
    void executeQuery() throws BaseXException {
        try (var basex = new BaseXApi(Path.of("basex"))) {
            // Arrange
            var name = "test";
            var document = "<test><a id=\"5\"></a></test>";
            basex.createDatabase(name, document);

            // Act
            var result = basex.executeQuery("//a");

            // Assert
            assertEquals("<a id=\"5\"/>", result);
        }
    }

    @Test
    void close() throws BaseXException {
        // Act
        Path path = Path.of("basex");
        try (var basex = new BaseXApi(path)) {
            var name = "test";
            var document = "<test><a id=\"5\"></a></test>";
            basex.createDatabase(name, document);
            assertThat(path).exists();
        }

        // Assert
        assertThat(path).doesNotExist();
    }

}
