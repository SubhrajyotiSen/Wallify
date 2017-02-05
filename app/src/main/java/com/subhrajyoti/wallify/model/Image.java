package com.subhrajyoti.wallify.model;

public class Image {

    private String path;
    private int id;

    public Image(int id, String path) {
        this.path = path;
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
