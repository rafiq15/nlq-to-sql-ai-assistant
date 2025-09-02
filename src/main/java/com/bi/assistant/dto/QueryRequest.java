package com.bi.assistant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QueryRequest {
    
    @NotBlank(message = "Query cannot be empty")
    @Size(min = 3, max = 500, message = "Query must be between 3 and 500 characters")
    private String query;
    
    private String dateRange;
    private Integer limit;
    private boolean includeMetadata = true;
}
