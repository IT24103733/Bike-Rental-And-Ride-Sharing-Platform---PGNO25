package com.example.pgno25oopgroupprojectfinalrelease.controller;

import com.example.pgno25oopgroupprojectfinalrelease.model.RideShare;
import com.example.pgno25oopgroupprojectfinalrelease.model.User;
import com.example.pgno25oopgroupprojectfinalrelease.service.RideShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/rideshares")
public class RideShareController {

    @Autowired
    private RideShareService rideShareService;

    /**
     * Show all ride shares
     */
    @GetMapping
    public String getAllRideShares(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<RideShare> offers = rideShareService.getAllOffers();
        List<RideShare> requests = rideShareService.getAllRequests();

        model.addAttribute("offers", offers);
        model.addAttribute("requests", requests);
        model.addAttribute("isAdmin", user.isAdmin());
        model.addAttribute("currentUserId", user.getId());

        return "rideshares";
    }

    /**
     * Show user's ride shares
     */
    @GetMapping("/my")
    public String getMyRideShares(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<RideShare> userRideShares = rideShareService.getRideSharesByUserId(user.getId());

        model.addAttribute("rideShares", userRideShares);
        model.addAttribute("isAdmin", user.isAdmin());
        model.addAttribute("currentUserId", user.getId());

        return "myRideShares";
    }

    /**
     * Show form to create a new ride share
     */
    @GetMapping("/create")
    public String showCreateForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Get all available ride offers to display when requesting a ride
        List<RideShare> availableOffers = rideShareService.getAllOffers();

        model.addAttribute("rideShare", new RideShare());
        model.addAttribute("availableOffers", availableOffers);
        model.addAttribute("currentUserId", user.getId());
        return "createRideShare";
    }

    /**
     * Create a new ride share
     */
    @PostMapping("/create")
    public String createRideShare(@ModelAttribute RideShare rideShare, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Set ride time if not provided
        if (rideShare.getRideTime() == null) {
            rideShare.setRideTime(LocalDateTime.now().plusHours(1)); // Default to 1 hour from now
        }

        RideShare created = rideShareService.createRideShare(rideShare, user);
        if (created != null) {
            redirectAttributes.addFlashAttribute("success", "Ride share created successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to create ride share");
        }

        return "redirect:/rideshares/my";
    }

    /**
     * Show form to edit a ride share
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        RideShare rideShare = rideShareService.getRideShareById(id);
        if (rideShare == null) {
            return "redirect:/rideshares";
        }

        // Check if user can edit this ride share
        if (!rideShareService.canModifyRideShare(rideShare, user)) {
            return "redirect:/rideshares";
        }

        model.addAttribute("rideShare", rideShare);
        return "editRideShare";
    }

    /**
     * Update a ride share
     */
    @PostMapping("/edit/{id}")
    public String updateRideShare(@PathVariable int id, @ModelAttribute RideShare rideShare, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Ensure ID matches path variable
        rideShare.setId(id);

        RideShare updated = rideShareService.updateRideShare(rideShare, user);
        if (updated != null) {
            redirectAttributes.addFlashAttribute("success", "Ride share updated successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to update ride share");
        }

        return "redirect:/rideshares/my";
    }

    /**
     * Delete a ride share
     */
    @GetMapping("/delete/{id}")
    public String deleteRideShare(@PathVariable int id, HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        boolean deleted = rideShareService.deleteRideShare(id, user);
        if (deleted) {
            redirectAttributes.addFlashAttribute("success", "Ride share deleted successfully");
        } else {
            redirectAttributes.addFlashAttribute("error", "Failed to delete ride share");
        }

        // If admin, redirect to all ride shares, otherwise to user's ride shares
        if (user.isAdmin()) {
            return "redirect:/rideshares";
        } else {
            return "redirect:/rideshares/my";
        }
    }

    /**
     * Join a ride share
     */
    @PostMapping("/join/{id}")
    @ResponseBody
    public ResponseEntity<?> joinRideShare(@PathVariable int id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Not authenticated");
            return ResponseEntity.status(401).body(response);
        }

        boolean joined = rideShareService.joinRideShare(id, user);
        if (!joined) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to join ride share");
            return ResponseEntity.status(400).body(response);
        }

        Map<String, Boolean> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    /**
     * Leave a ride share
     */
    @PostMapping("/leave/{id}")
    @ResponseBody
    public ResponseEntity<?> leaveRideShare(@PathVariable int id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Not authenticated");
            return ResponseEntity.status(401).body(response);
        }

        boolean left = rideShareService.leaveRideShare(id, user);
        if (!left) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to leave ride share");
            return ResponseEntity.status(400).body(response);
        }

        Map<String, Boolean> response = new HashMap<>();
        response.put("success", true);
        return ResponseEntity.ok(response);
    }
}
