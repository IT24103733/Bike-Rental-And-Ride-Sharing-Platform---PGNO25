package com.example.pgno25oopgroupprojectfinalrelease.service;

import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.ArrayList;

@Service
public class FileStorageService {

    // Base directory for all data files
    private static final String DATA_DIR = ".";

    // File names
    public static final String USERS_FILE = "users.data";
    public static final String BIKES_FILE = "bikes.data";
    public static final String BOOKINGS_FILE = "bookings.data";
    public static final String FEEDBACK_FILE = "feedback.data";
    public static final String MAINTENANCE_FILE = "maintenance.data";
    public static final String RIDESHARES_FILE = "rideshares.data";

    /**
     * Get the full path for a data file
     * @param fileName the name of the data file
     * @return the full path to the data file
     */
    public String getFilePath(String fileName) {
        return DATA_DIR + File.separator + fileName;
    }

    /**
     * Check if a data file exists
     * @param fileName the name of the data file
     * @return true if the file exists, false otherwise
     */
    public boolean fileExists(String fileName) {
        File file = new File(getFilePath(fileName));
        return file.exists();
    }

    /**
     * Create an empty data file if it doesn't exist
     * @param fileName the name of the data file
     * @return true if the file was created or already exists, false otherwise
     */
    public boolean createFileIfNotExists(String fileName) {
        if (fileExists(fileName)) {
            return true;
        }

        try {
            File file = new File(getFilePath(fileName));
            return file.createNewFile();
        } catch (Exception e) {
            System.err.println("Error creating file " + fileName + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Initialize all data files
     */
    public void initializeDataFiles() {
        // Create files if they don't exist
        createFileIfNotExists(USERS_FILE);
        createFileIfNotExists(BIKES_FILE);
        createFileIfNotExists(BOOKINGS_FILE);
        createFileIfNotExists(FEEDBACK_FILE);
        createFileIfNotExists(MAINTENANCE_FILE);
        createFileIfNotExists(RIDESHARES_FILE);

        // Initialize files with empty lists if they are empty
        initializeFileWithEmptyList(USERS_FILE);
        initializeFileWithEmptyList(BIKES_FILE);
        initializeFileWithEmptyList(BOOKINGS_FILE);
        initializeFileWithEmptyList(FEEDBACK_FILE);
        initializeFileWithEmptyList(MAINTENANCE_FILE);
        initializeFileWithEmptyList(RIDESHARES_FILE);
    }

    /**
     * Initialize a file with an empty ArrayList if it's empty
     * @param fileName the name of the data file
     */
    private void initializeFileWithEmptyList(String fileName) {
        File file = new File(getFilePath(fileName));
        if (file.exists() && file.length() == 0) {
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
                oos.writeObject(new ArrayList<>());
                System.out.println("Initialized file with empty list: " + fileName);
            } catch (Exception e) {
                System.err.println("Error initializing file " + fileName + ": " + e.getMessage());
            }
        }
    }
}
