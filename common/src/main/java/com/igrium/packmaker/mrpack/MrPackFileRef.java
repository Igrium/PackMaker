package com.igrium.packmaker.mrpack;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.igrium.packmaker.mrpack.MrPack.EnvSide;
import com.igrium.packmaker.mrpack.MrPack.EnvSupport;

public final class MrPackFileRef {
    private static transient HttpClient httpClient = HttpClient.newBuilder().followRedirects(Redirect.ALWAYS).build();

    private String path = "";

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = Objects.requireNonNull(path);
    }

    public static record Hashes(String sha1, String sha512) {};

    private Hashes hashes = new Hashes("", "");

    public Hashes getHashes() {
        return hashes;
    }

    public void setHashes(Hashes hashes) {
        this.hashes = hashes;
    }

    public static record EnvCompat(EnvSupport client, EnvSupport server) {
        public boolean isSupported(EnvSide side) {
            if (side == EnvSide.SERVER) return server != EnvSupport.UNSUPPORTED;
            else if (side == EnvSide.CLIENT) return client != EnvSupport.UNSUPPORTED;
            else throw new IllegalArgumentException("Unknown EnvSide: " + side);
        }

        public boolean isRequired(EnvSide side) {
            if (side == EnvSide.SERVER) return server == EnvSupport.REQUIRED;
            else if (side == EnvSide.CLIENT) return client == EnvSupport.REQUIRED;
            else throw new IllegalArgumentException("Unknown EnvSide: " + side);
        }
    };

    private EnvCompat env = new EnvCompat(EnvSupport.OPTIONAL, EnvSupport.OPTIONAL);

    public EnvCompat getEnv() {
        return env;
    }

    public void setEnv(EnvCompat envSupport) {
        this.env = envSupport;
    }

    private List<URI> downloads = new ArrayList<>();

    public List<URI> getDownloads() {
        return downloads;
    }

    private long fileSize;

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * Download this file, blocking until the download is complete.
     * @param gameDir Root minecraft directory.
     * @return Path of the downloaded file.
     * @throws IOException If an IO exception occurs while downloading the file.
     * @throws InterruptedException If the thread is interrupted during the download.
     */
    public Path download(Path gameDir) throws IOException, InterruptedException {
        Path targetFile = gameDir.resolve(path).normalize();
        if (!targetFile.startsWith(gameDir)) {
            throw new IOException("This file references a path outside the game directory.");
        }

        Files.createDirectories(targetFile.getParent());
        
        for (URI uri : downloads) {
            try {
                if (tryDownload(uri, targetFile)) return targetFile;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        throw new IOException("No working download URLs were found.");
    }

    private boolean tryDownload(URI uri, Path dest) throws IOException, InterruptedException {
        // TODO: hash verification
        var req = HttpRequest.newBuilder(uri).GET().build();
        var res = httpClient.send(req, BodyHandlers.ofFile(dest));
        if (res.statusCode() >= 400) {
            System.out.println("%s returned status code %d. Skipping.".formatted(uri, res.statusCode()));
            return false;
        } else {
            return true;
        }
    }
}
