# Database Configuration Guide

## 🎯 **Environment-Specific Database Setup**

### **Unit Tests** (H2 In-Memory)
```yaml
# application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password: 
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop
```

**Use Case**: Fast unit tests, disposable data  
**Limitations**: No PostgreSQL functions (ts_rank, similarity, etc.)

---

### **Development** (PostgreSQL Local)
```yaml
# application.yml (dev profile)
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/marketplace_db
    username: marketplace_user
    password: marketplace_pass
    driver-class-name: org.postgresql.Driver
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: validate  # Use Flyway for migrations
    properties:
      hibernate:
        default_schema: public
```

**Start PostgreSQL Locally**:
```bash
# Option 1: Docker Compose
docker-compose up -d postgres

# Option 2: Docker run
docker run -d \
  --name marketplace-postgres \
  -e POSTGRES_DB=marketplace_db \
  -e POSTGRES_USER=marketplace_user \
  -e POSTGRES_PASSWORD=marketplace_pass \
  -p 5432:5432 \
  postgres:16-alpine

# Run migrations
cd backend
mvn flyway:migrate
```

---

### **Production** (PostgreSQL Cloud)
```yaml
# application-prod.yml
spring:
  datasource:
    url: ${DATABASE_URL}  # From environment variable
    username: ${DB_USER}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  jpa:
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        format_sql: false
        show_sql: false
```

**Environment Variables**:
```bash
DATABASE_URL=jdbc:postgresql://your-db-host:5432/marketplace_db
DB_USER=your_user
DB_PASSWORD=your_secure_password
```

---

## 🔧 **Required PostgreSQL Extensions**

Your search features require these PostgreSQL extensions:

```sql
-- Enable in your database
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";    -- UUID generation
CREATE EXTENSION IF NOT EXISTS "pg_trgm";      -- Fuzzy matching
CREATE EXTENSION IF NOT EXISTS "pgcrypto";     -- Encryption
CREATE EXTENSION IF NOT EXISTS "btree_gin";    -- Better indexing
```

**These are already in your V1 migration**, but verify they're enabled:

```bash
# Connect to PostgreSQL
psql -U marketplace_user -d marketplace_db

# Check extensions
\dx

# You should see:
# uuid-ossp, pg_trgm, pgcrypto, btree_gin
```

---

## ⚠️ **NEVER USE H2 IN PRODUCTION!**

### Why H2 Fails in Production

1. **No Full-Text Search**
   ```sql
   -- ❌ FAILS with H2
   SELECT *, ts_rank(search_vector, plainto_tsquery('laptop')) 
   FROM products;
   -- Error: Function "TS_RANK" not found
   ```

2. **No Fuzzy Matching**
   ```sql
   -- ❌ FAILS with H2
   SELECT * FROM products 
   WHERE similarity(title, 'laptop') > 0.3;
   -- Error: Function "SIMILARITY" not found
   ```

3. **Data Loss**
   - H2 is in-memory by default
   - Restarts = data loss
   - Not suitable for production

4. **Performance**
   - No proper indexing for large datasets
   - Poor concurrent access handling
   - Limited query optimization

---

## ✅ **Production Deployment Checklist**

### Before Deploying

- [ ] PostgreSQL 14+ installed and running
- [ ] Extensions enabled (uuid-ossp, pg_trgm, pgcrypto, btree_gin)
- [ ] Flyway migrations run successfully
- [ ] Connection pool configured (HikariCP settings)
- [ ] Indexes created (via V3 migration)
- [ ] Database backups configured
- [ ] Monitoring enabled (pg_stat_statements)

### Database Performance Tuning

```sql
-- Check if indexes are being used
EXPLAIN ANALYZE 
SELECT * FROM products 
WHERE search_vector @@ plainto_tsquery('english', 'laptop');

-- Should show:
-- -> Bitmap Index Scan on idx_products_search_vector

-- Check index sizes
SELECT 
    tablename,
    indexname,
    pg_size_pretty(pg_relation_size(indexrelid)) AS index_size
FROM pg_indexes
WHERE schemaname = 'public'
ORDER BY pg_relation_size(indexrelid) DESC;
```

---

## 🧪 **Testing Against PostgreSQL**

### Option 1: TestContainers (Recommended for CI/CD)

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <version>1.19.3</version>
    <scope>test</scope>
</dependency>
```

```java
@SpringBootTest
@Testcontainers
class SearchServiceIntegrationTest {
    
    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = 
        new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test")
            .withInitScript("test-schema.sql");
    
