package com.example.oopprojectbeta.service;

import com.example.oopprojectbeta.model.Bike;
import com.example.oopprojectbeta.repository.BikeRepo;
import com.example.oopprojectbeta.utl.QuickSort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class BikeService {

    @Autowired
    private BikeRepo bikeRepo;

    public List<Bike> getAllBikes() {
        return bikeRepo.findAll();
    }

    public List<Bike> getAllAvailableBikes() {
        List<Bike> allBikes = bikeRepo.findAll();
        List<Bike> availableBikes = new ArrayList<>();

        for (Bike bike : allBikes) {
            if (bike.isAvailable()) {
                availableBikes.add(bike);
            }
        }

        // Use the QuickSort utility to sort bikes by type
        QuickSort.sort(availableBikes, Comparator.comparing(Bike::getType));
        return availableBikes;
    }

    public Bike getBikeById(int id) {
        return bikeRepo.findById(id);
    }

    public void addBike(Bike bike) {
        bikeRepo.save(bike);
    }

    public void updateBike(Bike bike) {
        bikeRepo.update(bike);
    }

    public boolean deleteBike(int id) {
        return bikeRepo.delete(id);
    }
}
