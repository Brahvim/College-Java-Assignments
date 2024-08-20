package com.brahvim.college_assignments.vehicles;

import java.util.Vector;

public abstract class Vehicle {

    private int wheelCount;
    private Vector position;

    private Vehicle() {
    }

    public int getWheelCount() {
        return this.wheelCount;
    }

}
