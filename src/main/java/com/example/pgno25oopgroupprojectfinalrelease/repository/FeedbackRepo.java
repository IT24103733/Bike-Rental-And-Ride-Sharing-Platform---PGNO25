package com.example.pgno25oopgroupprojectfinalrelease.repository;

import com.example.pgno25oopgroupprojectfinalrelease.model.Feedback;
import com.example.pgno25oopgroupprojectfinalrelease.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

    @Repository
    public class FeedbackRepo {

        @Autowired
        private FileStorageService fileStorageService;

        private List<Feedback> feedbacks;

        @PostConstruct
        public void init() {
            this.feedbacks = loadFeedbacks();
        }

        public List<Feedback> findAll() {
            return new ArrayList<>(feedbacks);
        }

        public Feedback findById(int id) {
            for (Feedback feedback : feedbacks) {
                if (feedback.getId() == id) {
                    return feedback;
                }
            }
            return null;
        }

        public List<Feedback> findByUserId(int userId) {
            List<Feedback> userFeedbacks = new ArrayList<>();
            for (Feedback feedback : feedbacks) {
                if (feedback.getUserId() == userId) {
                    userFeedbacks.add(feedback);
                }
            }
            return userFeedbacks;
        }

        public Feedback save(Feedback feedback) {
            // Generate a new ID if it's a new feedback
            if (feedback.getId() == 0) {
                feedback.setId(generateNextId());
                feedbacks.add(feedback);
            } else {
                // If feedback already exists, update it instead of adding a duplicate
                update(feedback);
                return feedback;
            }

            saveFeedbacks();
            return feedback;
        }

        public void update(Feedback updatedFeedback) {
            for (int i = 0; i < feedbacks.size(); i++) {
                if (feedbacks.get(i).getId() == updatedFeedback.getId()) {
                    feedbacks.set(i, updatedFeedback);
                    saveFeedbacks();
                    break;
                }
            }
        }

        public boolean delete(int id) {
            for (int i = 0; i < feedbacks.size(); i++) {
                if (feedbacks.get(i).getId() == id) {
                    feedbacks.remove(i);
                    saveFeedbacks();
                    return true;
                }
            }
            return false;
        }

        private int generateNextId() {
            int maxId = 0;
            for (Feedback feedback : feedbacks) {
                if (feedback.getId() > maxId) {
                    maxId = feedback.getId();
                }
            }
            return maxId + 1;
        }

        private List<Feedback> loadFeedbacks() {
            List<Feedback> loadedFeedbacks = new ArrayList<>();

            String filePath = fileStorageService.getFilePath(FileStorageService.FEEDBACK_FILE);
            File file = new File(filePath);
            if (file.exists()) {
                try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                    loadedFeedbacks = (List<Feedback>) ois.readObject();
                } catch (IOException | ClassNotFoundException e) {
                    System.err.println("Error loading feedbacks: " + e.getMessage());
                }
            }

            return loadedFeedbacks;
        }

        private void saveFeedbacks() {
            String filePath = fileStorageService.getFilePath(FileStorageService.FEEDBACK_FILE);
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
                oos.writeObject(feedbacks);
            } catch (IOException e) {
                System.err.println("Error saving feedbacks: " + e.getMessage());
            }
        }
    }

