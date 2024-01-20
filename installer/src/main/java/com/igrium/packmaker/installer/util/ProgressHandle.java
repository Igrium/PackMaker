package com.igrium.packmaker.installer.util;

public interface ProgressHandle {
    public void log(String message);

    public void updateProgress(int step, int numSteps);
}
