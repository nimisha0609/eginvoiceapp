# Invoice System (Spring Boot)

Simple REST API for creating, listing, paying, and processing invoices. The app runs entirely in memory.

## Features

- Create invoices with an amount and due date
- List invoices with status (PENDING, PAID, VOID)
- Apply payments to invoices
- Process overdue invoices with late fees and rollovers
- Simple Bootstrap UI at `/` for viewing and managing invoices

## Tech Stack

- Java 17 (required for build/test)
- Spring Boot 4.x
- Maven (wrapper included)
- Bootstrap 5 (CDN)

## Project Structure

```
invoicesystem/
  src/main/java/com/nimisha/invoicesystem
    controller/InvoiceController.java
    domain/Invoice.java
    repository/InMemoryInvoiceRepository.java
    service/InvoiceService.java
  src/main/resources/application.properties
  src/main/resources/static/index.html
```

## Getting Started

From the repo root:

```
cd invoicesystem
.\mvnw spring-boot:run
```

Run tests:

```
cd invoicesystem
.\mvnw test
```

The service starts on `http://localhost:8080`.

Open the UI at `http://localhost:8080/`.

## Docker

Build and run with Docker Compose from the repo root:

```
docker compose up --build
```

The service will be available on `http://localhost:8080`.

## API Endpoints

Base path: `/invoices`

### Create an invoice

`POST /invoices`

```
{
  "amount": "1250.00",
  "due_date": "2026-02-15"
}
```

Response:

```
{ "id": "UUID" }
```

### List invoices

`GET /invoices`

Optional query parameters:

- `status` (PENDING, PAID, VOID)
- `overdue` (true/false)
- `overdueDays` (default 10)
- `page` (default 0)
- `size` (default 10)

### Pay an invoice

`POST /invoices/{id}/payments`

```
{
  "amount": "200.00"
}
```

### Process overdue invoices

`POST /invoices/process-overdue`

```
{
  "late_fee": "25.00",
  "overdue_days": "10"
}
```

## Notes

- Data is stored in memory; restarting the app clears all invoices.
- Dates are ISO-8601 (`YYYY-MM-DD`).
- Postman Collection to test all the calls is kept in the root folder
