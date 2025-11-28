# Database Structure

This directory contains database-related documentation and utility scripts for the Campus Marketplace project.

## Directory Structure

```
db/
├── scripts/             # Database utility scripts
│   ├── backup.sh
│   ├── restore.sh
│   ├── monitor.sh
│   ├── test-connection.sh
│   ├── validate-connection.sh
│   └── crontab.example
└── docs/                # Database documentation
```

## Migration Files Location

**All SQL migration files are stored in the backend application:**

```
backend/src/main/resources/db/migration/
├── V1__campus_marketplace_core_schema.sql
├── V2__seed_demo_data.sql
├── V3__api_optimization_indexes.sql
├── V4__user_management_tables.sql
├── V5__search_discovery_features.sql
├── V6__communication_chat_tables.sql
├── V7__notification_preferences.sql
└── V8__listing_api_tables.sql
```

**Test data is also stored in the backend:**
```
backend/src/main/resources/test-products-h2.sql
```

## Why This Structure?

### Standard Spring Boot Convention
- SQL files in `src/main/resources/` are automatically packaged in the JAR
- Flyway expects migrations at `classpath:db/migration` by default
- No symlinks needed - works on all platforms (Windows, Mac, Linux)
- Guaranteed to work in production deployments

### Benefits
✅ **Production-Safe**: Files are properly packaged in JAR during build
✅ **Cross-Platform**: No symlink compatibility issues
✅ **Simple**: Standard Spring Boot structure everyone understands
✅ **Deployment-Ready**: Works on all cloud platforms without special configuration

## Usage

### Development (H2 Database)
- Flyway is **disabled** in dev profile (`application.yml`)
- Uses Hibernate `ddl-auto: create-drop` for automatic schema generation
- Test data loaded from `test-products-h2.sql`
- Run: `./mvnw spring-boot:run -Dspring-boot.run.profiles=dev`

### Production (PostgreSQL)
- Flyway migrations run automatically on application startup
- Reads migrations from `src/main/resources/db/migration/`
- Uses `classpath:db/migration` in application.yml
- Run: `java -jar backend.jar --spring.profiles.active=prod`

## Migration Naming Convention

Flyway migrations follow the pattern: `V{version}__{description}.sql`

Example: `V1__campus_marketplace_core_schema.sql`

## Adding New Migrations

1. Create a new SQL file in `backend/src/main/resources/db/migration/` with the next version number
2. Follow the naming convention: `V{N}__descriptive_name.sql`
3. Test in dev environment first (enable Flyway temporarily)
4. The migration will automatically run in production on next deployment

## Utility Scripts

The `scripts/` directory contains useful database utilities:

- **backup.sh**: Create database backups
- **restore.sh**: Restore from backup
- **monitor.sh**: Monitor database health and performance
- **test-connection.sh**: Test database connectivity
- **validate-connection.sh**: Validate database configuration

## Accessing Files

### From IDE
Navigate to: `backend/src/main/resources/db/migration/`

### From Command Line
```bash
cd backend/src/main/resources/db/migration/
ls -la
```

### In Production JAR
Files are automatically included in the JAR at:
```
BOOT-INF/classes/db/migration/
```
