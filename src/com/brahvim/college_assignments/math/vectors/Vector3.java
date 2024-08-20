package com.brahvim.college_assignments.math.vectors;

public class Vector3 extends Vector2 {

    protected float z;

    public float getZ() {
        return this.z;
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

}
