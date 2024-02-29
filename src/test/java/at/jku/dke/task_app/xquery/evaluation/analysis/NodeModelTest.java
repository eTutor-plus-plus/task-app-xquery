package at.jku.dke.task_app.xquery.evaluation.analysis;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NodeModelTest {

    @Test
    void constructor() {
        // Arrange
        var expectedPath = "test";
        var expectedName = "test";

        // Act
        var model = new NodeModel(expectedPath, expectedName);

        // Assert
        assertEquals(expectedPath, model.getPath());
        assertEquals(expectedName, model.getName());
    }

    @Test
    void getSetPath() {
        // Arrange
        var model = new NodeModel();
        var expected = "test";

        // Act
        model.setPath(expected);
        var result = model.getPath();

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void getSetName() {
        // Arrange
        var model = new NodeModel();
        var expected = "test";

        // Act
        model.setName(expected);
        var result = model.getName();

        // Assert
        assertEquals(expected, result);
    }

}
