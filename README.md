# Day Scheduler Backend

A Spring Boot REST API for managing goals, todos, and schedules in a day scheduler application using MongoDB.

## Features

- **Goals Management**: Create, read, update, and delete long-term goals
- **Todos Management**: Manage tasks with priorities, due dates, and completion tracking
- **Schedule Management**: Schedule time-bound activities with conflict detection
- **RESTful API**: Full CRUD operations with proper HTTP status codes
- **Data Validation**: Input validation and error handling
- **MongoDB Support**: Document-based storage with Spring Data MongoDB

## Technology Stack

- **Java 21**
- **Spring Boot 3.5.3**
- **Spring Data MongoDB**
- **MongoDB 7.0**
- **Lombok**
- **Spring Security** (configured for development)
- **Maven**

## Project Structure

```
src/main/java/com/rai/scheduler/day_scheduler/
├── config/
│   └── SecurityConfig.java
├── controller/
│   ├── GoalController.java
│   ├── TodoController.java
│   └── ScheduleController.java
├── exception/
│   ├── ErrorResponse.java
│   └── GlobalExceptionHandler.java
├── model/
│   ├── Goal.java
│   ├── Todo.java
│   └── Schedule.java
├── repository/
│   ├── GoalRepository.java
│   ├── TodoRepository.java
│   └── ScheduleRepository.java
├── service/
│   ├── GoalService.java
│   ├── TodoService.java
│   └── ScheduleService.java
└── DaySchedulerApplication.java
```

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6 or higher
- Docker and Docker Compose (for MongoDB)

### Running the Application

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd day_scheduler
   ```

2. **Start MongoDB using Docker Compose**
   ```bash
   docker-compose up -d
   ```

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

### Database Access

- **MongoDB**: `mongodb://localhost:27017/day_scheduler`
- **Mongo Express (Web UI)**: `http://localhost:8081`
  - Username: `admin`
  - Password: `password`

### Docker Setup

**Build and run the entire stack:**
```bash
# Build the application
mvn clean package

# Build and start all services
docker-compose up --build

# Or run in background
docker-compose up -d --build
```

**Individual Docker commands:**
```bash
# Build the application image
docker build -t day_scheduler:latest .

# Run the application with MongoDB
docker run -p 8080:8080 --network day_scheduler_day_scheduler_network day_scheduler:latest
```

## API Endpoints

### Goals API

#### Create a Goal
```http
POST /api/goals
Content-Type: application/json

{
  "title": "Learn Spring Boot",
  "description": "Master Spring Boot framework",
  "priority": "HIGH",
  "targetDate": "2024-12-31T23:59:59"
}
```

#### Get All Goals
```http
GET /api/goals
```

#### Get Goal by ID
```http
GET /api/goals/{id}
```

#### Update Goal
```http
PUT /api/goals/{id}
Content-Type: application/json

{
  "title": "Learn Spring Boot Advanced",
  "description": "Master advanced Spring Boot concepts",
  "status": "IN_PROGRESS",
  "priority": "URGENT"
}
```

#### Delete Goal
```http
DELETE /api/goals/{id}
```

#### Get Goals by Status
```http
GET /api/goals/status/{status}
```

#### Get Goals by Priority
```http
GET /api/goals/priority/{priority}
```

#### Get Overdue Goals
```http
GET /api/goals/overdue
```

#### Search Goals by Title
```http
GET /api/goals/search?title=Spring
```

### Todos API

#### Create a Todo
```http
POST /api/todos
Content-Type: application/json

{
  "title": "Complete project setup",
  "description": "Set up the development environment",
  "priority": "HIGH",
  "dueDate": "2024-01-15T17:00:00",
  "goalId": "507f1f77bcf86cd799439011"
}
```

#### Get All Todos
```http
GET /api/todos
```

#### Get Todo by ID
```http
GET /api/todos/{id}
```

#### Update Todo
```http
PUT /api/todos/{id}
Content-Type: application/json

{
  "title": "Complete project setup",
  "status": "IN_PROGRESS",
  "priority": "URGENT"
}
```

#### Mark Todo as Completed
```http
PATCH /api/todos/{id}/complete
```

#### Delete Todo
```http
DELETE /api/todos/{id}
```

#### Get Todos by Goal
```http
GET /api/todos/goal/{goalId}
```

#### Get Overdue Todos
```http
GET /api/todos/overdue
```

### Schedules API

#### Create a Schedule
```http
POST /api/schedules
Content-Type: application/json

{
  "title": "Team Meeting",
  "description": "Weekly team sync",
  "scheduleDate": "2024-01-15",
  "startTime": "10:00:00",
  "endTime": "11:00:00",
  "type": "MEETING",
  "location": "Conference Room A"
}
```

#### Get All Schedules
```http
GET /api/schedules
```

#### Get Schedule by ID
```http
GET /api/schedules/{id}
```

