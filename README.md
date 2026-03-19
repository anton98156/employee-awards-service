# Employee Awards Service

REST API service for uploading and managing company employee awards.
Supports uploading Excel (.xls, .xlsx) and CSV files with automatic data validation and processing.

## ✨ Features

- Uploading Excel and CSV files with employee awards
- Data validation and employee existence checks in the database
- Record processing with detailed error information
- Transactional processing of each record to ensure data integrity

## 🔖 Technologies

- **Java 21**
- **Spring Boot 3.5.7** (Spring MVC)
- **Spring Data JPA**
- **H2 Database** (in-memory)
- **Apache POI 5.4.0** (Excel)
- **OpenCSV 5.9** (CSV)
- **Gradle 8.5+**
- **JUnit 5**

## 🎯 Quick Start

### Requirements
- Java 21+
- Gradle 8.5+

### Installation and Launch

```bash
# Build the project
./gradlew clean build

# Run the application
./gradlew bootRun
```

The application will be available at: `http://localhost:8080`

### Running Tests

```bash
./gradlew test
```

## 📲 API

### POST /api/awards/upload

Uploads and processes an awards file.

**Parameters:**
- `file` (multipart/form-data) - file in CSV, XLS, or XLSX format

**Request example:**

```bash
curl -X POST http://localhost:8080/api/awards/upload \
  -F "file=@awards.csv"
```

**Response example:**

```json
{
  "totalRecords": 10,
  "processedRecords": 8,
  "skippedRecords": 2,
  "errors": [
    "Error processing record (employeeId=999, awardId=500): Employee not found: 999"
  ]
}
```

## 📝 File Format

The file must contain the following columns:

1. **Employee ID** (number)
2. **Employee full name** (text)
3. **Award ID** (number)
4. **Award name** (text)
5. **Award date** (ISO-8601: yyyy-MM-dd)

### CSV format

```csv
Employee ID,Employee full name,Award ID,Award name,Award date
1247,Ivanov Ivan Ivanovich,891,Employee of the Month,2025-03-22
```

### Excel format

The first row contains headers, and the following rows contain data.

## 🧩 Architectural Decisions

### Rationale for Choosing Spring MVC

Spring MVC was chosen because file data processing and persistence are performed sequentially and do not require an asynchronous or reactive approach. MVC is better suited for blocking libraries (Apache POI, OpenCSV), is simpler to implement, test, and debug, and provides reliable and predictable service behavior. WebFlux is optimal for high-load scenarios with many concurrent connections, which is not required in this case.

### Ambiguities / Assumptions Made

**Record processing:**
- Processing is done in a separate transaction for each record, which allows partial data persistence even when some records fail
- If an employee is not found in the database, the record is skipped with logging, while processing of other records continues
- If one record fails, other successfully processed records are persisted, and error details are collected and returned in the upload result
- This approach maximizes processing of valid file data and informs the user about problematic records

**Data models:**
- Models use internal system IDs, with separate fields added for external IDs
- Since system IDs must be unique and are auto-incremented, and the requirements do not specify how IDs in files are populated, there is no reason to assume file IDs match internal system IDs
- Therefore, external IDs are stored in separate fields to avoid potential issues and errors

**Date format:**
- The requirements do not specify whether award time must be stored
- Based on business meaning ("award date"), a calendar date without time is sufficient
- Therefore, the field is implemented as `LocalDate` in ISO-8601 format (`yyyy-MM-dd`)

## 🔄 Configuration

Main settings in `application.yaml`:

- **Port**: 8080
- **Database**: H2 (in-memory)
- **Maximum file size**: 20MB
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:employee_awards`
  - Username: `sa`
  - Password: (empty)

## ✅ Testing

The project includes a full set of tests:
- Unit tests for services, repositories, and parsers
- Integration tests for controllers

Test reports: `build/reports/tests/test/index.html`

## 📂 Project Structure

```text
src/main/java/ru/t2/employeeawards/
├── controller/     # REST API
├── service/        # Business logic
├── repository/     # Data access
├── model/          # Data models
├── parser/         # File parsers
├── validator/      # Validation
├── factory/        # File factory
└── exception/      # Exception handling
```
