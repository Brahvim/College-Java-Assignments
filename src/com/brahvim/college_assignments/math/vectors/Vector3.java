package com.brahvim.college_assignments.math.vectors;

public class Vector3 extends Vector2 {

	protected float z;

	public Vector3(final Vector2 p_vector) {
		this(p_vector.x, p_vector.y);
	}

	public Vector3(final Vector3 p_vector) {
		this(p_vector.x, p_vector.y, p_vector.z);
	}

	public Vector3(final float p_x, final float p_y) {
		super.x = p_x;
		super.y = p_y;
	}

	public Vector3(final float p_x, final float p_y, final float p_z) {
		this.z = p_z;

		super.x = p_x;
		super.y = p_y;
	}

	@Override
	public void add(final Vector p_toAdd) {
		if (!(p_toAdd instanceof final Vector3 toAdd))
			return;
		super.x += toAdd.x;
		super.y += toAdd.y;
		this.z += toAdd.z;

	}

	@Override
	public void sub(final Vector p_toSub) {
		if (!(p_toSub instanceof final Vector3 toSub))
			return;
		super.x += toSub.x;
		super.y += toSub.y;
		this.z += toSub.z;

	}

	public float getZ() {
		return this.z;
	}

}
