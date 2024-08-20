package com.brahvim.college_assignments.vehicles;

import com.brahvim.college_assignments.math.matrices.ModelSpaceMatrix;

public class Car extends Vehicle {

	public Car() {
		super.wheelCount = 4;
		super.transform = new ModelSpaceMatrix();
	}

}
