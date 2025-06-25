package com.rai.scheduler.day_scheduler.repository;

import com.rai.scheduler.day_scheduler.model.Todo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Todo document operations.
 * Provides data access methods for todos.
 */
@Repository
public interface TodoRepository extends MongoRepository<Todo, String> {
    
    /**
     * Find todos by completion status
     * @param completed the completion status to filter by
     * @return list of todos with the specified completion status
     */
    List<Todo> findByCompleted(boolean completed);
    
    /**
     * Find todos by date
     * @param date the date to filter by
     * @return list of todos on the specified date
     */
    @Query("{'date': {$gte: ?0, $lt: ?1}}")
    List<Todo> findByDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
    
    /**
     * Find todos by date range
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of todos within the date range
     */
    @Query("{'date': {$gte: ?0, $lte: ?1}}")
    List<Todo> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find todos by title containing the specified text (case-insensitive)
     * @param title the text to search for in todo titles
     * @return list of todos with matching titles
     */
    List<Todo> findByTitleContainingIgnoreCase(String title);
    
    /**
     * Find todos created within the last N days
     * @param startDate the start date to look back from
     * @return list of todos created within the specified period
     */
    @Query("{'createdAt': {$gte: ?0}}")
    List<Todo> findTodosCreatedInLastDays(LocalDateTime startDate);
    
    /**
     * Count todos by completion status
     * @param completed the completion status to count
     * @return count of todos with the specified completion status
     */
    long countByCompleted(boolean completed);
} 