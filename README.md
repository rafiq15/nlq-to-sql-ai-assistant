# NLQ-to-SQL AI Assistant

🤖 An AI-powered Business Intelligence Assistant that converts natural language queries into SQL using Ollama/Llama 3.2. Built with Spring Boot, PostgreSQL, and modern web technologies for intuitive data analysis.

## ✨ Key Features

- **AI-Powered SQL Generation**: Natural language to SQL conversion using Llama 3.2
- **Modern Web UI**: Responsive Bootstrap 5 interface with 150+ query examples
- **RESTful API**: Full REST endpoints for programmatic access
- **Real-time Analytics**: Interactive dashboards and query execution
- **Security First**: SQL injection protection and input validation
- **Production Ready**: Comprehensive monitoring, caching, and error handling

## 🛠 Tech Stack

**Backend**: Spring Boot 3.5.5, Java 21, PostgreSQL 17.4, Hibernate 6.6.26  
**AI**: Ollama with Llama 3.2 model, Spring AI integration  
**Frontend**: Thymeleaf, Bootstrap 5, jQuery, Font Awesome  
**Tools**: Gradle, Docker, Spring Boot Actuator

## 📊 Database Schema

**Products**: 24 items across Electronics, Appliances, Furniture, Accessories  
**Customers**: 10 profiles with Premium/Standard/Basic segments  
**Sales**: 40+ records across Q1-Q3 2025 with regional data

```sql
CREATE TABLE products (id, product_name, category, price, description, manufacturer);
CREATE TABLE customers (id, customer_name, email, segment, city, country);
CREATE TABLE sales (id, customer_id, product_id, quantity, sale_date, revenue, region);
```

## 🚀 Quick Start

### Prerequisites
- **Java 21+** ([Download](https://adoptium.net/))
- **PostgreSQL 17.4** ([Download](https://www.postgresql.org/download/))
- **Ollama** with Llama 3.2 ([Download](https://ollama.com/))

### Setup
```bash
# 1. Database
createdb bi_database

# 2. Install Ollama and pull model
ollama pull llama3.2

# 3. Run application
git clone https://github.com/rafiq15/nlq-to-sql-ai-assistant.git
cd nlq-to-sql-ai-assistant
./gradlew bootRun
```

**Access**: http://localhost:9080

### Configuration
Update `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/bi_database
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.model=llama3.2
```

## 📝 API Reference

### Endpoints
- **GET /**: Main UI interface
- **GET /queries**: Query examples page  
- **POST /api/query**: REST API endpoint
- **GET /actuator/health**: Health check

### REST API Usage
```bash
curl -X POST http://localhost:9080/api/query \
  -H "Content-Type: application/json" \
  -d '{"query": "Show top 5 products by revenue"}'
```

**Response:**
```json
{
  "success": true,
  "generatedSql": "SELECT p.product_name, SUM(s.revenue)...",
  "data": [...],
  "metadata": {"rowCount": 5, "executionTimeMs": 45}
}
```

## 🔍 Example Queries

**Revenue Analysis:**
- "Show me the top 5 products by revenue last quarter"
- "What is the total revenue by category this year?"
- "Show monthly revenue trends"

**Customer Analytics:**  
- "Which customers bought the most products?"
- "What is the average order value by customer segment?"
- "Show customers from Premium segment"

**Product Performance:**
- "Best selling products in Electronics category"
- "Products with highest average selling price"

**Regional & Sales Analysis:**
- "Sales trends by region"
- "Which sales person has the highest revenue?"

*Visit `/queries` for 150+ categorized examples*

## 🐳 Docker Deployment

```yaml
# docker-compose.yml
version: '3.8'
services:
  postgres:
    image: postgres:17-alpine
    environment:
      POSTGRES_DB: bi_database
      POSTGRES_USER: bi_user
      POSTGRES_PASSWORD: your_password
    ports: ["5432:5432"]
    volumes: ["postgres_data:/var/lib/postgresql/data"]

  ollama:
    image: ollama/ollama:latest
    ports: ["11434:11434"]
    volumes: ["ollama_data:/root/.ollama"]
    
  app:
    build: .
    ports: ["9080:9080"]
    depends_on: [postgres, ollama]
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/bi_database
      - SPRING_AI_OLLAMA_BASE_URL=http://ollama:11434

volumes:
  postgres_data:
  ollama_data:
```

```bash
docker-compose up -d
docker exec ollama-container ollama pull llama3.2
```

## 🧪 Testing

```bash
# Run tests
./gradlew test

# Test API endpoint
curl -X POST http://localhost:9080/api/query \
  -H "Content-Type: application/json" \
  -d '{"query": "List all customers"}'
```

## 📊 Performance & Monitoring

**Response Times:**
- Simple queries: 200-400ms
- Complex aggregations: 500-1200ms  
- Cached results: 15-50ms

**Health Monitoring:**
- `/actuator/health` - Application health
- `/actuator/metrics` - Performance metrics
- Built-in Ollama connectivity checks

## 🔧 Configuration

**Key Properties:**
```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/bi_database

# AI Model  
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.model=llama3.2
spring.ai.ollama.options.temperature=0.1

# Caching
spring.cache.caffeine.spec=maximumSize=100,expireAfterWrite=1h
```

## 🚀 Production Deployment

**Docker Compose** (recommended):
```bash
docker-compose up -d
docker exec ollama-container ollama pull llama3.2
```

**Traditional Server**:
- Ubuntu 20.04+ with Java 21
- PostgreSQL 17.4
- Ollama service with systemd
- Nginx reverse proxy (optional)

**System Requirements:**
- **Development**: 16GB RAM, 4 cores, 50GB storage
- **Production**: 32GB RAM, 8+ cores, 100GB SSD

## 🤝 Contributing

1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

MIT License - see [LICENSE](LICENSE) file for details.

---

**Built with ❤️ using Spring Boot, Ollama, and Llama 3.2**

[![Java](https://img.shields.io/badge/Java-21-orange)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-brightgreen)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17.4-blue)](https://postgresql.org/)
[![Ollama](https://img.shields.io/badge/Ollama-Llama%203.2-purple)](https://ollama.com/)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)
