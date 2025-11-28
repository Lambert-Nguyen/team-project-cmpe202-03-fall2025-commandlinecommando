# Campus Marketplace

A secure and scalable marketplace platform for university students to buy and sell items within their campus community.

## Team Name
**Commandline Commandos**

## Team Members
1. Vineet Kumar
2. Sakshat Patil
3. Wilson Huang
4. Lam Nguyen

## Technology Stack

### Backend
- **Java 17** with **Spring Boot 3.5.6**
- **Spring Security** with JWT authentication
- **Spring Data JPA** with Hibernate
- **PostgreSQL** (production) / **H2** (development)
- **Flyway** for database migrations
- **Redis** for caching and session management
- **Maven** for dependency management

### Frontend
- **React 18** with **TypeScript**
- **Vite** for build tooling
- **React Router** for navigation
- **Axios** for API communication

### AI Integration
- **OpenAI GPT-4 Mini** integration
- **Spring Boot** microservice for AI chat
- **Java 17** with OpenAI SDK

### Infrastructure
- **Docker** and **Docker Compose**
- **PostgreSQL 15+**
- **pgAdmin** for database management
- **Redis** for caching

## Project Structure

```
├── backend/                  # Main Spring Boot application (monolith)
│   ├── src/main/java/       # Application source code
│   │   ├── controller/      # REST API controllers
│   │   ├── service/         # Business logic layer
│   │   ├── repository/      # Data access layer
│   │   ├── model/           # JPA entities
│   │   ├── dto/             # Data transfer objects
│   │   ├── security/        # Security configuration & JWT
│   │   └── exception/       # Exception handling
│   ├── src/main/resources/  # Configuration files
│   │   ├── db/migration/    # Flyway migration scripts (V1-V8)
│   │   ├── application.yml  # Application configuration
│   │   └── test-products-h2.sql  # Test data for H2
│   └── pom.xml              # Maven dependencies
│
├── frontend/                 # React/Vite frontend application
│   ├── src/
│   │   ├── components/      # React components
│   │   ├── services/        # API service layer
│   │   └── utils/           # Utility functions
│   └── package.json
│
├── ai-integration-server/    # AI chat microservice
│   ├── src/main/java/       # AI service source code
│   └── pom.xml              # Maven dependencies
│
├── db/                       # Database utilities
│   └── scripts/             # Backup, monitoring, utility scripts
│
├── documentation/            # All project documentation
│   ├── API_TESTING.md                    # Curl tests for all endpoints
│   ├── AI_INTEGRATION.md                 # AI service documentation
│   ├── DATABASE_STRUCTURE.md             # Database schema info
│   ├── DOCKER_DEPLOYMENT.md              # Docker deployment guide
│   ├── Authentication_Authorization_ReadMe.md  # Auth documentation
│   ├── SECURITY_IMPROVEMENTS_SUMMARY_10_06_2025.md  # Security updates
│   └── TEAM_USAGE_SUMMARY.md             # Team workflow guide
│
├── docker-compose.yml        # PostgreSQL, pgAdmin, Redis services
├── setup-database.sh         # Database quick start script
└── .env.template            # Environment variables template
```

## Quick Start

### Prerequisites
- Java 17 or higher
- Node.js 18+ and npm
- Docker and Docker Compose
- PostgreSQL 15+ (for production) or Docker

### 1. Database Setup

#### Using Docker (Recommended)
```bash
# Copy environment template
cp .env.template .env  # Edit with your passwords

# Start PostgreSQL, pgAdmin, and Redis
docker-compose up -d

# Or use the quick setup script
./setup-database.sh
```

#### Manual PostgreSQL Setup
See [documentation/DATABASE_STRUCTURE.md](documentation/DATABASE_STRUCTURE.md)

### 2. Backend Setup

#### Development Mode (H2 Database)
```bash
cd backend
./mvnw spring-boot:run
```

The backend will start on **http://localhost:8080/api** with an H2 in-memory database.

#### Production Mode (PostgreSQL)
```bash
cd backend

# Set database password
export DB_APP_PASSWORD='CampusMarket2024!'

# Build and run
./mvnw clean package -DskipTests
java -jar target/campusmarketplace-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### 3. Frontend Setup
```bash
cd frontend
npm install
npm run dev
```

The frontend will start on **http://localhost:5173**

### 4. AI Integration Server (Optional)
```bash
cd ai-integration-server

# Copy environment file
cp .env.example .env
# Add your OpenAI API key to .env

