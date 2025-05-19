package com.example.pgno25oopgroupprojectfinalrelease.service;

import com.example.pgno25oopgroupprojectfinalrelease.model.Feedback;
import com.example.pgno25oopgroupprojectfinalrelease.model.User;
import com.example.pgno25oopgroupprojectfinalrelease.repository.FeedbackRepo;
import com.example.pgno25oopgroupprojectfinalrelease.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

    @Service
    public class FeedbackService {

        @Autowired
        private FeedbackRepo feedbackRepo;

        @Autowired
        private UserRepo userRepo;

        public List<Feedback> getAllFeedback() {
            return feedbackRepo.findAll();
        }

        public Feedback getFeedbackById(int id) {
            return feedbackRepo.findById(id);
        }

        public List<Feedback> getFeedbackByUserId(int userId) {
            return feedbackRepo.findByUserId(userId);
        }

        public Feedback addFeedback(Feedback feedback, User currentUser) {
            // Set the user information
            feedback.setUserId(currentUser.getId());
            feedback.setUsername(currentUser.getUsername());

            // Set timestamps
            LocalDateTime now = LocalDateTime.now();
            feedback.setCreatedAt(now);
            feedback.setUpdatedAt(now);

            return feedbackRepo.save(feedback);
        }

        public Feedback updateFeedback(Feedback feedback, User currentUser) {
            Feedback existingFeedback = feedbackRepo.findById(feedback.getId());

            // Check if feedback exists
            if (existingFeedback == null) {
                return null;
            }

            // Check if user is authorized to update this feedback
            if (!canModifyFeedback(existingFeedback, currentUser)) {
                return null;
            }

            // Update only the content and updatedAt timestamp
            existingFeedback.setContent(feedback.getContent());
            existingFeedback.setUpdatedAt(LocalDateTime.now());

            return feedbackRepo.save(existingFeedback);
        }

        public boolean deleteFeedback(int feedbackId, User currentUser) {
            Feedback feedback = feedbackRepo.findById(feedbackId);

            // Check if feedback exists
            if (feedback == null) {
                return false;
            }

            // Check if user is authorized to delete this feedback
            if (!canModifyFeedback(feedback, currentUser)) {
                return false;
            }

            return feedbackRepo.delete(feedbackId);
        }

        // Helper method to check if a user can modify (edit/delete) feedback
        private boolean canModifyFeedback(Feedback feedback, User user) {
            // Admins can modify any feedback
            if (user.isAdmin()) {
                return true;
            }

            // Regular users can only modify their own feedback
            return feedback.getUserId() == user.getId();
        }
    }

