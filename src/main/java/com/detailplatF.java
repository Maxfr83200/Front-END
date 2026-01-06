package com;

import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;

public class detailplatF {

    @FXML private ImageView imagePlat;
    @FXML private Text afficheNom;
    @FXML private Text afficheDesc;
    @FXML private Text affichePrix;

    private MenuItem item;
    private boolean isFrench;

    public void setLanguage(boolean isFrench) {
        this.isFrench = isFrench;
    }

    public void setItem(MenuItem item) {
        this.item = item;


        afficheNom.setText(item.getName());
        afficheDesc.setText(item.getDescription());
        affichePrix.setText(String.format("%.2f \u20AC", item.getPrice())); // € en unicode


        String imagePath = item.getImageUrl(); // ex: "images/ramen.png"
        if (imagePath != null && !imagePath.isBlank()) {
            Image img = new Image(getClass().getResourceAsStream("/" + imagePath));
            imagePlat.setImage(img);
        } else {
            imagePlat.setImage(null); // ou une image par défaut
        }
    }
}
