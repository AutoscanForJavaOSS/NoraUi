/**
 * NoraUi is licensed under the license GNU AFFERO GENERAL PUBLIC LICENSE
 * 
 * @author Nicolas HALLOUIN
 * @author Stéphane GRILLON
 */
package com.github.noraui.cli.model;

import java.util.ArrayList;
import java.util.List;

public class NoraUiApplicationFile {

    private String name;
    private String url;
    private List<NoraUiModel> models;
    private boolean status;

    public NoraUiApplicationFile() {
        this.name = "";
        this.url = "";
        this.models = new ArrayList<>();
        this.status = true;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<NoraUiModel> getModels() {
        return models;
    }

    public void setModels(List<NoraUiModel> models) {
        this.models = models;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

}
