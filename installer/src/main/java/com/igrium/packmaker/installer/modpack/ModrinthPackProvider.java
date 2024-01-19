package com.igrium.packmaker.installer.modpack;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipFile;

import com.igrium.packmaker.common.modrinth.ModrinthWebAPI;
import com.igrium.packmaker.common.modrinth.ModrinthWebTypes.ModrinthProjectVersion;
import com.igrium.packmaker.common.modrinth.ModrinthWebTypes.VersionFile;

import io.github.gaming32.mrpacklib.Mrpack;

public class ModrinthPackProvider implements ModpackProvider {

    private final ModrinthWebAPI modrinth;
    private final String versionId;

    public ModrinthPackProvider(ModrinthWebAPI modrinth, String versionId) {
        this.modrinth = modrinth;
        this.versionId = versionId;
    }

    @Override
    public Mrpack downloadPack() throws IOException, InterruptedException {
        ModrinthProjectVersion version = modrinth.getProjectVersion(versionId);
        if (version.files.isEmpty()) {
            throw new IOException("Modrinth version has no files.");
        }

        VersionFile file = version.files.get(0);
        
        HttpRequest req = HttpRequest.newBuilder(file.url).GET().build();
        Path localFile = Files.createTempFile("pack-", ".mrpack");
        localFile = modrinth.sendRequest(req, BodyHandlers.ofFileDownload(localFile));

        return new Mrpack(new ZipFile(localFile.toFile()));
    }
    
}
