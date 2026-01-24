# Quick Start Guide - University Management System

This guide provides quick instructions to build and run the entire University Management System in Apache Karaf.

## Prerequisites

- **JDK 17+** (Java Development Kit)
- **Maven 3.8+** (Build tool)
- **Apache Karaf 4.4.8+** (OSGi container)
- **PostgreSQL Database** (Local PostgreSQL or Supabase)

## Quick Setup (5 Steps)

### Step 1: Clone and Build

```bash
# Navigate to project directory
cd university-management-system-OSGi

# Build all bundles
mvn clean install
```

This builds:
- `university-management-api` - Shared models and interfaces
- `university-management-whiteboard` - REST APIs and service implementations
- `university-management-features` - Karaf feature repository

### Step 2: Setup Database

**Option A: Local PostgreSQL**
```sql
CREATE DATABASE university_management_db;
```

**Option B: Supabase (Recommended for Teams)**
- Create a new project in Supabase
- Note your connection string (e.g., `postgresql://user:pass@host:5432/dbname`)
- **For team setup, see [SUPABASE_SETUP.md](SUPABASE_SETUP.md)**

### Step 3: Start Apache Karaf

```bash
# Navigate to Karaf installation directory
cd /path/to/apache-karaf-4.4.8

# Start Karaf
bin/karaf        # Linux/Mac
bin\karaf.bat    # Windows
```

Wait for Karaf to fully start (you'll see "Karaf started in X seconds").

### Step 4: Install Features and Configure

In Karaf console, run:

```karaf
# Install required Karaf features
feature:repo-add cxf
feature:install cxf-jaxrs http-whiteboard jdbc transaction

# Install Pax JDBC for PostgreSQL
feature:repo-add mvn:org.ops4j.pax.jdbc/pax-jdbc-features/1.5.0/xml/features
feature:install pax-jdbc-postgresql pax-jdbc-config

# Configure database connection
# For Local PostgreSQL:
config:edit org.ops4j.datasource-university
config:property-set osgi.jdbc.driver.name "PostgreSQL JDBC Driver"
config:property-set url jdbc:postgresql://localhost:5432/university_management_db
config:property-set user postgres
config:property-set password your_password
config:property-set dataSourceName universityDS
config:update

# For Supabase (replace with your connection details):
config:edit org.ops4j.datasource-university
config:property-set osgi.jdbc.driver.name "PostgreSQL JDBC Driver"
config:property-set url jdbc:postgresql://your-project.supabase.co:5432/postgres
config:property-set user postgres
config:property-set password your_supabase_password
config:property-set dataSourceName universityDS
config:update

# Verify datasource
jdbc:ds-list
```

### Step 5: Install University Management Feature

```karaf
# Add feature repository
feature:repo-add mvn:com.example.university/university-management-features/1.0.0-SNAPSHOT/xml/features

# Install the complete system
feature:install university-management
```

This installs all bundles (API, Whiteboard) and all services (Exam, Fee, Course).

### Step 6: Verify Installation

```karaf
# Check bundle status
bundle:list | grep university-management

# All bundles should show Active status
```

## Test REST APIs

### Exam Service

```bash
# Get all exams
curl http://localhost:8181/api/exam/all-exams

# Schedule an exam
curl -X POST http://localhost:8181/api/exam/schedule \
  -H "Content-Type: application/json" \
  -d '{"courseCode":"CS101","venue":"Hall A","examDate":"2024-01-15T10:00:00"}'

# Get GPA for a student
curl http://localhost:8181/api/exam/gpa/U1234567

# Get transcript
curl http://localhost:8181/api/exam/transcript/U1234567
```

### Fee Service

```bash
# Get all payments
curl http://localhost:8181/api/payments

# Record a payment
curl -X POST http://localhost:8181/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "studentId":"U1234567",
    "amount":1000.00,
    "referenceNumber":"PAY001",
    "paymentMethod":"BANK_TRANSFER"
  }'

# Get payments by student
curl http://localhost:8181/api/payments/student/U1234567
```

### Course Service

```bash
# Get all courses
curl http://localhost:8181/api/courses

# Add a course
curl -X POST http://localhost:8181/api/courses \
  -H "Content-Type: application/json" \
  -d '{
    "courseCode":"CS101",
    "courseName":"Introduction to Computer Science",
    "department":"Computer Science",
    "credits":3,
    "semester":"Fall",
    "academicYear":"2024-2025"
  }'

# Enroll a student
curl -X POST http://localhost:8181/api/courses/enrollments \
  -H "Content-Type: application/json" \
  -d '{
    "courseId":1,
    "studentId":"U1234567",
    "semester":"Fall",
    "academicYear":"2024-2025"
  }'
```

## API Base URLs

- **Exam Service:** `http://localhost:8181/api/exam`
- **Fee Service:** `http://localhost:8181/api/payments`
- **Course Service:** `http://localhost:8181/api/courses`

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

# Test database connection
jdbc:query universityDS "SELECT 1"

# List installed features
feature:list

# Stop Karaf
shutdown
```

## Troubleshooting

### Bundle won't start
```karaf
bundle:diag <bundle-id>
log:tail
```

### Missing dependencies
```karaf
feature:list | grep -E "(cxf|jdbc|whiteboard)"
```

### Database connection issues
```karaf
jdbc:ds-list
jdbc:query universityDS "SELECT 1"
```

### REST endpoints not available
```karaf
service:list | grep Resource
http:list
```

## Next Steps

For detailed installation and configuration, see [INSTALLATION_GUIDE.md](INSTALLATION_GUIDE.md).

---

**Note:** Replace file paths and database credentials with your actual values. Use forward slashes `/` in Karaf file URLs, not backslashes `\`.
