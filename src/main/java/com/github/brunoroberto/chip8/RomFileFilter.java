package com.github.brunoroberto.chip8;

import javax.swing.filechooser.FileFilter;
import java.io.File;

public class RomFileFilter extends FileFilter {

    private static final String EXTENSION = ".ch8";

    @Override
    public boolean accept(File file) {
        return file.isDirectory() || file.getName().endsWith(EXTENSION);
    }

    @Override
    public String getDescription() {
        return "Chip8 ROM (.ch8)";
    }
}
