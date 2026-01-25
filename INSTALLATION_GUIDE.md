# Installation Guide - University Management System

This guide provides comprehensive step-by-step instructions for installing and running the University Management System as OSGi bundles in Apache Karaf.

## Table of Contents

1. [Overview](#overview)
2. [Prerequisites](#prerequisites)
3. [System Architecture](#system-architecture)
4. [Installation Steps](#installation-steps)
5. [Configuration](#configuration)
6. [Verification](#verification)
7. [Troubleshooting](#troubleshooting)
8. [API Documentation](#api-documentation)

## Overview

The University Management System is a modular OSGi application consisting of:

- **university-management-api**: Shared POJOs and service interfaces
- **university-management-whiteboard**: REST API implementations and service implementations
- **university-management-features**: Karaf feature repository for easy deployment

The system includes three main services:
- **Exam Service**: Exam scheduling, grading, GPA calculation, and transcript generation
- **Fee Service**: Payment processing, invoice management, financial aid, and account statements
- **Course Service**: Course catalog, enrollment management, timetable, and prerequisites

## Prerequisites

### Required Software

1. **Java Development Kit (JDK)**
   - Version: JDK 17 or higher
   - Download: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
   - Verify installation:
     ```bash
     java -version
     ```

2. **Apache Maven**
   - Version: 3.8.0 or higher
   - Download: [Apache Maven](https://maven.apache.org/download.cgi)
   - Ensure `mvn` is in your system PATH
   - Verify installation:
     ```bash
     mvn -version
     ```

3. **Apache Karaf**
   - Version: 4.4.8 or higher
   - Download: [Apache Karaf](https://karaf.apache.org/download.html)
   - Extract to a directory (e.g., `C:\karaf` on Windows or `/opt/karaf` on Linux/Mac)
   - Verify installation:
     ```bash
     cd /path/to/apache-karaf-4.4.8
     bin/karaf version
     ```

4. **PostgreSQL Database**
   - **Option A: Local PostgreSQL**
     - Version: 12 or higher
     - Download: [PostgreSQL](https://www.postgresql.org/download/)
     - Create a database named `university_management_db`
   
   - **Option B: Supabase (Cloud PostgreSQL)**
     - Sign up at [Supabase](https://supabase.com/)
     - Create a new project
     - Note your connection details from Project Settings > Database
     - **See [SUPABASE_SETUP.md](SUPABASE_SETUP.md) for detailed team setup instructions**

### System Requirements

- **RAM**: Minimum 2GB, Recommended 4GB+
- **Disk Space**: Minimum 500MB for Karaf + project
- **Network**: Required for Supabase (if using cloud database)

## System Architecture

```
┌─────────────────────────────────────────────────────────┐
│              Apache Karaf (OSGi Container)              │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  ┌──────────────────────────────────────────────────┐  │
│  │  university-management-features                   │  │
│  │  (Karaf Feature Repository)                      │  │
│  └──────────────────────────────────────────────────┘  │
│                          │                               │
│                          ▼                               │
│  ┌──────────────────────────────────────────────────┐  │
│  │  university-management-whiteboard                 │  │
│  │  - ExamResource, PaymentResource, CourseResource  │  │
│  │  - GradingServiceImpl, PaymentServiceImpl, etc.  │  │
│  └──────────────────────────────────────────────────┘  │
│                          │                               │
│                          ▼                               │
│  ┌──────────────────────────────────────────────────┐  │
│  │  university-management-api                        │  │
│  │  - Models (Exam, Payment, Course, etc.)          │  │
│  │  - Service Interfaces                            │  │
│  └──────────────────────────────────────────────────┘  │
│                                                          │
│  ┌──────────────────────────────────────────────────┐  │
│  │  Apache CXF (JAX-RS)                             │  │
│  │  Pax JDBC (Database Integration)                  │  │
│  └──────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────┘
                          │
                          ▼
              ┌───────────────────────┐
              │   PostgreSQL Database  │
              │  (Local or Supabase)   │
              └───────────────────────┘
```

## Installation Steps

### Step 1: Prepare the Project

1. **Clone or download the project**:
   ```bash
   git clone <repository-url>
   cd university-management-system-OSGi
   ```

2. **Verify project structure**:
   ```bash
   ls -la
   # Should see:
   # - university-management-api/
   # - university-management-whiteboard/
   # - university-management-features/
   # - pom.xml
   ```

### Step 2: Build the Project

1. **Build all bundles**:
   ```bash
   mvn clean install
   ```

2. **Verify build success**:
   - Check for `BUILD SUCCESS` message
   - Verify JAR files are created:
     ```bash
     ls university-management-api/target/*.jar
     ls university-management-whiteboard/target/*.jar
     ls university-management-features/target/*.jar
     ```

### Step 3: Setup Database

#### Option A: Local PostgreSQL

1. **Start PostgreSQL service**:
   ```bash
   # Windows
   net start postgresql-x64-XX
   
   # Linux/Mac
   sudo systemctl start postgresql
   ```

2. **Create database**:
   ```bash
   psql -U postgres
   ```
   ```sql
   CREATE DATABASE university_management_db;
   \q
   ```

3. **Note connection details**:
   - Host: `localhost`
   - Port: `5432` (default)
   - Database: `university_management_db`
   - Username: `postgres` (or your username)
   - Password: Your PostgreSQL password

#### Option B: Supabase

1. **Create Supabase project**:
   - Go to [Supabase Dashboard](https://app.supabase.com/)
   - Click "New Project"
   - Fill in project details
   - Wait for project to be created

2. **Get connection details**:
   - Go to Project Settings > Database
   - Note the following:
     - Host: `db.xxxxx.supabase.co`
     - Port: `5432`
     - Database: `postgres`
     - Username: `postgres`
     - Password: (shown in connection string)

### Step 4: Start Apache Karaf

1. **Navigate to Karaf directory**:
   ```bash
   cd /path/to/apache-karaf-4.4.8
   ```

2. **Start Karaf**:
   ```bash
   # Linux/Mac
   bin/karaf
   
   # Windows
   bin\karaf.bat
   ```

3. **Wait for startup**:
   - You should see: `Karaf started in X seconds`
   - The console prompt will change to: `karaf@root()>`

### Step 5: Install Required Karaf Features

In the Karaf console, install the required features:

```karaf
# Add CXF feature repository
feature:repo-add cxf

# Install JAX-RS and HTTP support
feature:install cxf-jaxrs http-whiteboard

# Install JDBC and transaction support
feature:install jdbc transaction

# Add Pax JDBC feature repository
feature:repo-add mvn:org.ops4j.pax.jdbc/pax-jdbc-features/1.5.0/xml/features

# Install Pax JDBC PostgreSQL support
feature:install pax-jdbc-postgresql pax-jdbc-config
```

### Step 6: Configure Database Connection

#### For Local PostgreSQL:

```karaf
# Create datasource configuration
config:edit org.ops4j.datasource-university
config:property-set osgi.jdbc.driver.name PostgreSQL
config:property-set url jdbc:postgresql://localhost:5432/university_management_db
config:property-set user postgres
config:property-set password your_password
config:property-set dataSourceName universityDS
config:update
```

#### For Supabase:

```karaf
# Create datasource configuration
config:edit org.ops4j.datasource-university
config:property-set osgi.jdbc.driver.name PostgreSQL
config:property-set url jdbc:postgresql://db.xxxxx.supabase.co:5432/postgres
config:property-set user postgres
config:property-set password your_supabase_password
config:property-set dataSourceName universityDS
config:update
```

**Important Notes:**
- Replace `your_password` with your actual database password
- Replace `db.xxxxx.supabase.co` with your Supabase host (if using Supabase)
- The `dataSourceName=universityDS` must match across all configurations

### Step 7: Verify Database Connection

```karaf
# List all datasources
jdbc:ds-list

# Test database connection
jdbc:query universityDS "SELECT 1"

# If successful, you should see:
# +---+
# | 1 |
# +---+
# | 1 |
# +---+
```

### Step 8: Install University Management System

```karaf
# Add feature repository
feature:repo-add mvn:com.example.university/university-management-features/1.0.0-SNAPSHOT/xml/features

# Install the complete system
feature:install university-management
```

This single command installs:
- `university-management-api` bundle
- `university-management-whiteboard` bundle
- All REST services (Exam, Fee, Course)
- All service implementations

### Step 9: Verify Installation

1. **Check bundle status**:
   ```karaf
   bundle:list | grep university-management
   ```

   You should see:
   ```
   [ID] [State] [Level] [Name]
   [XX] [Active] [80] com.example.university.university-management-api
   [XX] [Active] [80] com.example.university.university-management-whiteboard
   ```

2. **Check service registration**:
   ```karaf
   service:list | grep -E "(ExamResource|PaymentResource|CourseResource)"
   ```

3. **Check HTTP endpoints**:
   ```karaf
   http:list
   ```

   You should see endpoints registered on port 8181.

## Configuration

### Database Schema

The system uses JPA with automatic schema generation. Tables will be created automatically when the bundles start. The following tables will be created:

**Exam Service:**
- `exams`
- `grades`

**Fee Service:**
- `payments`
- `invoices`
- `invoice_line_items`
- `fee_structures`
- `fee_items`
- `financial_aids`
- `account_statements`
- `refunds`

**Course Service:**
- `courses`
- `course_enrollments`
- `course_schedules`
- `course_prerequisites`

### Persistence Configuration

The persistence configuration is located in:
```
university-management-whiteboard/src/main/resources/META-INF/persistence.xml
```

Key settings:
- **Persistence Unit**: `university-pu`
- **Transaction Type**: JTA
- **Schema Generation**: `update` (tables are updated, not dropped)
- **Dialect**: PostgreSQL

### Changing Database Settings

To change database connection settings:

```karaf
# Edit configuration
config:edit org.ops4j.datasource-university

# Update properties
config:property-set url jdbc:postgresql://newhost:5432/newdatabase
config:property-set user newuser
config:property-set password newpassword

# Save and update
config:update

# Restart bundles to apply changes
bundle:restart <bundle-id>
```

## Verification

### Test REST Endpoints

#### Exam Service

```bash
# Get all exams
curl http://localhost:8181/api/exam/all-exams

# Schedule an exam
curl -X POST http://localhost:8181/api/exam/schedule \
  -H "Content-Type: application/json" \
  -d '{
    "courseCode": "CS101",
    "venue": "Hall A",
    "examDate": "2024-01-15T10:00:00"
  }'

# Submit a grade
curl -X POST http://localhost:8181/api/exam/submit-grade \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": "U1234567",
    "courseCode": "CS101",
    "marks": 85.0
  }'

# Get GPA
curl http://localhost:8181/api/exam/gpa/U1234567

# Get transcript
curl http://localhost:8181/api/exam/transcript/U1234567
```

#### Fee Service

```bash
# Get all payments
curl http://localhost:8181/api/payments

# Record a payment
curl -X POST http://localhost:8181/api/payments \
  -H "Content-Type: application/json" \
  -d '{
    "studentId": "U1234567",
    "amount": 1000.00,
    "referenceNumber": "PAY001",
    "paymentMethod": "BANK_TRANSFER"
  }'

# Get payment by ID
curl http://localhost:8181/api/payments/1

# Get payments by student
curl http://localhost:8181/api/payments/student/U1234567

# Get payments by date range
curl "http://localhost:8181/api/payments/date-range?startDate=2024-01-01&endDate=2024-12-31"
```

#### Course Service

```bash
# Get all courses
curl http://localhost:8181/api/courses

# Add a course
curl -X POST http://localhost:8181/api/courses \
  -H "Content-Type: application/json" \
  -d '{
    "courseCode": "CS101",
    "courseName": "Introduction to Computer Science",
    "department": "Computer Science",
    "credits": 3,
    "semester": "Fall",
    "academicYear": "2024-2025",
    "maxCapacity": 50
  }'

# Get course by ID
curl http://localhost:8181/api/courses/1

# Enroll a student
curl -X POST http://localhost:8181/api/courses/enrollments \
  -H "Content-Type: application/json" \
  -d '{
    "courseId": 1,
    "studentId": "U1234567",
    "semester": "Fall",
    "academicYear": "2024-2025"
  }'

# Get enrollments by student
curl http://localhost:8181/api/courses/enrollments/student/U1234567

# Add course schedule
curl -X POST http://localhost:8181/api/courses/schedules \
  -H "Content-Type: application/json" \
  -d '{
    "courseId": 1,
    "courseCode": "CS101",
    "dayOfWeek": "MONDAY",
    "startTime": "09:00:00",
    "endTime": "11:00:00",
    "venue": "Building A",
    "semester": "Fall",
    "academicYear": "2024-2025"
  }'
```

## Troubleshooting

### Bundle Not Starting

1. **Check bundle diagnostics**:
   ```karaf
   bundle:diag <bundle-id>
   ```

2. **Check logs**:
   ```karaf
   log:tail
   ```

3. **Common issues**:
   - Missing dependencies: Install required features
   - Database connection failure: Verify datasource configuration
   - JPA configuration error: Check persistence.xml

### REST Endpoints Not Available

1. **Verify HTTP Whiteboard is installed**:
   ```karaf
   feature:list | grep whiteboard
   ```

2. **Check service registration**:
   ```karaf
   service:list | grep Resource
   ```

3. **Verify HTTP service**:
   ```karaf
   http:list
   ```

4. **Check CXF is installed**:
   ```karaf
   feature:list | grep cxf
   ```

### Database Connection Issues

1. **Test database connection**:
   ```karaf
   jdbc:query universityDS "SELECT 1"
   ```

2. **Verify datasource configuration**:
   ```karaf
   config:list | grep datasource
   ```

3. **Check PostgreSQL is running**:
   ```bash
   # Local PostgreSQL
   psql -U postgres -c "SELECT 1"
   
   # Supabase - test from browser or use psql
   ```

4. **Verify network connectivity** (for Supabase):
   ```bash
   telnet db.xxxxx.supabase.co 5432
   ```

### Missing Dependencies

1. **Check installed features**:
   ```karaf
   feature:list | grep -E "(cxf|jdbc|whiteboard|transaction)"
   ```

2. **Reinstall missing features**:
   ```karaf
   feature:install <feature-name>
   ```

### EntityManager Not Available

If you see errors about EntityManager not being available:

1. **Check JPA feature is installed**:
   ```karaf
   feature:list | grep jpa
   ```

2. **Verify datasource is registered**:
   ```karaf
   jdbc:ds-list
   ```

3. **Check persistence unit name matches**:
   - Configuration should reference: `(osgi.unit.name=university-pu)`
   - Verify in `persistence.xml`

## API Documentation

### Exam Service Endpoints

Base URL: `http://localhost:8181/api/exam`

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/schedule` | Schedule an exam |
| GET | `/all-exams` | Get all exams |
| POST | `/submit-grade` | Submit a grade |
| GET | `/all-grades` | Get all grades |
| GET | `/gpa/{studentId}` | Get GPA for a student |
| GET | `/transcript/{studentId}` | Get transcript for a student |

### Fee Service Endpoints

Base URL: `http://localhost:8181/api/payments`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get all payments |
| POST | `/` | Record a payment |
| GET | `/{id}` | Get payment by ID |
| GET | `/student/{studentId}` | Get payments by student |
| GET | `/date-range?startDate=...&endDate=...` | Get payments by date range |

### Course Service Endpoints

Base URL: `http://localhost:8181/api/courses`

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/` | Get all courses |
| POST | `/` | Add a course |
| PUT | `/` | Update a course |
| GET | `/{id}` | Get course by ID |
| GET | `/code/{courseCode}` | Get course by code |
| DELETE | `/{id}` | Delete a course |
| POST | `/enrollments` | Enroll a student |
| GET | `/enrollments/student/{studentId}` | Get enrollments by student |
| POST | `/schedules` | Add course schedule |
| GET | `/{courseId}/schedules` | Get schedules by course |
| POST | `/prerequisites` | Add prerequisite |
| GET | `/validate-prerequisites/{studentId}/{courseId}` | Validate prerequisites |

## Additional Resources

- [Apache Karaf Documentation](https://karaf.apache.org/manual/latest/)
- [OSGi Specification](https://www.osgi.org/developer/specifications/)
- [JAX-RS Documentation](https://jakarta.ee/specifications/restful-ws/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Supabase Documentation](https://supabase.com/docs)

## Support

For issues or questions:

1. Check the Karaf logs: `log:tail`
2. Review bundle diagnostics: `bundle:diag <bundle-id>`
3. Verify all dependencies are installed: `bundle:list`
4. Check the project README.md for additional information

---

**Last Updated**: January 2026  
**Version**: 1.0.0-SNAPSHOT  
**Team**: University Management System Development Team
