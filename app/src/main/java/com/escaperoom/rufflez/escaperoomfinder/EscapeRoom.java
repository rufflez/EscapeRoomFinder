package com.escaperoom.rufflez.escaperoomfinder;

/**
 * Created by aruffolo on 7/13/16.
 */
public class EscapeRoom {

    private String name;
    private String address;
    private String url;
    private String photoUrl;

    public EscapeRoom() {

    }

    public EscapeRoom(String name, String address, String url, String photoUrl) {
        this.name = name;
        this.address = address;
        this.url = url;
        this.photoUrl = photoUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }
}
