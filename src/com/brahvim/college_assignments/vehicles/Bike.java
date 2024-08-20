package com.brahvim.college_assignments.vehicles;

import com.brahvim.college_assignments.math.matrices.ModelSpaceMatrix;

public class Bike extends Vehicle {

	public Bike() {
		this.wheelCount = 2;
		this.transform = new ModelSpaceMatrix();
	}

}
