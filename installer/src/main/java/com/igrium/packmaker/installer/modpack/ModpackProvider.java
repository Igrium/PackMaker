package com.igrium.packmaker.installer.modpack;

import java.io.IOException;

import com.igrium.packmaker.mrpack.MrPack;

/**
 * A resource where a mrpack may be downloaded.
 */
public interface ModpackProvider {
    public MrPack downloadPack() throws IOException, InterruptedException;
}
