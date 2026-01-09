package com;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class confirmationF {

    // =========================
    // Constantes
    // =========================
    private static final String PANIER_FXML = "/com/panier.fxml";
    private static final String PAIEMENT_FXML = "/com/paiement.fxml";

    // =========================
    // État
    // =========================
    private boolean isFrench = true;
    private final CartModel cart = CartModel.getInstance();

    // =========================
    // Composants FXML
    // =========================
    @FXML private Label textTitle;
    @FXML private Button btnPayer;

    @FXML private VBox cartContainer;
    @FXML private Label price;

    // =========================
    // Langue
    // =========================

    /**
     * Définit la langue de l'écran et met à jour les textes (titre + bouton payer).
     *
     * @param isFrench true = français, false = anglais
     */
    public void setLanguage(Boolean isFrench) {
        this.isFrench = Boolean.TRUE.equals(isFrench);

        if (this.isFrench) {
            textTitle.setText("Confirmation de paiement");
            btnPayer.setText("Payer");
        } else {
            textTitle.setText("Payment Confirmation");
            btnPayer.setText("Pay");
        }
    }

    // =========================
    // Initialisation JavaFX
    // =========================

    /**
     * Initialise l'écran : affiche le panier, met à jour le total,
     * désactive le bouton payer si le panier est vide, et ajoute un effet de clic au bouton.
     */
    @FXML
    public void initialize() {
        refreshCartUI();
        updateTotalPrice();
        updatePayButtonState();
        setupButtonPressEffect(btnPayer);
    }

    /**
     * Active/désactive le bouton "Payer" en fonction du contenu du panier.
     */
    private void updatePayButtonState() {
        if (btnPayer != null) {
            btnPayer.setDisable(cart.getQuantities().isEmpty());
        }
    }

    /**
     * Ajoute un effet InnerShadow pendant l'appui (mouse pressed) sur un bouton.
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
     */
    private void refreshCartUI() {
        if (cartContainer == null) return;

        cartContainer.getChildren().clear();

        for (String key : cart.getQuantities().keySet()) {
            MenuItem item = cart.getItem(key);
            Integer qty = cart.getQuantities().get(key);
            String protein = cart.getProteinFromKey(key);

            if (item == null || qty == null) continue;

            String line = qty + " x " + item.getName();
            if (protein != null) line += " (" + protein + ")";
            line += " - " + String.format("%.2f \u20AC", item.getPrice() * qty);

            Label label = new Label(line);
            label.setStyle("-fx-font-size: 40px; -fx-text-fill: #1C1816; -fx-font-family: Berlin Sans FB;");
            cartContainer.getChildren().add(label);
        }
    }

    // =========================
    // Navigation
    // =========================

    /**
     * Lance l'écran de paiement si le panier n'est pas vide.
     * Vide le panier avant de naviguer vers l'écran de paiement.
     *
     * @param event événement du bouton payer
     */
    public void goPaiement(ActionEvent event) {
        if (cart.getQuantities().isEmpty()) {
            System.out.println("Impossible de payer : Le panier est vide.");
            return;
        }

        cart.clear();

        loadAndShowScene(PAIEMENT_FXML, event, controller -> {
            paiementF paiementCtrl = (paiementF) controller;
            paiementCtrl.setLanguage(isFrench);
        });
    }

    /**
     * Retourne à l'écran panier en transmettant la langue.
     *
     * @param event événement du bouton retour panier
     */
    public void goPanier(ActionEvent event) {
        loadAndShowScene(PANIER_FXML, event, controller -> {
            panierF panierCtrl = (panierF) controller;
            panierCtrl.setLanguage(isFrench);
        });
    }

    /**
     * Charge un FXML, récupère son contrôleur, exécute une action dessus, puis affiche la nouvelle scène.
     *
     * @param fxmlPath chemin du fichier FXML
     * @param event événement JavaFX (sert à récupérer le Stage courant)
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
     * Interface fonctionnelle interne permettant d'exécuter une action sur un contrôleur chargé.
     */
    @FunctionalInterface
    private interface ControllerConsumer {
        void accept(Object controller);
    }
}
