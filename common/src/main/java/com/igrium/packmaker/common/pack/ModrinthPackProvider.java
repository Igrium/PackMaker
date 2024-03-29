package com.igrium.packmaker.common.pack;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;

import com.igrium.packmaker.common.modrinth.ModrinthWebAPI;
import com.igrium.packmaker.common.modrinth.ModrinthWebTypes.ModrinthProjectVersion;
import com.igrium.packmaker.common.modrinth.ModrinthWebTypes.VersionFile;
import com.igrium.packmaker.mrpack.MrPack;

public class ModrinthPackProvider implements ModpackProvider {

    private final ModrinthWebAPI modrinth;
    private final String versionId;

    public ModrinthPackProvider(ModrinthWebAPI modrinth, String versionId) {
        this.modrinth = modrinth;
        this.versionId = versionId;
    }

    public String getVersionId() {
        return versionId;
    }

    @Override
    public MrPack downloadPack() throws IOException, InterruptedException {
        ModrinthProjectVersion version = modrinth.getProjectVersion(versionId);
        if (version.files.isEmpty()) {
            throw new IOException("Modrinth version has no files.");
        }

        VersionFile file = version.files.get(0);
        System.out.println("Downloading mrpack from " + file.url);
        HttpRequest req = HttpRequest.newBuilder(file.url).GET().build();
        Path localFile = Files.createTempFile("pack-", ".mrpack");
        localFile = modrinth.sendRequest(req, BodyHandlers.ofFile(localFile));

        return MrPack.open(localFile.toFile());
        // return new Mrpack(new ZipFile(localFile.toFile()));
    }
    
}
