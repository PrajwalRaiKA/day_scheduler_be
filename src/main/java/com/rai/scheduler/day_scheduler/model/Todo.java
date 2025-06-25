package com.rai.scheduler.day_scheduler.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

/**
 * Document representing a todo item in the day scheduler application.
 * Todos are specific tasks that need to be completed on specific dates.
 */
@Document(collection = "todos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Todo {
    
    @Id
    private String id;
    
    private String title;
    
    private String description;
    
    private boolean completed;
    
    private LocalDateTime date;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
} 