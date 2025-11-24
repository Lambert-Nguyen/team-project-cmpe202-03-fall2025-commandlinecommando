# Quick Start Guide - User Management System

## 🚀 Get Started in 5 Minutes

### Prerequisites
- Java 21
- Maven 3.8+
- PostgreSQL 14+
- Your favorite IDE (IntelliJ IDEA recommended)

---

## Step 1: Database Setup (2 minutes)

```bash
# Start PostgreSQL (if not running)
# macOS
brew services start postgresql@14

# Linux
sudo systemctl start postgresql

# Windows
# Use Services app to start PostgreSQL

# Create database and user
psql -U postgres <<EOF
CREATE DATABASE campusmarketplace_db;
CREATE USER cm_app_user WITH PASSWORD 'your_secure_password';
GRANT ALL PRIVILEGES ON DATABASE campusmarketplace_db TO cm_app_user;
\c campusmarketplace_db
GRANT ALL ON SCHEMA public TO cm_app_user;
EOF

# Run migrations
cd /path/to/project
psql -U cm_app_user -d campusmarketplace_db -f db/migrations/V1__campus_marketplace_core_schema.sql
psql -U cm_app_user -d campusmarketplace_db -f db/migrations/V4__user_management_tables.sql
```

---

## Step 2: Configure Application (1 minute)

Create `backend/src/main/resources/application-local.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/campusmarketplace_db
    username: cm_app_user
    password: your_secure_password

jwt:
  secret: myVerySecureJWTSecretKeyThatIsAtLeast256BitsLong123456789!
```

---

## Step 3: Build and Run (1 minute)

```bash
cd backend

# Build the project
mvn clean install -DskipTests

# Run the application
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Or use the jar
java -jar target/campusmarketplace-0.0.1-SNAPSHOT.jar --spring.profiles.active=local
```

Application starts at: `http://localhost:8080/api`

---

## Step 4: Test the API (1 minute)

### Register a User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@sjsu.edu",
    "password": "Test1234!",
    "firstName": "Test",
    "lastName": "User",
    "role": "BUYER"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "Test1234!"
  }'
```

Save the `accessToken` from the response!

### Get Profile
```bash
curl -X GET http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer YOUR_ACCESS_TOKEN"
```

---

## Step 5: Explore API Documentation

Open in browser: `http://localhost:8080/api/swagger-ui/index.html`

---

## 🎯 Quick Reference

### Main Endpoints
```
Authentication:
  POST   /api/auth/register
  POST   /api/auth/login
  POST   /api/auth/logout
  POST   /api/auth/refresh
  POST   /api/auth/forgot-password
  POST   /api/auth/reset-password

Profile:
  GET    /api/users/profile
  PUT    /api/users/profile
  POST   /api/users/change-password
  POST   /api/users/deactivate

Admin (requires ADMIN role):
  POST   /api/admin/users/search
  POST   /api/admin/users
  PUT    /api/admin/users/{userId}
  DELETE /api/admin/users/{userId}
  POST   /api/admin/users/{userId}/suspend
  POST   /api/admin/users/bulk-action
  
Analytics (requires ADMIN role):
  GET    /api/admin/analytics/dashboard
  GET    /api/admin/analytics/users
```

### Default Credentials

Create an admin user manually in database:
```sql
-- Run this in psql
INSERT INTO users (user_id, username, email, password_hash, first_name, last_name, role, university_id, is_active, verification_status)
VALUES (
  uuid_generate_v4(),
  'admin',
  'admin@sjsu.edu',
  '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5K8TQ.SvTLYhq', -- Password: Admin123!
  'Admin',
  'User',
  'ADMIN',
  (SELECT university_id FROM universities LIMIT 1),
  true,
  'VERIFIED'
);
```

---

## 🔧 Common Issues

### Issue: Database connection failed
**Solution**: 
- Check PostgreSQL is running: `pg_isready`
- Verify credentials in `application-local.yml`
- Ensure database exists: `psql -l | grep campusmarketplace`

### Issue: Port 8080 already in use
**Solution**: 
- Kill the process: `lsof -ti:8080 | xargs kill -9`
- Or change port in `application.yml`: `server.port: 8081`

### Issue: JWT token error
**Solution**:
- Ensure JWT secret is at least 256 bits (32 characters)
- Check token expiration time
- Verify token format: `Bearer <token>`

### Issue: Rate limit exceeded
**Solution**:
- Wait 1 minute
- Or disable temporarily: Add to `application.yml`:
  ```yaml
  rate:
    limiting:
      enabled: false
  ```

---

## 📚 Next Steps

1. **Read Full Documentation**: `/backend/USER_MANAGEMENT_README.md`
2. **Review Implementation**: `/backend/IMPLEMENTATION_SUMMARY.md`
3. **Explore API**: Swagger UI at `/swagger-ui/index.html`
4. **Run Tests**: `mvn test`
5. **Check Logs**: `tail -f logs/campus-marketplace.log`

---

## 🎨 IDE Setup (IntelliJ IDEA)

1. **Import Project**:
   - File → Open → Select `backend/pom.xml`
   - Import as Maven project

2. **Enable Lombok**:
   - File → Settings → Plugins → Install "Lombok"
   - Enable annotation processing

3. **Run Configuration**:
   - Run → Edit Configurations
   - Add new Spring Boot configuration
   - Main class: `CampusmarketplaceApplication`
   - VM options: `-Dspring.profiles.active=local`

4. **Database Tool**:
   - View → Tool Windows → Database
   - Add PostgreSQL datasource
   - Test connection

---

## 🐛 Debug Mode

Run with debug logging:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--logging.level.com.commandlinecommandos=DEBUG"
```

---

## ✅ Verification Checklist

- [ ] Application starts without errors
- [ ] Can register a new user
- [ ] Can login and get JWT token
- [ ] Can access profile endpoint with token
- [ ] Swagger UI loads correctly
- [ ] Database tables created
- [ ] Admin can search users
- [ ] Rate limiting works (try 6 login attempts)
- [ ] Audit logs are created

---

## 📞 Need Help?

- **Documentation**: Check `/backend/USER_MANAGEMENT_README.md`
- **API Docs**: `http://localhost:8080/api/swagger-ui/index.html`
- **Logs**: Check `logs/campus-marketplace.log`
- **Database**: Use pgAdmin or DBeaver to inspect tables

---

**Happy Coding! 🚀**

