package at.jku.dke.task_app.xquery.validation;

import at.jku.dke.task_app.xquery.dto.ModifyXQueryTaskGroupDto;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidTaskGroupNumberValidatorTest {

    @Test
    void isValidCorrectOrder() {
        // Arrange
        ValidTaskGroupNumberValidator validTaskGroupNumberValidator = new ValidTaskGroupNumberValidator();
        ModifyXQueryTaskGroupDto modifyXQueryTaskGroupDto = new ModifyXQueryTaskGroupDto(1, 2);

        // Act
        boolean result = validTaskGroupNumberValidator.isValid(modifyXQueryTaskGroupDto, null);

        // Assert
        assertTrue(result);
    }

    @Test
    void isValidSameValue() {
        // Arrange
        ValidTaskGroupNumberValidator validTaskGroupNumberValidator = new ValidTaskGroupNumberValidator();
        ModifyXQueryTaskGroupDto modifyXQueryTaskGroupDto = new ModifyXQueryTaskGroupDto(2, 2);

        // Act
        boolean result = validTaskGroupNumberValidator.isValid(modifyXQueryTaskGroupDto, null);

        // Assert
        assertFalse(result);
    }

    @Test
    void isValidIncorrectOrder() {
        // Arrange
        ValidTaskGroupNumberValidator validTaskGroupNumberValidator = new ValidTaskGroupNumberValidator();
        ModifyXQueryTaskGroupDto modifyXQueryTaskGroupDto = new ModifyXQueryTaskGroupDto(2, 1);

        // Act
        boolean result = validTaskGroupNumberValidator.isValid(modifyXQueryTaskGroupDto, null);

        // Assert
        assertFalse(result);
    }
}
