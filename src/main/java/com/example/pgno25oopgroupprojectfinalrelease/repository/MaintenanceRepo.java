package com.example.pgno25oopgroupprojectfinalrelease.repository;

import com.example.oopprojectbeta.model.Maintenance;
import com.example.oopprojectbeta.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
public class MaintenanceRepo {

    @Autowired
    private FileStorageService fileStorageService;

    private List<Maintenance> maintenanceRecords;

    @PostConstruct
    public void init() {
        this.maintenanceRecords = loadMaintenanceRecords();
    }

    public List<Maintenance> findAll() {
        return new ArrayList<>(maintenanceRecords);
    }

    public Maintenance findById(int id) {
        for (Maintenance maintenance : maintenanceRecords) {
            if (maintenance.getId() == id) {
                return maintenance;
            }
        }
        return null;
    }

    public List<Maintenance> findByBikeId(int bikeId) {
        List<Maintenance> bikeMaintenanceRecords = new ArrayList<>();
        for (Maintenance maintenance : maintenanceRecords) {
            if (Objects.equals(maintenance.getBikeId(), bikeId)) {
                bikeMaintenanceRecords.add(maintenance);
            }
        }
        return bikeMaintenanceRecords;
    }

    public Maintenance save(Maintenance maintenance) {
        // Generate a new ID if it's a new maintenance record
        if (maintenance.getId() == 0) {
            maintenance.setId(generateNextId());
            maintenanceRecords.add(maintenance);
        } else {
            // If maintenance record already exists, update it instead of adding a duplicate
            update(maintenance);
            return maintenance;
        }

        saveMaintenanceRecords();
        return maintenance;
    }

    public void update(Maintenance updatedMaintenance) {
        for (int i = 0; i < maintenanceRecords.size(); i++) {
            if (maintenanceRecords.get(i).getId() == updatedMaintenance.getId()) {
                maintenanceRecords.set(i, updatedMaintenance);
                saveMaintenanceRecords();
                break;
            }
        }
    }

    public boolean delete(int id) {
        for (int i = 0; i < maintenanceRecords.size(); i++) {
            if (maintenanceRecords.get(i).getId() == id) {
                maintenanceRecords.remove(i);
                saveMaintenanceRecords();
                return true;
            }
        }
        return false;
    }

    private int generateNextId() {
        int maxId = 0;
        for (Maintenance maintenance : maintenanceRecords) {
            if (maintenance.getId() > maxId) {
                maxId = maintenance.getId();
            }
        }
        return maxId + 1;
    }

    private List<Maintenance> loadMaintenanceRecords() {
        List<Maintenance> loadedMaintenanceRecords = new ArrayList<>();

        String filePath = fileStorageService.getFilePath(FileStorageService.MAINTENANCE_FILE);
        File file = new File(filePath);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                loadedMaintenanceRecords = (List<Maintenance>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading maintenance records: " + e.getMessage());
            }
        }

        return loadedMaintenanceRecords;
    }

    private void saveMaintenanceRecords() {
        String filePath = fileStorageService.getFilePath(FileStorageService.MAINTENANCE_FILE);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(maintenanceRecords);
        } catch (IOException e) {
            System.err.println("Error saving maintenance records: " + e.getMessage());
        }
    }
}
