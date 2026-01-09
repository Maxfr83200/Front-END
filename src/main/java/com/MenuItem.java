package com;

/**
 * Représente un élément du menu (plat/produit) provenant de l'API.
 * Utilisé pour l'affichage et l'édition dans l'interface admin.
 */
public class MenuItem {

    // =========================
    // Champs (mappés depuis le JSON)
    // =========================
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

    // =========================
    // Equals / HashCode
    // =========================

    /**
     * Compare deux items en se basant uniquement sur leur identifiant (id).
     *
     * @param o objet à comparer
     * @return true si les deux objets représentent le même item (même id)
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MenuItem)) return false;
        return this.id == ((MenuItem) o).id;
    }

    /**
     * Génère le hashCode de l'objet à partir de l'identifiant (id).
     *
     * @return hashCode basé sur id
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }

    // =========================
    // Getters
    // =========================

    /**
     * Retourne l'identifiant unique de l'item.
     *
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * Retourne le nom de l'item.
     *
     * @return nom
     */
    public String getName() {
        return name;
    }

    /**
     * Retourne la description de l'item.
     *
     * @return description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Retourne le prix de l'item.
     *
     * @return prix
     */
    public double getPrice() {
        return price;
    }

    /**
     * Retourne le chemin/URL d'image associé à l'item.
     *
     * @return imageUrl
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Retourne la valeur calorique (kcal) de l'item.
     *
     * @return calories
     */
    public int getCalories() {
        return calories;
    }

    /**
     * Indique si l'item est actuellement disponible.
     *
     * @return true si disponible
     */
    public boolean isAvailable() {
        return available;
    }

    /**
     * Indique si l'item est épicé.
     *
     * @return true si épicé
     */
    public boolean isSpicy() {
        return spicy;
    }

    /**
     * Indique si l'item est végétarien.
     *
     * @return true si végétarien
     */
    public boolean isVegetarian() {
        return vegetarian;
    }

    /**
     * Indique si l'item nécessite une option protéine (ex: choix de viande/tofu).
     *
     * @return true si une protéine est requise
     */
    public boolean isProteinRequired() {
        return proteinRequired;
    }

    /**
     * Retourne la catégorie de l'item (ex: entrée, plat, dessert...).
     *
     * @return catégorie
     */
    public String getCategory() {
        return category;
    }

    // =========================
    // Setters (édition admin)
    // =========================

    /**
     * Modifie le nom de l'item.
     *
     * @param name nouveau nom
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Modifie la description de l'item.
     *
     * @param description nouvelle description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Modifie le prix de l'item.
     *
     * @param price nouveau prix
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Modifie la disponibilité de l'item.
     *
     * @param available true si disponible
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }
}
