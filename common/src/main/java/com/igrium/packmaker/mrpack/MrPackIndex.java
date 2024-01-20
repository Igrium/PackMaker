package com.igrium.packmaker.mrpack;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.google.gson.annotations.SerializedName;

public final class MrPackIndex {

    public static final class MrPackDependencies {
        private String minecraft;

        public String getMinecraft() {
            return minecraft;
        }

        public void setMinecraft(String minecraft) {
            this.minecraft = minecraft;
        }

        private String forge;

        public String getForge() {
            return forge;
        }

        public void setForge(String forge) {
            this.forge = forge;
        }

        private String neoforge;

        public String getNeoforge() {
            return neoforge;
        }

        public void setNeoforge(String neoforge) {
            this.neoforge = neoforge;
        }

        @SerializedName("fabric-loader")
        private String fabricLoader;

        public String getFabricLoader() {
            return fabricLoader;
        }

        public void setFabricLoader(String fabricLoader) {
            this.fabricLoader = fabricLoader;
        }

        @SerializedName("quilt-loader")
        private String quiltLoader;

        public String getQuiltLoader() {
            return quiltLoader;
        }

        public void setQuiltLoader(String quiltLoader) {
            this.quiltLoader = quiltLoader;
        }
    }

    private int formatVersion = 1;

    public int getFormatVersion() {
        return formatVersion;
    }

    private String versionId = "";

    public String getVersionId() {
        return versionId;
    }

    public void setVersionId(String versionId) {
        this.versionId = Objects.requireNonNull(versionId);
    }

    private String summary;

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }
    
    private List<MrPackFileRef> files = new ArrayList<>();

    public List<MrPackFileRef> getFiles() {
        return files;
    }

    private MrPackDependencies dependencies = new MrPackDependencies();

    public MrPackDependencies getDependencies() {
        return dependencies;
    }
}
