package com.rai.scheduler.day_scheduler.repository;

import com.rai.scheduler.day_scheduler.model.Goal;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Goal document operations.
 * Provides data access methods for goals.
 */
@Repository
public interface GoalRepository extends MongoRepository<Goal, String> {
    
    /**
     * Find goals by date
     * @param date the date to filter by
     * @return list of goals on the specified date
     */
    @Query("{'date': {$gte: ?0, $lt: ?1}}")
    List<Goal> findByDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
    
    /**
     * Find goals by date range
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of goals within the date range
     */
    @Query("{'date': {$gte: ?0, $lte: ?1}}")
    List<Goal> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find goals by title containing the specified text (case-insensitive)
     * @param title the text to search for in goal titles
     * @return list of goals with matching titles
     */
    List<Goal> findByTitleContainingIgnoreCase(String title);
    
    /**
     * Find goals created within the last N days
     * @param startDate the start date to look back from
     * @return list of goals created within the specified period
     */
    @Query("{'createdAt': {$gte: ?0}}")
    List<Goal> findGoalsCreatedInLastDays(LocalDateTime startDate);
} 