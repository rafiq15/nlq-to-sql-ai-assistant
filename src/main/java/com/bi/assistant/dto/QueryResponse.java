package com.bi.assistant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryResponse {
    private boolean success;
    private String message;
    private String generatedSql;
    private List<Map<String, Object>> data;
    private QueryMetadata metadata;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QueryMetadata {
        private int rowCount;
        private long executionTimeMs;
        private String[] columnNames;
        private String queryType;
    }
}
