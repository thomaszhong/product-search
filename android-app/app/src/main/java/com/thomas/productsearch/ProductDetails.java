package com.thomas.productsearch;

public class ProductDetails {
    private String storeName;
    private String storeURL;
    private String feedbackScore;
    private double popularity;
    private String handlingTime;

    public ProductDetails(String feedbackScore, double popularity) {
        this.feedbackScore = feedbackScore;
        this.popularity = popularity;
    }

    public ProductDetails(String storeName, String storeURL, String feedbackScore, double popularity, String handlingTime) {
        this.storeName = storeName;
        this.storeURL = storeURL;
        this.feedbackScore = feedbackScore;
        this.popularity = popularity;
        this.handlingTime = handlingTime;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreURL() {
        return storeURL;
    }

    public void setStoreURL(String storeURL) {
        this.storeURL = storeURL;
    }

    public String getFeedbackScore() {
        return feedbackScore;
    }

    public void setFeedbackScore(String feedbackScore) {
        this.feedbackScore = feedbackScore;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public String getHandlingTime() {
        return handlingTime;
    }

    public void setHandlingTime(String handlingTime) {
        this.handlingTime = handlingTime;
    }
}
