package com;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
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

public class adminF {


    private static final String API_BASE = "http://localhost:8080";
    private final HttpClient http = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private Boolean isFrench;
    private MenuItem selectedItem;
    @FXML
    private VBox menuContainer;
    @FXML
    private TextField searchID;
    @FXML
    private ChoiceBox<String> dispo;
    @FXML
    private TextField newnom;
    @FXML
    private TextArea newdescription;
    @FXML
    private TextField newprix;
    @FXML
    private ScrollPane menuScroll;
    @FXML
    private Label textRecherche;
    @FXML
    private Label textNom;
    @FXML
    private Label textDescription;
    @FXML
    private Label textPrix;
    @FXML
    private Label textDisponibilite;
    @FXML
    private Button btnEnregistrer;
    @FXML
    private Label textTitle;


    @FXML
    private void initialize() {

        dispo.getItems().setAll("Dispo", "Non dispo");
        dispo.setValue("Dispo");


        // chiffres uniquement
        searchID.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                searchID.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        // recherche auto
        searchID.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.isEmpty()) {
                filterAll(null);
            } else {
                filterID();
            }
        });

        Platform.runLater(() -> {
            if (menuScroll != null) {
                menuScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

                var viewport = menuScroll.lookup(".viewport");
                if (viewport != null) viewport.setStyle("-fx-background-color: transparent;");

                var corner = menuScroll.lookup(".corner");
                if (corner != null) corner.setStyle("-fx-background-color: transparent;");
            }
        });
    }


    public void setLanguage(Boolean isFrench) {
        this.isFrench = isFrench;

        if (Boolean.TRUE.equals(isFrench)) {
            textDescription.setText("Description");
            textDisponibilite.setText("Disponibilite");
            textNom.setText("Nom");
            textPrix.setText("Prix");
            textTitle.setText("Interface Admin");
            textRecherche.setText("Recherche ID");
            btnEnregistrer.setText("Enregistrer");

        } else {
            textDescription.setText("Description");
            textDisponibilite.setText("Availability");
            textNom.setText("Name");
            textPrix.setText("Price");
            textTitle.setText("Admin Interface");
            textRecherche.setText("Search ID");
            btnEnregistrer.setText("Save");
        }
        filterAll(null);
    }

    public void retourAcceuil(ActionEvent event) {

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

    public void filterID() {

        String id = searchID.getText();

        if (Boolean.TRUE.equals(isFrench)) {
            fetchAndDisplay(API_BASE + "/fr/menu/" + id);
        } else {
            fetchAndDisplay(API_BASE + "/eng/menu/" + id);
        }

    }


    public void filterAll(ActionEvent e) {
        if (Boolean.TRUE.equals(isFrench)) {
            fetchAndDisplay(API_BASE + "/fr/menu/all");
        } else {
            fetchAndDisplay(API_BASE + "/eng/menu/all");
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

                if (response.statusCode() == 404) {
                    // ID inexistant → liste vide
                    return List.of();
                }

                if (response.statusCode() != 200) {
                    throw new RuntimeException("API error " + response.statusCode() + " : " + response.body());
                }

                String json = response.body();

                JsonElement root = JsonParser.parseString(json);

                if (root.isJsonArray()) {
                    Type listType = new TypeToken<List<MenuItem>>() {
                    }.getType();
                    return gson.fromJson(root, listType);
                } else {
                    MenuItem one = gson.fromJson(root, MenuItem.class);
                    return List.of(one);
                }
            }
        };

        task.setOnSucceeded(ev ->
                Platform.runLater(() -> refreshUI(task.getValue()))
        );

        task.setOnFailed(ev -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
        });

        new Thread(task, "api-fetch").start();
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

        Label l1 = new Label("ID: " + item.getId() + " - " + item.getName() + " - " + item.getPrice() + "0\u20AC");
        Label l2 = new Label(item.getDescription());
        Label l3 = new Label(item.getCalories() + " kcal");


        l1.setStyle("-fx-text-fill: black; -fx-font-size: 18px;");
        l2.setStyle("-fx-text-fill: black;-fx-font-size: 14px;");
        l3.setStyle("-fx-text-fill: black;-fx-font-size: 14px;");

        texts.getChildren().addAll(l1, l2, l3);

        HBox card = new HBox(12, img, texts);

        card.setMinHeight(150);
        card.setPrefHeight(150);
        card.setUserData(item);

        card.setMaxWidth(Double.MAX_VALUE);
        card.setStyle("""
                    -fx-background-color: white;
                    -fx-background-radius: 15;
                    -fx-padding: 15;
                    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 4);
                """);
        card.setOnMouseClicked(e -> {
            MenuItem clicked = (MenuItem) card.getUserData();
            fillForm(clicked);
        });

        card.setCursor(javafx.scene.Cursor.HAND);


        return card;
    }


    private void fillForm(MenuItem item) {
        selectedItem = item;
        newnom.setText(item.getName());
        newdescription.setText(item.getDescription());
        newprix.setText(String.valueOf(item.getPrice()));
        if (Boolean.TRUE.equals(isFrench)) {
            dispo.setValue(item.isAvailable() ? "Dispo" : "Non dispo");
        } else {
            dispo.setValue(item.isAvailable() ? "Available" : "Unavailable");
        }
    }


    @FXML
    private void saveItem(ActionEvent e) {
        if (selectedItem == null) {
            System.out.println("Aucun item sélectionné");
            return;
        }

        String name = newnom.getText().trim();
        String desc = newdescription.getText().trim();
        String priceText = newprix.getText().trim();
        boolean available = "Dispo".equals(dispo.getValue());
        if (Boolean.TRUE.equals(isFrench)) {
            available = "Dispo".equals(dispo.getValue());
        } else {
            available = "Available".equals(dispo.getValue());
        }

        if (name.isEmpty() || desc.isEmpty() || priceText.isEmpty()) {
            System.out.println("Champs manquants");
            return;
        }

        double price;
        try {
            price = Double.parseDouble(priceText);
        } catch (NumberFormatException ex) {
            System.out.println("Prix invalide");
            return;
        }
        int id = selectedItem.getId();
        selectedItem.setName(name);
        selectedItem.setDescription(desc);
        selectedItem.setPrice(price);
        selectedItem.setAvailable(available);

        updateItemOnApi(id, name, desc, price, available);
    }

    static class MenuItemUpdateRequest {
        String name;
        String description;
        double price;
        boolean available;

        MenuItemUpdateRequest(String name, String description, double price, boolean available) {
            this.name = name;
            this.description = description;
            this.price = price;
            this.available = available;
        }
    }

    private void updateItemOnApi(int id, String name, String desc, double price, boolean available) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {


                String url = API_BASE + "/fr/menu/" + id;

                String json = gson.toJson(new MenuItemUpdateRequest(name, desc, price, available));

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .PUT(HttpRequest.BodyPublishers.ofString(json))
                        .build();

                HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() != 204 && response.statusCode() != 200) {
                    throw new RuntimeException("API error " + response.statusCode() + " : " + response.body());
                }
                return null;
            }
        };

        task.setOnSucceeded(ev -> filterAll(null));
        task.setOnFailed(ev -> task.getException().printStackTrace());
        new Thread(task, "api-put-basic").start();
    }

}
