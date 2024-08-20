package com.brahvim.college_assignments;

import java.io.IOException;

import com.brahvim.college_assignments.model.loading.ObjModelLoader;
import com.brahvim.college_assignments.vehicles.Car;

public class App {

	public static void main(final String[] p_args) {
		final Car car = new Car();

		try (final ObjModelLoader loader = new ObjModelLoader("./res/models/car-model.obj")) {
			car.setModel(loader.load());
		} catch (final IOException e) {
			e.printStackTrace();
		}
	}

}
