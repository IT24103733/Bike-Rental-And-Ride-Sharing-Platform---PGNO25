package com.example.pgno25oopgroupprojectfinalrelease.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking implements Serializable {
    private int id;
    private int userId;
    private int bikeId;
    private LocalDateTime bookingTime;
    private boolean isRideRequest; // true if ride requested, false if bike rental
    private String address; // Only used if isRideRequest is true
}
