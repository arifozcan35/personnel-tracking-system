## 🏢 Personnel Tracking System

This is a comprehensive Spring Boot web backend project designed to manage personnel, units, gates, and working hours, ensuring proper access control and salary calculations.

---

## 🚀 Project Overview
- The Personnel Tracking System aims to streamline and automate personnel management in an institution.
- It covers functionalities like personnel and unit management, gate access permissions, working hours tracking, and salary calculations.

---

## 🧑‍💻 Technologies Used
- Java Spring Boot
- Spring Data JPA
- Spring Web
- Lombok
- MySQL
- PostgreSQL (with docker)
- Docker, Docker Compose
- Postman, Swagger (for testing)
- Maven

---

## 📋 Features

- 👨‍💼 Personnel management (Add, Update, Delete, List)
- 🏢 Unit management (Add, Update, Delete, List)
- 🏢 Entry Authorisation management (Gate access permissions of personnel)
- ⏰ Monitoring working hours and detecting tardiness
- 💰 Salary calculation (Salary deductions according to lateness)
- 📊 Executive privileges (Full access, overtime exemption, high salary)
- 🔍 Management and data extraction via API




- *Personnel Management:*
    - View personnel list
    - Add new personnel (Unit selection is mandatory)
    - Update personnel information
    - Delete personnel
    - Assign personnel as managers


- *Unit Management:*
    - View unit list
    - Add new unit
    - Update unit information
    - Delete unit
    - See the personnel belonging to the unit


- *Gate Management:*
    - View gate list
    - Add new gate
    - Update gate information
    - Delete gate
    - See the personnel belonging to the gate


- *Access Control:*
    - Grant floor access permissions
    - Only personnel can pass through gates


- *Working Hours Tracking:*
    - Working hours set from **09:00 to 18:00**
    - Personnel entry and exit times recorded
    - A total daily discrepancy of 15 minutes is allowed
    - Personnel’s working hour validity is recorded (`valid=1` for valid, `valid=0` for invalid)
    - Managers are exempt from working hour requirements


- *Salary Calculation:*
    - Manager salary: **40,000 TL**
    - Regular personnel salary: **30,000 TL**
    - **-200 TL** penalty for each invalid working day

---


## 🐳 Dockerizing

Put the following Docker codes into the terminal in this order:

- ./mvnw clean package -DskipTests

- docker-compose build springboot-app

- docker-compose up -d

---


## 🛠 Setup and Installation

- *Clone the repository:*
```bash
git clone https://github.com/arifozcan35/personnel-tracking-system.git