#### Update Schedule
```http
PUT /api/schedules/{id}
Content-Type: application/json

{
  "title": "Team Meeting",
  "startTime": "10:30:00",
  "endTime": "11:30:00"
}
```

#### Delete Schedule
```http
DELETE /api/schedules/{id}
```

#### Get Schedules by Date
```http
GET /api/schedules/date/2024-01-15
```

#### Get Schedules by Date Range
```http
GET /api/schedules/daterange?startDate=2024-01-01&endDate=2024-01-31
```

#### Check Time Conflicts
```http
GET /api/schedules/conflicts?date=2024-01-15&startTime=10:00:00&endTime=11:00:00
```

## Data Models

### Goal
- `id`: String (MongoDB ObjectId)
- `title`: String (Required)
- `description`: String (Optional)
- `status`: GoalStatus enum (ACTIVE, COMPLETED, CANCELLED, ON_HOLD)
- `priority`: GoalPriority enum (LOW, MEDIUM, HIGH, URGENT)
- `targetDate`: LocalDateTime (Optional)
- `createdAt`: LocalDateTime (Auto-generated)
- `updatedAt`: LocalDateTime (Auto-generated)

### Todo
- `id`: String (MongoDB ObjectId)
- `title`: String (Required)
- `description`: String (Optional)
- `status`: TodoStatus enum (PENDING, IN_PROGRESS, COMPLETED, CANCELLED)
- `priority`: TodoPriority enum (LOW, MEDIUM, HIGH, URGENT)
- `dueDate`: LocalDateTime (Optional)
- `completedAt`: LocalDateTime (Auto-set when completed)
- `goalId`: String (Reference to Goal)
- `createdAt`: LocalDateTime (Auto-generated)
- `updatedAt`: LocalDateTime (Auto-generated)

### Schedule
- `id`: String (MongoDB ObjectId)
- `title`: String (Required)
- `description`: String (Optional)
- `scheduleDate`: LocalDate (Required)
- `startTime`: LocalTime (Required)
- `endTime`: LocalTime (Required)
- `status`: ScheduleStatus enum (SCHEDULED, IN_PROGRESS, COMPLETED, CANCELLED, NO_SHOW)
- `type`: ScheduleType enum (APPOINTMENT, MEETING, TASK, BREAK, EXERCISE, STUDY, OTHER)
- `location`: String (Optional)
- `todoId`: String (Reference to Todo)
- `createdAt`: LocalDate (Auto-generated)
- `updatedAt`: LocalDate (Auto-generated)

## MongoDB Collections

The application uses the following MongoDB collections:
- `goals`: Stores goal documents
- `todos`: Stores todo documents
- `schedules`: Stores schedule documents

Each collection has appropriate indexes for optimal query performance.

## Error Handling

The API provides consistent error responses with the following structure:

```json
{
  "timestamp": "2024-01-15T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "details": {
    "title": "Title cannot be empty",
    "dueDate": "Due date cannot be in the past"
  }
}
```

## Development

### Running Tests
```bash
mvn test
```

### Building for Production
```bash
mvn clean package
```

### Actuator Endpoints
- Health Check: `http://localhost:8080/actuator/health`
- Application Info: `http://localhost:8080/actuator/info`
- Metrics: `http://localhost:8080/actuator/metrics`

## Configuration

The application uses the following configuration files:
- `application.properties`: Main configuration including MongoDB settings
- `SecurityConfig.java`: Security configuration
- `GlobalExceptionHandler.java`: Global error handling
- `docker-compose.yml`: MongoDB and Mongo Express setup
- `mongo-init.js`: MongoDB initialization script

## Environment Variables

You can override MongoDB configuration using environment variables:
- `SPRING_DATA_MONGODB_HOST`: MongoDB host (default: localhost)
- `SPRING_DATA_MONGODB_PORT`: MongoDB port (default: 27017)
- `SPRING_DATA_MONGODB_DATABASE`: Database name (default: day_scheduler)

## Future Enhancements

- User authentication and authorization
- API documentation with Swagger/OpenAPI
- Unit and integration tests
- CI/CD pipeline setup
- MongoDB Atlas integration for cloud deployment
- Data backup and recovery strategies

## License

This project is licensed under the MIT License.

## 🚀 **Docker Deployment (Recommended)**

Your day scheduler app is fully configured for Docker deployment! Here's how to deploy it:

### **Quick Start with Docker Compose**

1. **Build and Deploy Complete Stack:**
   ```bash
   # Build the application
   ./mvnw clean package -DskipTests
   
   # Deploy everything with Docker Compose
   docker-compose up -d --build
   ```

2. **Access Your Application:**
   - **Spring Boot API:** http://localhost:8080
   - **MongoDB Web UI:** http://localhost:8081 (admin/password)
   - **MongoDB Database:** localhost:27017

3. **Test the API:**
   ```bash
   # Get all goals
   curl -X GET http://localhost:8080/api/goals
   
   # Create a goal
   curl -X POST http://localhost:8080/api/goals \
     -H "Content-Type: application/json" \
     -d '{"title":"My Goal","description":"A test goal","targetDate":"2025-12-31T23:59:59","priority":"HIGH"}'
   ```

