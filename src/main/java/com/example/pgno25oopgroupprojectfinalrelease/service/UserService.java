package com.example.oopprojectbeta.service;

import com.example.oopprojectbeta.model.User;
import com.example.oopprojectbeta.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepo userRepo;

    public User login(String username, String password) {
        return userRepo.findByUsernameAndPassword(username, password);
    }

    public User register(User user) {
        // Ensure no admin creation through registration
        user.setAdmin(false);
        return userRepo.save(user);
    }

    public User getUserById(int id) {
        return userRepo.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepo.findAll();
    }

    public User updateUser(User user) {
        userRepo.update(user);
        return userRepo.findById(user.getId());
    }

    /**
     * Create a new user (admin only)
     * @param user the user to create
     * @return the created user
     */
    public User createUser(User user) {
        return userRepo.save(user);
    }

    /**
     * Delete a user by ID
     * @param id the ID of the user to delete
     * @return true if the user was deleted, false otherwise
     */
    public boolean deleteUser(int id) {
        return userRepo.delete(id);
    }

    /**
     * Check if a user can modify another user
     * @param currentUser the user performing the action
     * @param targetUserId the ID of the user being modified
     * @return true if the current user can modify the target user, false otherwise
     */
    public boolean canModifyUser(User currentUser, int targetUserId) {
        // Admins can modify any user
        if (currentUser.isAdmin()) {
            return true;
        }

        // Regular users can only modify themselves
        return currentUser.getId() == targetUserId;
    }
}
