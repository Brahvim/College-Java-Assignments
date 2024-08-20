package com.brahvim.college_assignments.model.loading;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.brahvim.college_assignments.model.Model;

public abstract class ModelLoader implements Closeable {

    protected final FileReader reader;

    protected ModelLoader(final File p_file) throws FileNotFoundException {
        this.reader = new FileReader(p_file);
    }

    public abstract Model load();

    public abstract String getFileExtension();

    @Override
    public void close() throws IOException {
        this.reader.close();
    }

}
