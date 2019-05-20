package com.thomas.productsearch;
/**
Class to store a product information from search result
*/
public class Product {
    private String image;
    private String title;
    private String shortTitle;
    private String zipCode;
    private String shipping;
    private String condition;
    private String price;
    private int cartId;
    private String itemId;
    private ProductDetails productDetails;

    public Product(String image, String title, String shortTitle, String zipCode, String shipping,
                   String condition, String price, int cartId, String itemId, ProductDetails productDetails) {
        this.image = image;
        this.title = title;
        this.shortTitle = shortTitle;
        this.zipCode = zipCode;
        this.shipping = shipping;
        this.condition = condition;
        this.price = price;
        this.cartId = cartId;
        this.itemId = itemId;
        this.productDetails = productDetails;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getShortTitle() {
        return shortTitle;
    }

    public void setShortTitle(String shortTitle) {
        this.shortTitle = shortTitle;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getShipping() {
        return shipping;
    }

    public void setShipping(String shipping) {
        this.shipping = shipping;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(Integer cartId) {
        this.cartId = cartId;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public ProductDetails getProductDetails() {
        return productDetails;
    }

    public void setProductDetails(ProductDetails productDetails) {
        this.productDetails = productDetails;
    }
}
