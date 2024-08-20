package com.brahvim.college_assignments.model.loading;

import java.io.IOException;

public class ModelFormatException extends IOException {

    public ModelFormatException(final String p_filePath) {
        super(String.format(

                "Could not load model `%s` since it's file extension is not accepted.",
                p_filePath

        ));
    }

}
