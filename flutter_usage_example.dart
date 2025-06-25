// Flutter Usage Example for Day Scheduler API
// Copy this to your Flutter project and modify as needed

import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import 'flutter_api_config.dart';
import 'flutter_api_service.dart';

// BLoC for Goals
class GoalsBloc extends Cubit<GoalsState> {
  final ApiService _apiService = ApiService();

  GoalsBloc() : super(GoalsInitial());

  Future<void> loadGoals() async {
    emit(GoalsLoading());
    
    final response = await _apiService.getGoals();
    
    if (response.success) {
      emit(GoalsLoaded(response.data!));
    } else {
      emit(GoalsError(response.message!));
    }
  }

  Future<void> createGoal(Goal goal) async {
    emit(GoalsLoading());
    
    final response = await _apiService.createGoal(goal);
    
    if (response.success) {
      await loadGoals(); // Reload the list
    } else {
      emit(GoalsError(response.message!));
    }
  }

  Future<void> deleteGoal(String id) async {
    emit(GoalsLoading());
    
    final response = await _apiService.deleteGoal(id);
    
    if (response.success) {
      await loadGoals(); // Reload the list
    } else {
      emit(GoalsError(response.message!));
    }
  }
}

// Goals States
abstract class GoalsState {}

class GoalsInitial extends GoalsState {}

class GoalsLoading extends GoalsState {}

class GoalsLoaded extends GoalsState {
  final List<Goal> goals;
  GoalsLoaded(this.goals);
}

class GoalsError extends GoalsState {
  final String message;
  GoalsError(this.message);
}

// Example Widget
class GoalsScreen extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return BlocProvider(
      create: (context) => GoalsBloc()..loadGoals(),
      child: Scaffold(
        appBar: AppBar(
          title: Text('Goals'),
          actions: [
            IconButton(
              icon: Icon(Icons.refresh),
              onPressed: () {
                context.read<GoalsBloc>().loadGoals();
              },
            ),
          ],
        ),
        body: BlocBuilder<GoalsBloc, GoalsState>(
          builder: (context, state) {
            if (state is GoalsLoading) {
              return Center(child: CircularProgressIndicator());
            } else if (state is GoalsLoaded) {
              return ListView.builder(
                itemCount: state.goals.length,
                itemBuilder: (context, index) {
                  final goal = state.goals[index];
                  return ListTile(
                    title: Text(goal.title),
                    subtitle: Text(goal.description),
                    trailing: PopupMenuButton(
                      itemBuilder: (context) => [
                        PopupMenuItem(
                          child: Text('Delete'),
                          value: 'delete',
                        ),
                      ],
                      onSelected: (value) {
                        if (value == 'delete') {
                          context.read<GoalsBloc>().deleteGoal(goal.id!);
                        }
                      },
                    ),
                  );
                },
              );
            } else if (state is GoalsError) {
              return Center(
                child: Column(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Text('Error: ${state.message}'),
                    SizedBox(height: 16),
                    ElevatedButton(
                      onPressed: () {
                        context.read<GoalsBloc>().loadGoals();
                      },
                      child: Text('Retry'),
                    ),
                  ],
                ),
              );
            }
            return Center(child: Text('No goals found'));
          },
        ),
        floatingActionButton: FloatingActionButton(
          onPressed: () => _showAddGoalDialog(context),
          child: Icon(Icons.add),
        ),
      ),
    );
  }

  void _showAddGoalDialog(BuildContext context) {
    final titleController = TextEditingController();
    final descriptionController = TextEditingController();

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text('Add Goal'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(
              controller: titleController,
              decoration: InputDecoration(labelText: 'Title'),
            ),
            TextField(
              controller: descriptionController,
              decoration: InputDecoration(labelText: 'Description'),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: Text('Cancel'),
          ),
          ElevatedButton(
            onPressed: () {
              final goal = Goal(
                title: titleController.text,
                description: descriptionController.text,
                status: 'ACTIVE',
                priority: 'MEDIUM',
                targetDate: DateTime.now().add(Duration(days: 30)),
              );
              
              context.read<GoalsBloc>().createGoal(goal);
              Navigator.pop(context);
            },
            child: Text('Add'),
          ),
        ],
      ),
    );
  }
}

// Connection Test Widget
class ConnectionTestWidget extends StatefulWidget {
  @override
  _ConnectionTestWidgetState createState() => _ConnectionTestWidgetState();
}

class _ConnectionTestWidgetState extends State<ConnectionTestWidget> {
  bool _isConnected = false;
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _testConnection();
  }

  Future<void> _testConnection() async {
    setState(() {
      _isLoading = true;
    });

    final apiService = ApiService();
    final isConnected = await apiService.testConnection();

    setState(() {
      _isConnected = isConnected;
      _isLoading = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              'API Connection Status',
              style: Theme.of(context).textTheme.headline6,
            ),
            SizedBox(height: 8),
            Row(
              children: [
                Icon(
                  _isConnected ? Icons.check_circle : Icons.error,
                  color: _isConnected ? Colors.green : Colors.red,
                ),
                SizedBox(width: 8),
                Text(
                  _isConnected ? 'Connected' : 'Not Connected',
                  style: TextStyle(
                    color: _isConnected ? Colors.green : Colors.red,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                if (_isLoading) ...[
                  SizedBox(width: 8),
                  SizedBox(
                    width: 16,
                    height: 16,
                    child: CircularProgressIndicator(strokeWidth: 2),
                  ),
                ],
              ],
            ),
            SizedBox(height: 8),
            Text(
              'Server: ${ApiConfig.baseUrl}',
              style: Theme.of(context).textTheme.caption,
            ),
            SizedBox(height: 8),
            ElevatedButton(
              onPressed: _isLoading ? null : _testConnection,
              child: Text('Test Connection'),
            ),
          ],
        ),
      ),
    );
  }
}

// Main App Example
class DaySchedulerApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Day Scheduler',
      theme: ThemeData(
        primarySwatch: Colors.blue,
        visualDensity: VisualDensity.adaptivePlatformDensity,
      ),
      home: Scaffold(
        appBar: AppBar(
          title: Text('Day Scheduler'),
        ),
        body: Padding(
          padding: EdgeInsets.all(16),
          child: Column(
            children: [
              ConnectionTestWidget(),
              SizedBox(height: 16),
              Expanded(
                child: GoalsScreen(),
              ),
            ],
          ),
        ),
      ),
    );
  }
} 