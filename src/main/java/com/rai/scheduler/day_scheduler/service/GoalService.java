package com.rai.scheduler.day_scheduler.service;

import com.rai.scheduler.day_scheduler.model.Goal;
import com.rai.scheduler.day_scheduler.repository.GoalRepository;
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
 * Service class for Goal business logic operations.
 * Handles all goal-related business operations and data validation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GoalService {
    
    private final GoalRepository goalRepository;
    
    /**
     * Create a new goal
     * @param goal the goal to create
     * @return the created goal
     */
    public Goal createGoal(Goal goal) {
        log.info("Creating new goal: {}", goal.getTitle());
        validateGoal(goal);
        Goal savedGoal = goalRepository.save(goal);
        log.info("Goal created successfully with ID: {}", savedGoal.getId());
        return savedGoal;
    }
    
    /**
     * Get all goals
     * @return list of all goals
     */
    @Transactional(readOnly = true)
    public List<Goal> getAllGoals() {
        log.info("Fetching all goals");
        return goalRepository.findAll();
    }
    
    /**
     * Get goal by ID
     * @param id the goal ID
     * @return optional containing the goal if found
     */
    @Transactional(readOnly = true)
    public Optional<Goal> getGoalById(String id) {
        log.info("Fetching goal by ID: {}", id);
        return goalRepository.findById(id);
    }
    
    /**
     * Update an existing goal
     * @param id the goal ID
     * @param goalDetails the updated goal details
     * @return the updated goal
     * @throws RuntimeException if goal not found
     */
    public Goal updateGoal(String id, Goal goalDetails) {
        log.info("Updating goal with ID: {}", id);
        Goal existingGoal = goalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Goal not found with ID: " + id));
        
        existingGoal.setTitle(goalDetails.getTitle());
        existingGoal.setDescription(goalDetails.getDescription());
        existingGoal.setDate(goalDetails.getDate());
        
        Goal updatedGoal = goalRepository.save(existingGoal);
        log.info("Goal updated successfully with ID: {}", updatedGoal.getId());
        return updatedGoal;
    }
    
    /**
     * Delete a goal
     * @param id the goal ID
     * @throws RuntimeException if goal not found
     */
    public void deleteGoal(String id) {
        log.info("Deleting goal with ID: {}", id);
        if (!goalRepository.existsById(id)) {
            throw new RuntimeException("Goal not found with ID: " + id);
        }
        goalRepository.deleteById(id);
        log.info("Goal deleted successfully with ID: {}", id);
    }
    
    /**
     * Get goals by date
     * @param date the date to filter by
     * @return list of goals on the specified date
     */
    @Transactional(readOnly = true)
    public List<Goal> getGoalsByDate(LocalDate date) {
        log.info("Fetching goals for date: {}", date);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return goalRepository.findByDateBetween(startOfDay, endOfDay);
    }
    
    /**
     * Get goals by date range
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of goals within the date range
     */
    @Transactional(readOnly = true)
    public List<Goal> getGoalsByDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("Fetching goals by date range: {} to {}", startDate, endDate);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        return goalRepository.findByDateRange(startDateTime, endDateTime);
    }
    
    /**
     * Get goals by title search
     * @param title the title to search for
     * @return list of goals with matching titles
     */
    @Transactional(readOnly = true)
    public List<Goal> searchGoalsByTitle(String title) {
        log.info("Searching goals by title: {}", title);
        return goalRepository.findByTitleContainingIgnoreCase(title);
    }
    
    /**
     * Get goals created in the last N days
     * @param days number of days to look back
     * @return list of goals created within the specified period
     */
    @Transactional(readOnly = true)
    public List<Goal> getGoalsCreatedInLastDays(int days) {
        log.info("Fetching goals created in last {} days", days);
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        return goalRepository.findGoalsCreatedInLastDays(startDate);
    }
    
    /**
     * Validate goal data
     * @param goal the goal to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateGoal(Goal goal) {
        if (goal.getTitle() == null || goal.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Goal title cannot be null or empty");
        }
        
        if (goal.getTitle().length() > 255) {
            throw new IllegalArgumentException("Goal title cannot exceed 255 characters");
        }
        
        if (goal.getDate() == null) {
            throw new IllegalArgumentException("Goal date cannot be null");
        }
    }
} 