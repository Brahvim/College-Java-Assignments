package com.brahvim.college_assignments.vehicles;

import com.brahvim.college_assignments.math.matrices.rendering.WorldSpaceMatrix;

public abstract class Vehicle {

	protected final int wheelCount;
	protected WorldSpaceMatrix transform;

	protected Vehicle(final int p_wheelCount) {
		this.wheelCount = p_wheelCount;
		this.transform = new WorldSpaceMatrix();
	}

	public int getWheelCount() {
		return this.wheelCount;
	}

	public WorldSpaceMatrix getTransform() {
		return this.transform;
	}

}
