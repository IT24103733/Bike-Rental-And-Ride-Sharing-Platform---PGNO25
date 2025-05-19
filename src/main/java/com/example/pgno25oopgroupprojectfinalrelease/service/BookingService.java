package com.example.pgno25oopgroupprojectfinalrelease.service;

import com.example.pgno25oopgroupprojectfinalrelease.model.Bike;
import com.example.pgno25oopgroupprojectfinalrelease.model.Booking;
import com.example.pgno25oopgroupprojectfinalrelease.repository.BikeRepo;
import com.example.pgno25oopgroupprojectfinalrelease.repository.BookingRepo;
import com.example.pgno25oopgroupprojectfinalrelease.utilities.BikeQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//time
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BookingService {

    @Autowired
    private BookingRepo bookingRepo;

    @Autowired
    private BikeRepo bikeRepo;

    @Autowired
    private BikeQueue bikeQueue;

    public Booking requestRide(int userId, int bikeId, String address) {
        Bike bike = bikeRepo.findById(bikeId);

        if (bike != null && bike.isAvailable()) {
            bike.setAvailable(false);
            bikeRepo.update(bike);

            Booking booking = new Booking();
            booking.setUserId(userId);
            booking.setBikeId(bikeId);
            booking.setBookingTime(LocalDateTime.now());
            booking.setRideRequest(true);
            booking.setAddress(address);

            // Add to queue for processing
            bikeQueue.enqueue(booking);

            return bookingRepo.save(booking);
        }

        return null;
    }

    public Booking rentBike(int userId, int bikeId) {
        Bike bike = bikeRepo.findById(bikeId);

        if (bike != null && bike.isAvailable()) {
            bike.setAvailable(false);
            bikeRepo.update(bike);

            Booking booking = new Booking();
            booking.setUserId(userId);
            booking.setBikeId(bikeId);
            booking.setBookingTime(LocalDateTime.now());
            booking.setRideRequest(false);

            return bookingRepo.save(booking);
        }

        return null;
    }

    public List<Booking> getUserBookings(int userId) {
        return bookingRepo.findByUserId(userId);
    }

    public List<Booking> getAllBookings() {
        return bookingRepo.findAll();
    }

    public boolean cancelBooking(int bookingId) {
        Booking booking = bookingRepo.findById(bookingId);
        if (booking != null) {
            // Make the bike available again
            Bike bike = bikeRepo.findById(booking.getBikeId());
            if (bike != null) {
                bike.setAvailable(true);
                bikeRepo.update(bike);
            }

            // Delete the booking
            return bookingRepo.delete(bookingId);
        }
        return false;
    }

    public Booking updateBooking(Booking booking) {
        Booking existingBooking = bookingRepo.findById(booking.getId());
        if (existingBooking != null) {
            // Update only allowed fields (address for ride requests)
            if (existingBooking.isRideRequest()) {
                existingBooking.setAddress(booking.getAddress());
                bookingRepo.update(existingBooking);
                return existingBooking;
            }
        }
        return null;
    }

    public Booking getBookingById(int id) {
        return bookingRepo.findById(id);
    }

    public Booking reassignBike(int bookingId, int newBikeId) {
        Booking booking = bookingRepo.findById(bookingId);
        if (booking == null) {
            return null;
        }

        // Make the old bike available
        Bike oldBike = bikeRepo.findById(booking.getBikeId());
        if (oldBike != null) {
            oldBike.setAvailable(true);
            bikeRepo.update(oldBike);
        }

        // Assign the new bike
        Bike newBike = bikeRepo.findById(newBikeId);
        if (newBike != null && newBike.isAvailable()) {
            newBike.setAvailable(false);
            bikeRepo.update(newBike);

            booking.setBikeId(newBikeId);
            bookingRepo.update(booking);
            return booking;
        }

        return null;
    }
}
