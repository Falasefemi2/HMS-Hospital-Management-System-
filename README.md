# Hospital Management System (Backend)

This is a Spring Boot–based Hospital Management System designed to handle appointments, consultations, and prescriptions with proper role-based access control. The system follows **clean architecture**, **REST best practices**, and **real-world healthcare workflows**.

This project is database-agnostic and works seamlessly with **PostgreSQL / Neon DB**.

---

## Features

### User & Role Management

* Roles: **ADMIN**, **DOCTOR**, **LAB_TECH**, **PATIENT**
* Role-based authorization enforced at the service layer

### Appointments

* Patients can book appointments with doctors
* Each appointment represents a single hospital visit

### Consultations

* One-to-one relationship with appointments
* Created and owned by a doctor
* Stores diagnosis and consultation notes

### Prescriptions

* A consultation can have **multiple prescriptions**
* Each prescription includes medication, dosage, and duration
* Prescriptions are viewable only by:

  * The prescribing doctor
  * The patient
  * Admin users

### Departments

* Doctors and lab technicians can belong to one or more departments
* Admin assigns staff to departments

### Medical Tests

* Doctors can request medical tests for patients during a consultation
* Each test request is linked to a consultation
* Lab technicians record test results
* Patients and doctors can view completed test results

---

## Domain Model Overview

```
Appointment 1 ─── 1 Consultation
Consultation 1 ─── * Prescription
Doctor      1 ─── * Consultation
Department  * ─── * User (Doctor/Nurse)
```

### Key Design Decisions

* **UUIDs** used as primary keys
* **Foreign keys live on the "many" side** of relationships
* **Consultation is the aggregate root** for prescriptions
* DTOs used for all API responses (entities never exposed)

---

## Tech Stack

* **Java 21+**
* **Spring Boot**
* **Spring Data JPA (Hibernate)**
* **PostgreSQL / Neon DB**
* **Spring Validation**
* **Lombok** (optional)

---

## Project Structure

```
src/main/java/com/example/hms
│
├── controller      # REST controllers
├── service         # Business logic & authorization
├── repository      # JPA repositories
├── entity          # JPA entities
├── dto
│   ├── request     # Request DTOs
│   └── response    # Response DTOs
├── security        # Authentication & authorization
└── config          # App configuration
```

---

## Security Model

Authorization is enforced in the **service layer**, not the controller.

### Examples:

* Only the assigned **doctor** can create prescriptions for a consultation
* Patients can only view **their own** prescriptions
* Admin users have full access

---

## Sample DTO

```java
public record PrescriptionResponse(
        UUID prescriptionId,
        UUID consultationId,
        String medication,
        String dosage,
        String duration,
        Instant createdAt
) {}
```

---

## Getting Started

### Prerequisites

* Java 17+
* Maven
* PostgreSQL / Neon DB

### Configuration

```properties
spring.datasource.url=jdbc:postgresql://<host>/<db>?sslmode=require
spring.datasource.username=<username>
spring.datasource.password=<password>

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### Run the application

```bash
mvn spring-boot:run
```

---

## Future Enhancements

* JWT-based authentication
* Pagination & filtering
* Advanced medical test workflows
* Audit logging
* Soft deletes
* OpenAPI / Swagger documentation

---

## Design Principles Followed

* Separation of concerns
* Aggregate-root consistency
* No business logic in controllers
* No entity exposure to API
* Real-world healthcare modeling

---

## License

This project is intended for educational and portfolio purposes.

---

## Author

Built with a focus on **real-world backend engineering**, not just CRUD.
