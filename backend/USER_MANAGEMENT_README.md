# Campus Marketplace - User Management System

## Overview

Comprehensive backend implementation for the Campus Marketplace User Management System with JWT authentication, role-based access control, audit logging, and admin management capabilities.

## 🎯 Features Implemented

### ✅ Authentication & Authorization
- **JWT-based Authentication** with access and refresh tokens
- **Secure Login** with failed attempt tracking and account lockout (5 attempts, 15-minute cooldown)
- **Role-Based Access Control** (BUYER, SELLER, ADMIN roles)
- **Token Management** with refresh token rotation and revocation
- **Multi-device Logout** support

### ✅ User Profile Management
- **View Profile** - Get user profile information
- **Update Profile** - Modify personal information, student details, preferences
- **Change Password** - Secure password change with current password verification
- **Account Deactivation** - Soft delete with 30-day recovery period

### ✅ Password Management
- **Forgot Password** - Email-based password reset flow
- **Reset Password** - Secure token-based password reset (1-hour expiration)
- **Email Verification** - Token-based email verification (24-hour expiration)

### ✅ Admin User Management
- **Search & Filter Users** - Advanced search with pagination, role, status filters
- **Create Users** - Admin can create user accounts with temporary passwords
- **Update Users** - Modify user information, roles, verification status
- **Suspend/Reactivate** - Account suspension with reason tracking
- **Delete Users** - Soft delete with audit trail
- **Bulk Operations** - Perform actions on up to 100 users at once

### ✅ Security Features
- **Audit Logging** - Comprehensive tracking of all user actions and admin operations
- **Login Attempt Tracking** - Failed login monitoring and account lockout
- **Security Events** - Severity-based security event logging (INFO, WARNING, ERROR, CRITICAL)
- **Account Action History** - Track all account status changes with admin accountability

### ✅ Analytics & Reporting
- **User Statistics** - Total users, breakdown by role, verification status
- **Registration Trends** - New users in last 7/30 days
- **Security Analytics** - Failed logins, security events by severity
- **Activity Metrics** - Active users, login patterns

### ✅ API Documentation
- **Swagger/OpenAPI** - Interactive API documentation
- Access at: `http://localhost:8080/api/swagger-ui/index.html`

## 📋 API Endpoints

### Authentication Endpoints
```
POST   /api/auth/register              - Register new user
POST   /api/auth/login                 - Login with credentials
POST   /api/auth/logout                - Logout (revoke refresh token)
POST   /api/auth/logout-all            - Logout from all devices
POST   /api/auth/refresh               - Refresh access token
GET    /api/auth/me                    - Get current user info
GET    /api/auth/validate              - Validate current token
POST   /api/auth/forgot-password       - Initiate password reset
POST   /api/auth/reset-password        - Reset password with token
```

### User Profile Endpoints
```
GET    /api/users/profile              - Get current user's profile
PUT    /api/users/profile              - Update profile
GET    /api/users/{userId}             - Get user by ID
POST   /api/users/change-password      - Change password
POST   /api/users/deactivate           - Deactivate account
```

### Admin User Management Endpoints
```
POST   /api/admin/users/search         - Search and filter users
GET    /api/admin/users/{userId}       - Get user details
POST   /api/admin/users                - Create new user
PUT    /api/admin/users/{userId}       - Update user
DELETE /api/admin/users/{userId}       - Delete user
POST   /api/admin/users/{userId}/suspend     - Suspend user
POST   /api/admin/users/{userId}/reactivate  - Reactivate user
POST   /api/admin/users/bulk-action    - Bulk operations
```

### Admin Analytics Endpoints
```
GET    /api/admin/analytics/dashboard  - Comprehensive dashboard data
GET    /api/admin/analytics/users      - User statistics
GET    /api/admin/analytics/security   - Security analytics
GET    /api/admin/analytics/activity   - Activity metrics
```

## 🗄️ Database Schema

### New Tables Added

#### `verification_tokens`
Stores email verification and password reset tokens
```sql
- token_id (UUID, PK)
- token (VARCHAR, unique)
- user_id (UUID, FK to users)
- token_type (ENUM: EMAIL_VERIFICATION, PASSWORD_RESET, EMAIL_CHANGE)
- expires_at (TIMESTAMP)
- used_at (TIMESTAMP)
- is_used (BOOLEAN)
- created_at (TIMESTAMP)
```

#### `audit_logs`
Comprehensive audit logging for all user and admin actions
```sql
- audit_id (UUID, PK)
- user_id (UUID, FK to users)
- username (VARCHAR)
- table_name (VARCHAR)
- record_id (UUID)
- action (VARCHAR)
- old_values (JSONB)
- new_values (JSONB)
- description (TEXT)
- ip_address (VARCHAR)
- user_agent (TEXT)
- severity (ENUM: INFO, WARNING, ERROR, CRITICAL)
- created_at (TIMESTAMP)
```

#### `login_attempts`
Track login attempts for security monitoring
```sql
- attempt_id (UUID, PK)
- username (VARCHAR)
- ip_address (VARCHAR)
- user_agent (TEXT)
- success (BOOLEAN)
- failure_reason (VARCHAR)
- device_info (VARCHAR)
- created_at (TIMESTAMP)
```

