package com.bi.assistant.service;

import com.bi.assistant.dto.QueryRequest;
import com.bi.assistant.dto.QueryResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QueryServiceTest {

    @Mock
    private ChatClient.Builder chatClientBuilder;
    
    @Mock
    private ChatClient chatClient;
    
    @Mock
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldProcessValidQuery() {
        // Create service instance
        when(chatClientBuilder.build()).thenReturn(chatClient);
        QueryService queryService = new QueryService(chatClientBuilder, jdbcTemplate);
        
        // Mock AI response for SQL generation
        when(chatClient.prompt(anyString())).thenReturn(mock(ChatClient.ChatClientRequestSpec.class));
        when(chatClient.prompt(anyString()).call()).thenReturn(mock(ChatClient.ChatClientRequest.CallResponseSpec.class));
        when(chatClient.prompt(anyString()).call().content()).thenReturn("SELECT product_name FROM products LIMIT 5");
        
        // Mock successful query execution
        List<Map<String, Object>> mockResults = Arrays.asList(
            createRow("Laptop Pro", null),
            createRow("Gaming Laptop", null)
        );
        
        when(jdbcTemplate.queryForList(anyString())).thenReturn(mockResults);
        
        // Create request
        QueryRequest request = new QueryRequest();
        request.setQuery("Show me top products");
        
        // Execute the query
        QueryResponse response = queryService.processNaturalLanguageQuery(request);
        
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getData());
        assertEquals(2, response.getData().size());
    }

    private Map<String, Object> createRow(String productName, double revenue) {
        Map<String, Object> row = new HashMap<>();
        row.put("product_name", productName);
        row.put("total_revenue", revenue);
        return row;
    }
}
