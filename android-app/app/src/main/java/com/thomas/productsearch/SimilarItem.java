package com.thomas.productsearch;
/**
Class that stores the information of a similar item
*/
public class SimilarItem {
    private String imageURL;
    private String title;
    private String shipping;
    private String daysLeft;
    private String price;
    private String storeURL;

    public SimilarItem(String imageURL, String title, String shipping, String daysLeft, String price, String storeURL) {
        this.imageURL = imageURL;
        this.title = title;
        this.shipping = shipping;
        this.daysLeft = daysLeft;
        this.price = price;
        this.storeURL = storeURL;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShipping() {
        return shipping;
    }

    public void setShipping(String shipping) {
        this.shipping = shipping;
    }

    public String getDaysLeft() {
        return daysLeft;
    }

    public void setDaysLeft(String daysLeft) {
        this.daysLeft = daysLeft;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStoreURL() {
        return storeURL;
    }

    public void setStoreURL(String storeURL) {
        this.storeURL = storeURL;
    }
}
