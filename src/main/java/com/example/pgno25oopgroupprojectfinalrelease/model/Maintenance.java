package com.example.pgno25oopgroupprojectfinalrelease.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Maintenance implements Serializable {
    private int id;
    private Integer bikeId;
    private String bikeName; // Store bike name for display purposes
    private int adminId;
    private String adminUsername; // Store admin username for display purposes
    private String description;
    private LocalDateTime maintenanceDate;
}

