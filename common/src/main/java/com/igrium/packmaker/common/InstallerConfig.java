package com.igrium.packmaker.common;

import java.util.Objects;

public class InstallerConfig {
    public static enum PackSource {
        MODRINTH,
        INTERNAL
    }

    // For Gson
    public InstallerConfig() {}

    private PackSource packSource = PackSource.MODRINTH;
    
    public PackSource getPackSource() {
        return packSource;
    }

    public void setPackSource(PackSource packSource) {
        this.packSource = Objects.requireNonNull(packSource);
    }

    private String modrinthId;

    public String getModrinthId() {
        return modrinthId;
    }

    public void setModrinthId(String modrinthId) {
        this.modrinthId = modrinthId;
    }
    
    private String modpackName = "[modpack name]";

    public String getModpackName() {
        return modpackName;
    }

    public void setModpackName(String modpackName) {
        this.modpackName = Objects.requireNonNull(modpackName);
    }

}
