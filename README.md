## 👥 Team - Commandline Commandos
# Campus Marketplace - CMPE-202 Project

A full-stack campus marketplace application built with Spring Boot backend and React frontend, featuring JWT authentication, role-based access control, and PostgreSQL database.

---

## 🔗 Links

- **Project Journal:**
- **Team Jira Board:**

### Key Contribution Area Per Member
- **Vineet Kumar** - Authentication & Authorization Backend System
    1. Authentication and Authorization Backend System
    2. UML Diagram Design for Authentication and Authorization System
    3. API Development for Registration, Login, Validate Token, Refresh Token, User Info and Logout functionality
    4. Unit tests for Authentication and Authorization module
    5. Initial Postgres database SQL schema model files to setup database across different systems and seed sample data
- **Sakshat Patil** - 
- **Wilson Huang** - 
- **Lam Nguyen** - 

---

## 🚀 Project Quick Start

### **Prerequisites**
- Java 21+
- Maven 3.6+
- PostgreSQL 12+
- Node.js 18+

### **Start Backend**
```bash
cd backend
mvn clean compile
mvn spring-boot:run
```
**Backend runs on:** `http://localhost:8080`

### **Start Frontend**
```bash
cd frontend
npm install
npm run dev
```
**Frontend runs on:** `http://localhost:5173`

---

## 📁 Project Structure

```
campus-marketplace/
├── backend/                 # Spring Boot API
│   ├── src/main/java/      # Java source code
│   ├── src/test/java/      # Unit tests
│   └── pom.xml            # Maven configuration
├── frontend/               # React application
│   ├── src/               # React source code
│   └── package.json       # Node.js dependencies
├── documentation/          # Project documentation
├── sql_files/             # Database schema and seed data
└── README.md              # This file
```

---

## 🔧 Database Setup

1. **Install PostgreSQL**
2. **Create Database:**
   ```sql
   CREATE DATABASE campus_marketplace;
   ```
3. **Run Schema:**
   ```bash
   psql -U your_username -d campus_marketplace -f sql_files/schema_postgres.sql
   ```
4. **Load Test Data:**
   ```bash
   psql -U your_username -d campus_marketplace -f sql_files/seed_data.sql
   ```

---

## 🧪 Testing

### **Test Users**
| Role | Username | Password | Email |
|------|----------|----------|--------|
| **Student** | `student` | `password123` | `student@sjsu.edu` |
| **Admin** | `admin` | `password123` | `admin@sjsu.edu` |

### **Quick Health Check**
```bash
# Test backend
curl http://localhost:8080/
curl http://localhost:8080/api/auth/validate

# Test frontend
curl http://localhost:5173/
```

---

## 📚 Documentation

- **Authentication & Authorization:** [`documentation/Authentication_Authorization_ReadMe.md`](documentation/Authentication_Authorization_ReadMe.md)
- **Backend API:** [`backend/README.md`](backend/README.md)
- **Database Schema:** [`sql_files/schema_postgres.sql`](sql_files/schema_postgres.sql)
- **Test Data:** [`sql_files/seed_data.sql`](sql_files/seed_data.sql)

---

## 🏗️ Architecture

### **Backend (Spring Boot)**
- **Framework:** Spring Boot 3.5.6
- **Security:** JWT Authentication with Spring Security
- **Database:** PostgreSQL with Hibernate ORM
- **API:** RESTful endpoints with role-based access control

### **Frontend (React)**
- **Framework:** React 18 with TypeScript
- **Build Tool:** Vite
- **Styling:** CSS modules
- **State Management:** React hooks

### **Database**
- **Engine:** PostgreSQL 17.5
- **ORM:** Hibernate 6.6.29
- **Schema:** Single table inheritance for User/Student/Admin

---

## 🔒 Security Features

- **JWT Authentication** with HS512 signing
- **Role-Based Access Control** (Student/Admin roles)
- **Refresh Token Management** (7-day expiration)
- **Password Hashing** with BCrypt
- **CORS Configuration** for frontend integration
- **Input Validation** with Bean Validation

---

## 📊 Technology Stack

| Component | Technology | Version |
|-----------|------------|---------|
| **Backend** | Spring Boot | 3.5.6 |
| **Security** | Spring Security | 6.2.11 |
| **Database** | PostgreSQL | 17.5 |
| **ORM** | Hibernate | 6.6.29 |
| **Frontend** | React | 18 |
| **Build Tool** | Maven/Vite | 3.9.10/5.0 |
| **Java** | OpenJDK | 21 |

---

## 🚀 Deployment

### **Development**
```bash
# Backend
cd backend && mvn spring-boot:run

# Frontend
cd frontend && npm run dev
```

### **Production**
```bash
# Build backend JAR
cd backend && mvn clean package
java -jar target/campusmarketplace-0.0.1-SNAPSHOT.jar

# Build frontend
cd frontend && npm run build
```

---

## 🐛 Troubleshooting

### **Common Issues**

1. **Port 8080 already in use:**
   ```bash
   lsof -i :8080 && kill -9 <PID>
   ```

2. **Database connection failed:**
   - Check PostgreSQL is running
   - Verify database credentials
   - Ensure database exists

3. **ClassNotFoundException:**
   ```bash
   mvn clean compile
   ```

---

## 📝 License

This project is part of CMPE-202 coursework at San Jose State University.

---
