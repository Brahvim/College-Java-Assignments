package com.brahvim.college_assignments.model.loading;

import java.io.File;
import java.io.FileNotFoundException;

import com.brahvim.college_assignments.model.Model;

public class FbxModelLoader extends ModelLoader {

    public static final String EXTENSION = "fbx";

    public FbxModelLoader(final String p_filePath) throws FileNotFoundException {
        this(new File(p_filePath));
    }

    public FbxModelLoader(final File p_file) throws FileNotFoundException {
        // if (p_file.exists()) // Don't need this; the `FileReader` will do it.
        super(p_file);
    }

    @Override
    public Model load() {
        return new Model(new float[0]);
    }

    @Override
    public String getFileExtension() {
        return FbxModelLoader.EXTENSION;
    }

}
