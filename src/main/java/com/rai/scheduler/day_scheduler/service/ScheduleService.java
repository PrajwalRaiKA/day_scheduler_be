package com.rai.scheduler.day_scheduler.service;

import com.rai.scheduler.day_scheduler.model.Schedule;
import com.rai.scheduler.day_scheduler.repository.ScheduleRepository;
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
 * Service class for Schedule business logic operations.
 * Handles all schedule-related business operations and data validation.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ScheduleService {
    
    private final ScheduleRepository scheduleRepository;
    
    /**
     * Create a new schedule
     * @param schedule the schedule to create
     * @return the created schedule
     */
    public Schedule createSchedule(Schedule schedule) {
        log.info("Creating new schedule: {}", schedule.getTitle());
        validateSchedule(schedule);
        checkForTimeConflicts(schedule);
        Schedule savedSchedule = scheduleRepository.save(schedule);
        log.info("Schedule created successfully with ID: {}", savedSchedule.getId());
        return savedSchedule;
    }
    
    /**
     * Get all schedules
     * @return list of all schedules
     */
    @Transactional(readOnly = true)
    public List<Schedule> getAllSchedules() {
        log.info("Fetching all schedules");
        return scheduleRepository.findAll();
    }
    
    /**
     * Get schedule by ID
     * @param id the schedule ID
     * @return optional containing the schedule if found
     */
    @Transactional(readOnly = true)
    public Optional<Schedule> getScheduleById(String id) {
        log.info("Fetching schedule by ID: {}", id);
        return scheduleRepository.findById(id);
    }
    
    /**
     * Update an existing schedule
     * @param id the schedule ID
     * @param scheduleDetails the updated schedule details
     * @return the updated schedule
     * @throws RuntimeException if schedule not found
     */
    public Schedule updateSchedule(String id, Schedule scheduleDetails) {
        log.info("Updating schedule with ID: {}", id);
        Schedule existingSchedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Schedule not found with ID: " + id));
        
        existingSchedule.setTitle(scheduleDetails.getTitle());
        existingSchedule.setDescription(scheduleDetails.getDescription());
        existingSchedule.setStartTime(scheduleDetails.getStartTime());
        existingSchedule.setEndTime(scheduleDetails.getEndTime());
        
        // Check for time conflicts excluding the current schedule
        checkForTimeConflictsExcluding(existingSchedule, id);
        
        Schedule updatedSchedule = scheduleRepository.save(existingSchedule);
        log.info("Schedule updated successfully with ID: {}", updatedSchedule.getId());
        return updatedSchedule;
    }
    
    /**
     * Delete a schedule
     * @param id the schedule ID
     * @throws RuntimeException if schedule not found
     */
    public void deleteSchedule(String id) {
        log.info("Deleting schedule with ID: {}", id);
        if (!scheduleRepository.existsById(id)) {
            throw new RuntimeException("Schedule not found with ID: " + id);
        }
        scheduleRepository.deleteById(id);
        log.info("Schedule deleted successfully with ID: {}", id);
    }
    
    /**
     * Get schedules by date
     * @param date the date to filter by
     * @return list of schedules on the specified date
     */
    @Transactional(readOnly = true)
    public List<Schedule> getSchedulesByDate(LocalDate date) {
        log.info("Fetching schedules for date: {}", date);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        return scheduleRepository.findByDateBetween(startOfDay, endOfDay);
    }
    
    /**
     * Get schedules by date range
     * @param startDate the start date (inclusive)
     * @param endDate the end date (inclusive)
     * @return list of schedules within the date range
     */
    @Transactional(readOnly = true)
    public List<Schedule> getSchedulesByDateRange(LocalDate startDate, LocalDate endDate) {
        log.info("Fetching schedules by date range: {} to {}", startDate, endDate);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);
        return scheduleRepository.findByDateRange(startDateTime, endDateTime);
    }
    
    /**
     * Get schedules by title search
     * @param title the title to search for
     * @return list of schedules with matching titles
     */
    @Transactional(readOnly = true)
    public List<Schedule> searchSchedulesByTitle(String title) {
        log.info("Searching schedules by title: {}", title);
        return scheduleRepository.findByTitleContainingIgnoreCase(title);
    }
    
    /**
     * Get schedules created in the last N days
     * @param days number of days to look back
     * @return list of schedules created within the specified period
     */
    @Transactional(readOnly = true)
    public List<Schedule> getSchedulesCreatedInLastDays(int days) {
        log.info("Fetching schedules created in last {} days", days);
        LocalDateTime startDate = LocalDateTime.now().minusDays(days);
        return scheduleRepository.findSchedulesCreatedInLastDays(startDate);
    }
    
    /**
     * Validate schedule data
     * @param schedule the schedule to validate
     * @throws IllegalArgumentException if validation fails
     */
    private void validateSchedule(Schedule schedule) {
        if (schedule.getTitle() == null || schedule.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Schedule title cannot be null or empty");
        }
        
        if (schedule.getTitle().length() > 255) {
            throw new IllegalArgumentException("Schedule title cannot exceed 255 characters");
        }
        
        if (schedule.getStartTime() == null) {
            throw new IllegalArgumentException("Start time cannot be null");
        }
        
        if (schedule.getEndTime() == null) {
            throw new IllegalArgumentException("End time cannot be null");
        }
        
        if (schedule.getStartTime().isAfter(schedule.getEndTime())) {
            throw new IllegalArgumentException("Start time cannot be after end time");
        }
    }
    
    /**
     * Check for time conflicts with existing schedules
     * @param schedule the schedule to check
     * @throws IllegalArgumentException if conflicts are found
     */
    private void checkForTimeConflicts(Schedule schedule) {
        // This is a simplified conflict check - you might want to implement more sophisticated logic
        List<Schedule> existingSchedules = scheduleRepository.findByDateBetween(
                schedule.getStartTime().toLocalDate().atStartOfDay(),
                schedule.getStartTime().toLocalDate().atTime(LocalTime.MAX));
        
        for (Schedule existing : existingSchedules) {
            if (existing.getId().equals(schedule.getId())) continue;
            
            if (schedule.getStartTime().isBefore(existing.getEndTime()) && 
                schedule.getEndTime().isAfter(existing.getStartTime())) {
                throw new IllegalArgumentException("Time conflict detected with existing schedule: " + existing.getTitle());
            }
        }
    }
    
    /**
     * Check for time conflicts excluding a specific schedule
     * @param schedule the schedule to check
     * @param excludeId the ID of the schedule to exclude from conflict checking
     * @throws IllegalArgumentException if conflicts are found
     */
    private void checkForTimeConflictsExcluding(Schedule schedule, String excludeId) {
        List<Schedule> existingSchedules = scheduleRepository.findByDateBetween(
                schedule.getStartTime().toLocalDate().atStartOfDay(),
                schedule.getStartTime().toLocalDate().atTime(LocalTime.MAX));
        
        for (Schedule existing : existingSchedules) {
            if (existing.getId().equals(excludeId)) continue;
            
            if (schedule.getStartTime().isBefore(existing.getEndTime()) && 
                schedule.getEndTime().isAfter(existing.getStartTime())) {
                throw new IllegalArgumentException("Time conflict detected with existing schedule: " + existing.getTitle());
            }
        }
    }
} 