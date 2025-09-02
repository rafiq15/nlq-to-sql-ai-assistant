package com.bi.assistant.service;

import com.bi.assistant.dto.QueryRequest;
import com.bi.assistant.dto.QueryResponse;
import com.bi.assistant.exception.QueryExecutionException;
import com.bi.assistant.exception.QueryGenerationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@Slf4j
public class QueryService {

    private final ChatClient chatClient;
    private final JdbcTemplate jdbcTemplate;

    private static final Pattern DANGEROUS_PATTERNS = Pattern.compile(
            "\\b(INSERT|UPDATE|DELETE|DROP|CREATE|ALTER|TRUNCATE|EXEC|EXECUTE)\\b",
            Pattern.CASE_INSENSITIVE
    );

    @Autowired
    public QueryService(ChatClient.Builder chatClientBuilder, JdbcTemplate jdbcTemplate) {
        this.chatClient = chatClientBuilder.build();
        this.jdbcTemplate = jdbcTemplate;
    }

    public QueryResponse executeNaturalLanguageQuery(QueryRequest request) {
        return executeNaturalLanguageQuery(request.getQuery());
    }

    @Cacheable(value = "queryCache", key = "#naturalQuery")
    public QueryResponse executeNaturalLanguageQuery(String naturalQuery) {
        long startTime = System.currentTimeMillis();

        try {
            log.info("Processing natural language query: {}", naturalQuery);

            String generatedSql = generateSqlQuery(naturalQuery);
            log.info("Generated SQL: {}", generatedSql);

            validateSqlQuery(generatedSql);

            List<Map<String, Object>> results = executeSqlQuery(generatedSql);

            long executionTime = System.currentTimeMillis() - startTime;

            return createSuccessResponse(generatedSql, results, executionTime);

        } catch (Exception e) {
            log.error("Error processing query: {}", e.getMessage(), e);
            return createErrorResponse(e.getMessage());
        }
    }

    // Legacy method for backward compatibility
    public List<Map<String, Object>> executeNaturalLanguageQuery_Legacy(String naturalQuery) {
        QueryResponse response = executeNaturalLanguageQuery(naturalQuery);
        if (response.isSuccess()) {
            return response.getData();
        } else {
            throw new QueryExecutionException(response.getMessage());
        }
    }

    private String preprocessQuery(String naturalQuery) {
        String lower = naturalQuery.toLowerCase();
        
        // Handle specific patterns that commonly cause SQL generation issues
        if (lower.contains("average order value") && lower.contains("customer segment")) {
            return "Calculate the average order value for each customer segment using subquery to calculate order totals first";
        }
        
        if (lower.contains("order value") && lower.contains("segment")) {
            return "Calculate average order value by customer segment with proper subquery aggregation";
        }
        
        // Handle monthly/quarterly analysis
        if (lower.contains("monthly") && lower.contains("trend")) {
            return "Show monthly sales trends with year and month grouping";
        }
        
        return naturalQuery;
    }

