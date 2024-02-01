package com.igrium.packmaker.util;

import com.igrium.packmaker.mrpack.MrPack;
import com.igrium.packmaker.mrpack.MrPack.EnvSide;

public final class PackUtils {
    
    /**
     * Use a variety of techniques to detect if a pack game dir should be separate from the launcher dir.
     * @param pack MrPack file.
     * @return If the pack is "intrusive"
     */
    public static boolean isIntrusive(MrPack pack) {
        // TODO: Is there a better way we can do this?
        return pack.getAllFilepaths(EnvSide.CLIENT).anyMatch(path -> {
            return path.contains("resourcepacks")
            || path.contains("saves");
        });
    }
}