    @Test
    void testFullTextSearch() {
        // This will run against real PostgreSQL!
        // No more "ts_rank not found" errors
    }
}
```

### Option 2: Local PostgreSQL for Integration Tests

```bash
# Start test database
docker run -d \
  --name test-postgres \
  -e POSTGRES_DB=test_db \
  -e POSTGRES_USER=test \
  -e POSTGRES_PASSWORD=test \
  -p 5433:5432 \
  postgres:16-alpine

# Run tests against it
mvn test -Dspring.profiles.active=integration
```

```yaml
# application-integration.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/test_db
    username: test
    password: test
```

---

## 📊 **Database Monitoring**

### Key Metrics to Monitor

```sql
-- Active connections
SELECT count(*) FROM pg_stat_activity 
WHERE datname = 'marketplace_db';

-- Slow queries
SELECT pid, now() - pg_stat_activity.query_start AS duration, query 
FROM pg_stat_activity 
WHERE (now() - pg_stat_activity.query_start) > interval '5 seconds';

-- Index usage
SELECT schemaname, tablename, indexname, idx_scan, idx_tup_read, idx_tup_fetch
FROM pg_stat_user_indexes
WHERE schemaname = 'public'
ORDER BY idx_scan DESC;

-- Cache hit ratio (should be > 95%)
SELECT 
  sum(heap_blks_read) as heap_read,
  sum(heap_blks_hit) as heap_hit,
  sum(heap_blks_hit) / (sum(heap_blks_hit) + sum(heap_blks_read)) as ratio
FROM pg_statio_user_tables;
```

---

## 🔐 **Security Best Practices**

### Connection Security

```yaml
spring:
  datasource:
    url: jdbc:postgresql://host:5432/db?sslmode=require
    hikari:
      connection-test-query: SELECT 1
      validation-timeout: 5000
```

### User Permissions

```sql
-- Create application user with limited permissions
CREATE USER marketplace_app WITH PASSWORD 'secure_password';

-- Grant only necessary permissions
GRANT CONNECT ON DATABASE marketplace_db TO marketplace_app;
GRANT USAGE ON SCHEMA public TO marketplace_app;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO marketplace_app;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA public TO marketplace_app;

-- Flyway needs more permissions (separate user)
CREATE USER marketplace_flyway WITH PASSWORD 'flyway_password';
GRANT ALL PRIVILEGES ON DATABASE marketplace_db TO marketplace_flyway;
```

---

## 🚀 **Quick Start Commands**

### Development Setup

```bash
# 1. Start PostgreSQL
docker-compose up -d postgres

# 2. Wait for PostgreSQL to be ready
docker-compose logs -f postgres  # Wait for "ready to accept connections"

# 3. Run migrations
cd backend
mvn flyway:migrate

# 4. Verify extensions
psql -U marketplace_user -d marketplace_db -c "\dx"

# 5. Start backend
mvn spring-boot:run
```

### Verify Full-Text Search

```bash
# Connect to database
psql -U marketplace_user -d marketplace_db

# Check if full-text search works
SELECT title, ts_rank(search_vector, plainto_tsquery('english', 'laptop')) as rank
FROM products
WHERE search_vector @@ plainto_tsquery('english', 'laptop')
ORDER BY rank DESC
LIMIT 5;

# Should return results, not errors!
```

---

## ❓ **FAQ**

### Q: Can I use H2 for development?
**A**: Not recommended. You'll miss PostgreSQL-specific features and may have different behavior in production.

### Q: What about MySQL or MongoDB?
**A**: No. Your code uses PostgreSQL-specific features:
- Full-text search (`tsvector`, `@@`, `ts_rank`)
- `pg_trgm` extension for fuzzy matching
- JSONB type for attributes
- GIN indexes

### Q: How do I backup the database?
**A**: 
```bash
# Backup
pg_dump -U marketplace_user marketplace_db > backup.sql

# Restore
psql -U marketplace_user marketplace_db < backup.sql
```

### Q: How do I reset the database?
**A**:
```bash
# Drop and recreate
psql -U postgres -c "DROP DATABASE marketplace_db;"
psql -U postgres -c "CREATE DATABASE marketplace_db OWNER marketplace_user;"

# Run migrations
mvn flyway:clean flyway:migrate
```

---

## 📚 **Additional Resources**

- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [pg_trgm Extension](https://www.postgresql.org/docs/current/pgtrgm.html)
- [Full-Text Search](https://www.postgresql.org/docs/current/textsearch.html)
- [TestContainers](https://www.testcontainers.org/)
- [Flyway Migrations](https://flywaydb.org/)

---

**Remember**: PostgreSQL is NOT optional for this project - it's REQUIRED for the search features to work!

