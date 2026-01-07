package com;

import java.util.HashMap;
import java.util.Map;

public class CartModel {

    private static CartModel instance;

    // MenuItem -> quantité
    private final Map<MenuItem, Integer> items = new HashMap<>();

    private CartModel() {}

    public static CartModel getInstance() {
        if (instance == null) {
            instance = new CartModel();
        }
        return instance;
    }

    // ➕ Ajouter
    public void addItem(MenuItem item, int qty) {
        items.put(item, items.getOrDefault(item, 0) + qty);
    }

    // ➖ Retirer
    public void removeItem(MenuItem item) {
        items.remove(item);
    }

    public Map<MenuItem, Integer> getItems() {
        return items;
    }

    public double getTotal() {
        return items.entrySet().stream()
                .mapToDouble(e -> e.getKey().getPrice() * e.getValue())
                .sum();
    }

    public void clear() {
        items.clear();
    }
}
