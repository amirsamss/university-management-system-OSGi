# Fee & Billing Management - Interactive Terminal Application

This is a standalone Java application for managing university fee and billing operations. It implements all use cases from UC-23 to UC-29.

## Features

- **UC-23**: Configure Tuition Fee
- **UC-24**: Generate Invoices
- **UC-25**: Calculate Tuition
- **UC-26**: Manage Financial Aid
- **UC-27**: Track Payments & Outstanding Fees
- **UC-28**: Process Refund
- **UC-29**: View Account Statements

## Building the Application

```bash
mvn clean package
```

## Running the Application

After building, a fat JAR with all dependencies will be created. Run it with:

**Windows (PowerShell):**
```powershell
java -jar target/fee-service-bundle.jar
```

**Linux/Mac:**
```bash
java -jar target/fee-service-bundle.jar
```

Or using Maven exec plugin:
```bash
mvn exec:java -Dexec.mainClass="com.example.university.fee.FeeManagementTerminal"
```

## Usage

The application provides an interactive menu-driven interface. Follow the prompts to:

1. Configure fee structures for different departments and student types
2. Generate invoices for students
3. Calculate tuition amounts
4. Manage financial aid (scholarships, grants, etc.)
5. Record payments and track outstanding fees
6. Process refunds for overpayments
7. View account statements and generate tax forms

## Database Connection

The application is **connected to Supabase PostgreSQL database**. All data is persisted in the cloud.

### Database Configuration

The database connection is configured in `src/main/resources/META-INF/persistence.xml`:
- **Host**: `aws-1-ap-northeast-2.pooler.supabase.com`
- **Port**: `5432`
- **Database**: `postgres`
- **Connection**: Session pooler (IPv4 compatible)

The connection uses SSL/TLS encryption for security.

### Database Schema

The application automatically creates/updates the following tables:
- `fee_structures` - Tuition fee configurations
- `fee_items` - Individual fee items
- `invoices` - Student billing invoices
- `invoice_line_items` - Invoice line items
- `payments` - Payment records
- `financial_aids` - Financial aid records
- `refunds` - Refund records
- `account_statements` - Account transaction history

Hibernate will automatically create/update the schema on startup (`hibernate.hbm2ddl.auto=update`).

## Requirements

- Java 17 or higher
- Maven 3.6 or higher
