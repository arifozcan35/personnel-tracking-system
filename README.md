## 🏢 Personnel Tracking System

This is a comprehensive Spring Boot web backend project designed to manage personnel, units, gates, and working hours, ensuring proper access control and salary calculations.

---

## 🚀 Project Overview
- The Personnel Tracking System aims to streamline and automate personnel management in an institution.
- It covers functionalities like personnel and unit management, gate access permissions, working hours tracking, and salary calculations.
- It uses Apache Kafka for event-driven communication. The system tracks personnel movement through gates and validates their work hours, sending email notifications based on the results.

---

## 🛠️ Requirements

### Minimum System Requirements
- **Java**: 21 or higher
- **Maven**: 3.8 or higher
- **PostgreSQL**: 13 or higher
- **Apache Kafka**: 2.8 or higher
- **Redis**: 6.0 or higher
- **Docker & Docker Compose**: (Optional, for containerized deployment)

---

## 🧑‍💻 Technologies Used
- **Java Spring Boot** 3.3.4
- **Spring Security** + JWT Authentication
- **Spring Data JPA** + PostgreSQL
- **Apache Kafka** (Event-Driven Architecture)
- **Redis** (Cache Layer)
- **Hazelcast** (Distributed Cache)
- **Java Mail Sender** (Email Notifications)
- **Lombok** (Code Generation)
- **MapStruct** (Object Mapping)
- **Thymeleaf** (Template Engine)
- **SpringDoc OpenAPI** (Swagger Documentation)
- **Liquibase** (Database Migration)
- **Docker & Docker Compose** (Containerization)
- **JUnit** (Testing)
- **Maven** (Build Tool)

---

## 📋 Features

### 👨‍💼 Personnel Management
- Add, update, delete, and list personnel
- Salary calculation based on different personnel types
- Executive privileges (full access, overtime exemption, high salary)

### 🏢 Structural Management
- Building, unit, floor, gate, turnstile management
- Add, update, delete, list operations and business logic procedures

### 🚪 Access Control
- Gate and turnstile access permission management
- Only authorized personnel can pass through gates
- Floor access permission granting

### ⏰ Working Hours Tracking
- Working hours set from **09:00 to 18:00**
- Personnel entry and exit time recording
- Daily total 15-minute deviation tolerance
- Tardiness detection

### 💰 Salary Calculation
- Different salaries based on personnel types
- **-300 TL** penalty for each invalid working day
- Automatic salary deductions

### 🔐 Security & Authorization
- JWT-based authentication
- ADMIN and USER roles
- Different API access permissions based on roles

### 📧 Email Notifications
- Asynchronous email sending with Apache Kafka
- Notifications to relevant personnel's manager based on entry time
- Information about work hours validity and salary deductions

### 📊 Cache Management
- **Redis**: Main cache layer
- **Hazelcast**: Distributed cache solution
- Performance optimization

### 🔍 API Management
- RESTful API design
- Swagger/OpenAPI documentation
- Role-based API access

---

## 📦 Installation and Configuration

### 1. Installing Required Software

#### Java 21 Installation
```bash
# Windows - Download Oracle JDK or OpenJDK from official website

# macOS
brew install openjdk@21
```

#### Maven Installation
```bash
# Windows - Download Maven from official website and add to PATH

# macOS
brew install maven
```

### 2. PostgreSQL Installation and Configuration

#### PostgreSQL Installation
```bash
# Windows - Download PostgreSQL from official website

# macOS
brew install postgresql
```

#### Database Creation
```sql
-- Connect to PostgreSQL

-- Create database and user
CREATE DATABASE dbpts;
CREATE USER postgres WITH PASSWORD 'your-password';
GRANT ALL PRIVILEGES ON DATABASE dbpts TO postgres;
```

### 3. Apache Kafka Installation

#### Kafka Download and Installation
```bash
# Download Kafka
wget https://downloads.apache.org/kafka/2.13-3.5.1/kafka_2.13-3.5.1.tgz
tar -xzf kafka_2.13-3.5.1.tgz
cd kafka_2.13-3.5.1
```

