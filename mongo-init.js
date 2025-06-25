// MongoDB initialization script for day_scheduler application

// Switch to the day_scheduler database
db = db.getSiblingDB('day_scheduler');

// Create collections
db.createCollection('goals');
db.createCollection('todos');
db.createCollection('schedules');

// Create indexes for better performance
db.goals.createIndex({ "title": 1 });
db.goals.createIndex({ "status": 1 });
db.goals.createIndex({ "priority": 1 });
db.goals.createIndex({ "targetDate": 1 });
db.goals.createIndex({ "createdAt": 1 });

db.todos.createIndex({ "title": 1 });
db.todos.createIndex({ "status": 1 });
db.todos.createIndex({ "priority": 1 });
db.todos.createIndex({ "goalId": 1 });
db.todos.createIndex({ "dueDate": 1 });
db.todos.createIndex({ "createdAt": 1 });

db.schedules.createIndex({ "title": 1 });
db.schedules.createIndex({ "status": 1 });
db.schedules.createIndex({ "type": 1 });
db.schedules.createIndex({ "scheduleDate": 1 });
db.schedules.createIndex({ "todoId": 1 });
db.schedules.createIndex({ "createdAt": 1 });

// Create compound indexes for common queries
db.goals.createIndex({ "status": 1, "priority": 1 });
db.todos.createIndex({ "status": 1, "priority": 1 });
db.schedules.createIndex({ "scheduleDate": 1, "startTime": 1, "endTime": 1 });

print('Day Scheduler database initialized successfully!');
print('Collections created: goals, todos, schedules');
print('Indexes created for optimal performance'); 