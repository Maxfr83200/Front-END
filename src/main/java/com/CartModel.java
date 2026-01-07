package com;

import java.util.HashMap;
import java.util.Map;

public class CartModel {

    private static CartModel instance;

    // clé = "itemId|protein"
    private final Map<String, Integer> quantities = new HashMap<>();
    private final Map<String, MenuItem> items = new HashMap<>();

    private CartModel() {}

    public static CartModel getInstance() {
        if (instance == null) {
            instance = new CartModel();
        }
        return instance;
    }

    private String buildKey(MenuItem item, String protein) {
        return item.getId() + "|" + (protein == null ? "null" : protein);
    }

    public void addItem(MenuItem item, String protein, int qty) {
        String key = buildKey(item, protein);

        items.putIfAbsent(key, item);
        quantities.put(key, quantities.getOrDefault(key, 0) + qty);
    }

    public Map<String, Integer> getQuantities() {
        return quantities;
    }

    public MenuItem getItem(String key) {
        return items.get(key);
    }

    public String getProteinFromKey(String key) {
        String[] parts = key.split("\\|");
        return "null".equals(parts[1]) ? null : parts[1];
    }

    public double getTotal() {
        double total = 0;
        for (String key : quantities.keySet()) {
            MenuItem item = items.get(key);
            int qty = quantities.get(key);
            total += item.getPrice() * qty;
        }
        return total;
    }
}


