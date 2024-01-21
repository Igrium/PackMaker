package com.igrium.packmaker.common.pack;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.igrium.packmaker.mrpack.MrPack;


public class URLPackProvider implements ModpackProvider {
    private final URL url;

    public URLPackProvider(URL url) {
        this.url = url;
    }

    public URL getUrl() {
        return url;
    }

    @Override
    public MrPack downloadPack() throws IOException {
        System.out.println("Loading pack from " + url);
        File packFile = File.createTempFile("pack-", ".mrpack");

        try(InputStream in = url.openStream(); BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(packFile))) {
            in.transferTo(out);
        }

        return MrPack.open(packFile);
    }
}
