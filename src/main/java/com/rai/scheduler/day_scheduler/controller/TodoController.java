package com.rai.scheduler.day_scheduler.controller;

import com.rai.scheduler.day_scheduler.model.Todo;
import com.rai.scheduler.day_scheduler.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for Todo operations.
 * Provides endpoints for managing todos in the day scheduler application.
 */
@RestController
@RequestMapping("/api/todos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class TodoController {
    
    private final TodoService todoService;
    
    /**
     * Create a new todo
     * @param todo the todo to create
     * @return the created todo with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<Todo> createTodo(@Valid @RequestBody Todo todo) {
        log.info("POST /api/todos - Creating new todo: {}", todo.getTitle());
        Todo createdTodo = todoService.createTodo(todo);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTodo);
    }
    
    /**
     * Get all todos
     * @return list of all todos
     */
    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos() {
        log.info("GET /api/todos - Fetching all todos");
        List<Todo> todos = todoService.getAllTodos();
        return ResponseEntity.ok(todos);
    }
    
    /**
     * Get todo by ID
     * @param id the todo ID
     * @return the todo if found, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable String id) {
        log.info("GET /api/todos/{} - Fetching todo by ID", id);
        return todoService.getTodoById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Update an existing todo
     * @param id the todo ID
     * @param todoDetails the updated todo details
     * @return the updated todo, or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable String id, @Valid @RequestBody Todo todoDetails) {
        log.info("PUT /api/todos/{} - Updating todo", id);
        try {
            Todo updatedTodo = todoService.updateTodo(id, todoDetails);
            return ResponseEntity.ok(updatedTodo);
        } catch (RuntimeException e) {
            log.error("Todo not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Delete a todo
     * @param id the todo ID
     * @return 204 No Content if successful, or 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTodo(@PathVariable String id) {
        log.info("DELETE /api/todos/{} - Deleting todo", id);
        try {
            todoService.deleteTodo(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Todo not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Mark todo as completed
     * @param id the todo ID
     * @return the updated todo, or 404 if not found
     */
    @PatchMapping("/{id}/complete")
    public ResponseEntity<Todo> markTodoAsCompleted(@PathVariable String id) {
        log.info("PATCH /api/todos/{}/complete - Marking todo as completed", id);
        try {
            Todo completedTodo = todoService.markTodoAsCompleted(id);
            return ResponseEntity.ok(completedTodo);
        } catch (RuntimeException e) {
            log.error("Todo not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Mark todo as incomplete
     * @param id the todo ID
     * @return the updated todo, or 404 if not found
     */
    @PatchMapping("/{id}/incomplete")
    public ResponseEntity<Todo> markTodoAsIncomplete(@PathVariable String id) {
        log.info("PATCH /api/todos/{}/incomplete - Marking todo as incomplete", id);
        try {
            Todo incompleteTodo = todoService.markTodoAsIncomplete(id);
            return ResponseEntity.ok(incompleteTodo);
        } catch (RuntimeException e) {
            log.error("Todo not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get todos by completion status
     * @param isCompleted the completion status
     * @return list of todos with the specified completion status
     */
    @GetMapping("/status/{isCompleted}")
    public ResponseEntity<List<Todo>> getTodosByCompletionStatus(@PathVariable boolean isCompleted) {
        log.info("GET /api/todos/status/{} - Fetching todos by completion status", isCompleted);
        List<Todo> todos = todoService.getTodosByCompletionStatus(isCompleted);
        return ResponseEntity.ok(todos);
    }
    
    /**
     * Get todos by date
     * @param date the date to filter by (format: yyyy-MM-dd)
     * @return list of todos on the specified date
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<List<Todo>> getTodosByDate(@PathVariable String date) {
        log.info("GET /api/todos/date/{} - Fetching todos by date", date);
        try {
            LocalDate todoDate = LocalDate.parse(date);
            List<Todo> todos = todoService.getTodosByDate(todoDate);
            return ResponseEntity.ok(todos);
        } catch (Exception e) {
            log.error("Invalid date format: {}", date);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get todos by date range
     * @param startDate the start date (format: yyyy-MM-dd)
     * @param endDate the end date (format: yyyy-MM-dd)
     * @return list of todos within the date range
     */
    @GetMapping("/daterange")
    public ResponseEntity<List<Todo>> getTodosByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        log.info("GET /api/todos/daterange - Fetching todos by date range: {} to {}", startDate, endDate);
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            List<Todo> todos = todoService.getTodosByDateRange(start, end);
            return ResponseEntity.ok(todos);
        } catch (Exception e) {
            log.error("Invalid date format: startDate={}, endDate={}", startDate, endDate);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Search todos by title
     * @param title the title to search for
     * @return list of todos with matching titles
     */
    @GetMapping("/search")
    public ResponseEntity<List<Todo>> searchTodosByTitle(@RequestParam String title) {
        log.info("GET /api/todos/search - Searching todos by title: {}", title);
        List<Todo> todos = todoService.searchTodosByTitle(title);
        return ResponseEntity.ok(todos);
    }
    
    /**
     * Get todos created in the last N days
     * @param days number of days to look back
     * @return list of todos created within the specified period
     */
    @GetMapping("/recent")
    public ResponseEntity<List<Todo>> getTodosCreatedInLastDays(@RequestParam(defaultValue = "7") int days) {
        log.info("GET /api/todos/recent - Fetching todos created in last {} days", days);
        List<Todo> todos = todoService.getTodosCreatedInLastDays(days);
        return ResponseEntity.ok(todos);
    }
    
    /**
     * Count todos by completion status
     * @param isCompleted the completion status
     * @return count of todos with the specified completion status
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countTodosByCompletionStatus(@RequestParam boolean isCompleted) {
        log.info("GET /api/todos/count - Counting todos by completion status: {}", isCompleted);
        long count = todoService.countTodosByCompletionStatus(isCompleted);
        return ResponseEntity.ok(count);
    }
} 