    private String generateSqlQuery(String naturalQuery) {
        // Preprocess query for common patterns that need specific handling
        naturalQuery = preprocessQuery(naturalQuery);
        
        String enhancedSchemaDescription = """
                Database Schema:
                
                Tables:
                1. products: 
                   - id (INTEGER, Primary Key)
                   - product_name (VARCHAR) - Name of the product
                   - category (VARCHAR) - Product category (Electronics, Appliances, Accessories, Furniture)
                   - price (DECIMAL) - Product price
                   - description (TEXT) - Product description
                   - manufacturer (VARCHAR) - Product manufacturer
                
                2. sales: 
                   - id (INTEGER, Primary Key)
                   - product_id (INTEGER, Foreign Key to products.id)
                   - sale_date (DATE) - Date of sale
                   - revenue (DECIMAL) - Revenue from the sale
                   - quantity (INTEGER) - Quantity sold
                   - customer_id (INTEGER, Foreign Key to customers.id)
                   - region (VARCHAR) - Sales region
                   - sales_person (VARCHAR) - Name of sales person
                
                3. customers:
                   - id (INTEGER, Primary Key)
                   - customer_name (VARCHAR) - Customer name
                   - email (VARCHAR) - Customer email
                   - phone (VARCHAR) - Customer phone
                   - address (TEXT) - Customer address
                   - city (VARCHAR) - Customer city
                   - country (VARCHAR) - Customer country
                   - customer_segment (VARCHAR) - Customer segment (Premium, Standard, Basic)
                
                Important Notes:
                - Use 'product_name' column for products table
                - Always join tables properly using foreign keys
                - Use appropriate date filtering for time-based queries
                """;

        LocalDate today = LocalDate.now();
        LocalDate quarterStart = today.minusMonths(3).withDayOfMonth(1);
        LocalDate quarterEnd = today.withDayOfMonth(1);
        LocalDate yearStart = today.withDayOfYear(1);

        PromptTemplate promptTemplate = new PromptTemplate("""
                    You are a PostgreSQL expert. Translate the following natural language query to SQL.
                
                    Schema: {schema}
                
                    Natural Language Query: {query}
                
                    Date Context:
                    - Today: {today}
                    - Last quarter: {quarter_start} to {quarter_end}
                    - This year: {year_start} to {today}
                
                    CRITICAL RULES:
                    1. Return ONLY ONE executable SQL statement
                    2. NO explanations, NO comments, NO alternative queries
                    3. NO "OR" statements, NO multiple options
                    4. ALWAYS use JOINs when accessing data from multiple tables
                    5. For "list all customers" queries, use: SELECT * FROM customers;
                    6. Use table aliases: p for products, s for sales, c for customers
                    7. For nested aggregation, use subqueries or CTEs
                
                    CORRECT Examples:
                    - "top 5 products by revenue":
                      SELECT p.product_name, SUM(s.revenue) AS total_revenue FROM products p JOIN sales s ON p.id = s.product_id GROUP BY p.product_name ORDER BY total_revenue DESC LIMIT 5;
                
                    - "list all customers":
                      SELECT * FROM customers;
                
                    - "revenue by category":
                      SELECT p.category, SUM(s.revenue) AS total_revenue FROM products p JOIN sales s ON p.id = s.product_id GROUP BY p.category ORDER BY total_revenue DESC;
                
                    - "average order value by customer segment":
                      SELECT c.customer_segment, AVG(order_total) AS avg_order_value FROM customers c JOIN (SELECT customer_id, SUM(p.price * s.quantity) AS order_total FROM sales s JOIN products p ON s.product_id = p.id GROUP BY customer_id) AS orders ON c.id = orders.customer_id GROUP BY c.customer_segment;
                
                    - "monthly sales trends":
                      SELECT EXTRACT(YEAR FROM sale_date) AS year, EXTRACT(MONTH FROM sale_date) AS month, SUM(revenue) AS monthly_revenue FROM sales GROUP BY EXTRACT(YEAR FROM sale_date), EXTRACT(MONTH FROM sale_date) ORDER BY year, month;
                
                    Return only the SQL query without any explanations:
                """);

        promptTemplate.add("schema", enhancedSchemaDescription);
        promptTemplate.add("query", naturalQuery);
        promptTemplate.add("today", today.toString());
        promptTemplate.add("quarter_start", quarterStart.toString());
        promptTemplate.add("quarter_end", quarterEnd.toString());
        promptTemplate.add("year_start", yearStart.toString());

        try {
            String generatedSql = chatClient
                    .prompt(promptTemplate.create())
                    .call()
                    .content();

            // Clean up the SQL by removing markdown code blocks if present
            generatedSql = generatedSql.replaceAll("```sql\\s*", "")
                    .replaceAll("```\\s*", "")
                    .trim();

            // Extract only the first valid SQL statement
            generatedSql = extractFirstSqlStatement(generatedSql);

            // Remove any trailing semicolon and normalize whitespace
            generatedSql = generatedSql.replaceAll(";\\s*$", "").trim();

            if (generatedSql.isEmpty()) {
                throw new QueryGenerationException("Generated SQL query is empty");
            }

            return generatedSql;

        } catch (Exception e) {
            log.error("Failed to generate SQL query for: {}", naturalQuery, e);
            throw new QueryGenerationException("Failed to generate SQL query: " + e.getMessage(), e);
        }
    }

