package com.brahvim.college_assignments.vehicles;

import com.brahvim.college_assignments.vectors.Vector3;

public abstract class Vehicle {

    protected int wheelCount;
    protected Vector3 position;

    protected Vehicle() {
    }

    public int getWheelCount() {
        return this.wheelCount;
    }

    public Vector3 getPosition() {
        return this.position;
    }

}
