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
   cd fee-service-bundle
   mvn clean install
   ```

3. **Deploy to Karaf** (see [fee-service-bundle/KARAF_DEPLOYMENT.md](fee-service-bundle/KARAF_DEPLOYMENT.md)):
   ```bash
   # In Karaf console:
   feature:install shell jpa transaction jdbc
   bundle:install file:///path/to/fee-service-bundle-1.0.0-SNAPSHOT.jar
   bundle:start <bundle-id>
   ```

4. **Use Karaf Shell Commands**:
   ```bash
   # In Karaf console:
   fee:menu                    # Interactive menu
   fee:help                    # List all commands
   fee:create-fee-structure   # Create fee structure
   fee:generate-invoice       # Generate invoice
   ```

For detailed deployment instructions, see [fee-service-bundle/KARAF_DEPLOYMENT.md](fee-service-bundle/KARAF_DEPLOYMENT.md).

## Karaf Shell Commands

The bundle provides **17 Karaf Shell Commands** for fee management:

### Fee Structure Management (UC-23)
- `fee:create-fee-structure` - Create fee structure
- `fee:list-fee-structures` - List all fee structures
- `fee:view-fee-structure` - View fee structure details
- `fee:add-fee-item` - Add fee item to structure

### Invoice & Tuition (UC-24, UC-25)
- `fee:generate-invoice` - Generate invoice for student
- `fee:generate-invoices-batch` - Batch invoice generation
- `fee:calculate-tuition` - Calculate tuition with breakdown

### Financial Aid (UC-26)
- `fee:manage-financial-aid` - Create/manage financial aid

### Payment Management (UC-27)
- `fee:record-payment` - Record payment
- `fee:view-payment` - View payment details
- `fee:view-payments` - List payments
- `fee:view-outstanding` - View outstanding fees
- `fee:reverse-payment` - Reverse a payment

### Refunds & Statements (UC-28, UC-29)
- `fee:process-refund` - Process refund
- `fee:view-statement` - View account statement
- `fee:view-all` - View all data

### Interactive Menu
- `fee:menu` - Interactive menu with all options

See [fee-service-bundle/KARAF_SHELL_COMMANDS_SUMMARY.md](fee-service-bundle/KARAF_SHELL_COMMANDS_SUMMARY.md) for complete command reference.

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
3. Test shell commands: `fee:menu` or `fee:help`
4. Check logs: `log:tail`

## Troubleshooting

See [INSTALLATION_GUIDE.md](INSTALLATION_GUIDE.md) for troubleshooting steps.

## Documentation

### Fee Service Bundle
- **[KARAF_DEPLOYMENT.md](fee-service-bundle/KARAF_DEPLOYMENT.md)** - Complete Karaf deployment guide
- **[KARAF_SHELL_COMMANDS_SUMMARY.md](fee-service-bundle/KARAF_SHELL_COMMANDS_SUMMARY.md)** - All shell commands reference
- **[KARAF_SHELL_USAGE.md](fee-service-bundle/KARAF_SHELL_USAGE.md)** - Detailed usage guide
- **[README_OSGI.md](fee-service-bundle/README_OSGI.md)** - OSGi deployment overview

## License

This project is part of a university assignment.

## Authors

University Management System Development Team

---

**Version**: 1.0.0-SNAPSHOT  
**Last Updated**: January 2026
