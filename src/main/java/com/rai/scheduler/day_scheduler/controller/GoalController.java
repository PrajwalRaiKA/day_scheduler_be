package com.rai.scheduler.day_scheduler.controller;

import com.rai.scheduler.day_scheduler.model.Goal;
import com.rai.scheduler.day_scheduler.service.GoalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for Goal operations.
 * Provides endpoints for managing goals in the day scheduler application.
 */
@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class GoalController {
    
    private final GoalService goalService;
    
    /**
     * Create a new goal
     * @param goal the goal to create
     * @return the created goal with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<Goal> createGoal(@Valid @RequestBody Goal goal) {
        log.info("POST /api/goals - Creating new goal: {}", goal.getTitle());
        Goal createdGoal = goalService.createGoal(goal);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGoal);
    }
    
    /**
     * Get all goals
     * @return list of all goals
     */
    @GetMapping
    public ResponseEntity<List<Goal>> getAllGoals() {
        log.info("GET /api/goals - Fetching all goals");
        List<Goal> goals = goalService.getAllGoals();
        return ResponseEntity.ok(goals);
    }
    
    /**
     * Get goal by ID
     * @param id the goal ID
     * @return the goal if found, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Goal> getGoalById(@PathVariable String id) {
        log.info("GET /api/goals/{} - Fetching goal by ID", id);
        return goalService.getGoalById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Update an existing goal
     * @param id the goal ID
     * @param goalDetails the updated goal details
     * @return the updated goal, or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Goal> updateGoal(@PathVariable String id, @Valid @RequestBody Goal goalDetails) {
        log.info("PUT /api/goals/{} - Updating goal", id);
        try {
            Goal updatedGoal = goalService.updateGoal(id, goalDetails);
            return ResponseEntity.ok(updatedGoal);
        } catch (RuntimeException e) {
            log.error("Goal not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Delete a goal
     * @param id the goal ID
     * @return 204 No Content if successful, or 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable String id) {
        log.info("DELETE /api/goals/{} - Deleting goal", id);
        try {
            goalService.deleteGoal(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Goal not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get goals by date
     * @param date the date to filter by (format: yyyy-MM-dd)
     * @return list of goals on the specified date
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<List<Goal>> getGoalsByDate(@PathVariable String date) {
        log.info("GET /api/goals/date/{} - Fetching goals by date", date);
        try {
            LocalDate goalDate = LocalDate.parse(date);
            List<Goal> goals = goalService.getGoalsByDate(goalDate);
            return ResponseEntity.ok(goals);
        } catch (Exception e) {
            log.error("Invalid date format: {}", date);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get goals by date range
     * @param startDate the start date (format: yyyy-MM-dd)
     * @param endDate the end date (format: yyyy-MM-dd)
     * @return list of goals within the date range
     */
    @GetMapping("/daterange")
    public ResponseEntity<List<Goal>> getGoalsByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        log.info("GET /api/goals/daterange - Fetching goals by date range: {} to {}", startDate, endDate);
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            List<Goal> goals = goalService.getGoalsByDateRange(start, end);
            return ResponseEntity.ok(goals);
        } catch (Exception e) {
            log.error("Invalid date format: startDate={}, endDate={}", startDate, endDate);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Search goals by title
     * @param title the title to search for
     * @return list of goals with matching titles
     */
    @GetMapping("/search")
    public ResponseEntity<List<Goal>> searchGoalsByTitle(@RequestParam String title) {
        log.info("GET /api/goals/search - Searching goals by title: {}", title);
        List<Goal> goals = goalService.searchGoalsByTitle(title);
        return ResponseEntity.ok(goals);
    }
    
    /**
     * Get goals created in the last N days
     * @param days number of days to look back
     * @return list of goals created within the specified period
     */
    @GetMapping("/recent")
    public ResponseEntity<List<Goal>> getGoalsCreatedInLastDays(@RequestParam(defaultValue = "7") int days) {
        log.info("GET /api/goals/recent - Fetching goals created in last {} days", days);
        List<Goal> goals = goalService.getGoalsCreatedInLastDays(days);
        return ResponseEntity.ok(goals);
    }
} 