### **Docker Services**

The Docker Compose setup includes:

- **day-scheduler-app:** Spring Boot application (port 8080)
- **mongodb:** MongoDB 7.0 database (port 27017)
- **mongo-express:** Web-based MongoDB admin interface (port 8081)

### **Docker Commands**

```bash
# Start all services
docker-compose up -d

# View logs
docker-compose logs -f day-scheduler-app

# Stop all services
docker-compose down

# Rebuild and restart
docker-compose up -d --build

# View running containers
docker-compose ps
```

## 🛠 **Local Development Setup**

### Prerequisites
- Java 21
- Maven 3.6+
- MongoDB 7.0 (or use Docker)

### Local MongoDB Setup
```bash
# Start MongoDB with Docker
docker run -d --name mongodb \
  -p 27017:27017 \
  -e MONGO_INITDB_ROOT_USERNAME=admin \
  -e MONGO_INITDB_ROOT_PASSWORD=password \
  -e MONGO_INITDB_DATABASE=day_scheduler \
  mongo:7.0

# Or use the provided docker-compose for just MongoDB
docker-compose up -d mongodb mongo-express
```

### Run Application
```bash
# Build the project
./mvnw clean package

# Run the application
./mvnw spring-boot:run
```

## 📊 **API Endpoints**

### Goals
- `GET /api/goals` - Get all goals
- `GET /api/goals/{id}` - Get goal by ID
- `POST /api/goals` - Create new goal
- `PUT /api/goals/{id}` - Update goal
- `DELETE /api/goals/{id}` - Delete goal

### Todos
- `GET /api/todos` - Get all todos
- `GET /api/todos/{id}` - Get todo by ID
- `POST /api/todos` - Create new todo
- `PUT /api/todos/{id}` - Update todo
- `DELETE /api/todos/{id}` - Delete todo
- `GET /api/todos/goal/{goalId}` - Get todos by goal

### Schedules
- `GET /api/schedules` - Get all schedules
- `GET /api/schedules/{id}` - Get schedule by ID
- `POST /api/schedules` - Create new schedule
- `PUT /api/schedules/{id}` - Update schedule
- `DELETE /api/schedules/{id}` - Delete schedule

## 🗄 **Database Schema**

### Goal Document
```json
{
  "id": "string",
  "title": "string",
  "description": "string",
  "status": "ACTIVE|COMPLETED|CANCELLED",
  "priority": "LOW|MEDIUM|HIGH",
  "targetDate": "datetime",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

### Todo Document
```json
{
  "id": "string",
  "title": "string",
  "description": "string",
  "status": "PENDING|IN_PROGRESS|COMPLETED|CANCELLED",
  "priority": "LOW|MEDIUM|HIGH",
  "goalId": "string",
  "dueDate": "datetime",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

### Schedule Document
```json
{
  "id": "string",
  "title": "string",
  "description": "string",
  "startTime": "datetime",
  "endTime": "datetime",
  "dayOfWeek": "MONDAY|TUESDAY|...|SUNDAY",
  "isRecurring": "boolean",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

## 🔧 **Configuration**

### Application Properties
- **Server Port:** 8080
- **MongoDB Host:** localhost (or mongodb in Docker)
- **MongoDB Port:** 27017
- **Database:** day_scheduler
- **Authentication:** admin/password (in Docker)

### Environment Variables (Docker)
- `SPRING_DATA_MONGODB_HOST` - MongoDB host
- `SPRING_DATA_MONGODB_PORT` - MongoDB port
- `SPRING_DATA_MONGODB_DATABASE` - Database name
- `SPRING_DATA_MONGODB_USERNAME` - MongoDB username
- `SPRING_DATA_MONGODB_PASSWORD` - MongoDB password

## 🧪 **Testing**

```bash
# Run tests
./mvnw test

# Run with coverage
./mvnw test jacoco:report
```

## 📝 **Features**

- ✅ **RESTful API** with proper HTTP status codes
- ✅ **MongoDB Integration** with Spring Data MongoDB
- ✅ **Global Exception Handling** with detailed error messages
- ✅ **Input Validation** with comprehensive error responses
- ✅ **Docker Support** with complete containerization
- ✅ **MongoDB Web UI** for database management
- ✅ **CORS Configuration** for frontend integration
- ✅ **Actuator Endpoints** for monitoring
- ✅ **Comprehensive Logging** for debugging

## 🚀 **Deployment Status**

✅ **Successfully deployed in Docker!**

- All containers running properly
- MongoDB authentication configured
- API endpoints responding correctly
- Data persistence working
- Web UI accessible

## 📞 **Support**

For issues or questions:
1. Check the application logs: `docker-compose logs day-scheduler-app`
2. Verify MongoDB connection: `docker-compose logs mongodb`
3. Test API endpoints with curl commands above 