#### `account_actions`
Track account status changes (suspension, deletion, etc.)
```sql
- action_id (UUID, PK)
- user_id (UUID, FK to users)
- performed_by (UUID, FK to users)
- action_type (ENUM: SUSPEND, REACTIVATE, DELETE, ROLE_CHANGE, etc.)
- reason (TEXT)
- notes (TEXT)
- scheduled_revert_at (TIMESTAMP)
- reverted_at (TIMESTAMP)
- is_reverted (BOOLEAN)
- created_at (TIMESTAMP)
```

## 🚀 Getting Started

### Prerequisites
- Java 21
- PostgreSQL 14+
- Maven 3.8+

### Configuration

Update `application.yml`:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/campusmarketplace_db
    username: cm_app_user
    password: your_password
    
jwt:
  secret: your_secure_jwt_secret_key
  access-token:
    expiration: 3600000  # 1 hour
  refresh-token:
    expiration: 604800000  # 7 days
```

### Database Setup

Run migrations:
```bash
# Using Flyway
mvn flyway:migrate

# Or apply migration manually
psql -U cm_app_user -d campusmarketplace_db -f db/migrations/V4__user_management_tables.sql
```

### Running the Application

```bash
# Development mode
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Production mode
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

Access:
- API: `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/api/swagger-ui/index.html`

## 🧪 Testing

### Run all tests
```bash
mvn test
```

### Test specific category
```bash
# Unit tests
mvn test -Dtest=*Test

# Integration tests
mvn test -Dtest=*IntegrationTest
```

## 🔐 Security Features

### Account Lockout
- **Max Failed Attempts**: 5
- **Lockout Duration**: 15 minutes
- **Tracking Window**: Last 15 minutes

### Token Configuration
- **Access Token**: 1 hour expiration, JWT signed with HS256
- **Refresh Token**: 7 days expiration, stored in database
- **Password Reset Token**: 1 hour expiration
- **Email Verification Token**: 24 hours expiration

### Password Requirements
- Minimum 8 characters
- At least one uppercase letter
- At least one lowercase letter
- At least one digit
- At least one special character (@#$%^&+=!)

## 📊 Audit Logging

All actions are logged asynchronously to the `audit_logs` table:
- User login/logout
- Profile updates
- Password changes
- Account status changes
- Role modifications
- Admin operations

Severity levels:
- **INFO**: Normal operations
- **WARNING**: Suspicious activity
- **ERROR**: Failed operations
- **CRITICAL**: Security incidents

## 🎨 DTOs Overview

### Request DTOs
- `RegisterRequest` - User registration
- `ProfileUpdateRequest` - Profile updates
- `PasswordChangeRequest` - Password change
- `ForgotPasswordRequest` - Password reset request
- `ResetPasswordRequest` - Password reset with token
- `UserSearchRequest` - User search/filter
- `CreateUserRequest` - Admin user creation
- `UpdateUserRequest` - Admin user update
- `BulkUserActionRequest` - Bulk operations

### Response DTOs
- `AuthResponse` - Authentication response with tokens
- `UserResponse` - User profile data
- `PagedResponse<T>` - Paginated results

## 🏗️ Architecture

### Service Layer
- `AuthService` - Authentication and token management
- `UserManagementService` - Profile and admin user operations
- `EmailService` - Email notifications
- `AuditService` - Audit logging (async)
- `LoginAttemptService` - Login tracking and lockout
- `VerificationTokenService` - Token management

### Repository Layer
- Standard JPA repositories with custom queries
- Specification-based dynamic queries for search

### Security Layer
- JWT authentication filter
- Role-based authorization with `@RequireRole` annotation
- Custom authentication entry point

## 📝 Usage Examples

### User Registration
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@sjsu.edu",
    "password": "SecurePass123!",
    "firstName": "John",
    "lastName": "Doe",
    "role": "BUYER"
  }'
```

### User Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "SecurePass123!"
  }'
```

### Get Profile (with JWT)
```bash
curl -X GET http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer <access_token>"
```

### Admin Search Users
```bash
curl -X POST http://localhost:8080/api/admin/users/search \
  -H "Authorization: Bearer <admin_access_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "searchTerm": "john",
    "role": "BUYER",
    "page": 0,
    "size": 20
  }'
```

## 🐛 Troubleshooting

### Issue: Account Locked
**Solution**: Wait 15 minutes or contact admin to reset login attempts.

### Issue: Token Expired
**Solution**: Use refresh token endpoint to get new access token.

### Issue: Email Not Sending
**Solution**: Check `EmailService` configuration. Currently logs emails; integrate with SMTP for production.

## 📦 Dependencies

Key dependencies:
- Spring Boot 3.5.6
- Spring Security 6.x
- JWT (jjwt) 0.12.6
- PostgreSQL Driver
- Hibernate/JPA
- Springdoc OpenAPI 2.2.0
- Lombok

## 🔮 Future Enhancements

- [ ] Multi-factor Authentication (2FA)
- [ ] Social Login (Google, Facebook OAuth)
- [ ] Profile Picture Upload to cloud storage
- [ ] Email template engine (Thymeleaf)
- [ ] Real-time notifications (WebSocket)
- [ ] Advanced user analytics dashboard
- [ ] Export user data (GDPR compliance)
- [ ] Rate limiting at API gateway level

## 📄 License

Apache License 2.0

## 👥 Contributors

Command Line Commando Team - CMPE 202 Fall 2025

## 📞 Support

For issues or questions:
- Email: support@campusmarketplace.edu
- GitHub Issues: [Project Repository]

