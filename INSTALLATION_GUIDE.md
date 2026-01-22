# Installation Guide - Fee Service OSGi Bundle

## Overview

This guide provides step-by-step instructions for installing and running the Fee Service module as an OSGi bundle in Apache Karaf with Eclipse Equinox framework.

## Prerequisites

### Required Software

1. **Java Development Kit (JDK)**
   - Version: JDK 17 or higher
   - Download: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)

2. **Apache Maven**
   - Version: 3.8.0 or higher
   - Download: [Apache Maven](https://maven.apache.org/download.cgi)
   - Ensure `mvn` is in your system PATH

3. **Apache Karaf**
   - Version: 4.4.8 (or compatible version)
   - Download: [Apache Karaf](https://karaf.apache.org/download.html)
   - Extract to a directory (e.g., `C:\karaf` or `/opt/karaf`)

4. **PostgreSQL Database**
   - Version: 12 or higher
   - Download: [PostgreSQL](https://www.postgresql.org/download/)
   - Create a database named `university_fee_db`

### Required OSGi Bundles

The following bundles must be installed in Karaf:

1. **karaf-rest-example-api** - REST API framework
2. **karaf-rest-example-whiteboard** - JAX-RS Whiteboard pattern support
3. **PostgreSQL Driver** - Database connectivity

## Installation Steps

### Step 1: Build the Fee Service Bundle

1. Open a terminal/command prompt
2. Navigate to the project root directory:
   ```bash
   cd C:\Users\rodzi\OneDrive\Desktop\g6-webprog\university-management-system-OSGi
   ```

3. Build the project using Maven:
   ```bash
   mvn clean install
   ```

4. The bundle JAR will be created at:
   ```
   fee-service-bundle/target/fee-service-bundle-1.0.0-SNAPSHOT.jar
   ```

### Step 2: Start Apache Karaf

1. Navigate to the Karaf installation directory:
   ```bash
   cd C:\karaf
   ```

2. Start Karaf:
   ```bash
   bin\karaf.bat    # Windows
   # or
   bin/karaf         # Linux/Mac
   ```

3. Wait for Karaf to fully start. You should see:
   ```
   Karaf started in X seconds
   ```

### Step 3: Install Required Features

In the Karaf console, install the required features:

```karaf
# Install JAX-RS support
feature:install http-whiteboard

# Install JPA support (if needed)
feature:install transaction jpa

# Install PostgreSQL driver
feature:install jdbc
```

### Step 4: Install PostgreSQL Driver Bundle

1. Download PostgreSQL JDBC driver JAR:
   - Download from: [PostgreSQL JDBC Driver](https://jdbc.postgresql.org/download/)
   - Version: 42.7.1 or compatible

2. Install the driver in Karaf:
   ```karaf
   bundle:install file:///path/to/postgresql-42.7.1.jar
   ```

3. Start the bundle:
   ```karaf
   bundle:start <bundle-id>
   ```

### Step 5: Configure Database Connection

1. Create a datasource configuration file:
   ```bash
   # In Karaf, create/edit etc/org.ops4j.datasource-university.cfg
   ```

2. Add the following configuration:
   ```properties
   osgi.jdbc.driver.name=PostgreSQL
   url=jdbc:postgresql://localhost:5432/university_fee_db
   user=your_username
   password=your_password
   dataSourceName=universityFeeDS
   ```

3. Reload the configuration:
   ```karaf
   config:reload
   ```

### Step 6: Install Fee Service Bundle

1. Copy the bundle JAR to a location accessible by Karaf:
   ```bash
   # Copy from project target directory
   cp fee-service-bundle/target/fee-service-bundle-1.0.0-SNAPSHOT.jar C:\karaf\deploy\
   ```

2. Or install directly from file path:
   ```karaf
   bundle:install file:///C:/Users/rodzi/OneDrive/Desktop/g6-webprog/university-management-system-OSGi/fee-service-bundle/target/fee-service-bundle-1.0.0-SNAPSHOT.jar
   ```

3. Start the bundle:
   ```karaf
   bundle:start <bundle-id>
   ```

   Replace `<bundle-id>` with the ID shown after installation.

### Step 7: Verify Installation

1. Check bundle status:
   ```karaf
   bundle:list | grep fee-service
   ```

2. Verify bundle is ACTIVE:
   ```karaf
   bundle:status <bundle-id>
   ```

3. Check REST endpoint:
   ```bash
   curl http://localhost:8181/api/payments
   ```

   Note: Default Karaf HTTP port is 8181. Adjust if your configuration differs.

## Configuration

### Database Schema

Create the required database tables. You can use the following SQL script as a reference:

```sql
-- Create tables based on JPA entities
-- Payment, Invoice, FeeStructure, FeeItem, FinancialAid, AccountStatement, Refund
-- (Tables will be auto-created if JPA is configured with hibernate.hbm2ddl.auto=create)
```

### JPA Persistence Configuration

Create `META-INF/persistence.xml` in your bundle or configure via OSGi Config Admin:

```xml
<persistence xmlns="http://java.sun.com/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://java.sun.com/xml/ns/persistence
             http://java.sun.com/xml/ns/persistence/persistence_2_0.xsd"
             version="2.0">
    <persistence-unit name="fee-service-pu" transaction-type="JTA">
        <jta-data-source>osgi:service/javax.sql.DataSource/(osgi.jndi.service.name=universityFeeDS)</jta-data-source>
        <class>com.example.university.fee.model.Payment</class>
        <class>com.example.university.fee.model.Invoice</class>
        <class>com.example.university.fee.model.FeeStructure</class>
        <class>com.example.university.fee.model.FeeItem</class>
        <class>com.example.university.fee.model.FinancialAid</class>
        <class>com.example.university.fee.model.AccountStatement</class>
        <class>com.example.university.fee.model.Refund</class>
        <properties>
            <property name="jakarta.persistence.schema-generation.database.action" value="create"/>
            <property name="hibernate.dialect" value="org.hibernate.dialect.PostgreSQLDialect"/>
        </properties>
    </persistence-unit>
</persistence>
```

## Troubleshooting

### Bundle Not Starting

1. Check bundle status:
   ```karaf
   bundle:diag <bundle-id>
   ```

2. Check logs:
   ```karaf
   log:tail
   ```

3. Common issues:
   - Missing dependencies: Install required bundles
   - Database connection failure: Verify datasource configuration
   - JPA configuration error: Check persistence.xml

### REST Endpoint Not Available

1. Verify HTTP Whiteboard is installed:
   ```karaf
   feature:list | grep whiteboard
   ```

2. Check service registration:
   ```karaf
   service:list | grep PaymentResource
   ```

3. Verify HTTP service:
   ```karaf
   http:list
   ```

### Database Connection Issues

1. Test database connection:
   ```karaf
   jdbc:query universityFeeDS "SELECT 1"
   ```

2. Verify datasource configuration:
   ```karaf
   config:list | grep datasource
   ```

## Environment Details

- **Runtime**: Apache Karaf 4.4.8
- **Framework**: Eclipse Equinox
- **REST Framework**: JAX-RS with Whiteboard pattern
- **Database**: PostgreSQL 12+
- **Java Version**: JDK 17+

## API Endpoints

Once installed, the following REST endpoints are available:

- `POST /api/payments` - Record a payment
- `GET /api/payments/{id}` - Get payment by ID
- `GET /api/payments/student/{studentId}` - Get payments by student
- `GET /api/payments/date-range?startDate=...&endDate=...` - Get payments by date range

Base URL: `http://localhost:8181` (default Karaf HTTP port)

## Additional Resources

- [Apache Karaf Documentation](https://karaf.apache.org/manual/latest/)
- [OSGi Specification](https://www.osgi.org/developer/specifications/)
- [JAX-RS Documentation](https://jakarta.ee/specifications/restful-ws/)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

## Support

For issues or questions:
1. Check the Karaf logs: `log:tail`
2. Review bundle diagnostics: `bundle:diag <bundle-id>`
3. Verify all dependencies are installed: `bundle:list`

---

**Last Updated**: January 2026
**Version**: 1.0.0-SNAPSHOT
