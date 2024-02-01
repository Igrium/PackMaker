package com.igrium.packmaker.installer.ui;

import java.util.Objects;

import com.igrium.packmaker.common.InstallerConfig.ScreenConfig;
import com.igrium.packmaker.common.util.StringParamHolder;
import com.igrium.packmaker.installer.InstallerUI;

public abstract class AbstractInstallerScreen extends StringParamHolder {

    private String titleTemplate = "";

    public String getTitleTemplate() {
        return titleTemplate;
    }

    public String getTitle() {
        return formatString(titleTemplate);
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
        return formatString(descriptionTemplate);
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

    @Override
    protected void onUpdate() {
        updateTitle(getTitle());
        updateDescription(getDescription());
    }

    public void updateContext(InstallerUI.Context context) {
        setParam("modpack", context.getModpackName(), true);
        setParam("profile", context.getProfileName(), true);
        onUpdate();
    }

}
