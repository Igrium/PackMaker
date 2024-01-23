package com.igrium.packmaker.exporter;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.igrium.packmaker.common.InstallerConfig;
import com.igrium.packmaker.common.InstallerConfig.PackSource;
import com.igrium.packmaker.common.pack.ModpackProvider;
import com.igrium.packmaker.common.pack.ModrinthPackProvider;
import com.igrium.packmaker.mrpack.MrPack;

public class Exporter {
    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final URL templateUrl;

    public Exporter(URL templateUrl) {
        this.templateUrl = templateUrl;
    }

    public URL getTemplateUrl() {
        return templateUrl;
    }

    public void export(OutputStream out, ModpackProvider modpack, InstallerConfig config) throws Exception {

        File modpackFile = null;
        if (modpack instanceof ModrinthPackProvider modrinth) {
            config.setPackSource(PackSource.MODRINTH);
            config.setModrinthId(modrinth.getVersionId());
        } else {
            MrPack pack = modpack.downloadPack();
            config.setPackSource(PackSource.INTERNAL);
            modpackFile = pack.getFile();
            pack.close();
        }

        ZipOutputStream zip = new ZipOutputStream(out);
        try (ZipInputStream in = new ZipInputStream(new BufferedInputStream(templateUrl.openStream()))) {
            ZipEntry entry;
            while ((entry = in.getNextEntry()) != null) {
                zip.putNextEntry(new ZipEntry(entry));
                
                if (entry.getName().equals("config.json")) {
                    Writer writer = new OutputStreamWriter(zip);
                    writer.write(GSON.toJson(config));
                    writer.flush();
                } else {
                    in.transferTo(zip);
                }
                zip.closeEntry();
            }

            if (modpackFile != null) {
                try(BufferedInputStream fileIn = new BufferedInputStream(new FileInputStream(modpackFile))) {
                    zip.putNextEntry(new ZipEntry("internal.mrpack"));
                    fileIn.transferTo(zip);
                    zip.closeEntry();
                }
            }

        } finally {
            zip.finish();
        }
    }
}
