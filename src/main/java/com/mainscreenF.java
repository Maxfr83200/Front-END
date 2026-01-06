package com;

import javafx.stage.Stage;

import java.io.IOException;

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

import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class mainscreenF {

    private Boolean isFrench;

    public void setLanguage(Boolean isFrench) {
        this.isFrench = isFrench;
    }

    private static final String API_BASE = "http://localhost:8080";
    private final HttpClient http = HttpClient.newHttpClient();
    private final Gson gson = new Gson();



    public void goConfirme(ActionEvent event) {
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
        filterAll(null);
    }


    @FXML
    private VBox menuContainer;

    public void filterAll(ActionEvent e) {
        fetchAndDisplay(API_BASE + "/menu");
    }

    public void filterPlats(ActionEvent e) {
        fetchAndDisplay(API_BASE + "/menu/plats");
    }

    public void filterSnacks(ActionEvent e) {
        fetchAndDisplay(API_BASE + "/menu/snacks");
    }

    public void filterBoissons(ActionEvent e) {
        fetchAndDisplay(API_BASE + "/menu/boissons");
    }

    public void filterDesserts(ActionEvent e) {
        fetchAndDisplay(API_BASE + "/menu/desserts");
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
        menuContainer.getChildren().clear();

        for (MenuItem item : items) {
            menuContainer.getChildren().add(createCard(item));
        }
    }

    private HBox createCard(MenuItem item) {
        ImageView img = new ImageView();
        img.setFitWidth(120);
        img.setFitHeight(120);
        img.setPreserveRatio(true);

        String imagePath = item.getImageUrl(); // ex: "images/ramen.png"

        if (imagePath != null && !imagePath.isBlank()) {
            Image image = new Image(
                    getClass().getResourceAsStream("/" + imagePath)
            );
            img.setImage(image);
        }

        VBox texts = new VBox(4);
        texts.getChildren().addAll(
                new Label(item.getName() + " - " + item.getPrice() + "euros"),
                new Label(item.getDescription()),
                new Label(item.getCalories() + " kcal")
        );

        HBox card = new HBox(12, img, texts);
        card.setStyle("-fx-padding: 10; -fx-border-color: #ddd; -fx-border-radius: 8; -fx-background-radius: 8;");
        return card;
    }
}