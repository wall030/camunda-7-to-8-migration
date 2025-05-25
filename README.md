# Quickstart Guide   

This guide will walk you through the steps to set up and run the **Camunda Exam Registration Application** project.

## Technology
In this project the following was used:
- **Docker** and **Docker Compose**
- **Java 17**
- **Gradle**

## Process Models
exam-registration.bpmn
![exam-registration](https://github.com/user-attachments/assets/a97ea40f-bf40-4f1d-8d28-03ac0915d71d)
initial-existence-check.bpmn
![initial-existence-check](https://github.com/user-attachments/assets/1dc0d086-d9b5-4d89-8e10-1ff5c547445f)
revise-course-size.bpmn
![revise-course-size](https://github.com/user-attachments/assets/1cd26020-d6bf-48da-ad0c-727e4e1546a9)



---

## 1. Run Docker Compose

In the root directory, run the following command to start the whole project with the clean start script. Alternatively run docker compose up in the backend directory after building the app. This starts 4 Containers (Spring App, Camunda DB, Business DB, External Task Client):

```bash
./clean-start.sh
```

```bash
./gradlew build
```

Your application should now be running and accessible at: http://localhost:8080

## 3. User Accounts

| **Role**               | **Username** | **Group**             | **Password** |
|------------------------|--------------|-----------------------|--------------|
| Student                | ca           | students              | demo         |
| Examination Office    | ba           | examOffice    | demo         |
| Technical Service     | rt           | technicalService    | demo         |
| Admin                  | demo         | -                     | demo         |


## 2. Start a Process

Currently there is only the "Exam Registration" available.

1. Log in to Camunda Tasklist as the student (ca):
   - Go to the Camunda web interface at: http://localhost:8080/camunda / http://localhost:8080/camunda/app/tasklist
   - Start the process "exam-registration" in start process tab
   - use Email "cassian@mail.com" and select prerequisites you have and the type the course you want to enroll. Then hit start.
   - If you are past the deadline the process will stop. You will get a User Task where you can decide to cancel or justify 

2. Log in to Tasklist as examination office member (ba) or technical service (rt)
   - You will get a user task where you can decide to accept or decline the justification (depends on justification type, technical or personal reason)
   - accepting, results in successfuly assigning the courses to student "ca"
  
3. You can check the assignment in the DB in table "student_course" and get an confirmation email, if you start the process with an course that is already enrolled or doesnt exist the process will stop and you get a information mail.
4. Feel free to test other paths :)

## 4. Database

| Key        | Value                                         |
|------------|-----------------------------------------------|
| `url`      | `jdbc:postgresql://localhost:5433/camunda_db` |
| `username` | `postgres`                                    |
| `password` | `secret`                                      |



