package com;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class confirmationF {
    private Boolean isFrench;

    public void setLanguage(Boolean isFrench) {
        this.isFrench = isFrench;
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

        for (String key : cart.getQuantities().keySet()) {
            MenuItem item = cart.getItem(key);
            int qty = cart.getQuantities().get(key);
            String protein = cart.getProteinFromKey(key);

            String text = qty + " x " + item.getName();
            if (protein != null) text += " (" + protein + ")";
            text += " - " + String.format("%.2f \u20AC", item.getPrice() * qty);

            Label label = new Label(text);
            label.setStyle("-fx-font-size: 25px;");
            cartContainer.getChildren().add(label);
        }

        price.setText(
                String.format("Total : %.2f \u20AC", cart.getTotal())
        );
    }


    public void retourAcceuil(ActionEvent event) {
        CartModel.getInstance().clear();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/accueil.fxml"));
        try {
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void goPanier(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/panier.fxml"));
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
