## 🏢 Personnel Tracking System

This is a comprehensive Spring Boot web backend project designed to manage personnel, units, gates, and working hours, ensuring proper access control and salary calculations.

---

## 🚀 Project Overview
- The Personnel Tracking System aims to streamline and automate personnel management in an institution.
- It covers functionalities like personnel and unit management, gate access permissions, working hours tracking, and salary calculations.
- It uses Apache Kafka for event-driven communication. The system tracks personnel movement through gates and validates their work hours, sending email notifications based on the results.

---

## 🧑‍💻 Technologies Used
- Java Spring Boot
- Spring Data JPA
- Spring Web
- Spring Security
- JWT Authentication
- Java Mail Sender
- Lombok
- Thymeleaf
- PostgreSQL, Liquibase
- Apache Kafka
- Redis, Hazelcast
- Docker, Docker Compose
- Postman, Swagger (for testing)
- Maven

---

## 📋 Features

- 👨‍💼 Personnel management (Add, Update, Delete, List)
- 🏢 Building, unit, floor, gate, turnstile management (Add, Update, Delete, List and logic procedures)
- 🚪 Entry Authorisation management (Gate and turnstile access permissions of personnel)
- ⏰ Monitoring working hours and detecting tardiness
- 💰 Salary calculation (Salary deductions according to lateness)
- 📊 Executive privileges (Full access, overtime exemption, high salary)
- 🔍 Management and data extraction via API
- 🕵️‍♂️ Role Mechanisms via APIs
- 📧 Sending Mail to Personnel with Apache Kafka
- ⏳ Cache operations with Redis and Hazelcast




- *Personnel, Floor, Unit, Gate Management:*
    - Viewing
    - Adding
    - Updating
    - Deleting


- *Turnstile Management:*
    - Viewing
    - Adding
    - Updating
    - Deleting
    - Turnstile passage calculations and procedures
    - List of personnel passing through the turnstile


- *Role Mechanism:*
    - Using ADMIN and USER roles to access different system APIs
    - Different permissions depending on roles


- *Access Control:*
    - Grant floor access permissions
    - Only personnel can pass through gates


- *Working Hours Tracking:*
    - Working hours set from **09:00 to 18:00**
    - Personnel entry and exit times recorded
    - A total daily discrepancy of 15 minutes is allowed
    - Personnel's working hour validity is recorded (`valid=1` for valid, `valid=0` for invalid)
    - Managers are exempt from working hour requirements


- *Salary Calculation:*
    - Manager salary: **60,000 TL**
    - Regular personnel salary: **50,000 TL**
    - **-300 TL** penalty for each invalid working day


- *Mail Sending:*
    - Sending an e-mail to the admin of the relevant staff member according to the entry time.
    - In the e-mail, information is given about whether the working hours of the relevant staff member are valid and whether salary deductions have been made.

---


## Kafka Features
The system uses Kafka for the following workflows:
1. **Gate Passage Tracking**: When personnel passes through a gate, a message is published to the `gate-passage-events` topic.
2. **Work Validation**: A consumer listens to gate passages and validates work hours, publishing to the `work-validation-events` topic.
3. **Email Notifications**: Based on work validation results, email notifications are sent through the `email-notification-events` topic.


## Setup Apache Kafka

### Install Kafka
1. Download Apache Kafka from the [official website](https://kafka.apache.org/downloads)
2. Extract the downloaded file:
   ```
   tar -xzf kafka_2.13-3.5.1.tgz
   cd kafka_2.13-3.5.1
   ```

### Start Kafka Environment
#### For Linux/Mac
1. Start the ZooKeeper service:
   ```
   bin/zookeeper-server-start.sh config/zookeeper.properties
   ```

2. In a new terminal, start the Kafka broker service:
   ```
   bin/kafka-server-start.sh config/server.properties
   ```

#### For Windows
1. Start the ZooKeeper service:
   ```
   .\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties
   ```

2. In a new terminal, start the Kafka broker service:
   ```
   .\bin\windows\kafka-server-start.bat .\config\server.properties
   ```

## Running the Application
1. Make sure PostgreSQL is running
2. Ensure Kafka is running as described above
3. Build the application:
   ```
   mvn clean install
   ```
4. Run the application:
   ```
   mvn spring-boot:run
   ```

## Testing Kafka Integration
1. Use the `/api/gate/personelpass/{gateId}` endpoint with a personnel ID to simulate a gate passage
2. The system will:
   - Publish a gate passage event to Kafka
   - Process the event to validate work hours
   - Generate an email notification based on the validation result

## Kafka Topics
- `gate-passage-events`: Records when personnel pass through gates
- `work-validation-events`: Contains work hour validation results
- `email-notification-events`: Triggers email notifications to personnel

### For Linux/Mac
```
# List topics
bin/kafka-topics.sh --list --bootstrap-server localhost:9092

# Consume messages from a topic
bin/kafka-console-consumer.sh --topic gate-passage-events --from-beginning --bootstrap-server localhost:9092
```

### For Windows
```
# List topics
.\bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092

# Consume messages from a topic
.\bin\windows\kafka-console-consumer.bat --topic gate-passage-events --from-beginning --bootstrap-server localhost:9092
```

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
