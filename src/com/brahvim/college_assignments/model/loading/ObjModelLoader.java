package com.brahvim.college_assignments.model.loading;

import java.io.File;
import java.io.FileNotFoundException;

import com.brahvim.college_assignments.model.Model;

public class ObjModelLoader extends ModelLoader {

    private static final String EXTENSION = "obj";

    public ObjModelLoader(final String p_filePath) throws FileNotFoundException {
        this(new File(p_filePath));
    }

    public ObjModelLoader(final File p_file) throws FileNotFoundException {
        super(p_file);
    }

    @Override
    public Model load() {
        throw new UnsupportedOperationException("Unimplemented method 'load'");
    }

    @Override
    public String getFileExtension() {
        return ObjModelLoader.EXTENSION;
    }

}
