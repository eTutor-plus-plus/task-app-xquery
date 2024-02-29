package at.jku.dke.task_app.xquery.evaluation.analysis;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AttributeModelTest {

    @Test
    void getSetPath() {
        // Arrange
        var model = new AttributeModel();
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
        var model = new AttributeModel();
        var expected = "test";

        // Act
        model.setName(expected);
        var result = model.getName();

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void getSetValue() {
        // Arrange
        var model = new AttributeModel();
        var expected = "test";

        // Act
        model.setValue(expected);
        var result = model.getValue();

        // Assert
        assertEquals(expected, result);
    }

}
