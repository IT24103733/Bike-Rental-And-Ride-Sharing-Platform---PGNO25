package com.example.pgno25oopgroupprojectfinalrelease.repository;

import com.example.pgno25oopgroupprojectfinalrelease.model.RideShare;
import com.example.pgno25oopgroupprojectfinalrelease.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class RideShareRepo {

    @Autowired
    private FileStorageService fileStorageService;

    private List<RideShare> rideShares;

    @PostConstruct
    public void init() {
        this.rideShares = loadRideShares();
    }

    public List<RideShare> findAll() {
        return new ArrayList<>(rideShares);
    }

    public RideShare findById(int id) {
        for (RideShare rideShare : rideShares) {
            if (rideShare.getId() == id) {
                return rideShare;
            }
        }
        return null;
    }

    public List<RideShare> findByUserId(int userId) {
        List<RideShare> userRideShares = new ArrayList<>();
        for (RideShare rideShare : rideShares) {
            if (rideShare.getUserId() == userId) {
                userRideShares.add(rideShare);
            }
        }
        return userRideShares;
    }

    public List<RideShare> findOffers() {
        List<RideShare> offers = new ArrayList<>();
        for (RideShare rideShare : rideShares) {
            if (rideShare.isOffer()) {
                offers.add(rideShare);
            }
        }
        return offers;
    }

    public List<RideShare> findRequests() {
        List<RideShare> requests = new ArrayList<>();
        for (RideShare rideShare : rideShares) {
            if (!rideShare.isOffer()) {
                requests.add(rideShare);
            }
        }
        return requests;
    }

    public RideShare save(RideShare rideShare) {
        // Generate a new ID if it's a new ride share
        if (rideShare.getId() == 0) {
            rideShare.setId(generateNextId());
            rideShares.add(rideShare);
        } else {
            // If ride share already exists, update it instead of adding a duplicate
            update(rideShare);
            return rideShare;
        }

        saveRideShares();
        return rideShare;
    }

    public void update(RideShare updatedRideShare) {
        for (int i = 0; i < rideShares.size(); i++) {
            if (rideShares.get(i).getId() == updatedRideShare.getId()) {
                rideShares.set(i, updatedRideShare);
                saveRideShares();
                break;
            }
        }
    }

    public boolean delete(int id) {
        for (int i = 0; i < rideShares.size(); i++) {
            if (rideShares.get(i).getId() == id) {
                rideShares.remove(i);
                saveRideShares();
                return true;
            }
        }
        return false;
    }

    private int generateNextId() {
        int maxId = 0;
        for (RideShare rideShare : rideShares) {
            if (rideShare.getId() > maxId) {
                maxId = rideShare.getId();
            }
        }
        return maxId + 1;
    }

    private List<RideShare> loadRideShares() {
        List<RideShare> loadedRideShares = new ArrayList<>();

        String filePath = fileStorageService.getFilePath(FileStorageService.RIDESHARES_FILE);
        File file = new File(filePath);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                loadedRideShares = (List<RideShare>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading ride shares: " + e.getMessage());
            }
        }

        return loadedRideShares;
    }

    private void saveRideShares() {
        String filePath = fileStorageService.getFilePath(FileStorageService.RIDESHARES_FILE);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(rideShares);
        } catch (IOException e) {
            System.err.println("Error saving ride shares: " + e.getMessage());
        }
    }
}