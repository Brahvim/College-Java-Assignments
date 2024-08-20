package com.brahvim.college_assignments.math.matrices.rendering;

import com.brahvim.college_assignments.math.matrices.Matrix4;
import com.brahvim.college_assignments.math.vectors.Vector3;

public class WorldSpaceTransformMatrix extends Matrix4 {

    public Vector3 getScaling() {
        return new Vector3(

                super.data[0],
                super.data[5],
                super.data[10]

        );
    }

    public Vector3 getTranslation() {
        return new Vector3(

                super.data[3],
                super.data[7],
                super.data[11]

        );
    }

}
