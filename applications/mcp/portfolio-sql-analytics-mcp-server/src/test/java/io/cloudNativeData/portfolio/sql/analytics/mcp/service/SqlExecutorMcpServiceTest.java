package io.cloudNativeData.portfolio.sql.analytics.mcp.service;

import io.cloudNativeData.trading.analytics.PortfolioQueryRequests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.converter.Converter;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SqlExecutorMcpServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private Converter<String, String> generateSqlFromText;

    @InjectMocks
    private SqlExecutorMcpService sqlExecutorMcpService;

    private final String textPrompt = "Show all portfolios";
    private final String expectedSql = "SELECT * FROM portfolios";

    @BeforeEach
    void setUp() {
        // Stub the converter since it's used in every test case
        lenient().when(generateSqlFromText.convert(textPrompt)).thenReturn(expectedSql);
    }

    @Test
    void executeTextAsSql_Success() {
        // Given
        List<Map<String, Object>> mockDbResults = List.of(
                Map.of("id", 1, "name", "Tech Growth Portfolio"),
                Map.of("id", 2, "name", "Conservative Income")
        );
        when(jdbcTemplate.queryForList(expectedSql)).thenReturn(mockDbResults);

        // When
        PortfolioQueryRequests response = sqlExecutorMcpService.executeTextAsSql(textPrompt);

        // Then
        assertNotNull(response);
        assertEquals(expectedSql, response.sql());
        assertEquals(2, response.results().size());
        assertEquals("Tech Growth Portfolio", response.results().get(0).get("name"));

        verify(generateSqlFromText, times(1)).convert(textPrompt);
        verify(jdbcTemplate, times(1)).queryForList(expectedSql);
    }

    @Test
    void executeTextAsSql_EmptyResults() {
        // Given
        when(jdbcTemplate.queryForList(expectedSql)).thenReturn(Collections.emptyList());

        // When
        PortfolioQueryRequests response = sqlExecutorMcpService.executeTextAsSql(textPrompt);

        // Then
        assertNotNull(response);
        assertEquals(expectedSql, response.sql());
        assertTrue(response.results().isEmpty());
    }

    @Test
    void executeTextAsSql_ThrowsException() {
        // Given
        String errorMessage = "Table 'portfolios' not found";
        when(jdbcTemplate.queryForList(expectedSql)).thenThrow(new RuntimeException(errorMessage));

        // When
        PortfolioQueryRequests response = sqlExecutorMcpService.executeTextAsSql(textPrompt);

        // Then
        assertNotNull(response);
        assertEquals(expectedSql, response.sql());
        assertEquals(1, response.results().size());

        Map<String, Object> errorMap = response.results().get(0);
        assertTrue(errorMap.containsKey("error"));
        assertEquals("Failed to execute query: " + errorMessage, errorMap.get("error"));
    }
}