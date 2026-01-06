package com;

public class MenuItem {

    private int id;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private int calories;
    private boolean isAvailable;
    private boolean isSpicy;
    private boolean isVegetarian;
    private boolean proteinRequired;
    private String category;



    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getCalories() {
        return calories;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public boolean isSpicy() {
        return isSpicy;
    }

    public boolean isVegetarian() {
        return isVegetarian;
    }

    public boolean isProteinRequired() {
        return proteinRequired;
    }

    public String getCategory() {
        return category;
    }
}
