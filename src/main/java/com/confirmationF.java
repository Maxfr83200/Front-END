package com;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class confirmationF {
    private Boolean isFrench;

    @FXML private Label textTitle;
    @FXML private Button btnPayer;

    public void setLanguage(Boolean isFrench) {
        this.isFrench = isFrench;
        if(isFrench){
            textTitle.setText("Confirmation de paiement");
            btnPayer.setText("Payer");
        }
        else {
            textTitle.setText("Payment Confirmation");
            btnPayer.setText("Pay");
        }
    }

    @FXML
    public void initialize() {
        updateTotalprice();
        refreshCartUI();

        if (cart.getQuantities().isEmpty()) {
            btnPayer.setDisable(true);
        }

        InnerShadow clickEffect = new InnerShadow();
        clickEffect.setRadius(10.0);
        clickEffect.setOffsetX(3.0);
        clickEffect.setOffsetY(3.0);
        clickEffect.setColor(Color.rgb(0, 0, 0, 0.6));

        if (btnPayer != null) {
            // Applique l'effet quand on appuie
            btnPayer.setOnMousePressed(event -> {
                btnPayer.setEffect(clickEffect);
            });

            // Retire l'effet quand on relâche
            btnPayer.setOnMouseReleased(event -> {
                btnPayer.setEffect(null);
            });

            // Sécurité : retire l'effet si la souris quitte le bouton
            btnPayer.setOnMouseExited(event -> {
                btnPayer.setEffect(null);
            });
        }
    }

    CartModel cart = CartModel.getInstance();

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
            label.setStyle("-fx-font-size: 40px; -fx-text-fill: #1C1816; -fx-font-family: Berlin Sans FB");
            cartContainer.getChildren().add(label);
        }

        price.setText(
                String.format("Total : %.2f \u20AC", cart.getTotal())
        );
    }


    public void goPaiement(ActionEvent event) {

        if (CartModel.getInstance().getQuantities().isEmpty()) {
            System.out.println("Impossible de payer : Le panier est vide.");
            return;
        }

        CartModel.getInstance().clear();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/paiement.fxml"));
        try {
            Parent root = loader.load();
            paiementF mainCtrl = loader.getController();
            mainCtrl.setLanguage(isFrench);

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

            panierF mainCtrl = loader.getController();
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
