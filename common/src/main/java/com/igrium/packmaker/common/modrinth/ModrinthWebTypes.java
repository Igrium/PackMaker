package com.igrium.packmaker.common.modrinth;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class ModrinthWebTypes {
    public static enum ModrinthProjectType {
        @SerializedName("mod")
        MOD,
        @SerializedName("modpack")
        MODPACK,
        @SerializedName("resourcepack")
        RESOURCEPACK,
        @SerializedName("shader")
        SHADER
    }

    public static enum ModrinthVersionType {
        @SerializedName("release")
        RELEASE,
        @SerializedName("beta")
        BETA,
        @SerializedName("alpha")
        ALPHA
    }

    public static class ModrinthProject {
        public String slug;
        public String title;
        public String description;

        @SerializedName("project_type")
        public ModrinthProjectType projectType;
    }

    public static class ModrinthProjectVersion {
        public String name = "";
        
        @SerializedName("version_number")
        public String versionNumber = "0";

        @SerializedName("game_versions")
        public List<String> gameVersions = new ArrayList<>();

        @SerializedName("version_type")
        public ModrinthVersionType versionType;
        
        public List<String> loaders = new ArrayList<>();

        public String id;

        @SerializedName("project_id")
        public String projectId;

        @SerializedName("author_id")
        public String authorId;

        public List<VersionFile> files = new ArrayList<>();
    }

    public static record VersionHashes(String sha512, String sha1) {};

    public static class VersionFile {
        public VersionHashes hashes;
        public URI url;
        public String filename;
        public boolean primary;
        public long size;

        @SerializedName("file_type")
        public String fileType;
    }
}
