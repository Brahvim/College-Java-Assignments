package com.brahvim.college_assignments.math.matrices;

public abstract class Matrix {

	protected final float[] data;
	protected final int numValues;

	protected Matrix(final int p_numValues) {
		this.numValues = p_numValues;
		this.data = new float[this.numValues];
	}

}
