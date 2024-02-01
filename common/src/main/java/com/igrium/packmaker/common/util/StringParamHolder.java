package com.igrium.packmaker.common.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StringParamHolder {
    private Map<String, String> params = new HashMap<>();

    public Map<String, String> getParams() {
        return Collections.unmodifiableMap(params);
    }

    public void setParam(String key, String value) {
        setParam(key, value, false);
    }

    public void setParam(String key, String value, boolean suppressUpdate) {
        if (value == null)
            params.remove(key);
        else
            params.put(key, value);
        if (!suppressUpdate)
            onUpdate();
    }

    public String getParam(String key) {
        return params.get(key);
    }

    public void setParams(Map<? extends String, ? extends String> params) {
        this.params.clear();
        this.params.putAll(params);
        onUpdate();
    }

    public void addParams(Map<? extends String, ? extends String> params) {
        this.params.putAll(params);
        onUpdate();
    }

    public void clearParams() {
        this.params.clear();
        onUpdate();
    }

    protected void onUpdate() {

    }

    /**
     * Format a string using the set parameters.
     * 
     * @param template String to format, where instances of <code>{key}</code> will
     *                 be replaced by corresponding value, if it exists.
     * @return Formatted string.
     */
    public String formatString(String template) {
        for (var entry : params.entrySet()) {
            template = template.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return template;
    }
}
