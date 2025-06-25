package com.rai.scheduler.day_scheduler.service;

import com.rai.scheduler.day_scheduler.model.Todo;
import com.rai.scheduler.day_scheduler.repository.TodoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Todo business logic operations.
 * Handles all todo-related business operations and data validation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class TodoService {
    
    private final TodoRepository todoRepository;
    
    /**
     * Create a new todo
     * @param todo the todo to create
     * @return the created todo
     */
    public Todo createTodo(Todo todo) {
        log.info("Creating new todo: {}", todo.getTitle());
        validateTodo(todo);
        Todo savedTodo = todoRepository.save(todo);
        log.info("Todo created successfully with ID: {}", savedTodo.getId());
        return savedTodo;
    }
    
    /**
     * Get all todos
     * @return list of all todos
     */
    @Transactional(readOnly = true)
    public List<Todo> getAllTodos() {
        log.info("Fetching all todos");
        return todoRepository.findAll();
    }
    
    /**
     * Get todo by ID
     * @param id the todo ID
     * @return optional containing the todo if found
     */
    @Transactional(readOnly = true)
    public Optional<Todo> getTodoById(String id) {
        log.info("Fetching todo by ID: {}", id);
        return todoRepository.findById(id);
    }
    
    /**
     * Update an existing todo
     * @param id the todo ID
     * @param todoDetails the updated todo details
     * @return the updated todo
     * @throws RuntimeException if todo not found
     */
    public Todo updateTodo(String id, Todo todoDetails) {
        log.info("Updating todo with ID: {}", id);
        Todo existingTodo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found with ID: " + id));
        
        existingTodo.setTitle(todoDetails.getTitle());
        existingTodo.setDescription(todoDetails.getDescription());
        existingTodo.setCompleted(todoDetails.isCompleted());
        existingTodo.setDate(todoDetails.getDate());
        
        Todo updatedTodo = todoRepository.save(existingTodo);
        log.info("Todo updated successfully with ID: {}", updatedTodo.getId());
        return updatedTodo;
    }
    
    /**
     * Delete a todo
     * @param id the todo ID
     * @throws RuntimeException if todo not found
     */
    public void deleteTodo(String id) {
        log.info("Deleting todo with ID: {}", id);
        if (!todoRepository.existsById(id)) {
            throw new RuntimeException("Todo not found with ID: " + id);
        }
        todoRepository.deleteById(id);
        log.info("Todo deleted successfully with ID: {}", id);
    }
    
    /**
     * Mark todo as completed
     * @param id the todo ID
     * @return the updated todo
     * @throws RuntimeException if todo not found
     */
    public Todo markTodoAsCompleted(String id) {
        log.info("Marking todo as completed with ID: {}", id);
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found with ID: " + id));
        
        todo.setCompleted(true);
        
        Todo updatedTodo = todoRepository.save(todo);
        log.info("Todo marked as completed with ID: {}", updatedTodo.getId());
        return updatedTodo;
    }
    
    /**
     * Mark todo as incomplete
     * @param id the todo ID
     * @return the updated todo
     * @throws RuntimeException if todo not found
     */
    public Todo markTodoAsIncomplete(String id) {
        log.info("Marking todo as incomplete with ID: {}", id);
        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Todo not found with ID: " + id));
        
        todo.setCompleted(false);
        
        Todo updatedTodo = todoRepository.save(todo);
        log.info("Todo marked as incomplete with ID: {}", updatedTodo.getId());
        return updatedTodo;
    }
    
    /**
     * Get todos by completion status
     * @param isCompleted the completion status
     * @return list of todos with the specified completion status
     */
    @Transactional(readOnly = true)
    public List<Todo> getTodosByCompletionStatus(boolean isCompleted) {
        log.info("Fetching todos by completion status: {}", isCompleted);
        return todoRepository.findByCompleted(isCompleted);
    }
    
    /**
     * Get todos by date
     * @param date the date to filter by
     * @return list of todos on the specified date
     */
    @Transactional(readOnly = true)
    public List<Todo> getTodosByDate(LocalDate date) {
        log.info("Fetching todos for date: {}", date);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return todoRepository.findByDateBetween(startOfDay, endOfDay);
    }
    
    /**
     * Get todos by date range
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of todos within the date range
     */
    @Transactional(readOnly = true)
    public List<Todo> getTodosByDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("Fetching todos by date range: {} to {}", startDate, endDate);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        return todoRepository.findByDateRange(startDateTime, endDateTime);
    }
    
    /**
     * Get todos by title search
     * @param title the title to search for
     * @return list of todos with matching titles
     */
    @Transactional(readOnly = true)
    public List<Todo> searchTodosByTitle(String title) {
        log.info("Searching todos by title: {}", title);
        return todoRepository.findByTitleContainingIgnoreCase(title);
    }
    
    /**
     * Get todos created in the last N days
     * @param days number of days to look back
     * @return list of todos created within the specified period
     */
    @Transactional(readOnly = true)
    public List<Todo> getTodosCreatedInLastDays(int days) {
        log.info("Fetching todos created in last {} days", days);
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        return todoRepository.findTodosCreatedInLastDays(startDate);
    }
    
    /**
     * Count todos by completion status
     * @param isCompleted the completion status
     * @return count of todos with the specified completion status
     */
    @Transactional(readOnly = true)
    public long countTodosByCompletionStatus(boolean isCompleted) {
        log.info("Counting todos by completion status: {}", isCompleted);
        return todoRepository.countByCompleted(isCompleted);
    }
    
    /**
     * Validate todo data
     * @param todo the todo to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateTodo(Todo todo) {
        if (todo.getTitle() == null || todo.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Todo title cannot be null or empty");
        }
        
        if (todo.getTitle().length() > 255) {
            throw new IllegalArgumentException("Todo title cannot exceed 255 characters");
        }
        
        if (todo.getDate() == null) {
            throw new IllegalArgumentException("Todo date cannot be null");
        }
    }
} 