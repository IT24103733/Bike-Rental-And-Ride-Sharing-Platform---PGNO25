package com.example.pgno25oopgroupprojectfinalrelease.utl;

import com.example.pgno25oopgroupprojectfinalrelease.model.Booking;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;

@Component
public class BikeQueue {

    private Queue<Booking> rideRequests;

    public BikeQueue() {
        this.rideRequests = new LinkedList<>();
    }

    // Add a ride request to the queue
    public void enqueue(Booking booking) {
        if (booking.isRideRequest()) {
            rideRequests.add(booking);
            System.out.println("Ride request added to queue: Bike ID " + booking.getBikeId() 
                    + " to " + booking.getAddress());
        }
    }

    // Get the next ride request from the queue
    public Booking dequeue() {
        return rideRequests.poll();
    }

    // Check if queue is empty
    public boolean isEmpty() {
        return rideRequests.isEmpty();
    }

    // Get the current size of the queue
    public int size() {
        return rideRequests.size();
    }

    // Peek at the next request without removing it
    public Booking peek() {
        return rideRequests.peek();
    }
}
