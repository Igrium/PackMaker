package com.igrium.packmaker.common.util;

import java.util.Locale;

public enum OperatingSystem {
    WINDOWS,
    MACOS,
    LINUX;

    public static final OperatingSystem CURRENT = getCurrent();

    private static OperatingSystem getCurrent() {
        String osName = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);

        if (osName.contains("win")) {
            return WINDOWS;
        } else if (osName.contains("mac")) {
            return MACOS;
        } else {
            return LINUX;
        }
    }
}
