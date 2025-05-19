package com.example.pgno25oopgroupprojectfinalrelease.service;

import com.example.pgno25oopgroupprojectfinalrelease.model.RideShare;
import com.example.pgno25oopgroupprojectfinalrelease.model.User;
import com.example.pgno25oopgroupprojectfinalrelease.repository.RideShareRepo;
import com.example.pgno25oopgroupprojectfinalrelease.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RideShareService {

    @Autowired
    private RideShareRepo rideShareRepo;

    @Autowired
    private UserRepo userRepo;

    /**
     * Get all ride shares
     * @return list of all ride shares
     */
    public List<RideShare> getAllRideShares() {
        return rideShareRepo.findAll();
    }

    /**
     * Get a ride share by ID
     * @param id the ID of the ride share
     * @return the ride share, or null if not found
     */
    public RideShare getRideShareById(int id) {
        return rideShareRepo.findById(id);
    }

    /**
     * Get ride shares by user ID
     * @param userId the ID of the user
     * @return list of ride shares created by the user
     */
    public List<RideShare> getRideSharesByUserId(int userId) {
        return rideShareRepo.findByUserId(userId);
    }

    /**
     * Get all ride offers
     * @return list of all ride offers
     */
    public List<RideShare> getAllOffers() {
        return rideShareRepo.findOffers();
    }

    /**
     * Get all ride requests
     * @return list of all ride requests
     */
    public List<RideShare> getAllRequests() {
        return rideShareRepo.findRequests();
    }

    /**
     * Create a new ride share
     * @param rideShare the ride share to create
     * @param currentUser the user creating the ride share
     * @return the created ride share
     */
    public RideShare createRideShare(RideShare rideShare, User currentUser) {
        // Set the user information
        rideShare.setUserId(currentUser.getId());
        rideShare.setUsername(currentUser.getUsername());
        
        // Set timestamps
        LocalDateTime now = LocalDateTime.now();
        rideShare.setCreatedAt(now);
        rideShare.setUpdatedAt(now);
        
        // If it's an offer, initialize capacity and spots available
        if (rideShare.isOffer() && rideShare.getCapacity() <= 0) {
            rideShare.setCapacity(1); // Default to 1 if not specified
        }
        
        if (rideShare.isOffer()) {
            rideShare.setSpotsAvailable(rideShare.getCapacity());
        }
        
        return rideShareRepo.save(rideShare);
    }

    /**
     * Update a ride share
     * @param rideShare the updated ride share
     * @param currentUser the user updating the ride share
     * @return the updated ride share, or null if the user is not authorized or the ride share doesn't exist
     */
    public RideShare updateRideShare(RideShare rideShare, User currentUser) {
        RideShare existingRideShare = rideShareRepo.findById(rideShare.getId());
        
        // Check if ride share exists
        if (existingRideShare == null) {
            return null;
        }
        
        // Check if user is authorized to update this ride share
        if (!canModifyRideShare(existingRideShare, currentUser)) {
            return null;
        }
        
        // Preserve user information
        rideShare.setUserId(existingRideShare.getUserId());
        rideShare.setUsername(existingRideShare.getUsername());
        
        // Preserve creation timestamp
        rideShare.setCreatedAt(existingRideShare.getCreatedAt());
        
        // Update the timestamp
        rideShare.setUpdatedAt(LocalDateTime.now());
        
        // If changing from request to offer, initialize capacity and spots available
        if (!existingRideShare.isOffer() && rideShare.isOffer()) {
            if (rideShare.getCapacity() <= 0) {
                rideShare.setCapacity(1); // Default to 1 if not specified
            }
            rideShare.setSpotsAvailable(rideShare.getCapacity());
        }
        
        rideShareRepo.update(rideShare);
        return rideShare;
    }

    /**
     * Delete a ride share
     * @param rideShareId the ID of the ride share to delete
     * @param currentUser the user deleting the ride share
     * @return true if the ride share was deleted, false otherwise
     */
    public boolean deleteRideShare(int rideShareId, User currentUser) {
        RideShare rideShare = rideShareRepo.findById(rideShareId);
        
        // Check if ride share exists
        if (rideShare == null) {
            return false;
        }
        
        // Check if user is authorized to delete this ride share
        if (!canModifyRideShare(rideShare, currentUser)) {
            return false;
        }
        
        return rideShareRepo.delete(rideShareId);
    }

    /**
     * Check if a user can modify a ride share
     * @param rideShare the ride share to check
     * @param user the user to check
     * @return true if the user can modify the ride share, false otherwise
     */
    public boolean canModifyRideShare(RideShare rideShare, User user) {
        // Admins can modify any ride share
        if (user.isAdmin()) {
            return true;
        }
        
        // Regular users can only modify their own ride shares
        return rideShare.getUserId() == user.getId();
    }

    /**
     * Join a ride share offer
     * @param rideShareId the ID of the ride share to join
     * @param currentUser the user joining the ride share
     * @return true if the user successfully joined the ride share, false otherwise
     */
    public boolean joinRideShare(int rideShareId, User currentUser) {
        RideShare rideShare = rideShareRepo.findById(rideShareId);
        
        // Check if ride share exists and is an offer
        if (rideShare == null || !rideShare.isOffer()) {
            return false;
        }
        
        // Check if there are spots available
        if (rideShare.getSpotsAvailable() <= 0) {
            return false;
        }
        
        // Check if the user is not the creator of the ride share
        if (rideShare.getUserId() == currentUser.getId()) {
            return false;
        }
        
        // Decrease available spots
        rideShare.setSpotsAvailable(rideShare.getSpotsAvailable() - 1);
        rideShareRepo.update(rideShare);
        
        return true;
    }

    /**
     * Leave a ride share offer
     * @param rideShareId the ID of the ride share to leave
     * @param currentUser the user leaving the ride share
     * @return true if the user successfully left the ride share, false otherwise
     */
    public boolean leaveRideShare(int rideShareId, User currentUser) {
        RideShare rideShare = rideShareRepo.findById(rideShareId);
        
        // Check if ride share exists and is an offer
        if (rideShare == null || !rideShare.isOffer()) {
            return false;
        }
        
        // Check if the user is not the creator of the ride share
        if (rideShare.getUserId() == currentUser.getId()) {
            return false;
        }
        
        // Increase available spots
        rideShare.setSpotsAvailable(rideShare.getSpotsAvailable() + 1);
        rideShareRepo.update(rideShare);
        
        return true;
    }
}