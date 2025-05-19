package com.example.pgno25oopgroupprojectfinalrelease.controller;

import com.example.pgno25oopgroupprojectfinalrelease.model.Feedback;
import com.example.pgno25oopgroupprojectfinalrelease.model.User;
import com.example.pgno25oopgroupprojectfinalrelease.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;


    @Controller
    @RequestMapping("/feedback")
    public class FeedbackController {

        @Autowired
        private FeedbackService feedbackService;

        @GetMapping
        public String getAllFeedback(Model model, HttpSession session) {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return "redirect:/login";
            }

            model.addAttribute("feedbacks", feedbackService.getAllFeedback());
            model.addAttribute("isAdmin", user.isAdmin());
            model.addAttribute("currentUserId", user.getId());
            model.addAttribute("newFeedback", new Feedback());

            return "feedback";
        }

        @PostMapping("/add")
        public String addFeedback(@ModelAttribute Feedback feedback, HttpSession session) {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return "redirect:/login";
            }

            feedbackService.addFeedback(feedback, user);
            return "redirect:/feedback";
        }

        @PostMapping("/update")
        @ResponseBody
        public ResponseEntity<?> updateFeedback(@RequestBody Feedback feedback, HttpSession session) {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Not authenticated");
                return ResponseEntity.status(401).body(response);
            }

            Feedback updatedFeedback = feedbackService.updateFeedback(feedback, user);
            if (updatedFeedback == null) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Not authorized or feedback not found");
                return ResponseEntity.status(403).body(response);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("feedback", updatedFeedback);
            return ResponseEntity.ok(response);
        }

        @DeleteMapping("/{id}")
        @ResponseBody
        public ResponseEntity<?> deleteFeedback(@PathVariable int id, HttpSession session) {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Not authenticated");
                return ResponseEntity.status(401).body(response);
            }

            boolean deleted = feedbackService.deleteFeedback(id, user);
            if (!deleted) {
                Map<String, String> response = new HashMap<>();
                response.put("error", "Not authorized or feedback not found");
                return ResponseEntity.status(403).body(response);
            }

            Map<String, Boolean> response = new HashMap<>();
            response.put("success", true);
            return ResponseEntity.ok(response);
        }

        @GetMapping("/user")
        public String getUserFeedback(Model model, HttpSession session) {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return "redirect:/login";
            }

            model.addAttribute("feedbacks", feedbackService.getFeedbackByUserId(user.getId()));
            model.addAttribute("isAdmin", user.isAdmin());
            model.addAttribute("currentUserId", user.getId());
            model.addAttribute("newFeedback", new Feedback());

            return "userFeedback";
        }
    }

