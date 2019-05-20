package com.thomas.productsearch;
/**
Class to store a photo from Google API call
*/
public class GooglePhoto {
    private String img;

    public GooglePhoto(String img) {
        this.img = img;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
