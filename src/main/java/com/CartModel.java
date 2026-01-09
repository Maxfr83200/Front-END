package com;

import java.util.HashMap;
import java.util.Map;

/**
 * Modèle de panier (singleton) : stocke les items ajoutés au panier et leurs quantités.
 * Les items sont identifiés par une clé composée : "id|protein".
 */
public class CartModel {

    // =========================
    // Constantes
    // =========================
    private static final int MAX_QTY = 9;
    private static final String KEY_SEPARATOR = "|";
    private static final String NULL_TOKEN = "null";

    // =========================
    // Singleton
    // =========================
    private static CartModel instance;

    // =========================
    // Données du panier
    // =========================
    private final Map<String, Integer> quantities = new HashMap<>();
    private final Map<String, MenuItem> items = new HashMap<>();

    /**
     * Constructeur privé pour empêcher l'instanciation externe (singleton).
     */
    private CartModel() {
    }

    /**
     * Retourne l'instance unique du panier (singleton).
     *
     * @return instance unique de CartModel
     */
    public static synchronized CartModel getInstance() {
        if (instance == null) {
            instance = new CartModel();
        }
        return instance;
    }

    // =========================
    // Clés / Identifiants
    // =========================

    /**
     * Construit la clé unique d'un item pour le panier, en fonction de l'ID et de la protéine.
     *
     * @param item    l'item concerné
     * @param protein la protéine choisie (null si aucune)
     * @return clé unique sous la forme "id|protein"
     */
    private String buildKey(MenuItem item, String protein) {
        String proteinPart = (protein == null) ? NULL_TOKEN : protein;
        return item.getId() + KEY_SEPARATOR + proteinPart;
    }

    /**
     * Extrait la protéine depuis une clé de panier "id|protein".
     *
     * @param key clé du panier
     * @return protéine ou null si aucune / clé invalide
     */
    public String getProteinFromKey(String key) {
        if (key == null) return null;

        String[] parts = key.split("\\|", -1);
        if (parts.length < 2) return null;

        return NULL_TOKEN.equals(parts[1]) ? null : parts[1];
    }

    // =========================
    // Ajout / Accès
    // =========================

    /**
     * Ajoute un item au panier (ou augmente sa quantité si déjà présent).
     *
     * @param item    item à ajouter
     * @param protein protéine choisie (null si aucune)
     * @param qty     quantité à ajouter
     */
    public void addItem(MenuItem item, String protein, int qty) {
        String key = buildKey(item, protein);

        items.putIfAbsent(key, item);
        quantities.put(key, quantities.getOrDefault(key, 0) + qty);
    }

    /**
     * Retourne la map des quantités par clé ("id|protein").
     *
     * @return map clé -> quantité
     */
    public Map<String, Integer> getQuantities() {
        return quantities;
    }

    /**
     * Retourne l'item associé à une clé de panier.
     *
     * @param key clé du panier
     * @return item correspondant (ou null si absent)
     */
    public MenuItem getItem(String key) {
        return items.get(key);
    }

    // =========================
    // Calculs
    // =========================

    /**
     * Calcule le total du panier (somme des prix * quantités).
     *
     * @return total du panier
     */
    public double getTotal() {
        double total = 0;

        for (String key : quantities.keySet()) {
            MenuItem item = items.get(key);
            Integer qty = quantities.get(key);

            if (item == null || qty == null) {
                continue; // sécurité en cas d'incohérence (normalement impossible)
            }

            total += item.getPrice() * qty;
        }

        return total;
    }

    // =========================
    // Gestion des quantités
    // =========================

    /**
     * Augmente la quantité d'un item (jusqu'à MAX_QTY).
     *
     * @param key clé de l'item dans le panier
     */
    public void increaseQuantity(String key) {
        Integer current = quantities.get(key);
        if (current == null) return;

        if (current < MAX_QTY) {
            quantities.put(key, current + 1);
        }
    }

    /**
     * Diminue la quantité d'un item.
     * Si la quantité passe en dessous de 1, l'item est supprimé.
     *
     * @param key clé de l'item dans le panier
     */
    public void decreaseQuantity(String key) {
        Integer currentQty = quantities.get(key);
        if (currentQty == null) return;

        if (currentQty > 1) {
            quantities.put(key, currentQty - 1);
        } else {
            removeItem(key);
        }
    }

    /**
     * Supprime complètement un item du panier (quantité + item).
     *
     * @param key clé de l'item
     */
    public void removeItem(String key) {
        quantities.remove(key);
        items.remove(key);
    }

    /**
     * Vide entièrement le panier.
     */
    public void clear() {
        quantities.clear();
        items.clear();
    }
}
