package com.example.pgno25oopgroupprojectfinalrelease.repository;

import com.example.pgno25oopgroupprojectfinalrelease.model.Bike;
import com.example.pgno25oopgroupprojectfinalrelease.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class BikeRepo {

    @Autowired
    private FileStorageService fileStorageService;

    private List<Bike> bikes;

    @PostConstruct
    public void init() {
        this.bikes = loadBikes();

        // If no bikes exist yet, create some demo bikes
        if (bikes.isEmpty()) {
            createDemoBikes();
        }
    }

    public List<Bike> findAll() {
        return this.bikes;
    }

    public Bike findById(int id) {
        for (Bike bike : bikes) {
            if (bike.getId() == id) {
                return bike;
            }
        }
        return null;
    }

    public Bike save(Bike bike) {
        // Generate a new ID if it's a new bike
        if (bike.getId() == 0) {
            bike.setId(generateNextId());
            bikes.add(bike);
            saveBikes();
        } else {
            // If bike already exists, update it instead of adding a duplicate
            update(bike);
        }

        return bike;
    }

    public void update(Bike updatedBike) {
        for (int i = 0; i < bikes.size(); i++) {
            if (bikes.get(i).getId() == updatedBike.getId()) {
                bikes.set(i, updatedBike);
                saveBikes();
                break;
            }
        }
    }

    public boolean delete(int id) {
        for (int i = 0; i < bikes.size(); i++) {
            if (bikes.get(i).getId() == id) {
                bikes.remove(i);
                saveBikes();
                return true;
            }
        }
        return false;
    }

    private int generateNextId() {
        int maxId = 0;
        for (Bike bike : bikes) {
            if (bike.getId() > maxId) {
                maxId = bike.getId();
            }
        }
        return maxId + 1;
    }

    private List<Bike> loadBikes() {
        List<Bike> loadedBikes = new ArrayList<>();

        String filePath = fileStorageService.getFilePath(FileStorageService.BIKES_FILE);
        File file = new File(filePath);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                loadedBikes = (List<Bike>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading bikes: " + e.getMessage());
            }
        }

        return loadedBikes;
    }

    private void saveBikes() {
        String filePath = fileStorageService.getFilePath(FileStorageService.BIKES_FILE);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(bikes);
        } catch (IOException e) {
            System.err.println("Error saving bikes: " + e.getMessage());
        }
    }

    private void createDemoBikes() {
        bikes.add(new Bike(1, "Mountain Explorer", "mountain", 10.99, true));
        bikes.add(new Bike(2, "City Cruiser", "road", 8.99, true));
        bikes.add(new Bike(3, "Speedy Electric", "electric", 15.99, true));
        bikes.add(new Bike(4, "Trail Blazer", "mountain", 12.99, true));
        bikes.add(new Bike(5, "Urban Commuter", "road", 9.99, true));
        bikes.add(new Bike(6, "Electric Zoom", "electric", 16.99, true));
        bikes.add(new Bike(7, "Offroad Beast", "mountain", 13.99, true));
        bikes.add(new Bike(8, "Highway Rider", "road", 10.99, true));
        bikes.add(new Bike(9, "E-Power Plus", "electric", 17.99, true));
        bikes.add(new Bike(10, "Mountain King", "mountain", 14.99, true));
        saveBikes();
    }
}
