package com.example.pgno25oopgroupprojectfinalrelease.repository;

import com.example.oopprojectbeta.model.Booking;
import com.example.oopprojectbeta.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BookingRepo {

    @Autowired
    private FileStorageService fileStorageService;

    private List<Booking> bookings;

    @PostConstruct
    public void init() {
        this.bookings = loadBookings();
    }

    public List<Booking> findAll() {
        return this.bookings;
    }

    public Booking findById(int id) {
        for (Booking booking : bookings) {
            if (booking.getId() == id) {
                return booking;
            }
        }
        return null;
    }

    public List<Booking> findByUserId(int userId) {
        List<Booking> userBookings = new ArrayList<>();
        for (Booking booking : bookings) {
            if (booking.getUserId() == userId) {
                userBookings.add(booking);
            }
        }
        return userBookings;
    }

    public Booking save(Booking booking) {
        // Generate a new ID if it's a new booking
        if (booking.getId() == 0) {
            booking.setId(generateNextId());
            bookings.add(booking);
        } else {
            // If booking already exists, update it instead of adding a duplicate
            update(booking);
            return booking;
        }

        saveBookings();
        return booking;
    }

    public void update(Booking updatedBooking) {
        for (int i = 0; i < bookings.size(); i++) {
            if (bookings.get(i).getId() == updatedBooking.getId()) {
                bookings.set(i, updatedBooking);
                saveBookings();
                break;
            }
        }
    }

    public boolean delete(int id) {
        for (int i = 0; i < bookings.size(); i++) {
            if (bookings.get(i).getId() == id) {
                bookings.remove(i);
                saveBookings();
                return true;
            }
        }
        return false;
    }

    private int generateNextId() {
        int maxId = 0;
        for (Booking booking : bookings) {
            if (booking.getId() > maxId) {
                maxId = booking.getId();
            }
        }
        return maxId + 1;
    }

    private List<Booking> loadBookings() {
        List<Booking> loadedBookings = new ArrayList<>();

        String filePath = fileStorageService.getFilePath(FileStorageService.BOOKINGS_FILE);
        File file = new File(filePath);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                loadedBookings = (List<Booking>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading bookings: " + e.getMessage());
            }
        }

        return loadedBookings;
    }

    private void saveBookings() {
        String filePath = fileStorageService.getFilePath(FileStorageService.BOOKINGS_FILE);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(bookings);
        } catch (IOException e) {
            System.err.println("Error saving bookings: " + e.getMessage());
        }
    }
}
