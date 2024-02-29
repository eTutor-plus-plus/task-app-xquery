package at.jku.dke.task_app.xquery.evaluation.analysis;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IncorrectTextValueModelTest {

    @Test
    void getSetPath() {
        // Arrange
        var model = new IncorrectTextValueModel();
        var expected = "test";

        // Act
        model.setPath(expected);
        var result = model.getPath();

        // Assert
        assertEquals(expected, result);
    }

    @Test
    void getSetExpectedValue() {
        // Arrange
        var model = new IncorrectTextValueModel();
        var expected = "test";

        // Act
        model.setExpectedValue(expected);
        var result = model.getExpectedValue();

        // Assert
        assertEquals(expected, result);
    }

}
