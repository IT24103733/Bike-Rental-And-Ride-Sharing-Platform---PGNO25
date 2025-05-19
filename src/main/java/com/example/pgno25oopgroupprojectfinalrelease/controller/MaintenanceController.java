package com.example.pgno25oopgroupprojectfinalrelease.controller;

import com.example.pgno25oopgroupprojectfinalrelease.model.Bike;
import com.example.pgno25oopgroupprojectfinalrelease.model.Maintenance;
import com.example.pgno25oopgroupprojectfinalrelease.model.User;
import com.example.pgno25oopgroupprojectfinalrelease.service.BikeService;
import com.example.pgno25oopgroupprojectfinalrelease.service.MaintenanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/maintenance")
public class MaintenanceController {

    @Autowired
    private MaintenanceService maintenanceService;

    @Autowired
    private BikeService bikeService;

    @GetMapping
    public String getAllMaintenanceRecords(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Only admins can access maintenance records
        if (!user.isAdmin()) {
            return "redirect:/bikes";
        }

        model.addAttribute("maintenanceRecords", maintenanceService.getAllMaintenanceRecords());
        model.addAttribute("bikes", bikeService.getAllBikes());
        model.addAttribute("newMaintenance", new Maintenance());
        model.addAttribute("isAdmin", user.isAdmin());

        return "maintenance";
    }

    @GetMapping("/bike/{bikeId}")
    public String getBikeMaintenanceRecords(@PathVariable int bikeId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Only admins can access maintenance records
        if (!user.isAdmin()) {
            return "redirect:/bikes";
        }

        Bike bike = bikeService.getBikeById(bikeId);
        if (bike == null) {
            return "redirect:/bikes";
        }

        model.addAttribute("bike", bike);
        model.addAttribute("maintenanceRecords", maintenanceService.getMaintenanceByBikeId(bikeId));
        model.addAttribute("newMaintenance", new Maintenance());
        model.addAttribute("bikeId", bikeId);
        model.addAttribute("isAdmin", user.isAdmin());

        return "bikeMaintenanceRecords";
    }

    @PostMapping("/add")
    public String addMaintenance(@ModelAttribute Maintenance maintenance, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            return "redirect:/bikes";
        }

        maintenanceService.addMaintenance(maintenance, user);

        // Redirect to the bike's maintenance page if bikeId is provided
        if (maintenance.getBikeId() != null && maintenance.getBikeId() > 0) {
            return "redirect:/maintenance/bike/" + maintenance.getBikeId();
        }

        return "redirect:/maintenance";
    }

    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<?> updateMaintenance(@RequestBody Maintenance maintenance, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Not authenticated");
            return ResponseEntity.status(401).body(response);
        }

        if (!user.isAdmin()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Not authorized");
            return ResponseEntity.status(403).body(response);
        }

        Maintenance updatedMaintenance = maintenanceService.updateMaintenance(maintenance, user);
        if (updatedMaintenance == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Maintenance record not found");
            return ResponseEntity.status(404).body(response);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("maintenance", updatedMaintenance);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteMaintenance(@PathVariable int id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Not authenticated");
            return ResponseEntity.status(401).body(response);
        }

        if (!user.isAdmin()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Not authorized");
            return ResponseEntity.status(403).body(response);
        }

        boolean deleted = maintenanceService.deleteMaintenance(id, user);
        if (!deleted) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Maintenance record not found");
            return ResponseEntity.status(404).body(response);
        }

        Map<String, Boolean> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }
}
