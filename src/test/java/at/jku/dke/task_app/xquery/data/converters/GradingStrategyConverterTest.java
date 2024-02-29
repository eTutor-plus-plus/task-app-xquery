package at.jku.dke.task_app.xquery.data.converters;

import at.jku.dke.task_app.xquery.data.entities.GradingStrategy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GradingStrategyConverterTest {
    //#region --- convertToDatabaseColumn ---
    @Test
    void testConvertToDatabaseColumn() {
        // Arrange
        var converter = new GradingStrategyConverter();
        var status = GradingStrategy.EACH;

        // Act
        var result = converter.convertToDatabaseColumn(status);

        // Assert
        assertEquals(status.name().toLowerCase(), result);
    }

    @Test
    void testConvertToDatabaseColumnNullValue() {
        // Arrange
        var converter = new GradingStrategyConverter();

        // Act
        var result = converter.convertToDatabaseColumn(null);

        // Assert
        assertNull(result);
    }
    //#endregion

    //#region --- convertToEntityAttribute ---
    @Test
    void testConvertToEntityAttribute() {
        // Arrange
        var converter = new GradingStrategyConverter();
        var status = GradingStrategy.GROUP;

        // Act
        var result = converter.convertToEntityAttribute(status.name().toLowerCase());

        // Assert
        assertEquals(status, result);
    }

    @Test
    void testConvertToEntityAttributeNullValue() {
        // Arrange
        var converter = new GradingStrategyConverter();

        // Act
        var result = converter.convertToEntityAttribute(null);

        // Assert
        assertNull(result);
    }

    @Test
    void testConvertToEntityAttributeInvalidValueThrowsException() {
        // Arrange
        var converter = new GradingStrategyConverter();

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> converter.convertToEntityAttribute("invalid"));
    }
    //#endregion
}
