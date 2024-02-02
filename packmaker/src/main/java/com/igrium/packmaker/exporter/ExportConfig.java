package com.igrium.packmaker.exporter;

import java.util.Objects;

import com.igrium.packmaker.exporter.Exporter.ExportType;

public final class ExportConfig {
    private ExportType exportType = ExportType.JAR;

    public ExportType getExportType() {
        return exportType;
    }

    public ExportConfig setExportType(ExportType exportType) {
        this.exportType = Objects.requireNonNull(exportType);
        return this;
    }

    private boolean bundlePack;

    public boolean bundlePack() {
        return bundlePack;
    }

    public ExportConfig setBundlePack(boolean bundlePack) {
        this.bundlePack = bundlePack;
        return this;
    }
}