    private String extractFirstSqlStatement(String response) {
        if (response == null || response.trim().isEmpty()) {
            throw new QueryGenerationException("Empty response from AI model");
        }

        // Split the response into lines
        String[] lines = response.split("\n");
        StringBuilder sqlBuilder = new StringBuilder();
        boolean foundSql = false;

        for (String line : lines) {
            line = line.trim();

            // Skip empty lines and obvious explanatory text
            if (line.isEmpty()) continue;

            // Skip explanatory text patterns
            if (line.toLowerCase().startsWith("to ") ||
                    line.toLowerCase().startsWith("if you") ||
                    line.toLowerCase().startsWith("note:") ||
                    line.toLowerCase().startsWith("explanation:") ||
                    line.toLowerCase().contains("you can add") ||
                    line.toLowerCase().contains("filter results") ||
                    line.toLowerCase().startsWith("for ")) {
                continue;
            }

            // Stop at "OR" which indicates alternative queries
            if (line.toUpperCase().equals("OR")) {
                break;
            }

            // Check if this looks like start of SQL
            if (line.toUpperCase().startsWith("SELECT") ||
                    line.toUpperCase().startsWith("WITH") ||
                    line.toUpperCase().startsWith("INSERT") ||
                    line.toUpperCase().startsWith("UPDATE") ||
                    line.toUpperCase().startsWith("DELETE")) {
                foundSql = true;
            }

            // If we found SQL, keep adding lines until we hit a semicolon
            if (foundSql) {
                sqlBuilder.append(line).append(" ");

                // Stop at semicolon (end of SQL statement)
                if (line.endsWith(";")) {
                    break;
                }
            }
        }

        String finalSql = sqlBuilder.toString().trim();

        // If no SQL found, try to find it in the original response
        if (!foundSql || finalSql.isEmpty()) {
            // Look for SQL patterns in the entire response
            if (response.toUpperCase().contains("SELECT")) {
                int selectIndex = response.toUpperCase().indexOf("SELECT");
                String fromSelect = response.substring(selectIndex);

                // Find the end of the statement (semicolon or end of line)
                int semicolonIndex = fromSelect.indexOf(";");
                int newlineIndex = fromSelect.indexOf("\n");

                if (semicolonIndex > 0) {
                    finalSql = fromSelect.substring(0, semicolonIndex + 1).trim();
                } else if (newlineIndex > 0) {
                    finalSql = fromSelect.substring(0, newlineIndex).trim();
                } else {
                    finalSql = fromSelect.trim();
                }
            }
        }

        // Clean up extra whitespace
        finalSql = finalSql.replaceAll("\\s+", " ").trim();

        if (finalSql.isEmpty()) {
            throw new QueryGenerationException("Could not extract valid SQL from AI response: " + response);
        }

        return finalSql;
    }

    private void validateSqlQuery(String sql) {
        String sqlUpper = sql.toUpperCase().trim();

        // Check if it's a SELECT query
        if (!sqlUpper.startsWith("SELECT") && !sqlUpper.startsWith("WITH")) {
            throw new QueryExecutionException("Only SELECT queries are allowed");
        }

        // Check for dangerous patterns
        if (DANGEROUS_PATTERNS.matcher(sqlUpper).find()) {
            throw new QueryExecutionException("Query contains potentially dangerous SQL operations");
        }

        // Additional validation for common SQL injection patterns
        if (sql.contains("--") || sql.contains("/*") || sql.contains("*/")) {
            log.warn("Query contains comment patterns, reviewing: {}", sql);
        }
    }

