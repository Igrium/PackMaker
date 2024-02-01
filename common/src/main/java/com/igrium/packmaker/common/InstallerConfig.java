package com.igrium.packmaker.common;

import java.util.Objects;

public final class InstallerConfig {
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

    /**
     * If not <code>null</code>, this warning will be displayed if the user attempts
     * to use the launcher directory as the game directory.
     */
    private String twinFolderWarning = null;

    /**
     * If not <code>null</code>, this warning will be displayed if the user attempts
     * to use the launcher directory as the game directory.
     */
    public String getTwinFolderWarning() {
        return twinFolderWarning;
    }

    /**
     * If not <code>null</code>, this warning will be displayed if the user attempts
     * to use the launcher directory as the game directory.
     */
    public void setTwinFolderWarning(String twinGameFolderWarning) {
        this.twinFolderWarning = twinGameFolderWarning;
    }

    public final static class ScreenConfig {
        // For gson
        public ScreenConfig() {}

        private String header = "";

        public String getHeader() {
            return header;
        }

        public void setHeader(String header) {
            this.header = header;
        }

        private String description = "";

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public void copyFrom(ScreenConfig other) {
            header = other.getHeader();
            description = other.getDescription();
        }
    }

    private ScreenConfig welcomeScreen = new ScreenConfig();

    public ScreenConfig getWelcomeScreen() {
        return welcomeScreen;
    }

    public void setWelcomeScreen(ScreenConfig welcomeScreen) {
        this.welcomeScreen = Objects.requireNonNull(welcomeScreen);
    }

    private ScreenConfig gameDirScreen = new ScreenConfig();

    public ScreenConfig getGameDirScreen() {
        return gameDirScreen;
    }

    public void setGameDirScreen(ScreenConfig gameDirScreen) {
        this.gameDirScreen = Objects.requireNonNull(gameDirScreen);
    }

    private ScreenConfig launcherDirScreen = new ScreenConfig();

    public ScreenConfig getLauncherDirScreen() {
        return launcherDirScreen;
    }

    public void setLauncherDirScreen(ScreenConfig launcherDirScreen) {
        this.launcherDirScreen = Objects.requireNonNull(launcherDirScreen);
    }

    private ScreenConfig completeScreen = new ScreenConfig();

    public ScreenConfig getCompleteScreen() {
        return completeScreen;
    }
    
    public void setCompleteScreen(ScreenConfig completeScreen) {
        this.completeScreen = Objects.requireNonNull(completeScreen);
    }

    public void copyFrom(InstallerConfig other) {
        setPackSource(other.getPackSource());
        setModrinthId(other.modrinthId);
        setModpackName(other.modpackName);
        welcomeScreen.copyFrom(other.welcomeScreen);
        launcherDirScreen.copyFrom(other.launcherDirScreen);
        gameDirScreen.copyFrom(other.gameDirScreen);
        completeScreen.copyFrom(other.completeScreen);
    }
}

