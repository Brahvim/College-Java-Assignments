package com.brahvim.college_assignments.model.loading;

public class ModelFormatException extends Exception {

    public ModelFormatException(final String p_filePath) {
        super(String.format(

                "Could not load model `%s` since it's file extension is not accepted.",
                p_filePath

        ));
    }

}
