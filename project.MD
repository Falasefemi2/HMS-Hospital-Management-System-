# Hospital Management System (HMS) - Spring Boot Implementation Guide
## A Beginner-Friendly Breakdown for Junior Developers

---

## 📋 Table of Contents
1. Project Overview
2. Core Concepts & Terminology
3. Database Design (Tables & Relationships)
4. Module Breakdown
5. Implementation Roadmap
6. Key Technologies
7. Development Team Roles
8. Common Gotchas & Tips

---

## 1️⃣ Project Overview

### What is HMS?
A **web application** that helps hospitals manage:
- **Patients**: Registration, records, appointments
- **Doctors**: Consultations, prescriptions, test requests
- **Nurses**: Patient vitals, care notes
- **Admins**: System configuration, user management, reports
- **Hospital Operations**: Departments, wards, schedules

### Key Goal
Replace paper-based processes with a digital system that is **secure, fast, and easy to use**.

### Real-World Example
When a patient arrives at the hospital:
1. They register or log in (Patient)
2. A nurse records their vitals (Nurse)
3. The doctor views their history and creates a prescription (Doctor)
4. The admin gets reports on how many patients visited (Admin)

---

## 2️⃣ Core Concepts & Terminology

Before diving into code, understand these terms:

| Term | Meaning |
|------|---------|
| **RBAC** | Role-Based Access Control - Different users see different features based on their role |
| **JWT** | JSON Web Token - A secure way to authenticate users without storing sessions |
| **Entity** | A Java class that represents a database table |
| **Repository** | A Java interface that handles database queries |
| **Service** | Business logic layer (handles calculations, validations) |
| **Controller** | Handles HTTP requests and responses |
| **DTO** | Data Transfer Object - An object to transfer data between layers |
| **Immutable** | Data that cannot be changed once created (important for medical records) |
| **Audit Log** | A record of who accessed/modified what data and when |

---

## 3️⃣ Database Design (Tables & Relationships)

### Core Tables You'll Need

