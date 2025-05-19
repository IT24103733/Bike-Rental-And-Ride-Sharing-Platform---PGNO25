package com.example.oopprojectbeta.repository;

import com.example.oopprojectbeta.model.User;
import com.example.oopprojectbeta.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UserRepo {

    @Autowired
    private FileStorageService fileStorageService;

    private List<User> users;

    @PostConstruct
    public void init() {
        this.users = loadUsers();

        // If no users exist yet, create an admin account
        if (users.isEmpty()) {
            createAdminUser();
        }
    }

    public List<User> findAll() {
        return new ArrayList<>(users);
    }

    public User findById(int id) {
        for (User user : users) {
            if (user.getId() == id) {
                return user;
            }
        }
        return null;
    }

    public User findByUsernameAndPassword(String username, String password) {
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public User save(User user) {
        // Generate a new ID if it's a new user
        if (user.getId() == 0) {
            user.setId(generateNextId());
            users.add(user);
        } else {
            // If user already exists, update it instead of adding a duplicate
            update(user);
            return user;
        }

        saveUsers();
        return user;
    }

    public void update(User updatedUser) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == updatedUser.getId()) {
                users.set(i, updatedUser);
                saveUsers();
                break;
            }
        }
    }

    /**
     * Delete a user by ID
     * @param id the ID of the user to delete
     * @return true if the user was deleted, false otherwise
     */
    public boolean delete(int id) {
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId() == id) {
                users.remove(i);
                saveUsers();
                return true;
            }
        }
        return false;
    }

    private int generateNextId() {
        int maxId = 0;
        for (User user : users) {
            if (user.getId() > maxId) {
                maxId = user.getId();
            }
        }
        return maxId + 1;
    }

    private List<User> loadUsers() {
        List<User> loadedUsers = new ArrayList<>();

        String filePath = fileStorageService.getFilePath(FileStorageService.USERS_FILE);
        File file = new File(filePath);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                loadedUsers = (List<User>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                System.err.println("Error loading users: " + e.getMessage());
            }
        }

        return loadedUsers;
    }

    private void saveUsers() {
        String filePath = fileStorageService.getFilePath(FileStorageService.USERS_FILE);
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.err.println("Error saving users: " + e.getMessage());
        }
    }

    private void createAdminUser() {
        User admin = new User(1, "admin", "admin123", true, "Head Office");
        User admin2 = new User(2, "admin2", "admin123", true, "Head Office");
        users.add(admin);
        saveUsers();
    }
}
