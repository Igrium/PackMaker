package com.igrium.packmaker.installer.modpack;

import java.io.IOException;
import java.net.URL;

import com.igrium.packmaker.common.InstallerConfig;
import com.igrium.packmaker.common.InstallerConfig.PackSource;
import com.igrium.packmaker.common.modrinth.ModrinthWebAPI;
import com.igrium.packmaker.mrpack.MrPack;

/**
 * A resource where a mrpack may be downloaded.
 */
public interface ModpackProvider {
    public MrPack downloadPack() throws IOException, InterruptedException;

    public static ModpackProvider fromConfig(InstallerConfig config) {
        if (config.getPackSource() == PackSource.MODRINTH) {
            String id = config.getModrinthId();
            if (id == null) {
                throw new IllegalStateException("Modrinth ID must be set when packSource is set to MODRINTH.");
            }
            
            return new ModrinthPackProvider(ModrinthWebAPI.getGlobalInstance(), id);
        } else if (config.getPackSource() == PackSource.INTERNAL) {
            URL url = ModpackProvider.class.getResource("/internal.mrpack");
            if (url == null) {
                throw new IllegalStateException("packSource was set to INTERNAL, but no 'internal.mrpack' was bundled!");
            }
            return new URLPackProvider(url);
        } else {
            throw new IllegalArgumentException("Unknown pack source: " + config.getPackSource());
        }
    }
}
