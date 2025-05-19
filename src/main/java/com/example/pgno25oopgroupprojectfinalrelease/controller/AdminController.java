package com.example.pgno25oopgroupprojectfinalrelease.controller;

import com.example.pgno25oopgroupprojectfinalrelease.model.User;
import com.example.pgno25oopgroupprojectfinalrelease.service.AnalyticsService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AnalyticsService analyticsService;

    /**
     * Show the admin analytics dashboard
     */
    @GetMapping("/analytics")
    public String showAnalyticsDashboard(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            return "redirect:/bikes";
        }

        // Get analytics data - this is an incomplete function please complete---------------------------------
        int totalUsers = analyticsService.getTotalUsers();
        double totalRevenue = analyticsService.getTotalRevenue();
        int totalBikes = analyticsService.getTotalBikes();
        int availableBikes = analyticsService.getAvailableBikes();
        int totalBookings = analyticsService.getTotalBookings();

        // Add data to model
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalBikes", totalBikes);
        model.addAttribute("availableBikes", availableBikes);
        model.addAttribute("totalBookings", totalBookings);

        return "adminAnalytics";
    }
}
