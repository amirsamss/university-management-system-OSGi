# Quick Start Guide - University Management System OSGi

This guide provides quick instructions to build and run the entire University Management System in Apache Karaf.

## Prerequisites

- **JDK 17+** (Java Development Kit)
- **Maven 3.8+** (Build tool)
- **Apache Karaf 4.4.8+** (OSGi container)
- **PostgreSQL 12+** (Database)

## Step 1: Setup Database

1. Start PostgreSQL service
2. Create database:
   ```sql
   CREATE DATABASE university_management_db;
   ```

## Step 2: Build All Bundles

From the project root directory:

```bash
cd C:\Users\rodzi\OneDrive\Desktop\g6-webprog\university-management-system-OSGi
mvn clean install
```

This builds both bundles:
- `fee-service-bundle/target/fee-service-bundle-1.0.0-SNAPSHOT.jar`
- `exam-service-bundle/target/exam-service-bundle-1.0.0-SNAPSHOT.jar`

## Step 3: Start Apache Karaf

```bash
cd C:\karaf\apache-karaf-4.4.9
bin\karaf.bat
```

Wait for Karaf to fully start (you'll see "Karaf started in X seconds").

## Step 4: Install Required Features

In Karaf console, install required features:

```karaf
feature:install http-whiteboard
feature:install jdbc
feature:install transaction
feature:repo-add cxf
feature:install cxf-jaxrs
```

## Step 5: Install PostgreSQL Driver

Download PostgreSQL JDBC driver (e.g., `postgresql-42.7.9.jar`) and install:

```karaf
bundle:install file:///C:/path/to/postgresql-42.7.9.jar
bundle:start <bundle-id>
```

## Step 6: Configure Database Connection

1. Create file: `C:\karaf\apache-karaf-4.4.9\etc\org.ops4j.datasource-university.cfg`

2. Add content:
   ```properties
   osgi.jdbc.driver.name=PostgreSQL
   url=jdbc:postgresql://localhost:5432/university_management_db
   user=postgres
   password=your_password
   dataSourceName=universityDS
   ```

3. Restart Karaf:
   ```karaf
   shutdown
   ```
   Then start again: `bin\karaf.bat`

4. Verify datasource:
   ```karaf
   jdbc:ds-list
   ```

## Step 7: Install Fee Service Bundle

```karaf
bundle:install file:///C:/Users/rodzi/OneDrive/Desktop/g6-webprog/university-management-system-OSGi/fee-service-bundle/target/fee-service-bundle-1.0.0-SNAPSHOT.jar
bundle:start <bundle-id>
```

## Step 8: Install Exam Service Bundle

```karaf
bundle:install file:///C:/Users/rodzi/OneDrive/Desktop/g6-webprog/university-management-system-OSGi/exam-service-bundle/target/exam-service-bundle-1.0.0-SNAPSHOT.jar
bundle:start <bundle-id>
```

## Step 9: Verify Installation

Check bundle status:
```karaf
bundle:list | grep -E "(fee-service|exam-service)"
```

Both bundles should show **Active** status.

## Step 10: Test REST APIs

### Fee Service Endpoints

- **Get all payments:**
  ```bash
  curl http://localhost:8181/api/payments
  ```

- **Record payment:**
  ```bash
  curl -X POST http://localhost:8181/api/payments \
    -H "Content-Type: application/json" \
    -d '{"studentId":"U1234567","amount":1000.00,"referenceNumber":"PAY001"}'
  ```

- **Get payments by student:**
  ```bash
  curl http://localhost:8181/api/payments/student/U1234567
  ```

### Exam Service Endpoints

- **Get all exams:**
  ```bash
  curl http://localhost:8181/api/exam/all-exams
  ```

- **Schedule exam:**
  ```bash
  curl -X POST http://localhost:8181/api/exam/schedule \
    -H "Content-Type: application/json" \
    -d '{"courseCode":"CS101","examDate":"2024-01-15"}'
  ```

- **Get GPA:**
  ```bash
  curl http://localhost:8181/api/exam/gpa/U1234567
  ```

- **Get transcript:**
  ```bash
  curl http://localhost:8181/api/exam/transcript/U1234567
  ```

## Troubleshooting

### Bundle won't start

1. Check bundle diagnostics:
   ```karaf
   bundle:diag <bundle-id>
   ```

2. Check logs:
   ```karaf
   log:tail
   ```

### Missing dependencies

- Verify JAX-RS is installed: `feature:list | grep jaxrs`
- Verify JDBC is installed: `feature:list | grep jdbc`
- Verify PostgreSQL driver is installed: `bundle:list | grep postgresql`

### Database connection issues

1. Test connection:
   ```karaf
   jdbc:query universityDS "SELECT 1"
   ```

2. Verify datasource exists:
   ```karaf
   jdbc:ds-list
   ```

3. Check PostgreSQL is running and database exists

## API Base URLs

- **Fee Service:** `http://localhost:8181/api/payments`
- **Exam Service:** `http://localhost:8181/api/exam`

Default Karaf HTTP port is **8181**. Adjust if your configuration differs.

## Quick Commands Reference

```karaf
# List all bundles
bundle:list

# Check bundle status
bundle:status <id>

# View logs
log:tail

# List datasources
jdbc:ds-list

# Test database
jdbc:query universityDS "SELECT 1"

# List features
feature:list

# Stop Karaf
shutdown
```

---

**Note:** Replace file paths with your actual paths. Use forward slashes `/` in Karaf file URLs, not backslashes `\`.
