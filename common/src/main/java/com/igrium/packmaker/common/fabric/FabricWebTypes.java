package com.igrium.packmaker.common.fabric;

public class FabricWebTypes {
    public static record LoaderVersion(
            String separator,
            int build,
            String maven,
            String version,
            boolean stable) {

    }

    // public static class LoaderVersion {
    //     public String separator = ".";
    //     public int build;
    //     public String maven = "";
    //     public String version = "";
    //     public boolean stable;
        
        
    // }
}
