package com.brahvim.college_assignments.model.loading;

import java.io.File;
import java.io.IOException;

import com.brahvim.college_assignments.model.Model;

public class FbxModelLoader extends ModelLoader {

    public static final String EXTENSION = "fbx";

    public FbxModelLoader(final String p_filePath) throws IOException, ModelFormatException {
        this(new File(p_filePath));
    }

    public FbxModelLoader(final File p_file) throws ModelFormatException, IOException {
        super(p_file);

        final String path = p_file.getPath();
        if (!path.endsWith(FbxModelLoader.EXTENSION)) {
            super.close();
            throw new ModelFormatException(path);
        }
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
