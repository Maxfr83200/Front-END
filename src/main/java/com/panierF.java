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

public class panierF {

    private Boolean isFrench;
    @FXML private Label textTitle;
    @FXML private Button btnConfirmer;

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


    @FXML
    public void initialize() {
        updateTotalprice();
        refreshCartUI();
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
        cartContainer.getChildren().clear();

        CartModel cart = CartModel.getInstance();


        if (cart.getQuantities().isEmpty()) {
            Label empty = new Label("Votre panier est vide");
            if (Boolean.TRUE.equals(isFrench)) {
                empty = new Label("Votre panier est vide");
            } else {
                empty = new Label("Your cart is empty");
            }
            empty.setStyle("-fx-font-size: 18px; -fx-text-fill: grey;");
            cartContainer.getChildren().add(empty);
            updateTotalprice();
            return;
        }

        for (String key : cart.getQuantities().keySet()) {
            MenuItem item = cart.getItem(key);
            int qty = cart.getQuantities().get(key);
            String protein = cart.getProteinFromKey(key);


            String nomPlat = item.getName();
            if (protein != null) nomPlat += " (" + protein + ")";

            Label nameLabel = new Label(nomPlat);
            nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

            Label priceLabel = new Label(String.format("%.2f \u20AC", item.getPrice() * qty));
            priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

            VBox infoBox = new VBox(nameLabel, priceLabel);
            infoBox.setPrefWidth(200);

            Button btnMinus = new Button("-");
            Button btnPlus = new Button("+");
            Button btnDelete = new Button("X");
            Label qtyLabel = new Label(String.valueOf(qty));

            btnMinus.setStyle("-fx-min-width: 30px; -fx-background-color: #f0f0f0;");
            btnPlus.setStyle("-fx-min-width: 30px; -fx-background-color: #f0f0f0;");
            btnDelete.setStyle("-fx-background-color: #ff4444; -fx-text-fill: white; -fx-font-weight: bold;");
            qtyLabel.setStyle("-fx-font-size: 16px; -fx-padding: 0 10 0 10;");

            btnMinus.setOnAction(e -> {
                cart.decreaseQuantity(key);
                refreshCartUI();
                updateTotalprice();
            });

            btnPlus.setOnAction(e -> {
                cart.increaseQuantity(key);
                refreshCartUI();
                updateTotalprice();
            });

            btnDelete.setOnAction(e -> {
                cart.removeItem(key);
                refreshCartUI();
                updateTotalprice();
            });

            // --- 4. Assemblage final dans une ligne (HBox) ---
            HBox row = new HBox(10); // Espace de 10px entre les éléments
            row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            row.setStyle("-fx-padding: 10; -fx-border-color: #eee; -fx-border-width: 0 0 1 0;");

            // Ordre : Infos | - | Qty | + | Espace | X
            HBox buttonsBox = new HBox(5, btnMinus, qtyLabel, btnPlus);
            buttonsBox.setAlignment(javafx.geometry.Pos.CENTER);

            row.getChildren().addAll(infoBox, buttonsBox, new Label("   "), btnDelete);

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