package com.example.pgno25oopgroupprojectfinalrelease.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RideShare implements Serializable {
    private int id;
    private int userId;
    private String username; // Store username for display purposes
    private String landmark; // The location/landmark for the ride
    private String description; // Additional details about the ride
    private boolean isOffer; // true if offering a ride, false if requesting a ride
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime rideTime; // When the ride is scheduled
    private int capacity; // Number of people that can join (for offers)
    private int spotsAvailable; // Number of spots still available
}