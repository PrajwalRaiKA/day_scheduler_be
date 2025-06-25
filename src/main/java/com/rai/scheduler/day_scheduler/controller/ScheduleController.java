package com.rai.scheduler.day_scheduler.controller;

import com.rai.scheduler.day_scheduler.model.Schedule;
import com.rai.scheduler.day_scheduler.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;

/**
 * REST controller for Schedule operations.
 * Provides endpoints for managing schedules in the day scheduler application.
 */
@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ScheduleController {
    
    private final ScheduleService scheduleService;
    
    /**
     * Create a new schedule
     * @param schedule the schedule to create
     * @return the created schedule with HTTP 201 status
     */
    @PostMapping
    public ResponseEntity<Schedule> createSchedule(@Valid @RequestBody Schedule schedule) {
        log.info("POST /api/schedules - Creating new schedule: {}", schedule.getTitle());
        Schedule createdSchedule = scheduleService.createSchedule(schedule);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSchedule);
    }
    
    /**
     * Get all schedules
     * @return list of all schedules
     */
    @GetMapping
    public ResponseEntity<List<Schedule>> getAllSchedules() {
        log.info("GET /api/schedules - Fetching all schedules");
        List<Schedule> schedules = scheduleService.getAllSchedules();
        return ResponseEntity.ok(schedules);
    }
    
    /**
     * Get schedule by ID
     * @param id the schedule ID
     * @return the schedule if found, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Schedule> getScheduleById(@PathVariable String id) {
        log.info("GET /api/schedules/{} - Fetching schedule by ID", id);
        return scheduleService.getScheduleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Update an existing schedule
     * @param id the schedule ID
     * @param scheduleDetails the updated schedule details
     * @return the updated schedule, or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Schedule> updateSchedule(@PathVariable String id, @Valid @RequestBody Schedule scheduleDetails) {
        log.info("PUT /api/schedules/{} - Updating schedule", id);
        try {
            Schedule updatedSchedule = scheduleService.updateSchedule(id, scheduleDetails);
            return ResponseEntity.ok(updatedSchedule);
        } catch (RuntimeException e) {
            log.error("Schedule not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Delete a schedule
     * @param id the schedule ID
     * @return 204 No Content if successful, or 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable String id) {
        log.info("DELETE /api/schedules/{} - Deleting schedule", id);
        try {
            scheduleService.deleteSchedule(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("Schedule not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get schedules by date
     * @param date the date to filter by (format: yyyy-MM-dd)
     * @return list of schedules on the specified date
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<List<Schedule>> getSchedulesByDate(@PathVariable String date) {
        log.info("GET /api/schedules/date/{} - Fetching schedules by date", date);
        try {
            LocalDate scheduleDate = LocalDate.parse(date);
            List<Schedule> schedules = scheduleService.getSchedulesByDate(scheduleDate);
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            log.error("Invalid date format: {}", date);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Get schedules by date range
     * @param startDate the start date (format: yyyy-MM-dd)
     * @param endDate the end date (format: yyyy-MM-dd)
     * @return list of schedules within the date range
     */
    @GetMapping("/daterange")
    public ResponseEntity<List<Schedule>> getSchedulesByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        log.info("GET /api/schedules/daterange - Fetching schedules by date range: {} to {}", startDate, endDate);
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);
            List<Schedule> schedules = scheduleService.getSchedulesByDateRange(start, end);
            return ResponseEntity.ok(schedules);
        } catch (Exception e) {
            log.error("Invalid date format: startDate={}, endDate={}", startDate, endDate);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Search schedules by title
     * @param title the title to search for
     * @return list of schedules with matching titles
     */
    @GetMapping("/search")
    public ResponseEntity<List<Schedule>> searchSchedulesByTitle(@RequestParam String title) {
        log.info("GET /api/schedules/search - Searching schedules by title: {}", title);
        List<Schedule> schedules = scheduleService.searchSchedulesByTitle(title);
        return ResponseEntity.ok(schedules);
    }
    
    /**
     * Get schedules created in the last N days
     * @param days number of days to look back
     * @return list of schedules created within the specified period
     */
    @GetMapping("/recent")
    public ResponseEntity<List<Schedule>> getSchedulesCreatedInLastDays(@RequestParam(defaultValue = "7") int days) {
        log.info("GET /api/schedules/recent - Fetching schedules created in last {} days", days);
        List<Schedule> schedules = scheduleService.getSchedulesCreatedInLastDays(days);
        return ResponseEntity.ok(schedules);
    }
} 