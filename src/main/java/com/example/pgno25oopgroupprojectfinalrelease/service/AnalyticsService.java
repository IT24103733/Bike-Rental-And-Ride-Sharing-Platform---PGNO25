package com.example.pgno25oopgroupprojectfinalrelease.service;

import com.example.pgno25oopgroupprojectfinalrelease.model.Bike;
import com.example.pgno25oopgroupprojectfinalrelease.model.Booking;
import com.example.pgno25oopgroupprojectfinalrelease.model.RideShare;
import com.example.pgno25oopgroupprojectfinalrelease.repository.BikeRepo;
import com.example.pgno25oopgroupprojectfinalrelease.repository.BookingRepo;
import com.example.pgno25oopgroupprojectfinalrelease.repository.RideShareRepo;
import com.example.pgno25oopgroupprojectfinalrelease.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnalyticsService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private BikeRepo bikeRepo;

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private RideShareRepo rideShareRepo;

    /**
     * Get the total number of registered users
     * @return the total number of users
     */
    public int getTotalUsers() {
        return userRepo.findAll().size();
    }

    /**
     * Get the total number of bikes
     * @return the total number of bikes
     */
    public int getTotalBikes() {
        return bikeRepo.findAll().size();
    }

    /**
     * Get the number of available bikes
     * @return the number of available bikes
     */
    public int getAvailableBikes() {
        List<Bike> allBikes = bikeRepo.findAll();
        int availableBikes = 0;
        for (Bike bike : allBikes) {
            if (bike.isAvailable()) {
                availableBikes++;
            }
        }
        return availableBikes;
    }

    /**
     * Calculate the total revenue from bookings and ride shares
     * @return the total revenue
     */
    public double getTotalRevenue() {
        double bookingRevenue = calculateBookingRevenue();
        double rideShareRevenue = calculateRideShareRevenue();
        return bookingRevenue + rideShareRevenue;
    }

    /**
     * Calculate the revenue from bike bookings
     * @return the booking revenue
     */
    private double calculateBookingRevenue() {
        List<Booking> allBookings = bookingRepo.findAll();
        double totalRevenue = 0.0;

        for (Booking booking : allBookings) {
            Bike bike = bikeRepo.findById(booking.getBikeId());
            if (bike != null) {
                // Assume each booking is for 1 hour if it's a ride request
                // or 2 hours if it's a bike rental
                double hours = booking.isRideRequest() ? 1.0 : 2.0;
                totalRevenue += bike.getPricePerHour() * hours;
            }
        }

        return totalRevenue;
    }

    /**
     * Calculate the revenue from ride shares.
     * @return the ride share revenue
     */
    private double calculateRideShareRevenue() {
        List<RideShare> allRideShares = rideShareRepo.findAll();
        double totalRevenue = 0.0;

        for (RideShare rideShare : allRideShares) {
            if (rideShare.isOffer()) {
                // Assume each ride share offer generates $5 per capacity
                totalRevenue += 5.0 * rideShare.getCapacity();
            }
        }

        return totalRevenue;
    }

    /**
     * Get the total number of bookings
     * @return the total number of bookings
     */
    public int getTotalBookings() {
        return bookingRepo.findAll().size();
    }
}
