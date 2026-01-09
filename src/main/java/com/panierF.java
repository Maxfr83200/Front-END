package com;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class panierF {

    private Boolean isFrench;
    @FXML private Label textTitle;
    @FXML private Button btnConfirmer;

    @FXML
    public void initialize() {
        updateTotalprice();
        refreshCartUI();

        InnerShadow clickEffect = new InnerShadow();
        clickEffect.setRadius(10.0);
        clickEffect.setOffsetX(3.0);
        clickEffect.setOffsetY(3.0);
        clickEffect.setColor(Color.rgb(0, 0, 0, 0.6));

        if (btnConfirmer != null) {
            btnConfirmer.setOnMousePressed(event -> {
                btnConfirmer.setEffect(clickEffect);
            });

            btnConfirmer.setOnMouseReleased(event -> {
                btnConfirmer.setEffect(null);
            });

            btnConfirmer.setOnMouseExited(event -> {
                btnConfirmer.setEffect(null);
            });
        }
    }

    public void setLanguage(Boolean isFrench) {
        this.isFrench = isFrench;

        if (Boolean.TRUE.equals(isFrench)) {
            textTitle.setText("Votre Panier");
            btnConfirmer.setText("Confirmer");
        } else {
            textTitle.setText("Your Cart");
            btnConfirmer.setText("Confirm");
        }
    }


    CartModel cart = CartModel.getInstance();



    private HBox createCardtoCart(MenuItem item, int qty) {

        Label label = new Label(qty + " x " + item.getName() + "  -  " +
                String.format("%.2f \u20AC", item.getPrice() * qty));
        label.setStyle("-fx-font-size: 16px;");

        HBox card = new HBox(label);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle("-fx-padding: 6;");

        return card;
    }

    @FXML
    private VBox cartContainer;

    @FXML
    private Label price;

    private void updateTotalprice() {
        price.setText(String.format("Total : %.2f \u20AC", cart.getTotal()));
    }


    private void refreshCartUI() {
        // 1. On nettoie le conteneur
        cartContainer.getChildren().clear();

        // TRÈS IMPORTANT : Force la VBox à étirer ses enfants horizontalement
        cartContainer.setFillWidth(true);

        CartModel cart = CartModel.getInstance();

        // 2. Gestion du panier vide
        if (cart.getQuantities().isEmpty()) {
            Label empty = new Label(Boolean.TRUE.equals(isFrench) ? "Votre panier est vide" : "Your cart is empty");
            empty.setStyle("-fx-font-size: 18px; -fx-text-fill: grey; -fx-padding: 20;");
            cartContainer.getChildren().add(empty);
            updateTotalprice();
            return;
        }

        // 3. Boucle sur les articles
        for (String key : cart.getQuantities().keySet()) {
            MenuItem item = cart.getItem(key);
            int qty = cart.getQuantities().get(key);
            String protein = cart.getProteinFromKey(key);

            // --- GAUCHE : INFOS ---
            String nomPlat = item.getName();
            if (protein != null) nomPlat += " (" + protein + ")";

            Label nameLabel = new Label(nomPlat);
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #333333;");

            Label priceLabel = new Label(String.format("%.2f \u20AC", item.getPrice() * qty));
            priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555;");

            VBox infoBox = new VBox(5, nameLabel, priceLabel);
            infoBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            // --- MILIEU : LE RESSORT (SPACER) ---
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            // --- DROITE : BOUTONS ---
            Button btnMinus = new Button("-");
            Button btnPlus = new Button("+");
            Button btnDelete = new Button("X");
            Label qtyLabel = new Label(String.valueOf(qty));

            btnMinus.setStyle("-fx-min-width: 35px; -fx-background-radius: 5; -fx-cursor: hand;");
            btnPlus.setStyle("-fx-min-width: 35px; -fx-background-radius: 5; -fx-cursor: hand;");
            btnDelete.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");
            qtyLabel.setStyle("-fx-font-size: 16px; -fx-padding: 0 10 0 10; -fx-text-fill: #333333;");

            // Effets au clic
            InnerShadow clickEffect = new InnerShadow(5, 2, 2, Color.rgb(0,0,0,0.6));
            for (Button b : new Button[]{btnMinus, btnPlus, btnDelete}) {
                b.setOnMousePressed(e -> b.setEffect(clickEffect));
                b.setOnMouseReleased(e -> b.setEffect(null));
            }

            // Actions
            btnPlus.setOnAction(e -> { cart.increaseQuantity(key); refreshCartUI(); });
            btnMinus.setOnAction(e -> { cart.decreaseQuantity(key); refreshCartUI(); });
            btnDelete.setOnAction(e -> { cart.removeItem(key); refreshCartUI(); });

            HBox controls = new HBox(10, btnMinus, qtyLabel, btnPlus, new Label("  "), btnDelete);
            controls.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

            // --- ASSEMBLAGE DE LA CARTE ---
            HBox row = new HBox();
            row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

            // TRÈS IMPORTANT : Autorise la ligne à s'étendre horizontalement
            row.setMaxWidth(Double.MAX_VALUE);

            row.setStyle("-fx-padding: 15; -fx-background-color: white; -fx-background-radius: 10; " +
                    "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 2);");

            row.getChildren().addAll(infoBox, spacer, controls);

            // Ajout d'une marge pour ne pas que la carte touche les bords de la boîte
            VBox.setMargin(row, new javafx.geometry.Insets(5, 10, 5, 10));

            cartContainer.getChildren().add(row);
        }
        updateTotalprice();
    }

    public void goConfirmation(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/confirmation.fxml"));
        try {
            Parent root = loader.load();
            confirmationF ctrl = loader.getController();
            ctrl.setLanguage(isFrench);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goMain(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mainscreen.fxml"));
        try {
            Parent root = loader.load();
            mainscreenF mainCtrl = loader.getController();
            mainCtrl.setLanguage(isFrench);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}