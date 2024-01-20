package com.igrium.packmaker.installer.util;

import java.io.File;

public class OSUtil {

    /**
     * Attempt to get the default .minecraft folder on a user's system.
     * @return Launcher path. <code>null</code> if we're not on Windows or the path was not found.
     */
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
}
