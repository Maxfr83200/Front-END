package com;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class detailplatF {


    @FXML private Text quantityText;
    @FXML private ImageView imagePlat;
    @FXML private Text afficheNom;
    @FXML private Text afficheDesc;
    @FXML private Text affichePrix;

    private MenuItem item;
    private boolean isFrench;
    private int quantity = 1;

    @FXML
    private void initialize() {
        refreshQtyUI();
    }

    @FXML
    private void plusQty() {
        if (quantity < 9) {
            quantity++;
            refreshQtyUI();
        }
    }

    @FXML
    private void minusQty() {
        if (quantity > 1) {
            quantity--;
            refreshQtyUI();
        }
    }

    CartModel cart = CartModel.getInstance();

    @FXML
    private void addToCart(ActionEvent event) {
        cart.addItem(item, quantity);
        goMain(event);
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

    private void refreshQtyUI() {
        if (quantityText != null) quantityText.setText(String.valueOf("Quantit\u00E9 : "+quantity));

        if (item != null && affichePrix != null) {
            double total = item.getPrice() * quantity;
            affichePrix.setText(String.format("%.2f \u20AC", total));
        }
    }

    public void setLanguage(boolean isFrench) {
        this.isFrench = isFrench;
    }

    public void setItem(MenuItem item) {
        this.item = item;


        afficheNom.setText(item.getName());
        afficheDesc.setText(item.getDescription());
        affichePrix.setText(String.format("%.2f \u20AC", item.getPrice())); // € en unicode


        String imagePath = item.getImageUrl();
        if (imagePath != null && !imagePath.isBlank()) {
            Image img = new Image(getClass().getResourceAsStream("/" + imagePath));
            imagePlat.setImage(img);
        } else {
            imagePlat.setImage(null);
        }
    }
}