#### Starting Kafka Services

##### Linux/Mac
```bash
# Start ZooKeeper
bin/zookeeper-server-start.sh config/zookeeper.properties

# Start Kafka broker in a new terminal
bin/kafka-server-start.sh config/server.properties
```

##### Windows
```bash
# Start ZooKeeper
.\bin\windows\zookeeper-server-start.bat .\config\zookeeper.properties

# Start Kafka broker in a new terminal
.\bin\windows\kafka-server-start.bat .\config\server.properties
```

#### Kafka Monitoring
```bash
# Start Kafdrop
java -jar kafdrop-4.1.0.jar --kafka.brokerConnect=localhost:9092

# And go to localhost:9000 to monitor it
```


### 4. Redis Installation

#### Redis Installation
```bash
# Windows - Download Redis from Microsoft Store or official website
# macOS
brew install redis
```

#### Starting Redis Service
```bash
# Linux/macOS
redis-server

# Start as service (Linux)
sudo systemctl start redis-server
sudo systemctl enable redis-server

# Connection test
redis-cli ping
# Response: PONG
```

#### Redis Monitoring
Download Redis Insight and connect to database


### 5. Hazelcast Configuration
Hazelcast runs embedded within the application and requires no additional installation. Configuration is available in the `application.properties` file.

---

## 🚀 Application Installation

### 1. Clone the Repository
```bash
git clone https://github.com/arifozcan35/personnel-tracking-system.git
cd personnel-tracking-system
```

### 2. Edit Configuration File
Make necessary settings in the `src/main/resources/application.properties` file:

```properties
# PostgreSQL connection settings
spring.datasource.url=jdbc:postgresql://localhost:5432/your-database-name
spring.datasource.username=your-username
spring.datasource.password=your-password

# Email settings (for Gmail)
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password

# Change JWT secret key
app.jwt.secret=your-secret-key-here
```

### 3. Build with Maven
```bash
./mvnw clean install
```

### 4. Run the Application
```bash
./mvnw spring-boot:run
```

The application will start running at `http://localhost:8080`.

---

## 🐳 Docker Installation

### Full System Installation with Docker Compose
```bash
# Maven build (skipping tests)
./mvnw clean package -DskipTests

# Build Spring Boot application
docker-compose build springboot-app

# Start all services
docker-compose up -d
```

This command starts the following services:
- **PostgreSQL** (Port: 5432)
- **Spring Boot Application** (Port: 8080)
- **pgAdmin** (Port: 5050)

### Checking Service Status
```bash
# View running containers
docker ps

# Check logs
docker-compose logs springboot-app
docker-compose logs postgres
```

---

## 📚 API Documentation

### Swagger UI
After running the application, access API documentation at:
```
http://localhost:8080/swagger-ui.html
```

### Main API Endpoints

#### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration
- `POST /api/auth/refresh` - Token refresh

#### Personnel Management
- `GET /api/personnel` - List all personnel
- `POST /api/personnel` - Add new personnel
- `PUT /api/personnel/{id}` - Update personnel
- `DELETE /api/personnel/{id}` - Delete personnel

---


## 🔧 Cache Management

### Redis Cache
- Personnel information caching
- Fast access to working hours
- Session management

### Hazelcast Distributed Cache
- Data sharing in cluster environment
- High-performance cache solution
- Automatic failover support

### Cache Monitoring
```bash
# Check Redis cache status
redis-cli
> KEYS *
> GET "cache-key-name"
```

---

## 📊 Monitoring and Logging

### Logs
- Application logs: `logs/` directory
- Logback configuration: `src/main/resources/logback-spring.xml`

### Performance Monitoring
- Spring Boot Actuator endpoints
- Redis and Hazelcast metrics
- Kafka consumer/producer metrics

---

## 📞 Contact

- **Developer**: Arif Özcan
- **Email**: zcanarif@gmail.com
- **GitHub**: [@arifozcan35](https://github.com/arifozcan35)
