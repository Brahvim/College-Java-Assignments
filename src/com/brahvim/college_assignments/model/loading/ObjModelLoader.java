package com.brahvim.college_assignments.model.loading;

import java.io.File;
import java.io.IOException;

import com.brahvim.college_assignments.model.Model;

public class ObjModelLoader extends ModelLoader {

    private static final String EXTENSION = "obj";

    public ObjModelLoader(final String p_filePath) throws IOException, ModelFormatException {
        this(new File(p_filePath));
    }

    public ObjModelLoader(final File p_file) throws ModelFormatException, IOException {
        super(p_file);

        final String path = p_file.getPath();
        if (!path.endsWith(ObjModelLoader.EXTENSION)) {
            super.close();
            throw new ModelFormatException(path);
        }
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
