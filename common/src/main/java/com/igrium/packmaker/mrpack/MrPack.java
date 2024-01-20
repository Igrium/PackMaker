package com.igrium.packmaker.mrpack;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

public class MrPack implements Closeable {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static enum EnvSupport {
        @SerializedName("required")
        REQUIRED,
        @SerializedName("optional")
        OPTIONAL,
        @SerializedName("unsupported")
        UNSUPPORTED
    }

    public static enum OverrideType {
        GLOBAL, SERVER, CLIENT
    }

    public static enum EnvSide {
        SERVER,
        CLIENT
    }

    public static MrPack open(File file) throws IOException {
        MrPack pack = new MrPack(new ZipFile(file));
        pack.load();
        return pack;
    }

    private final ZipFile zipFile;

    protected MrPack(ZipFile zipFile) {
        this.zipFile = zipFile;
    }

    public ZipFile getZipFile() {
        return zipFile;
    }

    private MrPackIndex index;

    protected void load() throws IOException {
        ZipEntry indexFile = zipFile.getEntry("modrinth.index.json");
        if (indexFile == null) {
            throw new IOException("Index file not found.");
        }

        try(BufferedReader in = new BufferedReader(new InputStreamReader(zipFile.getInputStream(indexFile)))) {
            index = GSON.fromJson(in, MrPackIndex.class);
        }

        discoverOverrides();
    }

    public MrPackIndex getIndex() {
        return index;
    }

    private Collection<MrPackOverride> globalOverrides;
    private Collection<MrPackOverride> clientOverrides;
    private Collection<MrPackOverride> serverOverrides;
    

    private void discoverOverrides() {
        List<MrPackOverride> globalOverrides = new LinkedList<>();
        List<MrPackOverride> clientOverrides = new LinkedList<>();
        List<MrPackOverride> serverOverrides = new LinkedList<>();

        var entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();

            if (entry.getName().startsWith("overrides/")) {
                globalOverrides.add(new MrPackOverride(
                        entry, entry.getName().replace("overrides/", ""), OverrideType.GLOBAL));

            } else if (entry.getName().startsWith("client-overrides/")) {
                clientOverrides.add(new MrPackOverride(
                    entry, entry.getName().replace("client-overrides/", ""), OverrideType.CLIENT));

            } else if (entry.getName().startsWith("server-overrides/")) {
                serverOverrides.add(new MrPackOverride(
                    entry, entry.getName().replace("server-overrides/", ""), OverrideType.SERVER));
            }
        }

        this.globalOverrides = List.copyOf(globalOverrides);
        this.clientOverrides = List.copyOf(clientOverrides);
        this.serverOverrides = List.copyOf(serverOverrides);
    }
    
    public Collection<MrPackOverride> getGlobalOverrides() {
        return globalOverrides;
    }

    public Collection<MrPackOverride> getClientOverrides() {
        return clientOverrides;
    }

    public Collection<MrPackOverride> getServerOverrides() {
        return serverOverrides;
    }

    /**
     * Get all of the overrides that will be used on a given side.
     * @param side Side to use.
     * @return Collection of all overrides.
     */
    public Collection<MrPackOverride> getAllOverrides(EnvSide side) {
        Collection<MrPackOverride> localOverrides = side == EnvSide.CLIENT ? getClientOverrides() : getServerOverrides();
        return Stream.concat(globalOverrides.stream(), localOverrides.stream()).toList();
    }

    public class MrPackOverride {
        private final ZipEntry entry;
        private final String path;
        private final OverrideType type;
        
        protected MrPackOverride(ZipEntry entry, String path, OverrideType type) {
            this.entry = entry;
            this.path = path;
            this.type = type;
        }

        public ZipEntry getEntry() {
            return entry;
        }


        public String getPath() {
            return path;
        };
        
        public Path extract(Path gameDir) throws IOException {
            Path targetPath = gameDir.resolve(path);
            try(InputStream stream = zipFile.getInputStream(entry)) {
                Files.copy(stream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }
            return targetPath;
        }

        public InputStream openStream() throws IOException {
            return zipFile.getInputStream(entry);
        }
        
        @Override
        public String toString() {
            return String.format("MrPackOverride[entry=%s,path=%s,type=%s]", entry, path, type);
        }
        
    }

    @Override
    public void close() throws IOException {
        zipFile.close();
    }
}
