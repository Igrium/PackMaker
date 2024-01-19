package com.igrium.packmaker.installer.util;

public interface ProgressHandle {
    public void log(String message);
    public void updateProgress(float progress);

    public default void updateProgress(int step, int numSteps) {
        updateProgress((float) step / (float) numSteps);
    }
}