# Run the server
./mvnw spring-boot:run
```

The AI service will start on **http://localhost:3001**

## Access Services

- **Main Application API**: http://localhost:8080/api
- **Frontend Application**: http://localhost:5173
- **AI Chat Service**: http://localhost:3001/api
- **H2 Console** (dev only): http://localhost:8080/api/h2-console
  - JDBC URL: `jdbc:h2:mem:campusmarketplace`
  - Username: `sa`
  - Password: `password`
- **pgAdmin** (if using Docker): http://localhost:8080
  - Email: admin@campusmarketplace.com
  - Password: (from .env file)

## Features

### Authentication & Authorization
- JWT-based authentication with refresh tokens
- Role-based access control (BUYER, SELLER, ADMIN)
- Secure password hashing with BCrypt
- Session management with Redis

### Listing Management
- Create, read, update, delete listings
- Advanced search with filters (category, price, condition, location)
- Mark listings as sold
- Image upload support (planned)
- Pagination support

### Communication System
- Real-time messaging between buyers and sellers
- Conversation management per listing
- Message read/unread tracking
- Unread message count

### User Management
- User registration and profile management
- Role-based permissions
- Profile updates

### AI Integration
- AI-powered chat assistance
- Product recommendations
- Price comparison insights
- Marketplace analytics

### Admin Features
- Report management system
- Content moderation
- User management
- Analytics dashboard

## API Endpoints

### Authentication (`/api/auth/*`)
- `POST /auth/register` - Register new user
- `POST /auth/login` - Login user
- `POST /auth/refresh` - Refresh access token
- `POST /auth/logout` - Logout user

### Listings (`/api/listings/*`)
- `POST /listings` - Create listing (SELLER/ADMIN)
- `GET /listings/search` - Search listings with filters
- `GET /listings/{id}` - Get listing by ID
- `GET /listings/my-listings` - Get current user's listings
- `PUT /listings/{id}` - Update listing (owner only)
- `DELETE /listings/{id}` - Delete listing (owner only)
- `PUT /listings/{id}/mark-sold` - Mark listing as sold

### Communication (`/api/conversations/*`, `/api/messages/*`)
- `POST /conversations` - Create/get conversation
- `GET /conversations` - Get all user's conversations
- `GET /conversations/{id}` - Get specific conversation
- `DELETE /conversations/{id}` - Delete conversation
- `POST /messages/{conversationId}` - Send message
- `GET /messages/{conversationId}` - Get conversation messages
- `PUT /messages/{conversationId}/mark-read` - Mark as read
- `GET /messages/unread` - Get unread messages
- `DELETE /messages/{messageId}` - Delete message

### Users (`/api/users/*`)
- `GET /users/profile` - Get current user profile
- `PUT /users/profile` - Update user profile

For detailed API testing examples, see [documentation/API_TESTING.md](documentation/API_TESTING.md)

## Database

### Flyway Migrations
All database migrations are located in `backend/src/main/resources/db/migration/`:
- V1: Core schema (users, listings)
- V2: Seed demo data
- V3: API optimization indexes
- V4: User management tables
- V5: Search & discovery features
- V6: Communication & chat tables
- V7: Notification preferences
- V8: Listing API tables

### Database Profiles
- **Development (`dev`)**: H2 in-memory database with `ddl-auto: create-drop`
- **Production (`prod`)**: PostgreSQL with Flyway migrations
- **Test (`test`)**: Isolated H2 database for testing

### Database Scripts
```bash
# Health check
./db/scripts/monitor.sh --health

# Create backup
./db/scripts/backup.sh

# Restore from backup
./db/scripts/restore.sh --latest

# Test connection
./db/scripts/test-connection.sh
```

## Documentation

All documentation is located in the [documentation/](documentation/) folder:

- **[API Testing Guide](documentation/API_TESTING.md)** - Curl commands for all API endpoints
- **[AI Integration](documentation/AI_INTEGRATION.md)** - AI service setup and usage
- **[Database Structure](documentation/DATABASE_STRUCTURE.md)** - Database schema and migrations
- **[Docker Deployment](documentation/DOCKER_DEPLOYMENT.md)** - Docker setup and deployment
- **[Authentication & Authorization](documentation/Authentication_Authorization_ReadMe.md)** - Security implementation
- **[Security Improvements](documentation/SECURITY_IMPROVEMENTS_SUMMARY_10_06_2025.md)** - Recent security updates
- **[Team Usage Guide](documentation/TEAM_USAGE_SUMMARY.md)** - Team workflow and practices

## Development Workflow

### 1. Setup Environment
```bash
# Clone repository and setup database
./setup-database.sh
cp .env.template .env  # Update with your values
```

### 2. Start Development
```bash
# Terminal 1: Backend (H2 database)
cd backend
./mvnw spring-boot:run

# Terminal 2: Frontend
cd frontend
npm install
npm run dev
```

### 3. Test with Production Database
```bash
cd backend
export DB_APP_PASSWORD='CampusMarket2024!'
./mvnw spring-boot:run -Dspring.profiles.active=prod
```

### 4. Run Tests
```bash
# Backend tests
cd backend
./mvnw test

# Frontend tests
cd frontend
npm test
```

## Troubleshooting

### Common Issues

**Database Connection Failed**
```bash
# Check if Docker containers are running
docker ps

# Restart database services
./setup-database.sh restart
```

**Port Conflicts**
- Backend: Default port 8080
- Frontend: Default port 5173
- AI Service: Default port 3001
- PostgreSQL: Default port 5432

**Authentication Errors**
- Verify credentials in `.env` file
- Check JWT secret configuration in `application.yml`
- Ensure database has user records

**Build Failures**
```bash
# Clean and rebuild
cd backend
./mvnw clean install -DskipTests
```

## Security Features

- Strong password policies with BCrypt hashing
- JWT token-based authentication
- Role-based authorization (BUYER, SELLER, ADMIN)
- CORS configuration for frontend integration
- Input validation and sanitization
- SQL injection prevention with JPA
- XSS protection
- CSRF protection
- Secure session management with Redis

## Monitoring & Maintenance

### Health Monitoring
```bash
# Quick health check
./setup-database.sh status

# Database monitoring
./db/scripts/monitor.sh --health

# Connection monitoring
./db/scripts/monitor.sh --connections
```

### Automated Backups
- Daily backups at 2:00 AM
- 7 days local retention
- Integrity verification with checksums
- Point-in-time recovery capability

## License

Part of the Campus Marketplace project by Commandline Commandos.

## Summary of Areas of Contributions (Per Member)

## Link to Project Journal

## Team Google Sheet or Project Board
(Product Backlog and Sprint Backlog for each Sprint)

---

[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/kvgvOCnV)
