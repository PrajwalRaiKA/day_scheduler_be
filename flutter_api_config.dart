// Flutter API Configuration for Day Scheduler
// Copy this file to your Flutter project's lib/config/ directory

class ApiConfig {
  // Local Network Configuration
  static const String baseUrl = 'http://192.168.0.101:8080';
  static const String apiVersion = '/api';
  
  // API Endpoints
  static const String goalsEndpoint = '$baseUrl$apiVersion/goals';
  static const String todosEndpoint = '$baseUrl$apiVersion/todos';
  static const String schedulesEndpoint = '$baseUrl$apiVersion/schedules';
  
  // Headers
  static const Map<String, String> defaultHeaders = {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  };
  
  // Timeout Configuration
  static const Duration connectionTimeout = Duration(seconds: 30);
  static const Duration receiveTimeout = Duration(seconds: 30);
  
  // Error Messages
  static const String networkErrorMessage = 'Network error. Please check your connection.';
  static const String serverErrorMessage = 'Server error. Please try again later.';
  static const String timeoutErrorMessage = 'Request timeout. Please try again.';
}

// API Response Models
class ApiResponse<T> {
  final bool success;
  final T? data;
  final String? message;
  final int? statusCode;

  ApiResponse({
    required this.success,
    this.data,
    this.message,
    this.statusCode,
  });

  factory ApiResponse.success(T data) {
    return ApiResponse(
      success: true,
      data: data,
    );
  }

  factory ApiResponse.error(String message, {int? statusCode}) {
    return ApiResponse(
      success: false,
      message: message,
      statusCode: statusCode,
    );
  }
}

// Goal Model
class Goal {
  final String? id;
  final String title;
  final String description;
  final String status;
  final String priority;
  final DateTime targetDate;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  Goal({
    this.id,
    required this.title,
    required this.description,
    required this.status,
    required this.priority,
    required this.targetDate,
    this.createdAt,
    this.updatedAt,
  });

  factory Goal.fromJson(Map<String, dynamic> json) {
    return Goal(
      id: json['id'],
      title: json['title'],
      description: json['description'],
      status: json['status'],
      priority: json['priority'],
      targetDate: DateTime.parse(json['targetDate']),
      createdAt: json['createdAt'] != null ? DateTime.parse(json['createdAt']) : null,
      updatedAt: json['updatedAt'] != null ? DateTime.parse(json['updatedAt']) : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'title': title,
      'description': description,
      'status': status,
      'priority': priority,
      'targetDate': targetDate.toIso8601String(),
    };
  }
}

// Todo Model
class Todo {
  final String? id;
  final String title;
  final String description;
  final String status;
  final String priority;
  final String? goalId;
  final DateTime? dueDate;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  Todo({
    this.id,
    required this.title,
    required this.description,
    required this.status,
    required this.priority,
    this.goalId,
    this.dueDate,
    this.createdAt,
    this.updatedAt,
  });

  factory Todo.fromJson(Map<String, dynamic> json) {
    return Todo(
      id: json['id'],
      title: json['title'],
      description: json['description'],
      status: json['status'],
      priority: json['priority'],
      goalId: json['goalId'],
      dueDate: json['dueDate'] != null ? DateTime.parse(json['dueDate']) : null,
      createdAt: json['createdAt'] != null ? DateTime.parse(json['createdAt']) : null,
      updatedAt: json['updatedAt'] != null ? DateTime.parse(json['updatedAt']) : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'title': title,
      'description': description,
      'status': status,
      'priority': priority,
      'goalId': goalId,
      'dueDate': dueDate?.toIso8601String(),
    };
  }
}

// Schedule Model
class Schedule {
  final String? id;
  final String title;
  final String description;
  final DateTime startTime;
  final DateTime endTime;
  final String dayOfWeek;
  final bool isRecurring;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  Schedule({
    this.id,
    required this.title,
    required this.description,
    required this.startTime,
    required this.endTime,
    required this.dayOfWeek,
    required this.isRecurring,
    this.createdAt,
    this.updatedAt,
  });

  factory Schedule.fromJson(Map<String, dynamic> json) {
    return Schedule(
      id: json['id'],
      title: json['title'],
      description: json['description'],
      startTime: DateTime.parse(json['startTime']),
      endTime: DateTime.parse(json['endTime']),
      dayOfWeek: json['dayOfWeek'],
      isRecurring: json['isRecurring'],
      createdAt: json['createdAt'] != null ? DateTime.parse(json['createdAt']) : null,
      updatedAt: json['updatedAt'] != null ? DateTime.parse(json['updatedAt']) : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'title': title,
      'description': description,
      'startTime': startTime.toIso8601String(),
      'endTime': endTime.toIso8601String(),
      'dayOfWeek': dayOfWeek,
      'isRecurring': isRecurring,
    };
  }
} 