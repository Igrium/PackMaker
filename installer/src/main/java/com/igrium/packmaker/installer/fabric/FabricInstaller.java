package com.igrium.packmaker.installer.fabric;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.igrium.packmaker.common.fabric.FabricWebApi;

public class FabricInstaller {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static JsonObject downloadJson(Path gameDir, String gameVersion, String loaderVersion, FabricWebApi api) throws IOException {
        String profileName = String.format("%s-%s-%s", "fabric-loader", loaderVersion, gameVersion);
        // Path versionFile = gameDir.resolve(profileName).resolve(profileName + ".json");
        Path versionFile = gameDir.resolve("versions/%s/%s.json".formatted(profileName, profileName));

        if (Files.isRegularFile(versionFile)) {
            try(BufferedReader reader = Files.newBufferedReader(versionFile)) {
                return GSON.fromJson(reader, JsonObject.class);
            }
        }
        
        String json;
        try {
            json = api.getVersionJson(gameVersion, loaderVersion);
        } catch (InterruptedException e) {
            return null;
        }

        if (json == null) return null;

        Files.createDirectories(versionFile.getParent());
        try (BufferedWriter writer = Files.newBufferedWriter(versionFile)) {
            writer.write(json);
        };

        return GSON.fromJson(json, JsonObject.class);

    }

    public static void installProfile(Path launcherDir, Path gameDir, String name, String icon, JsonObject versionJson) throws IOException {
        JsonObject profile = new JsonObject();
        String time = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date());

        profile.addProperty("name", name);
        profile.addProperty("type", "custom");
        profile.addProperty("created", time);
        profile.addProperty("lastUsed", time);
        profile.addProperty("icon", icon);
        profile.addProperty("javaArgs", "-Xmx4G -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:G1NewSizePercent=20 -XX:G1ReservePercent=20 -XX:MaxGCPauseMillis=50 -XX:G1HeapRegionSize=32M");
        profile.addProperty("lastVersionId", versionJson.get("id").getAsString());
        if (!launcherDir.equals(gameDir)) {
            profile.addProperty("gameDir", gameDir.toAbsolutePath().toString());
        }

        Path profileFile = launcherDir.resolve("launcher_profiles.json");

        JsonObject profileJson;
        try(BufferedReader reader = Files.newBufferedReader(profileFile)) {
            profileJson = GSON.fromJson(reader, JsonObject.class);
        }

        profileJson.getAsJsonObject("profiles").add(name, profile);

        try (JsonWriter writer = new JsonWriter(Files.newBufferedWriter(profileFile))) {
            GSON.toJson(profileJson, writer);
        }

        System.out.println("Installed modded profile.");
    }

}
