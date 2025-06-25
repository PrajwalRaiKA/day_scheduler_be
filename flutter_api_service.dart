// Flutter API Service for Day Scheduler
// Copy this file to your Flutter project's lib/services/ directory

import 'dart:convert';
import 'package:http/http.dart' as http;
import 'flutter_api_config.dart';

class ApiService {
  static final ApiService _instance = ApiService._internal();
  factory ApiService() => _instance;
  ApiService._internal();

  final http.Client _client = http.Client();

  // Generic HTTP methods
  Future<ApiResponse<T>> _get<T>(String url, T Function(Map<String, dynamic>) fromJson) async {
    try {
      final response = await _client
          .get(Uri.parse(url), headers: ApiConfig.defaultHeaders)
          .timeout(ApiConfig.connectionTimeout);

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        if (data is List) {
          return ApiResponse.success(data.map((item) => fromJson(item)).toList() as T);
        } else {
          return ApiResponse.success(fromJson(data) as T);
        }
      } else {
        return ApiResponse.error('HTTP ${response.statusCode}: ${response.reasonPhrase}');
      }
    } catch (e) {
      return ApiResponse.error('Network error: $e');
    }
  }

  Future<ApiResponse<T>> _post<T>(String url, Map<String, dynamic> body, T Function(Map<String, dynamic>) fromJson) async {
    try {
      final response = await _client
          .post(
            Uri.parse(url),
            headers: ApiConfig.defaultHeaders,
            body: json.encode(body),
          )
          .timeout(ApiConfig.connectionTimeout);

      if (response.statusCode == 201 || response.statusCode == 200) {
        final data = json.decode(response.body);
        return ApiResponse.success(fromJson(data) as T);
      } else {
        return ApiResponse.error('HTTP ${response.statusCode}: ${response.reasonPhrase}');
      }
    } catch (e) {
      return ApiResponse.error('Network error: $e');
    }
  }

  Future<ApiResponse<T>> _put<T>(String url, Map<String, dynamic> body, T Function(Map<String, dynamic>) fromJson) async {
    try {
      final response = await _client
          .put(
            Uri.parse(url),
            headers: ApiConfig.defaultHeaders,
            body: json.encode(body),
          )
          .timeout(ApiConfig.connectionTimeout);

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return ApiResponse.success(fromJson(data) as T);
      } else {
        return ApiResponse.error('HTTP ${response.statusCode}: ${response.reasonPhrase}');
      }
    } catch (e) {
      return ApiResponse.error('Network error: $e');
    }
  }

  Future<ApiResponse<bool>> _delete(String url) async {
    try {
      final response = await _client
          .delete(Uri.parse(url), headers: ApiConfig.defaultHeaders)
          .timeout(ApiConfig.connectionTimeout);

      if (response.statusCode == 204 || response.statusCode == 200) {
        return ApiResponse.success(true);
      } else {
        return ApiResponse.error('HTTP ${response.statusCode}: ${response.reasonPhrase}');
      }
    } catch (e) {
      return ApiResponse.error('Network error: $e');
    }
  }

  // Goals API
  Future<ApiResponse<List<Goal>>> getGoals() async {
    return _get(ApiConfig.goalsEndpoint, (json) => Goal.fromJson(json));
  }

  Future<ApiResponse<Goal>> getGoal(String id) async {
    return _get('${ApiConfig.goalsEndpoint}/$id', (json) => Goal.fromJson(json));
  }

  Future<ApiResponse<Goal>> createGoal(Goal goal) async {
    return _post(ApiConfig.goalsEndpoint, goal.toJson(), (json) => Goal.fromJson(json));
  }

  Future<ApiResponse<Goal>> updateGoal(String id, Goal goal) async {
    return _put('${ApiConfig.goalsEndpoint}/$id', goal.toJson(), (json) => Goal.fromJson(json));
  }

  Future<ApiResponse<bool>> deleteGoal(String id) async {
    return _delete('${ApiConfig.goalsEndpoint}/$id');
  }

  // Todos API
  Future<ApiResponse<List<Todo>>> getTodos() async {
    return _get(ApiConfig.todosEndpoint, (json) => Todo.fromJson(json));
  }

  Future<ApiResponse<Todo>> getTodo(String id) async {
    return _get('${ApiConfig.todosEndpoint}/$id', (json) => Todo.fromJson(json));
  }

  Future<ApiResponse<List<Todo>>> getTodosByGoal(String goalId) async {
    return _get('${ApiConfig.todosEndpoint}/goal/$goalId', (json) => Todo.fromJson(json));
  }

  Future<ApiResponse<Todo>> createTodo(Todo todo) async {
    return _post(ApiConfig.todosEndpoint, todo.toJson(), (json) => Todo.fromJson(json));
  }

  Future<ApiResponse<Todo>> updateTodo(String id, Todo todo) async {
    return _put('${ApiConfig.todosEndpoint}/$id', todo.toJson(), (json) => Todo.fromJson(json));
  }

  Future<ApiResponse<bool>> deleteTodo(String id) async {
    return _delete('${ApiConfig.todosEndpoint}/$id');
  }

  // Schedules API
  Future<ApiResponse<List<Schedule>>> getSchedules() async {
    return _get(ApiConfig.schedulesEndpoint, (json) => Schedule.fromJson(json));
  }

  Future<ApiResponse<Schedule>> getSchedule(String id) async {
    return _get('${ApiConfig.schedulesEndpoint}/$id', (json) => Schedule.fromJson(json));
  }

  Future<ApiResponse<Schedule>> createSchedule(Schedule schedule) async {
    return _post(ApiConfig.schedulesEndpoint, schedule.toJson(), (json) => Schedule.fromJson(json));
  }

  Future<ApiResponse<Schedule>> updateSchedule(String id, Schedule schedule) async {
    return _put('${ApiConfig.schedulesEndpoint}/$id', schedule.toJson(), (json) => Schedule.fromJson(json));
  }

  Future<ApiResponse<bool>> deleteSchedule(String id) async {
    return _delete('${ApiConfig.schedulesEndpoint}/$id');
  }

  // Test connection
  Future<bool> testConnection() async {
    try {
      final response = await _client
          .get(Uri.parse('${ApiConfig.baseUrl}/actuator/health'))
          .timeout(const Duration(seconds: 5));
      return response.statusCode == 200;
    } catch (e) {
      return false;
    }
  }

  void dispose() {
    _client.close();
  }
} 