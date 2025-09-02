package com.bi.assistant.controller;

import com.bi.assistant.dto.QueryRequest;
import com.bi.assistant.dto.QueryResponse;
import com.bi.assistant.service.QueryService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
@Slf4j
public class QueryController {

    private final QueryService queryService;

    @Autowired
    public QueryController(QueryService queryService) {
        this.queryService = queryService;
    }

    // REST API endpoints
    @PostMapping("/api/query")
    @ResponseBody
    public ResponseEntity<QueryResponse> handleQueryApi(@Valid @RequestBody QueryRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            QueryResponse errorResponse = new QueryResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMessage("Validation failed: " + bindingResult.getFieldError().getDefaultMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        log.info("API Query received: {}", request.getQuery());
        QueryResponse response = queryService.executeNaturalLanguageQuery(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Legacy REST API endpoint for backward compatibility
    @GetMapping("/query")
    @ResponseBody
    public List<Map<String, Object>> handleQueryLegacy(@RequestParam String q) {
        log.info("Legacy API Query received: {}", q);
        return queryService.executeNaturalLanguageQuery_Legacy(q);
    }

    // Web UI endpoints
    @GetMapping("/")
    public String showQueryForm(Model model) {
        model.addAttribute("query", "");
        model.addAttribute("suggestions", getSampleQueries());
        return "index";
    }

    @PostMapping("/")
    public String processQuery(@RequestParam String query, Model model) {
        log.info("Web UI Query received: {}", query);
        
        try {
            QueryResponse response = queryService.executeNaturalLanguageQuery(query);
            
            if (response.isSuccess()) {
                model.addAttribute("results", response.getData());
                model.addAttribute("metadata", response.getMetadata());
                model.addAttribute("generatedSql", response.getGeneratedSql());
                model.addAttribute("success", true);
            } else {
                model.addAttribute("error", response.getMessage());
                model.addAttribute("generatedSql", response.getGeneratedSql()); // Include SQL even on error
            }
            
            model.addAttribute("query", query);
        } catch (Exception e) {
            log.error("Error processing web UI query: {}", e.getMessage(), e);
            model.addAttribute("error", "Error processing query: " + e.getMessage());
            model.addAttribute("query", query);
        }
        
        model.addAttribute("suggestions", getSampleQueries());
        return "index";
    }

    @GetMapping("/queries")
    public String showQueriesPage() {
        log.info("Serving queries page");
        return "queries";
    }

    private String[] getSampleQueries() {
        return new String[]{
            "Show me the top 5 products by revenue last quarter",
            "What is the total revenue by category this year?",
            "Which customers bought the most products?",
            "Show me sales trends by region",
            "What are the best selling products in Electronics category?",
            "Show me monthly revenue for this year",
            "Which sales person has the highest revenue?",
            "What is the average order value by customer segment?"
        };
    }
}