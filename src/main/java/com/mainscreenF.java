package com;

import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;
import javafx.scene.input.MouseEvent;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import java.util.ArrayList;
import javafx.scene.text.Font;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class mainscreenF {

    private Boolean isFrench;

    @FXML private Button btnTous;
    @FXML private Button btnPlats;
    @FXML private Button btnSnacks;
    @FXML private Button btnBoissons;
    @FXML private Button btnDesserts;
    @FXML private Button btnPayer;
    @FXML private Button btnRetour;

    public void setLanguage(Boolean isFrench) {
        this.isFrench = isFrench;

        if (Boolean.TRUE.equals(isFrench)) {
            // Version Française
            if(btnTous != null) btnTous.setText("Tous");
            if(btnPlats != null) btnPlats.setText("Plats");
            if(btnSnacks != null) btnSnacks.setText("Snacks");
            if(btnBoissons != null) btnBoissons.setText("Boissons");
            if(btnPayer != null) btnPayer.setText("Paiement");
            if(btnRetour != null) btnRetour.setText("Retour");
        } else {
            // Version Anglaise
            if(btnTous != null) btnTous.setText("All");
            if(btnPlats != null) btnPlats.setText("Dishes");
            if(btnSnacks != null) btnSnacks.setText("Snacks");
            if(btnBoissons != null) btnBoissons.setText("Drinks");
            if(btnPayer != null) btnPayer.setText("Payement");
            if(btnRetour != null) btnRetour.setText("Return");
        }
        if(btnDesserts != null) btnDesserts.setText("Desserts");
        filterAll(null);
    }

    private static final String API_BASE = "http://localhost:8080";
    private final HttpClient http = HttpClient.newHttpClient();
    private final Gson gson = new Gson();


    @FXML
    private VBox menuContainer;

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
            label.setStyle("-fx-font-size: 18px;");
            cartContainer.getChildren().add(label);
        }

        price.setText(
                String.format("Total : %.2f \u20AC", cart.getTotal())
        );
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



    public void goDetail(MouseEvent event, MenuItem item) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/detailplat.fxml"));
        try {
            Parent root = loader.load();
            detailplatF ctrl = loader.getController();
            ctrl.setLanguage(isFrench);
            ctrl.setItem(item);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
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

    public void retourAccueil(ActionEvent event) {
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

    @FXML
    public void initialize() {
        updateTotalprice();
        refreshCartUI();
    }




    public void filterAll(ActionEvent e) {
        if (Boolean.TRUE.equals(isFrench)){
            fetchAndDisplay(API_BASE + "/fr/menu/all");
        }
        else{
            fetchAndDisplay(API_BASE + "/eng/menu/all");
        }

    }

    public void filterPlats(ActionEvent e) {
        if (Boolean.TRUE.equals(isFrench)){
            fetchAndDisplay(API_BASE + "/fr/menu/plats");
        }
        else{
            fetchAndDisplay(API_BASE + "/eng/menu/plats");
        }
    }

    public void filterSnacks(ActionEvent e) {
        if (Boolean.TRUE.equals(isFrench)){
            fetchAndDisplay(API_BASE + "/fr/menu/snacks");
        }
        else{
            fetchAndDisplay(API_BASE + "/eng/menu/snacks");
        }
    }

    public void filterBoissons(ActionEvent e) {
        if (Boolean.TRUE.equals(isFrench)){
            fetchAndDisplay(API_BASE + "/fr/menu/boissons");
        }
        else{
            fetchAndDisplay(API_BASE + "/eng/menu/boissons");
        }
    }

    public void filterDesserts(ActionEvent e) {
        if (Boolean.TRUE.equals(isFrench)){
            fetchAndDisplay(API_BASE + "/fr/menu/desserts");
        }
        else{
            fetchAndDisplay(API_BASE + "/eng/menu/desserts");
        }
    }


    private void fetchAndDisplay(String url) {
        Task<List<MenuItem>> task = new Task<>() {
            @Override
            protected List<MenuItem> call() throws Exception {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                HttpResponse<String> response =
                        http.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 200) {
                    throw new RuntimeException("API error " + response.statusCode() + " : " + response.body());
                }

                Type listType = new TypeToken<List<MenuItem>>() {}.getType();
                return gson.fromJson(response.body(), listType);
            }
        };

        task.setOnSucceeded(ev ->
                Platform.runLater(() -> refreshUI(task.getValue()))
        );

        task.setOnFailed(ev -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
        });

        new Thread(task, "api-fetch-all").start();
    }

    private void refreshUI(List<MenuItem> items) {
        System.out.println("Nombre d'articles reçus de l'API : " + items.size()); // DEBUG
        menuContainer.getChildren().clear();


        menuContainer.setPrefHeight(VBox.USE_COMPUTED_SIZE);
        menuContainer.setMinHeight(VBox.USE_COMPUTED_SIZE);

        for (MenuItem item : items) {
            menuContainer.getChildren().add(createCard(item));
        }


        System.out.println("Hauteur finale du conteneur : " + menuContainer.getBoundsInParent().getHeight());
    }





    private HBox createCard(MenuItem item) {
        ImageView img = new ImageView();
        img.setFitWidth(120);
        img.setFitHeight(120);
        img.setPreserveRatio(true);

        String imagePath = item.getImageUrl();

        if (imagePath != null && !imagePath.isBlank()) {

            var inputStream = getClass().getResourceAsStream("/" + imagePath);
            if (inputStream != null) {
                Image image = new Image(inputStream);
                img.setImage(image);
            } else {
                System.err.println("Image introuvable : " + imagePath);
            }
        }

        VBox texts = new VBox(4);
        texts.getChildren().addAll(
                new Label(item.getName() + " - " + item.getPrice() + "0\u20AC"),
                new Label(item.getDescription()),
                new Label(item.getCalories() + " kcal")
        );

        HBox card = new HBox(12, img, texts);

        card.setMinHeight(150);
        card.setPrefHeight(150);

        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-radius: 8; -fx-background-radius: 8;");
        card.setOnMouseClicked(e -> goDetail(e, item));



        return card;
    }
}