package com.thomas.productsearch;

import java.util.Comparator;

/**
Comparator used to sort similar items
*/
public class ProductComparator implements Comparator<SimilarItem> {

    private String sortBy;
    private int sortOrderMultipler;

    public ProductComparator(String sortBy, String sortOrder) {
        this.sortBy = sortBy;
        if (sortOrder.equals("Ascending")) {
            this.sortOrderMultipler = 1;
        } else {
            this.sortOrderMultipler = -1;
        }
    }

    @Override
    public int compare(SimilarItem item1, SimilarItem item2) {
        switch (sortBy) {
            case "Name":
                return sortOrderMultipler * (item1.getTitle().compareTo(item2.getTitle()));
            case "Price":
                double price1 = Double.parseDouble(item1.getPrice());
                double price2 = Double.parseDouble(item2.getPrice());
                return sortOrderMultipler * (price1 > price2 ? 1 : -1);
            case "Days":
                int days1 = Integer.parseInt(item1.getDaysLeft());
                int days2 = Integer.parseInt(item2.getDaysLeft());
                return sortOrderMultipler * (days1 - days2);
        }
        return 0;
    }
}