    private List<Map<String, Object>> executeSqlQuery(String sql) {
        try {
            return jdbcTemplate.queryForList(sql);
        } catch (Exception e) {
            log.error("SQL execution failed for query: {}", sql, e);

            String errorMessage = e.getMessage();

            // Check for specific column not found errors and try to fix them
            if (errorMessage.contains("column \"product_name\" does not exist") ||
                    errorMessage.contains("column \"category\" does not exist")) {

                log.info("Attempting to fix missing JOIN for products table");
                String fixedSql = attemptToFixProductJoin(sql);
                if (!fixedSql.equals(sql)) {
                    try {
                        log.info("Retrying with fixed SQL: {}", fixedSql);
                        return jdbcTemplate.queryForList(fixedSql);
                    } catch (Exception retryException) {
                        log.warn("Fixed SQL also failed: {}", retryException.getMessage());
                    }
                }

                throw new QueryExecutionException(
                        "Column not found in sales table. Product information (product_name, category) requires JOIN with products table. " +
                                "Query attempted: " + sql
                );
            } else if (errorMessage.contains("column \"customer_name\" does not exist")) {

                log.info("Attempting to fix missing JOIN for customers table");
                String fixedSql = attemptToFixCustomerJoin(sql);
                if (!fixedSql.equals(sql)) {
                    try {
                        log.info("Retrying with fixed SQL: {}", fixedSql);
                        return jdbcTemplate.queryForList(fixedSql);
                    } catch (Exception retryException) {
                        log.warn("Fixed SQL also failed: {}", retryException.getMessage());
                    }
                }

                throw new QueryExecutionException(
                        "Column 'customer_name' not found in sales table. Customer information requires JOIN with customers table. " +
                                "Query attempted: " + sql
                );
            } else if (errorMessage.contains("relation") && errorMessage.contains("does not exist")) {
                throw new QueryExecutionException("Referenced table or column does not exist in the database");
            } else if (errorMessage.contains("syntax error")) {
                throw new QueryExecutionException("Generated SQL query has syntax errors: " + sql);
            } else if (errorMessage.contains("column") && errorMessage.contains("must appear")) {
                throw new QueryExecutionException("Query grouping error - all selected columns must be in GROUP BY clause");
            } else {
                throw new QueryExecutionException("SQL execution failed: " + errorMessage);
            }
        }
    }

    private String attemptToFixProductJoin(String sql) {
        // Simple pattern matching to add products JOIN if missing
        if (!sql.toUpperCase().contains("JOIN") && !sql.toUpperCase().contains("FROM PRODUCTS")) {
            if (sql.toUpperCase().contains("FROM SALES") &&
                    (sql.contains("product_name") || sql.contains("category"))) {

                sql = sql.replace("FROM sales", "FROM products p JOIN sales s ON p.id = s.product_id");
                sql = sql.replace("sales.", "s.");
                sql = sql.replace("product_name", "p.product_name");
                sql = sql.replace("category", "p.category");
            }
        }
        return sql;
    }

    private String attemptToFixCustomerJoin(String sql) {
        // Simple pattern matching to add customers JOIN if missing
        if (!sql.toUpperCase().contains("JOIN") && !sql.toUpperCase().contains("FROM CUSTOMERS")) {
            if (sql.toUpperCase().contains("FROM SALES") && sql.contains("customer_name")) {
                sql = sql.replace("FROM sales", "FROM customers c JOIN sales s ON c.id = s.customer_id");
                sql = sql.replace("sales.", "s.");
                sql = sql.replace("customer_name", "c.customer_name");
            }
        }
        return sql;
    }

    private QueryResponse createSuccessResponse(String sql, List<Map<String, Object>> results, long executionTime) {
        QueryResponse response = new QueryResponse();
        response.setSuccess(true);
        response.setMessage("Query executed successfully");
        response.setGeneratedSql(sql);
        response.setData(results);

        // Create metadata
        QueryResponse.QueryMetadata metadata = new QueryResponse.QueryMetadata();
        metadata.setRowCount(results.size());
        metadata.setExecutionTimeMs(executionTime);

        if (!results.isEmpty()) {
            String[] columnNames = results.get(0).keySet().toArray(new String[0]);
            metadata.setColumnNames(columnNames);
        } else {
            metadata.setColumnNames(new String[0]);
        }

        metadata.setQueryType(determineQueryType(sql));
        response.setMetadata(metadata);

        return response;
    }

    private QueryResponse createErrorResponse(String errorMessage) {
        QueryResponse response = new QueryResponse();
        response.setSuccess(false);
        response.setMessage(errorMessage);
        return response;
    }

    private String determineQueryType(String sql) {
        String upperSql = sql.toUpperCase().trim();
        if (upperSql.contains("GROUP BY")) {
            return "AGGREGATION";
        } else if (upperSql.contains("ORDER BY")) {
            return "SORTED_LIST";
        } else if (upperSql.contains("JOIN")) {
            return "RELATIONSHIP";
        } else {
            return "SIMPLE_SELECT";
        }
    }
}
