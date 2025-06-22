# Camunda 7 to Camunda 8 Migration

This repository contains the practical implementation of a migration from Camunda 7 to Camunda 8, developed as part of a bachelor thesis analyzing technical challenges and solutions for this migration process.

## Project Overview

This project demonstrates the migration of a complex business process (exam registration) from Camunda 7 to Camunda 8. The implementation includes:

- **Processes**:
  exam-registration.bpmn
  ![exam-registration](https://github.com/user-attachments/assets/a97ea40f-bf40-4f1d-8d28-03ac0915d71d)
  initial-existence-check.bpmn
  ![initial-existence-check](https://github.com/user-attachments/assets/1dc0d086-d9b5-4d89-8e10-1ff5c547445f)
  revise-course-size.bpmn
  ![revise-course-size](https://github.com/user-attachments/assets/1cd26020-d6bf-48da-ad0c-727e4e1546a9)

## Migration Flow

**Starting Point**: [release/camunda7](https://github.com/wall030/camunda-7-to-8-migration/tree/release/camunda7) - Original Camunda 7 implementation

**Migration Process**: [feature/migration](https://github.com/wall030/camunda-7-to-8-migration/tree/feature/migration) - Follow the commit history to see each migration step

**End Result**: [release/camunda8](https://github.com/wall030/camunda-7-to-8-migration/tree/release/camunda8) - Complete Camunda 8 implementation with refactored code

> **Note**: Tests for the original Camunda 7 version are preserved in the [release/camunda7](link) branch.
> 


## Camunda 8 Project:
## Prerequisites

- **Java 21+**
- **Docker and Docker Compose**
- **Camunda 8 SaaS Account** for the Camunda 8 Demo
- **Node.js 10+** (for external job worker)


## Installation & Setup

1. **Clone the repository**
```bash
git clone https://github.com/wall030/camunda-7-to-8-migration
```

2. **Configure Environment variables**

Edit the environment variables in [docker compose file](c8/compose.yaml). Edit the file with your credentials for Camunda 8 SaaS and the Mailservice

3. **Run the Project**

```bash
./start.sh
````
or
```bash
./clean-start.sh
````

4. **Start a Process Instance and complete user tasks**

Use the [Postman Collection](c8/Camunda%20Migration.postman_collection.json) to start the exam-registration process, assign or complete user tasks.

Possible Path of the Process:
1. Start the process "exam-registration".
2. use Email "cassian@mail.com" and select prerequisites you have and type the course you want to enroll. 
3. If you are past the deadline the process will stop. You will get a User Task where you can decide to cancel or justify 
4. As examination office member (ba) or technical service (rt)
    - You will get a user task where you can decide to accept or decline the justification (depends on justification type, technical or personal reason)
    - accepting results in successfuly assigning the courses to student "ca"
5. You can check the assignment in the DB in table "student_course" and get an confirmation email, if you start the process with an course that is already enrolled or doesnt exist the process will stop and you get a information mail.
6. Feel free to test other paths :)

## User Accounts

| **Role**               | **Username** | **Group**             | **Password** |
|------------------------|--------------|-----------------------|--------------|
| Student                | ca           | students              | demo         |
| Examination Office    | ba           | examOffice    | demo         |
| Technical Service     | rt           | technicalService    | demo         |
| Admin                  | demo         | -                     | demo         |

## Database

The database contains process variables, like enrollments or justifications, that are persisted during service task execution.

| Key        | Value                                          |
|------------|------------------------------------------------|
| `url`      | `jdbc:postgresql://localhost:5432/business_db` |
| `username` | `postgres`                                     |
| `password` | `secret`                                       |


## Camunda 7 Project:
## Prerequisites

- **Java 17+**
- **Docker and Docker Compose**
- **No Accounts required**, runs fully local
- **Node.js 10+** (for external job worker)


## Installation & Setup
Installation and setup follows the same approach as the Camunda 8 project. Note that the embedded process engine in Camunda 7 requires its own database for process data, in addition to the business database used in the Camunda 8 version.

Camunda Database:

| Key        | Value                                         |
|------------|-----------------------------------------------|
| `url`      | `jdbc:postgresql://localhost:5433/camunda_db` |
| `username` | `postgres`                                    |
| `password` | `secret`                                      |
