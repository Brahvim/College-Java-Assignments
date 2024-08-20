package com.brahvim.college_assignments.vehicles;

import com.brahvim.college_assignments.math.matrices.ModelSpaceMatrix;

public abstract class Vehicle {

	protected int wheelCount;
	protected ModelSpaceMatrix transform;

	protected Vehicle() {
	}

	public int getWheelCount() {
		return this.wheelCount;
	}

	public ModelSpaceMatrix getTransform() {
		return this.transform;
	}

}
