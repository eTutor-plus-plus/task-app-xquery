package at.jku.dke.task_app.xquery.services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HashIdsTest {
    @Test
    void encode() {
        // Arrange
        var decoded = 48429L;

        // Act
        var encoded = HashIds.encode(decoded);

        // Assert
        assertEquals(decoded, HashIds.decode(encoded));
    }

    @Test
    void decode() {
        // Arrange
        var encoded = "Hthn";

        // Act
        var decoded = HashIds.decode(encoded);

        // Assert
        assertEquals(22600L, decoded);
    }
}
