package com.example.pgno25oopgroupprojectfinalrelease.service;

import com.example.pgno25oopgroupprojectfinalrelease.model.Bike;
import com.example.pgno25oopgroupprojectfinalrelease.model.Maintenance;
import com.example.pgno25oopgroupprojectfinalrelease.model.User;
import com.example.pgno25oopgroupprojectfinalrelease.repository.BikeRepo;
import com.example.pgno25oopgroupprojectfinalrelease.repository.MaintenanceRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MaintenanceService {

    @Autowired
    private MaintenanceRepo maintenanceRepo;

    @Autowired
    private BikeRepo bikeRepo;

    public List<Maintenance> getAllMaintenanceRecords() {
        return maintenanceRepo.findAll();
    }

    public Maintenance getMaintenanceById(int id) {
        return maintenanceRepo.findById(id);
    }

    public List<Maintenance> getMaintenanceByBikeId(int bikeId) {
        return maintenanceRepo.findByBikeId(bikeId);
    }

    public Maintenance addMaintenance(Maintenance maintenance, User admin) {
        // Check if user is an admin
        if (!admin.isAdmin()) {
            return null;
        }

        // Check if bikeId is provided
        if (maintenance.getBikeId() == null) {
            return null;
        }

        // Get bike details
        Bike bike = bikeRepo.findById(maintenance.getBikeId());
        if (bike == null) {
            return null;
        }

        // Set bike name
        maintenance.setBikeName(bike.getName());

        // Set admin information
        maintenance.setAdminId(admin.getId());
        maintenance.setAdminUsername(admin.getUsername());

        // Set maintenance date if not provided.
        if (maintenance.getMaintenanceDate() == null) {
            maintenance.setMaintenanceDate(LocalDateTime.now());
        }

        return maintenanceRepo.save(maintenance);
    }

    public Maintenance updateMaintenance(Maintenance maintenance, User admin) {
        // Check if user is an admin
        if (!admin.isAdmin()) {
            return null;
        }

        Maintenance existingMaintenance = maintenanceRepo.findById(maintenance.getId());

        // Check if maintenance record exists
        if (existingMaintenance == null) {
            return null;
        }

        // Update only the description and date
        existingMaintenance.setDescription(maintenance.getDescription());
        if (maintenance.getMaintenanceDate() != null) {
            existingMaintenance.setMaintenanceDate(maintenance.getMaintenanceDate());
        }

        return maintenanceRepo.save(existingMaintenance);
    }

    public boolean deleteMaintenance(int maintenanceId, User admin) {
        // Check if user is an admin
        if (!admin.isAdmin()) {
            return false;
        }

        Maintenance maintenance = maintenanceRepo.findById(maintenanceId);

        // Check if maintenance record exists
        if (maintenance == null) {
            return false;
        }

        return maintenanceRepo.delete(maintenanceId);
    }
}
