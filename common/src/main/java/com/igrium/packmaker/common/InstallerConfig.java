package com.igrium.packmaker.common;

import java.util.Objects;

public class InstallerConfig {
    public static enum PackSource {
        MODRINTH,
        INTERNAL
    }

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

}
