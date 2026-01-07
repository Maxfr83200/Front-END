package com;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.ToggleGroup;

import java.io.IOException;

public class detailplatF {

    @FXML private HBox proteinBox;
    @FXML private RadioButton rbPoulet;
    @FXML private RadioButton rbBoeuf;
    @FXML private RadioButton rbTofu;
    @FXML private RadioButton rbCrevette;
    @FXML private ToggleGroup proteinGroup;



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



    private void hideProteinOptions() {
        proteinBox.setVisible(false);
        proteinBox.setManaged(false);

        rbPoulet.setDisable(true);
        rbBoeuf.setDisable(true);
        rbTofu.setDisable(true);
        rbCrevette.setDisable(true);

        rbPoulet.setSelected(false);
        rbBoeuf.setSelected(false);
        rbTofu.setSelected(false);
        rbCrevette.setSelected(false);
    }

    private void showProteinOptions() {
        proteinBox.setVisible(true);
        proteinBox.setManaged(true);

        rbPoulet.setDisable(false);
        rbBoeuf.setDisable(false);
        rbTofu.setDisable(false);
        rbCrevette.setDisable(false);

        rbBoeuf.setSelected(true);
    }






    CartModel cart = CartModel.getInstance();

    @FXML
    private void addToCart(ActionEvent event) {
        String protein = null;

        if (item.isProteinRequired()) {
            RadioButton selected =
                    (RadioButton) proteinGroup.getSelectedToggle();
            protein = selected != null ? selected.getText() : null;
        }

        CartModel.getInstance().addItem(item, protein, quantity);
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

        if (item.isProteinRequired()) {
            showProteinOptions();
        } else {
            hideProteinOptions();
        }
    }
}