#### 1. **users** (Stores all users - Admins, Doctors, Nurses, Patients)
```sql
CREATE TABLE users (
    user_id UUID PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    phone VARCHAR(20),
    role ENUM('ADMIN', 'DOCTOR', 'NURSE', 'PATIENT') NOT NULL,
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Why this table?** All users (doctors, nurses, admins, patients) need to log in. This stores their credentials and basic info.

---

#### 2. **patients** (Stores patient-specific information)
```sql
CREATE TABLE patients (
    patient_id UUID PRIMARY KEY,
    user_id UUID UNIQUE NOT NULL REFERENCES users(user_id),
    date_of_birth DATE,
    gender ENUM('MALE', 'FEMALE', 'OTHER'),
    blood_group VARCHAR(5),
    emergency_contact_name VARCHAR(255),
    emergency_contact_phone VARCHAR(20),
    medical_history TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Why?** A patient is a special type of user with medical information. Not all users are patients.

---

#### 3. **doctors** (Stores doctor-specific information)
```sql
CREATE TABLE doctors (
    doctor_id UUID PRIMARY KEY,
    user_id UUID UNIQUE NOT NULL REFERENCES users(user_id),
    specialization VARCHAR(255),
    license_number VARCHAR(255) UNIQUE,
    department_id UUID REFERENCES departments(department_id),
    consultation_fee DECIMAL(10, 2),
    is_available BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Why?** Doctors have specializations, license numbers, and fees—info patients need to see.

---

#### 4. **nurses** (Stores nurse-specific information)
```sql
CREATE TABLE nurses (
    nurse_id UUID PRIMARY KEY,
    user_id UUID UNIQUE NOT NULL REFERENCES users(user_id),
    department_id UUID REFERENCES departments(department_id),
    shift VARCHAR(50),
    license_number VARCHAR(255) UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Why?** Track which department nurses work in and their shifts.

---

#### 5. **departments** (Cardiology, Pediatrics, etc.)
```sql
CREATE TABLE departments (
    department_id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    head_doctor_id UUID REFERENCES doctors(doctor_id),
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Why?** Hospitals have multiple departments. Admins manage them.

---

#### 6. **wards** (Wards and rooms within departments)
```sql
CREATE TABLE wards (
    ward_id UUID PRIMARY KEY,
    department_id UUID NOT NULL REFERENCES departments(department_id),
    name VARCHAR(255) NOT NULL,
    capacity INT,
    available_beds INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Why?** Track wards and bed availability.

---

#### 7. **appointments** (Appointment scheduling)
```sql
CREATE TABLE appointments (
    appointment_id UUID PRIMARY KEY,
    patient_id UUID NOT NULL REFERENCES patients(patient_id),
    doctor_id UUID NOT NULL REFERENCES doctors(doctor_id),
    appointment_date TIMESTAMP NOT NULL,
    duration_minutes INT DEFAULT 30,
    status ENUM('PENDING', 'CONFIRMED', 'COMPLETED', 'CANCELLED') DEFAULT 'PENDING',
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Why?** Core feature: patients book appointments with doctors.

---

#### 8. **doctor_availability** (Doctor time slots)
```sql
CREATE TABLE doctor_availability (
    availability_id UUID PRIMARY KEY,
    doctor_id UUID NOT NULL REFERENCES doctors(doctor_id),
    day_of_week VARCHAR(10),
    start_time TIME,
    end_time TIME,
    max_appointments INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Why?** Doctors define when they're available. Prevents double-booking.

---

#### 9. **consultations** (Doctor-patient consultation records)
```sql
CREATE TABLE consultations (
    consultation_id UUID PRIMARY KEY,
    appointment_id UUID NOT NULL REFERENCES appointments(appointment_id),
    patient_id UUID NOT NULL REFERENCES patients(patient_id),
    doctor_id UUID NOT NULL REFERENCES doctors(doctor_id),
    diagnosis TEXT NOT NULL,
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_editable BOOLEAN DEFAULT false -- After 24h, becomes immutable
);
```

**Why?** Medical records must be immutable (cannot be deleted). This tracks doctor's findings.

---

#### 10. **prescriptions** (Doctor prescriptions)
```sql
CREATE TABLE prescriptions (
    prescription_id UUID PRIMARY KEY,
    consultation_id UUID NOT NULL REFERENCES consultations(consultation_id),
    patient_id UUID NOT NULL REFERENCES patients(patient_id),
    doctor_id UUID NOT NULL REFERENCES doctors(doctor_id),
    medication_name VARCHAR(255) NOT NULL,
    dosage VARCHAR(100) NOT NULL,
    frequency VARCHAR(100) NOT NULL, -- e.g., "3 times daily"
    duration_days INT,
    instructions TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_immutable BOOLEAN DEFAULT false
);
```

**Why?** Prescriptions are part of medical records. Must be trackable and immutable.

---

#### 11. **patient_vitals** (Nurse records vital signs)
```sql
CREATE TABLE patient_vitals (
    vital_id UUID PRIMARY KEY,
    patient_id UUID NOT NULL REFERENCES patients(patient_id),
    nurse_id UUID NOT NULL REFERENCES nurses(nurse_id),
    blood_pressure VARCHAR(20), -- e.g., "120/80"
    temperature DECIMAL(5, 2),
    pulse INT,
    weight DECIMAL(6, 2),
    recorded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Why?** Nurses record vitals before consultations. Doctors need this data.

---

#### 12. **lab_tests** (Lab test requests and results)
```sql
CREATE TABLE lab_tests (
    test_id UUID PRIMARY KEY,
    patient_id UUID NOT NULL REFERENCES patients(patient_id),
    doctor_id UUID NOT NULL REFERENCES doctors(doctor_id),
    test_name VARCHAR(255) NOT NULL,
    test_type VARCHAR(100), -- e.g., "BLOOD_TEST", "X_RAY"
    status ENUM('REQUESTED', 'IN_PROGRESS', 'COMPLETED') DEFAULT 'REQUESTED',
    result_file_path VARCHAR(500), -- Path to uploaded result
    notes TEXT,
    requested_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP
);
```

**Why?** Doctors request tests, results are uploaded, stored, and visible to relevant staff.

---

#### 13. **patient_care_notes** (Nurse's observations)
```sql
CREATE TABLE patient_care_notes (
    note_id UUID PRIMARY KEY,
    patient_id UUID NOT NULL REFERENCES patients(patient_id),
    nurse_id UUID NOT NULL REFERENCES nurses(nurse_id),
    appointment_id UUID REFERENCES appointments(appointment_id),
    note TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Why?** Nurses document patient observations for doctors to review.

---

#### 14. **audit_logs** (Track all sensitive actions)
```sql
CREATE TABLE audit_logs (
    log_id UUID PRIMARY KEY,
    user_id UUID REFERENCES users(user_id),
    action VARCHAR(255), -- e.g., "VIEWED_PATIENT_RECORD", "CREATED_PRESCRIPTION"
    resource_type VARCHAR(100), -- e.g., "PATIENT", "APPOINTMENT"
    resource_id UUID,
    changes TEXT, -- JSON format of what changed
    ip_address VARCHAR(50),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Why?** **Compliance requirement!** Hospitals must track who accessed patient data and when. Legal requirement.

---

#### 15. **hospital_config** (System settings)
```sql
CREATE TABLE hospital_config (
    config_id UUID PRIMARY KEY,
    working_hours_start TIME,
    working_hours_end TIME,
    appointment_duration_minutes INT DEFAULT 30,
    max_same_day_cancellation_hours INT DEFAULT 24,
    enable_patient_self_registration BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

**Why?** Admins configure system settings here without touching code.

---

### Entity Relationship Diagram (Simplified)

```
┌─────────────┐
│    users    │
│  (All roles)│
└──────┬──────┘
       │
    ┌──┴──────────────┬──────────────┬──────────────┐
    │                 │              │              │
┌───▼────┐    ┌──────▼──┐   ┌──────▼──┐  ┌────────▼──┐
│patients │    │ doctors │   │ nurses  │  │  admins   │
└───┬────┘    └──────┬──┘   └──────┬──┘  └───────────┘
    │                │             │
    │         ┌──────▼──────────────┘
    │         │
    │    ┌────▼────────┐
    │    │departments  │
    │    │   & wards   │
    │    └─────────────┘
    │
┌───▼──────────────┐
│  appointments    │ ─────┬──────────────────┬─────────────┐
└────────────────┬─┘     │                  │             │
                 │   ┌───▼──────┐   ┌──────▼──┐  ┌───────▼────┐
                 │   │doctor_   │   │consult- │  │patient_    │
                 │   │availab   │   │ations   │  │vitals      │
                 │   └──────────┘   └────┬────┘  └────────────┘
                 │                       │
                 │                   ┌───▼──────┐
                 │                   │prescr-   │
                 │                   │iptions   │
                 │                   └──────────┘
                 │
             ┌───▼─────────────┐
             │   lab_tests     │
             └─────────────────┘
```

---

## 4️⃣ Module Breakdown (What Gets Built & By Whom)

### Module 1: Hospital Administration
**Who uses it?** Hospital Admins only

**What can they do?**
- Create/edit users (doctors, nurses, admins)
- Manage departments and wards
- Configure system settings (working hours, appointment duration)
- View reports and analytics
- Access audit logs

**Database Tables Used:**
- `users`, `doctors`, `nurses`, `departments`, `wards`, `hospital_config`, `audit_logs`

**Spring Boot Components:**
```java
// Controller
@RestController
@RequestMapping("/api/admin")
public class AdminController { ... }

// Service
@Service
public class AdminService { ... }

// Repository
@Repository
public interface UserRepository extends JpaRepository<User, UUID> { ... }
```

---

### Module 2: Patient Management
**Who uses it?** Admins, Nurses, Patients

**What can they do?**
- Register patients (admin or self-registration)
- View patient profiles and medical history
- Update patient information

**Database Tables Used:**
- `users`, `patients`, `appointments`, `consultations`, `prescriptions`, `lab_tests`

**Spring Boot Components:**
```java
@RestController
@RequestMapping("/api/patients")
public class PatientController { ... }

@Service
public class PatientService { ... }
```

---

### Module 3: Appointments & Booking
**Who uses it?** Patients, Admins, Nurses, Doctors

**What can they do?**
- **Patients**: Book, view, cancel appointments
- **Admins/Nurses**: Book on behalf of patients
- **Doctors**: Set availability, view scheduled appointments

**Database Tables Used:**
- `appointments`, `doctor_availability`, `doctors`, `patients`

**Key Logic:**
- Prevent double-booking (check doctor availability)
- No same-day cancellation (configurable in `hospital_config`)
- Auto-generate notifications (future enhancement)

---

### Module 4: Doctor Module
**Who uses it?** Doctors only

**What can they do?**
- View daily appointment dashboard
- Access patient profiles and medical history
- Create consultation notes
- Write prescriptions
- Request lab tests
- View lab results

**Database Tables Used:**
- `appointments`, `consultations`, `prescriptions`, `lab_tests`, `patients`, `patient_vitals`

**Important:** Doctors can only see patients they're assigned to (access control)

---

### Module 5: Nurse Module
**Who uses it?** Nurses only

**What can they do?**
- Record patient vitals (BP, temperature, pulse, weight)
- Write care notes for doctors
- Check in patients for appointments
- Update appointment status

**Database Tables Used:**
- `patient_vitals`, `patient_care_notes`, `appointments`, `patients`

---

### Module 6: Patient Portal
**Who uses it?** Patients only (self-service)

**What can they do?**
- View upcoming appointments
- Book/cancel appointments
- View prescriptions and test results
- Update personal information (limited fields)

**Database Tables Used:**
- `appointments`, `prescriptions`, `lab_tests`, `patients`

**Security:** Patients can only see their own data

---

### Module 7: Medical Records Management
**Who uses it?** All users (but with role-based visibility)

**What happens here?**
- All consultations, prescriptions, lab results are stored here
- **Immutable**: Once created, records cannot be deleted
- **Versioning**: If edited, old versions are kept with timestamps
- **Audit trail**: Every access is logged

**Key Rules:**
- Consultation can be edited only within 24 hours
- After 24 hours, marked as immutable
- All deletions are audited (actually soft-deletes: mark as deleted, not removed)

---

### Module 8: Security & Compliance
**Who manages it?** Admin (with automatic system enforcement)

**What's included?**
- **JWT Authentication**: Secure login without storing sessions
- **Password Hashing**: Passwords never stored as plaintext (bcrypt/argon2)
- **Role-Based Access Control (RBAC)**: Users see only their permitted features
- **Audit Logs**: Track all sensitive actions
- **Data Encryption**: In-transit (HTTPS) and at-rest (database encryption optional)

---

## 5️⃣ Implementation Roadmap (Step-by-Step)

### Phase 1: Setup & Foundation (Week 1)
- [ ] Create Spring Boot project with dependencies
- [ ] Configure PostgreSQL database
- [ ] Create all entity classes (User, Patient, Doctor, etc.)
- [ ] Setup Spring Security with JWT
- [ ] Create user authentication endpoints

### Phase 2: Core Data Models (Week 2)
- [ ] Implement Patient Management (CRUD operations)
- [ ] Implement Doctor Management
- [ ] Implement Department & Ward Management
- [ ] Create repositories for each entity

### Phase 3: Appointment System (Week 3)
- [ ] Create Appointment entity and repository
- [ ] Implement appointment booking logic (prevent double-booking)
- [ ] Create doctor availability management
- [ ] Implement cancellation logic with time restrictions

### Phase 4: Doctor Features (Week 4)
- [ ] Create Consultation entity
- [ ] Implement prescription creation
- [ ] Implement lab test request feature
- [ ] Create doctor dashboard endpoint

### Phase 5: Nurse Features (Week 5)
- [ ] Implement patient vitals recording
- [ ] Implement patient care notes
- [ ] Appointment check-in functionality

### Phase 6: Patient Portal (Week 6)
- [ ] Patient self-registration
- [ ] Patient appointment booking
- [ ] View personal medical records
- [ ] View prescriptions and results

### Phase 7: Admin Dashboard & Reports (Week 7)
- [ ] User management endpoints
- [ ] Hospital configuration management
- [ ] Reports and analytics endpoints
- [ ] Audit log viewing

### Phase 8: Testing & Deployment (Week 8)
- [ ] Unit tests for services
- [ ] Integration tests
- [ ] Security testing
- [ ] Deployment to server

---

## 6️⃣ Key Technologies

### Backend Stack
```
Spring Boot 3.x
├── Spring Data JPA (Database access)
├── Spring Security (Authentication & Authorization)
├── Spring Validation (Input validation)
└── Spring Web MVC (REST APIs)

PostgreSQL (Database)
├── UUID for all IDs (more secure than auto-increment)
└── ENUM types for roles and statuses

JWT (JSON Web Tokens)
└── For stateless authentication

Maven (Dependency Management)
```

### Recommended Dependencies (for pom.xml)
```xml
<!-- Core Spring Boot -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- Database -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Security & JWT -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.13.0</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.13.0</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.13.0</version>
    <scope>runtime</scope>
</dependency>

<!-- Validation -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- Lombok (reduces boilerplate) -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>

<!-- Testing -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.springframework.security</groupId>
    <artifactId>spring-security-test</artifactId>
    <scope>test</scope>
</dependency>
```

---

## 7️⃣ Development Team Roles in a Real Company

### Backend Development Team

| Role | Responsibilities | Example Tasks |
|------|-----------------|----------------|
| **Senior Developer** | Architecture, code review, mentoring | Design database schema, review junior's code, solve complex issues |
| **Mid-Level Developer** | Feature implementation, unit testing | Implement Doctor module, write service logic |
| **Junior Developer (YOU)** | Implement assigned features under supervision | Implement patient registration, create CRUD endpoints |
| **DevOps Engineer** | Deployment, server setup, CI/CD | Set up PostgreSQL, deploy to production server |
| **QA Engineer** | Testing, bug reporting | Write test cases, test appointment booking feature |
| **Database Admin** | Database optimization, backups | Create backup strategy, optimize slow queries |

### Frontend Team (Different from You, But Good to Know)
- Build Angular/React UI that calls your REST APIs

### Project Management
- **Product Manager**: Decides what features to build
- **Scrum Master**: Runs daily standups, removes blockers

### Example Daily Standup (15 mins)
```
Junior Dev: "Yesterday I completed patient login. Today I'll work on patient 
            registration endpoint. Blocker: Need DB schema from senior dev."
Senior Dev: "I'll send the schema today. Also review your login code later."
QA: "I tested the login feature, found a bug with password reset."
```

---

## 8️⃣ Common Gotchas & Tips

### ❌ Gotcha 1: Forgetting RBAC
**Problem:** You let any logged-in user access any data
```java
// ❌ WRONG - No access control
@GetMapping("/patients/{patientId}")
public Patient getPatient(@PathVariable UUID patientId) {
    return patientRepository.findById(patientId).orElse(null);
}
```

**Solution:** Check user's role and data ownership
```java
// ✅ CORRECT - With access control
@GetMapping("/patients/{patientId}")
public Patient getPatient(@PathVariable UUID patientId, @AuthenticationPrincipal User user) {
    Patient patient = patientRepository.findById(patientId).orElse(null);
    
    // Only patient can view own data, doctor can view assigned patients, admin can view all
    if (!user.hasPermissionToView(patient)) {
        throw new AccessDeniedException("You cannot view this patient");
    }
    return patient;
}
```

---

### ❌ Gotcha 2: Storing Passwords in Plaintext
**Problem:**
```java
// ❌ WRONG
user.setPassword(password); // Storing plaintext password!
```

**Solution:** Use Spring Security's password encoder
```java
// ✅ CORRECT
user.setPassword(passwordEncoder.encode(password)); // Hashed password
```

---

### ❌ Gotcha 3: Deleting Medical Records
**Problem:** You permanently delete a prescription
```java
// ❌ WRONG
prescriptionRepository.delete(prescription);
```

**Solution:** Soft delete (mark as deleted, don't remove)
```java
// ✅ CORRECT
@Column(name = "is_deleted")
private Boolean isDeleted = false;

// In repository query, exclude deleted records
@Query("SELECT p FROM Prescription p WHERE p.isDeleted = false")
List<Prescription> findAllActive();
```

---

### ❌ Gotcha 4: No Audit Logging
**Problem:** You don't track who accessed patient data (compliance violation!)

**Solution:** Log all sensitive actions
```java
@Service
public class PatientService {
    
    public Patient getPatient(UUID patientId, UUID userId) {
        Patient patient = patientRepository.findById(patientId).orElse(null);
        
        // Log this access
        auditLogRepository.save(new AuditLog(
            userId, 
            "VIEWED_PATIENT_RECORD", 
            "PATIENT", 
            patientId
        ));
        
        return patient;
    }
}
```

---

### ❌ Gotcha 5: Allowing Double-Booking
**Problem:** Two patients book same doctor at same time
```java
// ❌ WRONG - No check for existing appointments
appointmentRepository.save(newAppointment);
```

**Solution:** Check availability before booking
```java
// ✅ CORRECT
public void bookAppointment(UUID doctorId, LocalDateTime dateTime) {
    // Check if doctor already has appointment at this time
    int existingAppointments = appointmentRepository.countByDoctorAndDateTime(doctorId, dateTime);
    
    if (existingAppointments > 0) {
        throw new AppointmentUnavailableException("Time slot already booked");
    }
    
    appointmentRepository.save(newAppointment);
}
```

---

### ✅ Tip 1: Use DTOs for API Responses
**Why?** Don't expose internal database structure to frontend
```java
// ✅ Entity (Database)
@Entity
public class Patient {
    private UUID patientId;
    private UUID userId;
    private String bloodGroup;
    private LocalDate dateOfBirth;
    private String medicalHistory;
}

// ✅ DTO (API Response - Only what frontend needs)
@Data
public class PatientDTO {
    private UUID patientId;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    // Sensitive info like userId is NOT included
}
```

---

### ✅ Tip 2: Use Java Enums for Roles and Statuses
```java
// ✅ GOOD - Type-safe, prevents invalid values
public enum UserRole {
    ADMIN, DOCTOR, NURSE, PATIENT
}

public enum AppointmentStatus {
    PENDING, CONFIRMED, COMPLETED, CANCELLED
}

// Usage
User user = new User();
user.setRole(UserRole.DOCTOR); // Compiler checks this is valid
```

---

### ✅ Tip 3: Validate Input at Service Layer
```java
// ✅ GOOD - Multiple layers of validation
@PostMapping("/appointments")
public Appointment bookAppointment(@Valid @RequestBody AppointmentRequest request) {
    // Controller validates basic format (email, date format, etc.)
    
    return appointmentService.bookAppointment(request);
    // Service validates business logic (appointment in future, doctor exists, etc.)
}

@Service
public class AppointmentService {
    public Appointment bookAppointment(AppointmentRequest request) {
        // Business logic validation
        if (request.getAppointmentDate().isBefore(LocalDateTime.now())) {
            throw new InvalidAppointmentException("Cannot book past appointments");
        }
        
        if (!doctorExists(request.getDoctorId())) {
            throw new DoctorNotFoundException("Doctor not found");
        }
        
        // Create appointment
        return appointmentRepository.save(new Appointment(request));
    }
}
```

---

### ✅ Tip 4: Use @Transactional for Multi-Step Operations
```java
// ✅ GOOD - If any step fails, entire operation rolls back
@Transactional
public void completeAppointment(UUID appointmentId) {
    // Step 1: Update appointment status
    Appointment appointment = appointmentRepository.findById(appointmentId).orElse(null);
    appointment.setStatus(AppointmentStatus.COMPLETED);
    appointmentRepository.save(appointment);
    
    // Step 2: Create consultation record
    Consultation consultation = new Consultation(appointment);
    consultationRepository.save(consultation);
    
    // Step 3: Record audit log
    auditLogRepository.save(new AuditLog(...));
    
    // If Step 3 fails, all changes are rolled back
}
```

---

### ✅ Tip 5: Structure Your Project Like This
```
src/main/java/com/hospital/
├── config/
│   ├── SecurityConfig.java          # JWT & Spring Security setup
│   └── DatabaseConfig.java
├── entities/
│   ├── User.java
│   ├── Patient.java
│   ├── Doctor.java
│   ├── Nurse.java
│   ├── Appointment.java
│   ├── Consultation.java
│   ├── Prescription.java
│   └── ... (other entities)
├── dto/
│   ├── UserDTO.java
│   ├── PatientDTO.java
│   ├── AppointmentDTO.java
│   └── ... (other DTOs)
├── repositories/
│   ├── UserRepository.java
│   ├── PatientRepository.java
│   ├── AppointmentRepository.java
│   └── ... (other repositories)
├── services/
│   ├── UserService.java
│   ├── PatientService.java
│   ├── AppointmentService.java
│   ├── AuthenticationService.java
│   └── ... (other services)
├── controllers/
│   ├── AuthController.java
│   ├── PatientController.java
│   ├── AppointmentController.java
│   ├── DoctorController.java
│   ├── AdminController.java
│   └── ... (other controllers)
├── exceptions/
│   ├── ResourceNotFoundException.java
│   ├── UnauthorizedException.java
│   └── ... (custom exceptions)
├── security/
│   ├── JwtTokenProvider.java
│   └── CustomUserDetailsService.java
└── HmsApplication.java              # Main entry point
```

---

## 9️⃣ Example: Building a Simple Feature (Patient Registration)

### Step 1: Create Entity
```java
@Entity
@Table(name = "patients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID patientId;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    private LocalDate dateOfBirth;
    private String gender;
    private String bloodGroup;
    private String emergencyContactName;
    private String emergencyContactPhone;
    private String medicalHistory;
    
    @CreationTimestamp
    private LocalDateTime createdAt;
}
```

### Step 2: Create Repository
```java
@Repository
public interface PatientRepository extends JpaRepository<Patient, UUID> {
    Optional<Patient> findByUserId(UUID userId);
    List<Patient> findByBloodGroup(String bloodGroup);
}
```

### Step 3: Create DTO for Registration
```java
@Data
public class PatientRegistrationRequest {
    @NotBlank
    private String firstName;
    
    @NotBlank
    private String lastName;
    
    @Email
    private String email;
    
    @NotBlank
    private String password;
    
    @NotNull
    private LocalDate dateOfBirth;
    
    private String bloodGroup;
    private String emergencyContactPhone;
}
```

### Step 4: Create Service
```java
@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientRepository patientRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Transactional
    public Patient registerPatient(PatientRegistrationRequest request) {
        // Validate user doesn't already exist
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already registered");
        }
        
        // Create user account
        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.PATIENT);
        user.setActive(true);
        userRepository.save(user);
        
        // Create patient record
        Patient patient = new Patient();
        patient.setUser(user);
        patient.setDateOfBirth(request.getDateOfBirth());
        patient.setBloodGroup(request.getBloodGroup());
        patient.setEmergencyContactPhone(request.getEmergencyContactPhone());
        patientRepository.save(patient);
        
        return patient;
    }
}
```

### Step 5: Create Controller
```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final PatientService patientService;
    
    @PostMapping("/register")
    public ResponseEntity<?> registerPatient(
            @Valid @RequestBody PatientRegistrationRequest request) {
        try {
            Patient patient = patientService.registerPatient(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(patient);
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
```

### Step 6: Test It
```java
@SpringBootTest
@AutoConfigureMockMvc
public class PatientRegistrationTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    public void testPatientRegistration() throws Exception {
        PatientRegistrationRequest request = new PatientRegistrationRequest();
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john@example.com");
        request.setPassword("password123");
        request.setDateOfBirth(LocalDate.of(1990, 5, 15));
        
        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isCreated());
    }
}
```

---

## 🎯 Final Checklist Before You Start Coding

- [ ] Database schema designed and reviewed by senior dev
- [ ] Spring Boot project setup with correct dependencies
- [ ] PostgreSQL installed and running
- [ ] JWT configuration ready
- [ ] Spring Security configured
- [ ] Project structure created
- [ ] First entity and repository created as a test
- [ ] Basic CRUD endpoint working
- [ ] Git repository initialized
- [ ] Development environment setup (IDE, Maven, etc.)

---

## 📚 Resources for Learning

1. **Spring Boot Documentation**: https://spring.io/projects/spring-boot
2. **Spring Data JPA**: https://spring.io/projects/spring-data-jpa
3. **Spring Security**: https://spring.io/projects/spring-security
4. **JWT Tutorial**: https://www.baeldung.com/spring-boot-basic-auth
5. **PostgreSQL Basics**: https://www.postgresql.org/docs/

---

## 🚀 Your First Steps

1. **Read this guide** - You're doing this! ✓
2. **Setup your environment** - Install Spring Boot, PostgreSQL
3. **Create the project** - Use Spring Initializr (start.spring.io)
4. **Create one entity** - Start with `User` entity
5. **Create one repository** - `UserRepository`
6. **Create one service** - `UserService`
7. **Create one controller** - `UserController` with GET/POST endpoints
8. **Test it** - Use Postman to call your endpoints
9. **Repeat for other entities** - Patient, Doctor, etc.
10. **Ask your senior dev for code review**

Good luck! 🎉
