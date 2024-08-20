package com.brahvim.college_assignments.vehicles;

import com.brahvim.college_assignments.math.matrices.rendering.WorldSpaceTransformMatrix;
import com.brahvim.college_assignments.model.Model;

public abstract class Vehicle {

	protected final int wheelCount;

	protected Model model;
	protected WorldSpaceTransformMatrix transform;

	protected Vehicle(final int p_wheelCount) {
		this.wheelCount = p_wheelCount;
		this.transform = new WorldSpaceTransformMatrix();
	}

	public void setModel(final Model p_model) {
		this.model = p_model;
	}

	public Model getModel() {
		return this.model;
	}

	public int getWheelCount() {
		return this.wheelCount;
	}

	public WorldSpaceTransformMatrix getTransform() {
		return this.transform;
	}

}
