package com.rai.scheduler.day_scheduler.repository;

import com.rai.scheduler.day_scheduler.model.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Schedule document operations.
 * Provides data access methods for schedules.
 */
@Repository
public interface ScheduleRepository extends MongoRepository<Schedule, String> {
    
    /**
     * Find schedules by date
     * @param date the date to filter by
     * @return list of schedules on the specified date
     */
    @Query("{'startTime': {$gte: ?0, $lt: ?1}}")
    List<Schedule> findByDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);
    
    /**
     * Find schedules by date range
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of schedules within the date range
     */
    @Query("{'startTime': {$gte: ?0, $lte: ?1}}")
    List<Schedule> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find schedules by title containing the specified text (case-insensitive)
     * @param title the text to search for in schedule titles
     * @return list of schedules with matching titles
     */
    List<Schedule> findByTitleContainingIgnoreCase(String title);
    
    /**
     * Find schedules created within the last N days
     * @param startDate the start date to look back from
     * @return list of schedules created within the specified period
     */
    @Query("{'createdAt': {$gte: ?0}}")
    List<Schedule> findSchedulesCreatedInLastDays(LocalDateTime startDate);
} 