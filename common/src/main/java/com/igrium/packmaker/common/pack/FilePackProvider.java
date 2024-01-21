package com.igrium.packmaker.common.pack;

import java.io.File;
import java.io.IOException;

import com.igrium.packmaker.mrpack.MrPack;

public class FilePackProvider implements ModpackProvider {

    private final File file;

    public FilePackProvider(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    @Override
    public MrPack downloadPack() throws IOException, InterruptedException {
        return MrPack.open(file);
    }
    
}
