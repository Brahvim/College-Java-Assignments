package com.brahvim.college_assignments.math.vectors;

public class Vector2 extends Vector {

    protected float x, y;

    public float getX() {
        return this.x;
    }

    public float getY() {
        return this.y;
    }

    @Override
    public void add(final Vector p_toAdd) {
        if (!(p_toAdd instanceof final Vector2 toAdd))
            return;
        this.x += toAdd.x;
        this.y += toAdd.y;
    }

    @Override
    public void sub(final Vector p_toSub) {
        if (!(p_toSub instanceof final Vector2 toSub))
            return;
        this.x -= toSub.x;
        this.y -= toSub.y;
    }

}
