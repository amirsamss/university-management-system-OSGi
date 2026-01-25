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
├── university-management-api/          # Shared POJOs and interfaces
│   ├── exam/ (Exam, Grade, GradingService)
│   ├── fee/ (Payment, Invoice, PaymentService)
│   └── course/ (Course, Enrollment, CourseService)
│
├── university-management-whiteboard/   # REST APIs and implementations
│   ├── exam/ (ExamResource, GradingServiceImpl)
│   ├── fee/ (PaymentResource, PaymentServiceImpl)
│   └── course/ (CourseResource, CourseServiceImpl)
│
├── university-management-features/     # Karaf feature repository
│
├── [Old bundles kept for reference]
│   ├── exam-service-bundle/
│   ├── fee-service-bundle/
│   └── course-service-bundle/
│
├── pom.xml                              # Parent POM
├── QUICKSTART.md                        # Quick start guide
└── INSTALLATION_GUIDE.md                # Detailed installation guide
```

## Services Implemented

### Exam Service
- Exam scheduling
- Grade submission
- GPA calculation
- Transcript generation

### Fee Service
- Payment processing
- Invoice management
- Financial aid management
- Account statements
- Refund processing

### Course Service
- Course catalog management
- Student enrollment
- Course timetable
- Prerequisite validation

## Features

- **OSGi Declarative Services**: Service components with dependency injection
- **JAX-RS REST API**: RESTful endpoints using Whiteboard pattern
- **JPA Persistence**: Database integration with PostgreSQL
- **Modular Architecture**: Hot-deployable OSGi bundles

## Quick Start

For a quick setup guide, see [QUICKSTART.md](QUICKSTART.md).

For detailed installation and configuration instructions, see [INSTALLATION_GUIDE.md](INSTALLATION_GUIDE.md).

### Quick Commands

```bash
# Build all bundles
mvn clean install

# In Karaf console:
feature:repo-add mvn:com.example.university/university-management-features/1.0.0-SNAPSHOT/xml/features
feature:install university-management
```

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

### REST API Endpoints

#### Exam Service
- `POST /api/exam/schedule` - Schedule an exam
- `GET /api/exam/all-exams` - Get all exams
- `POST /api/exam/submit-grade` - Submit a grade
- `GET /api/exam/gpa/{studentId}` - Get GPA
- `GET /api/exam/transcript/{studentId}` - Get transcript

#### Fee Service
- `POST /api/payments` - Record a payment
- `GET /api/payments/{id}` - Get payment by ID
- `GET /api/payments/student/{studentId}` - Get payments by student
- `GET /api/payments/date-range?startDate=...&endDate=...` - Get payments by date range

#### Course Service
- `GET /api/courses` - Get all courses
- `POST /api/courses` - Add a course
- `POST /api/courses/enrollments` - Enroll a student
- `GET /api/courses/{courseId}/schedules` - Get course schedules
- `GET /api/courses/validate-prerequisites/{studentId}/{courseId}` - Validate prerequisites

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
3. Test shell commands: `fee:menu` or `fee:help`
4. Check logs: `log:tail`

## Troubleshooting

See [INSTALLATION_GUIDE.md](INSTALLATION_GUIDE.md) for troubleshooting steps.

## Documentation

- [Quick Start Guide](QUICKSTART.md) - Quick setup instructions for getting started
- [Installation Guide](INSTALLATION_GUIDE.md) - Comprehensive installation and configuration instructions

## License

This project is part of a university assignment.

## Authors

University Management System Development Team

---

**Version**: 1.0.0-SNAPSHOT  
**Last Updated**: January 2026
