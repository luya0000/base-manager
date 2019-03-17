package com.manage.source.bean;

import java.io.Serializable;

/**
 * Created by luya on 2019/3/16.
 */
public class MVDirBean implements Serializable {

    private String id;
    private String parent;
    private String text;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
