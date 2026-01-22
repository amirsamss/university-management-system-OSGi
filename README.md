# University Management System - OSGi Implementation

University Management System developed using OSGi (Apache Karaf with Eclipse Equinox framework).

## Overview

This project implements the Fee & Billing Management Module (UC-23 to UC-29) as an OSGi bundle, providing a modular, service-oriented architecture for managing university fee structures, invoices, payments, financial aid, and account statements.

## Architecture

- **Runtime**: Apache Karaf 4.4.8
- **Framework**: Eclipse Equinox
- **REST API**: JAX-RS with Whiteboard pattern
- **Database**: PostgreSQL 12+
- **Java Version**: JDK 17+

## Module Structure

```
university-management-system-OSGi/
├── fee-service-bundle/          # Fee Service OSGi Bundle
│   ├── src/main/java/
│   │   └── com/example/university/fee/
│   │       ├── Activator.java           # Bundle activator
│   │       ├── model/                   # JPA entities
│   │       │   ├── Payment.java
│   │       │   ├── Invoice.java
│   │       │   ├── FeeStructure.java
│   │       │   ├── FeeItem.java
│   │       │   ├── FinancialAid.java
│   │       │   ├── AccountStatement.java
│   │       │   └── Refund.java
│   │       ├── service/                 # Service interfaces
│   │       │   └── PaymentService.java
│   │       ├── service/impl/           # Service implementations
│   │       │   └── PaymentServiceImpl.java
│   │       └── api/                    # REST API resources
│   │           └── PaymentResource.java
│   └── src/main/resources/
│       ├── OSGI-INF/                    # OSGi service components
│       └── META-INF/
│           └── persistence.xml          # JPA configuration
├── pom.xml                              # Parent POM
└── INSTALLATION_GUIDE.md                # Installation instructions
```

## Use Cases Implemented

- **UC-23**: Configure Tuition Fee
- **UC-24**: Generate Invoices
- **UC-25**: Calculate Tuition
- **UC-26**: Manage Financial Aid
- **UC-27**: Track Payments & Outstanding Fees
- **UC-28**: Process Refund
- **UC-29**: View Account Statements

## Features

- **OSGi Declarative Services**: Service components with dependency injection
- **JAX-RS REST API**: RESTful endpoints using Whiteboard pattern
- **JPA Persistence**: Database integration with PostgreSQL
- **Modular Architecture**: Hot-deployable OSGi bundles

## Quick Start

1. **Prerequisites**: JDK 17+, Maven 3.8+, Apache Karaf 4.4.8, PostgreSQL 12+

2. **Build the bundle**:
   ```bash
   mvn clean install
   ```

3. **Install in Karaf**:
   ```bash
   bundle:install file:///path/to/fee-service-bundle-1.0.0-SNAPSHOT.jar
   bundle:start <bundle-id>
   ```

4. **Access REST API**:
   ```bash
   curl http://localhost:8181/api/payments
   ```

For detailed installation instructions, see [INSTALLATION_GUIDE.md](INSTALLATION_GUIDE.md).

## API Endpoints

### Payment Management (UC-27)

- `POST /api/payments` - Record a payment
- `GET /api/payments/{id}` - Get payment by ID
- `GET /api/payments/student/{studentId}` - Get payments by student
- `GET /api/payments/date-range?startDate=...&endDate=...` - Get payments by date range

Base URL: `http://localhost:8181` (default Karaf HTTP port)

## Dependencies

### Required OSGi Bundles

- `karaf-rest-example-api` - REST API framework
- `karaf-rest-example-whiteboard` - JAX-RS Whiteboard pattern
- PostgreSQL JDBC Driver (42.7.1+)

### Maven Dependencies

- OSGi Core (7.0.0)
- JAX-RS API (3.0.0)
- JPA API (3.1.0)
- PostgreSQL Driver (42.7.1)
- SLF4J (1.7.36)

## Development

### Building

```bash
mvn clean install
```

### Bundle Structure

The bundle exports:
- `com.example.university.fee.api` - REST API resources
- `com.example.university.fee.model` - Entity classes
- `com.example.university.fee.service` - Service interfaces

### OSGi Service Components

Services are registered using OSGi Declarative Services (DS) annotations:
- `@Component` - Marks a class as a service component
- `@Reference` - Injects OSGi services

## Testing

1. Start Karaf and install the bundle
2. Verify bundle status: `bundle:list | grep fee-service`
3. Test REST endpoints using curl or Postman
4. Check logs: `log:tail`

## Troubleshooting

See [INSTALLATION_GUIDE.md](INSTALLATION_GUIDE.md) for troubleshooting steps.

## Documentation

- [Installation Guide](INSTALLATION_GUIDE.md) - Detailed installation and configuration instructions

## License

This project is part of a university assignment.

## Authors

University Management System Development Team

---

**Version**: 1.0.0-SNAPSHOT  
**Last Updated**: January 2026
