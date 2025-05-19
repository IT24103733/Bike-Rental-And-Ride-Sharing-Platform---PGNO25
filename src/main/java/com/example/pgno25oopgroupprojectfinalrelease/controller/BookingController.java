package com.example.pgno25oopgroupprojectfinalrelease.controller;



import com.example.oopprojectbeta.model.Bike;
import com.example.oopprojectbeta.model.Booking;
import com.example.oopprojectbeta.model.User;
import com.example.oopprojectbeta.service.BikeService;
import com.example.oopprojectbeta.service.BookingService;
import com.example.oopprojectbeta.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @Autowired
    private BikeService bikeService;

    @Autowired
    private UserService userService;

    @GetMapping
    public String getUserBookings(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        List<Booking> userBookings = bookingService.getUserBookings(user.getId());
        model.addAttribute("bookings", userBookings);

        return "bookings";
    }

    @GetMapping("/rent/{bikeId}")
    public String showRentForm(@PathVariable int bikeId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Prevent admins from booking bikes
        if (user.isAdmin()) {
            return "redirect:/bikes";
        }

        Bike bike = bikeService.getBikeById(bikeId);
        if (bike == null || !bike.isAvailable()) {
            return "redirect:/bikes";
        }

        model.addAttribute("bike", bike);
        return "rentBike";
    }

    @PostMapping("/rent/{bikeId}")
    public String rentBike(@PathVariable int bikeId, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Booking booking = bookingService.rentBike(user.getId(), bikeId);
        if (booking == null) {
            return "redirect:/bikes?error=true";
        }

        return "redirect:/bookings?success=true";
    }

    @GetMapping("/request/{bikeId}")
    public String showRequestForm(@PathVariable int bikeId, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Prevent admins from requesting rides
        if (user.isAdmin()) {
            return "redirect:/bikes";
        }

        Bike bike = bikeService.getBikeById(bikeId);
        if (bike == null || !bike.isAvailable()) {
            return "redirect:/bikes";
        }

        model.addAttribute("bike", bike);
        model.addAttribute("address", user.getAddress());
        return "requestRide";
    }

    @PostMapping("/request/{bikeId}")
    public String requestRide(@PathVariable int bikeId,
                              @RequestParam String address,
                              HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Booking booking = bookingService.requestRide(user.getId(), bikeId, address);
        if (booking == null) {
            return "redirect:/bikes?error=true";
        }

        return "redirect:/bookings?success=true";
    }

    @GetMapping("/admin")
    public String adminBookings(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            return "redirect:/bikes";
        }

        List<Booking> allBookings = bookingService.getAllBookings();
        model.addAttribute("bookings", allBookings);

        // Get user info for each booking
        Map<Integer, User> userMap = new HashMap<>();
        for (Booking booking : allBookings) {
            int userId = booking.getUserId();
            if (!userMap.containsKey(userId)) {
                User bookingUser = userService.getUserById(userId);
                userMap.put(userId, bookingUser);
            }
        }
        model.addAttribute("userMap", userMap);

        // Get available bikes for reassignment
        model.addAttribute("availableBikes", bikeService.getAllAvailableBikes());

        return "adminBookings";
    }

    @PostMapping("/admin/reassign")
    public String reassignBike(@RequestParam int bookingId,
                               @RequestParam int newBikeId,
                               HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            return "redirect:/bikes";
        }

        Booking updatedBooking = bookingService.reassignBike(bookingId, newBikeId);
        if (updatedBooking == null) {
            return "redirect:/bookings/admin?error=true";
        }

        return "redirect:/bookings/admin?success=true";
    }

    @GetMapping("/admin/delete/{id}")
    public String adminDeleteBooking(@PathVariable int id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            return "redirect:/bikes";
        }

        boolean deleted = bookingService.cancelBooking(id);
        if (!deleted) {
            return "redirect:/bookings/admin?error=true";
        }

        return "redirect:/bookings/admin?success=true";
    }

    @GetMapping("/update/{id}")
    public String showUpdateBookingForm(@PathVariable int id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Booking booking = bookingService.getBookingById(id);
        if (booking == null) {
            return "redirect:/bookings?error=true";
        }

        // Check if user owns this booking
        if (booking.getUserId() != user.getId() && !user.isAdmin()) {
            return "redirect:/bookings?error=unauthorized";
        }

        model.addAttribute("booking", booking);

        // If it's a ride request, get the bike for display
        if (booking.isRideRequest()) {
            Bike bike = bikeService.getBikeById(booking.getBikeId());
            model.addAttribute("bike", bike);
        }

        return "updateBooking";
    }

    @PostMapping("/update")
    public String updateBooking(@ModelAttribute Booking booking, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        // Check if user owns this booking
        Booking existingBooking = bookingService.getBookingById(booking.getId());
        if (existingBooking == null) {
            return "redirect:/bookings?error=true";
        }

        if (existingBooking.getUserId() != user.getId() && !user.isAdmin()) {
            return "redirect:/bookings?error=unauthorized";
        }

        Booking updatedBooking = bookingService.updateBooking(booking);
        if (updatedBooking == null) {
            return "redirect:/bookings?error=true";
        }

        return "redirect:/bookings?success=true";
    }

    @GetMapping("/cancel/{id}")
    public String cancelBooking(@PathVariable int id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Booking booking = bookingService.getBookingById(id);
        if (booking == null) {
            return "redirect:/bookings?error=true";
        }

        // Check if user owns this booking
        if (booking.getUserId() != user.getId() && !user.isAdmin()) {
            return "redirect:/bookings?error=unauthorized";
        }

        boolean canceled = bookingService.cancelBooking(id);
        if (!canceled) {
            return "redirect:/bookings?error=true";
        }

        return "redirect:/bookings?success=true";
    }
}
