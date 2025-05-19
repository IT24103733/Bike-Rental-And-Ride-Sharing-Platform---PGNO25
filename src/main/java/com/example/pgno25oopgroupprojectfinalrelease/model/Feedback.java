package com.example.pgno25oopgroupprojectfinalrelease.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Feedback implements Serializable {
        private int id;
        private int userId;
        private String username; // Store username for display purposes
        private String content;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

