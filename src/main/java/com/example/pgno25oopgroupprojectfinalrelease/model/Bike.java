package com.example.oopprojectbeta.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bike implements Serializable {
    private int id;
    private String name;
    private String type; // mountain, road, electric, etc.
    private double pricePerHour;
    private boolean available;

    // Used for sorting
    public String getType() {
        return type;
    }
}
