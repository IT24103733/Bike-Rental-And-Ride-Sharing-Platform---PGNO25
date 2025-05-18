package com.example.oopprojectbeta.controller;

import com.example.oopprojectbeta.model.Bike;
import com.example.oopprojectbeta.model.User;
import com.example.oopprojectbeta.service.BikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/bikes")
public class BikeController {

    @Autowired
    private BikeService bikeService;

    @GetMapping
    public String getAllBikes(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        if (user.isAdmin()) {

            model.addAttribute("bikes", bikeService.getAllBikes());
            model.addAttribute("isAdmin", true);
            return "adminBikes";
        } else {

            model.addAttribute("bikes", bikeService.getAllAvailableBikes());
            model.addAttribute("isAdmin", false);
            return "bikes";
        }
    }

    @GetMapping("/add")
    public String showAddBikeForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            return "redirect:/bikes";
        }

        model.addAttribute("bike", new Bike());
        return "addBike";
    }

    @PostMapping("/add")
    public String addBike(@ModelAttribute Bike bike, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            return "redirect:/bikes";
        }

        bike.setAvailable(true);
        bikeService.addBike(bike);

        return "redirect:/bikes";
    }

    @GetMapping("/{id}")
    public String getBikeDetails(@PathVariable int id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }

        Bike bike = bikeService.getBikeById(id);
        if (bike == null) {
            return "redirect:/bikes";
        }

        model.addAttribute("bike", bike);
        return "bikeDetails";
    }

    @GetMapping("/edit/{id}")
    public String showEditBikeForm(@PathVariable int id, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            return "redirect:/bikes";
        }

        Bike bike = bikeService.getBikeById(id);
        if (bike == null) {
            return "redirect:/bikes";
        }

        model.addAttribute("bike", bike);
        return "editBike";
    }

    @PostMapping("/edit/{id}")
    public String editBike(@PathVariable int id, @ModelAttribute Bike bike, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            return "redirect:/bikes";
        }


        bike.setId(id);
        bikeService.updateBike(bike);

        return "redirect:/bikes";
    }

    @GetMapping("/delete/{id}")
    public String deleteBike(@PathVariable int id, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !user.isAdmin()) {
            return "redirect:/bikes";
        }

        bikeService.deleteBike(id);
        return "redirect:/bikes";
    }
}
