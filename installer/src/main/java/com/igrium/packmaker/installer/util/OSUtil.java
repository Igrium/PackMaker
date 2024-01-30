package com.igrium.packmaker.installer.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.igrium.packmaker.common.util.OperatingSystem;

public class OSUtil {

    /**
     * Attempt to get the default .minecraft folder on a user's system.
     * @return Launcher path. <code>null</code> if we're not on Windows or the path was not found.
     */
    @Deprecated
    public static File getDefaultLauncherPath() {
        String os = System.getProperty("os.name");
        if (os.toLowerCase().contains("windows")) {
            String appdata = System.getenv("APPDATA");
            if (appdata == null) return null;
            
            File gameDir = new File(appdata + "/.minecraft");
            if (!gameDir.exists()) return null;

            return gameDir;
        }

        return null;
    }

    public static Path findDefaultLauncherPath() {
        Path dir;

        if (OperatingSystem.CURRENT == OperatingSystem.WINDOWS && System.getenv("APPDATA") != null) {
            dir = Paths.get(System.getProperty("APPDATA")).resolve(".minecraft");
        } else {
            Path home = Paths.get(System.getProperty("user.home", "."));

            if (OperatingSystem.CURRENT == OperatingSystem.MACOS) {
                dir = home.resolve("Library").resolve("Application Support").resolve("minecraft");
            } else {
                dir = home.resolve(".minecraft");
                if (OperatingSystem.CURRENT == OperatingSystem.LINUX && !Files.isDirectory(dir)) {
                    // Flatpack
                    Path flatpack = home.resolve(".var").resolve("app").resolve("com.mojang.Minecraft").resolve(".minecraft");
                    if (Files.exists(flatpack)) {
                        dir = flatpack;
                    }
                }
            }
        }

        return dir;
    }
}
