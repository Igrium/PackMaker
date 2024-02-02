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
import com.igrium.packmaker.App.LoadedPack;
import com.igrium.packmaker.common.InstallerConfig;
import com.igrium.packmaker.common.InstallerConfig.PackSource;
import com.igrium.packmaker.common.pack.ModrinthPackProvider;
import com.igrium.packmaker.mrpack.MrPack;

public class Exporter {
    public static enum ExportType {
        JAR(".jar"),
        EXE(".exe");

        private final String extension;

        private ExportType(String extension) {
            this.extension = extension;
        }

        public String getExtension() {
            return extension;
        }
    }

    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private final URL templateUrl;
    private final URL bootstrapUrl;

    public Exporter(URL templateUrl, URL bootstrapUrl) {
        this.templateUrl = templateUrl;
        this.bootstrapUrl = bootstrapUrl;
    }

    public URL getTemplateUrl() {
        return templateUrl;
    }

    public URL getBootstrapUrl() {
        return bootstrapUrl;
    }

    public void export(OutputStream out, LoadedPack modpack, ExportConfig exportConfig, InstallerConfig installerConfig) throws Exception {

        if (exportConfig.getExportType() == ExportType.EXE && bootstrapUrl != null) {
            try(BufferedInputStream bootstrap = new BufferedInputStream(bootstrapUrl.openStream())) {
                bootstrap.transferTo(out);
            }
        } 

        File modpackFile = null;
        if (modpack.provider() instanceof ModrinthPackProvider modrinth && !exportConfig.bundlePack()) {
            installerConfig.setPackSource(PackSource.MODRINTH);
            installerConfig.setModrinthId(modrinth.getVersionId());
        } else {
            MrPack pack = modpack.pack();
            installerConfig.setPackSource(PackSource.INTERNAL);
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
                    writer.write(GSON.toJson(installerConfig));
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
