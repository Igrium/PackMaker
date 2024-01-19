package com.igrium.packmaker.installer.modpack;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.zip.ZipFile;

import io.github.gaming32.mrpacklib.Mrpack;

public class URLPackProvider implements ModpackProvider {
    private final URL url;

    public URLPackProvider(URL url) {
        this.url = url;
    }

    public URL getUrl() {
        return url;
    }

    @Override
    public Mrpack downloadPack() throws IOException {
        File packFile = File.createTempFile("pack-", ".mrpack");

        try(InputStream in = url.openStream(); BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(packFile))) {
            in.transferTo(out);
        }

        return new Mrpack(new ZipFile(packFile));
    }
}
