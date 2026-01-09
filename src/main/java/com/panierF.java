package com;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class panierF {

    // =========================
    // Constantes
    // =========================
    private static final String CONFIRMATION_FXML = "/com/confirmation.fxml";
    private static final String MAINSCREEN_FXML = "/com/mainscreen.fxml";

    // =========================
    // État
    // =========================
    private boolean isFrench = true;
    private final CartModel cart = CartModel.getInstance();

    // =========================
    // Composants FXML
    // =========================
    @FXML private Label textTitle;
    @FXML private Button btnConfirmer;

    @FXML private VBox cartContainer;
    @FXML private Label price;

    // =========================
    // Initialisation JavaFX
    // =========================

    /**
     * Initialise l'écran : affiche le panier, met à jour le total, et ajoute un effet de clic au bouton confirmer.
     */
    @FXML
    public void initialize() {
        refreshCartUI();
        updateTotalPrice();
        setupButtonPressEffect(btnConfirmer);
    }

    // =========================
    // Langue
    // =========================

    /**
     * Définit la langue de l'écran et met à jour le titre + bouton confirmer.
     *
     * @param isFrench true = français, false = anglais
     */
    public void setLanguage(Boolean isFrench) {
        this.isFrench = Boolean.TRUE.equals(isFrench);

        if (this.isFrench) {
            textTitle.setText("Votre Panier");
            btnConfirmer.setText("Confirmer");
        } else {
            textTitle.setText("Your Cart");
            btnConfirmer.setText("Confirm");
        }

        // Optionnel : rafraîchir les textes du panier (panier vide, etc.)
        refreshCartUI();
        updateTotalPrice();
    }

    // =========================
    // UI Panier
    // =========================

    /**
     * Met à jour l'affichage du total panier.
     */
    private void updateTotalPrice() {
        if (price != null) {
            price.setText(String.format("Total : %.2f \u20AC", cart.getTotal()));
        }
    }

    /**
     * Rafraîchit l'affichage de la liste d'items du panier.
     * Gère le panier vide et génère une ligne avec contrôles (plus, moins, supprimer) par item.
     */
    private void refreshCartUI() {
        if (cartContainer == null) return;

        cartContainer.getChildren().clear();
        cartContainer.setFillWidth(true);

        if (cart.getQuantities().isEmpty()) {
            Label empty = new Label(isFrench ? "Votre panier est vide" : "Your cart is empty");
            empty.setStyle("-fx-font-size: 18px; -fx-text-fill: grey; -fx-padding: 20;");
            cartContainer.getChildren().add(empty);
            return;
        }

        for (String key : cart.getQuantities().keySet()) {
            MenuItem item = cart.getItem(key);
            Integer qty = cart.getQuantities().get(key);
            String protein = cart.getProteinFromKey(key);

            if (item == null || qty == null) continue;

            cartContainer.getChildren().add(createCartRow(key, item, qty, protein));
        }
    }

    /**
     * Crée une ligne (carte) représentant un item du panier avec :
     * - à gauche : nom + prix,
     * - à droite : boutons -, + et suppression.
     *
     * @param key     clé panier (id|protein)
     * @param item    item concerné
     * @param qty     quantité
     * @param protein protéine (peut être null)
     * @return une HBox prête à être ajoutée au cartContainer
     */
    private HBox createCartRow(String key, MenuItem item, int qty, String protein) {
        // --- GAUCHE : INFOS ---
        String name = item.getName();
        if (protein != null) name += " (" + protein + ")";

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #333333;");

        Label priceLabel = new Label(String.format("%.2f \u20AC", item.getPrice() * qty));
        priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555;");

        VBox infoBox = new VBox(5, nameLabel, priceLabel);
        infoBox.setAlignment(Pos.CENTER_LEFT);

        // --- MILIEU : SPACER ---
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // --- DROITE : CONTROLS ---
        HBox controls = createControls(key, qty);

        // --- ASSEMBLAGE ---
        HBox row = new HBox(infoBox, spacer, controls);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setMaxWidth(Double.MAX_VALUE);

        row.setStyle(
                "-fx-padding: 15; -fx-background-color: white; -fx-background-radius: 10; " +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);"
        );

        VBox.setMargin(row, new Insets(5, 10, 5, 10));

        return row;
    }

    /**
     * Crée les contrôles d'une ligne panier : bouton -, label quantité, bouton +, bouton suppression.
     *
     * @param key clé panier
     * @param qty quantité actuelle
     * @return HBox des contrôles
     */
    private HBox createControls(String key, int qty) {
        Button btnMinus = new Button("-");
        Button btnPlus = new Button("+");
        Button btnDelete = new Button("X");
        Label qtyLabel = new Label(String.valueOf(qty));

        btnMinus.setStyle("-fx-min-width: 35px; -fx-background-radius: 5; -fx-cursor: hand;");
        btnPlus.setStyle("-fx-min-width: 35px; -fx-background-radius: 5; -fx-cursor: hand;");
        btnDelete.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");
        qtyLabel.setStyle("-fx-font-size: 16px; -fx-padding: 0 10 0 10; -fx-text-fill: #333333;");

        setupRowButtonEffects(btnMinus, btnPlus, btnDelete);

        btnPlus.setOnAction(e -> {
            cart.increaseQuantity(key);
            refreshCartUI();
            updateTotalPrice();
        });

        btnMinus.setOnAction(e -> {
            cart.decreaseQuantity(key);
            refreshCartUI();
            updateTotalPrice();
        });

        btnDelete.setOnAction(e -> {
            cart.removeItem(key);
            refreshCartUI();
            updateTotalPrice();
        });

        HBox controls = new HBox(10, btnMinus, qtyLabel, btnPlus, new Label("  "), btnDelete);
        controls.setAlignment(Pos.CENTER_RIGHT);
        return controls;
    }

    /**
     * Ajoute un effet visuel au clic sur les boutons de ligne (-, +, X).
     *
     * @param buttons boutons à décorer
     */
    private void setupRowButtonEffects(Button... buttons) {
        InnerShadow clickEffect = new InnerShadow(5, 2, 2, Color.rgb(0, 0, 0, 0.6));

        for (Button b : buttons) {
            if (b == null) continue;
            b.setOnMousePressed(e -> b.setEffect(clickEffect));
            b.setOnMouseReleased(e -> b.setEffect(null));
            b.setOnMouseExited(e -> b.setEffect(null));
        }
    }

    /**
     * Ajoute un effet de clic (InnerShadow) sur un bouton.
     *
     * @param button bouton à décorer
     */
    private void setupButtonPressEffect(Button button) {
        if (button == null) return;

        InnerShadow clickEffect = new InnerShadow();
        clickEffect.setRadius(10.0);
        clickEffect.setOffsetX(3.0);
        clickEffect.setOffsetY(3.0);
        clickEffect.setColor(Color.rgb(0, 0, 0, 0.6));

        button.setOnMousePressed(event -> button.setEffect(clickEffect));
        button.setOnMouseReleased(event -> button.setEffect(null));
        button.setOnMouseExited(event -> button.setEffect(null));
    }

    // =========================
    // Navigation
    // =========================

    /**
     * Ouvre l'écran de confirmation en transmettant la langue.
     *
     * @param event événement du bouton confirmer
     */
    public void goConfirmation(ActionEvent event) {
        loadAndShowScene(CONFIRMATION_FXML, event, controller -> {
            confirmationF ctrl = (confirmationF) controller;
            ctrl.setLanguage(isFrench);
        });
    }

    /**
     * Retourne à l'écran principal (mainscreen) en transmettant la langue.
     *
     * @param event événement du bouton retour
     */
    public void goMain(ActionEvent event) {
        loadAndShowScene(MAINSCREEN_FXML, event, controller -> {
            mainscreenF ctrl = (mainscreenF) controller;
            ctrl.setLanguage(isFrench);
        });
    }

    /**
     * Charge un fichier FXML, récupère son contrôleur, exécute une action optionnelle dessus,
     * puis remplace la scène actuelle par la nouvelle.
     *
     * @param fxmlPath chemin du fichier FXML
     * @param event événement servant à récupérer le Stage courant
     * @param onLoaded action à exécuter sur le contrôleur
     */
    private void loadAndShowScene(String fxmlPath, ActionEvent event, ControllerConsumer onLoaded) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

        try {
            Parent root = loader.load();
            Object controller = loader.getController();

            if (onLoaded != null) {
                onLoaded.accept(controller);
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Interface fonctionnelle interne pour exécuter une action sur un contrôleur chargé via FXMLLoader.
     */
    @FunctionalInterface
    private interface ControllerConsumer {
        void accept(Object controller);
    }
}
