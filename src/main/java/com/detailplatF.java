package com;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.control.ToggleGroup;
import javafx.scene.shape.Rectangle;
import javafx.application.Platform;

import java.io.IOException;

public class detailplatF {

    @FXML private HBox proteinBox;
    @FXML private RadioButton rbPoulet;
    @FXML private RadioButton rbBoeuf;
    @FXML private RadioButton rbTofu;
    @FXML private RadioButton rbCrevette;
    @FXML private ToggleGroup proteinGroup;
    @FXML private Button btnAjoutPanier;
    @FXML private Button btnPlus;
    @FXML private Button btnMinus;


    @FXML
    private Text quantityText;
    @FXML
    private ImageView imagePlat;
    @FXML
    private Text afficheNom;
    @FXML
    private Text afficheDesc;
    @FXML
    private Text affichePrix;


    private MenuItem item;
    private boolean isFrench;
    private int quantity = 1;

    public void setLanguage(boolean isFrench) {
        this.isFrench = isFrench;
        if(isFrench){
            rbBoeuf.setText("Boeuf");
            rbCrevette.setText("Crevette");
            rbPoulet.setText("Poulet");
            btnAjoutPanier.setText("Ajouter au Panier");
        }
        else {
            rbBoeuf.setText("Beef");
            rbCrevette.setText("Shrimp");
            rbPoulet.setText("Chicken");
            btnAjoutPanier.setText("Add to Cart");
        }
        rbTofu.setText("Tofu");
        refreshQtyUI();
    }

    @FXML
    private void initialize() {

        refreshQtyUI();

        Rectangle clip = new Rectangle();
        clip.setArcWidth(60);
        clip.setArcHeight(60);
        imagePlat.setClip(clip);


        Runnable updateClip = () -> {
            var b = imagePlat.getLayoutBounds();
            clip.setWidth(b.getWidth());
            clip.setHeight(b.getHeight());
        };


        Platform.runLater(updateClip);


        imagePlat.layoutBoundsProperty().addListener((obs, oldB, newB) -> {
            clip.setWidth(newB.getWidth());
            clip.setHeight(newB.getHeight());
        });

        InnerShadow clickEffect = new InnerShadow();
        clickEffect.setRadius(10.0);
        clickEffect.setOffsetX(3.0);
        clickEffect.setOffsetY(3.0);
        clickEffect.setColor(Color.rgb(0, 0, 0, 0.6));

        Button[] buttons = {btnAjoutPanier, btnMinus, btnPlus};

        for (Button b : buttons) {
            if (b != null) {
                b.setOnMousePressed(e -> b.setEffect(clickEffect));
                b.setOnMouseReleased(e -> b.setEffect(null));
                b.setOnMouseExited(e -> b.setEffect(null));
            }
        }
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


    @FXML
    private void onProteinChanged() {
        if (proteinGroup.getSelectedToggle() == null || item == null) return;

        RadioButton selected =
                (RadioButton) proteinGroup.getSelectedToggle();

        String protein = selected.getText();

        switch (protein) {
            case "Poulet" -> setProteinImage("poulet");
            case "Boeuf" -> setProteinImage("boeuf");
            case "Tofu" -> setProteinImage("tofu");
            case "Crevette" -> setProteinImage("crevette");
        }
    }

    private void setProteinImage(String protein) {

        // ex : ramen_boeuf.png, ramen_tofu.png...
        String baseName = item.getImageUrl().replace(".png", "");

        String path = "/" + baseName + "_" + protein + ".png";

        var stream = getClass().getResourceAsStream(path);
        if (stream != null) {
            imagePlat.setImage(new Image(stream));
        } else {
            System.err.println("Image protéine introuvable : " + path);
        }
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
        if(Boolean.TRUE.equals(isFrench)){
            if (quantityText != null) quantityText.setText(String.valueOf("Quantit\u00E9 : "+quantity));
        }
        else{
            if (quantityText != null) quantityText.setText(String.valueOf("Quantity : "+quantity));
        }

        if (item != null && affichePrix != null) {
            double total = item.getPrice() * quantity;
            affichePrix.setText(String.format("%.2f \u20AC", total));
        }
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
