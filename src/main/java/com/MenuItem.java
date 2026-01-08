package com;

import com.google.gson.annotations.SerializedName;

public class MenuItem {

    private int id;
    private String name;
    private String description;
    private double price;
    private String imageUrl;
    private int calories;
    private boolean available;
    private boolean spicy;
    private boolean vegetarian;
    private boolean proteinRequired;
    private String category;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MenuItem)) return false;
        return this.id == ((MenuItem) o).id;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

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
        return available;
    }

    public boolean isSpicy() {
        return spicy;
    }

    public boolean isVegetarian() {
        return vegetarian;
    }

    public boolean isProteinRequired() {
        return proteinRequired;
    }

    public String getCategory() {
        return category;
    }


    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
