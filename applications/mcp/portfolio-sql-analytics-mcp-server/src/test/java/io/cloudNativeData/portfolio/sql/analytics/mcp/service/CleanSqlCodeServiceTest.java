package io.cloudNativeData.portfolio.sql.analytics.mcp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class CleanSqlCodeServiceTest {

    private CleanSqlCodeService cleanSqlCodeService;

    @BeforeEach
    void setUp() {
        cleanSqlCodeService = new CleanSqlCodeService();
    }

    @Test
    @DisplayName("Should successfully strip markdown code blocks and trim whitespace")
    void shouldCleanStandardSqlCodeBlock() {
        // Given
        String input = "```sql\nSELECT * FROM users;\n```";
        String expected = "SELECT * FROM users;";

        // When
        String actual = cleanSqlCodeService.cleanSqlCodeBlocks(input);

        // Then
        assertEquals(expected, actual);
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "   "})
    @DisplayName("Should return empty string for null, empty, or blank inputs")
    void shouldReturnEmptyStringForEmptyInputs(String input) {
        // When
        String actual = cleanSqlCodeService.cleanSqlCodeBlocks(input);

        // Then
        assertEquals("", actual);
    }

    @ParameterizedTest
    @CsvSource({
            "'Here is your query: ```sql SELECT 1; ```', 'SELECT 1;'",
            "'```sql SELECT 1; ``` and some trailing text', 'SELECT 1;'"
    })
    @DisplayName("Should handle text surrounding the markdown blocks")
    void shouldHandleSurroundingText(String input, String expected) {
        // When
        String actual = cleanSqlCodeService.cleanSqlCodeBlocks(input);

        // Then
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Should return unchanged but trimmed string if no markdown exists")
    void shouldReturnTrimmedStringIfNoMarkdown() {
        // Given
        String input = "  SELECT * FROM products;  ";
        String expected = "SELECT * FROM products;";

        // When
        String actual = cleanSqlCodeService.cleanSqlCodeBlocks(input);

        // Then
        assertEquals(expected, actual);
    }

}