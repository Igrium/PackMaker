package com.igrium.packmaker.installer.ui;

import java.util.Objects;

import com.igrium.packmaker.common.InstallerConfig.ScreenConfig;

public abstract class AbstractInstallerScreen {
    private String param;

    public String getParam() {
        return param;
    }
    
    public void setParam(String param) {
        this.param = param;
        updateTitle(getTitle());
        updateDescription(getDescription());
    }

    private String titleTemplate = "";

    public String getTitleTemplate() {
        return titleTemplate;
    }

    public String getTitle() {
        return titleTemplate.replaceAll("\\%s", param);
    }

    public void setTitleTemplate(String titleTemplate) {
        this.titleTemplate = Objects.requireNonNull(titleTemplate);
        updateTitle(getTitle());
    }

    private String descriptionTemplate = "";

    public String getDescriptionTemplate() {
        return descriptionTemplate;
    }

    public String getDescription() {
        return descriptionTemplate.replaceAll("\\%s", param);
    }

    public void setDescriptionTemplate(String descriptionTemplate) {
        this.descriptionTemplate = descriptionTemplate;
        updateDescription(getDescription());
    }

    protected abstract void updateTitle(String titleText);
    protected abstract void updateDescription(String descriptionText);

    public void setFromConfig(ScreenConfig config) {
        setTitleTemplate(config.getHeader());
        setDescriptionTemplate(config.getDescription());
    }
}
