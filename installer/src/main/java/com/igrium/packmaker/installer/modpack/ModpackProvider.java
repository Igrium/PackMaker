package com.igrium.packmaker.installer.modpack;

import java.io.IOException;

import io.github.gaming32.mrpacklib.Mrpack;

/**
 * A resource where a mrpack may be downloaded.
 */
public interface ModpackProvider {
    public Mrpack downloadPack() throws IOException, InterruptedException;
}
