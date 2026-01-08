package com;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.text.FontWeight;
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
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;

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

    @FXML
    private VBox menuBox;
    @FXML
    private Button btnTous;
    @FXML
    private Button btnPlats;
    @FXML
    private Button btnSnacks;
    @FXML
    private Button btnBoissons;
    @FXML
    private Button btnDesserts;
    @FXML
    private Button btnPayer;
    @FXML
    private Button btnRetour;


    private Font normalFont;
    private Font activeFont;

    public void setLanguage(Boolean isFrench) {
        this.isFrench = isFrench;

        if (Boolean.TRUE.equals(isFrench)) {
            // Version Française
            if (btnTous != null) btnTous.setText("Tout");
            if (btnPlats != null) btnPlats.setText("Plats");
            if (btnSnacks != null) btnSnacks.setText("Snacks");
            if (btnBoissons != null) btnBoissons.setText("Boissons");
            if (btnPayer != null) btnPayer.setText("Panier");
            if (btnRetour != null) btnRetour.setText("Retour");
        } else {
            // Version Anglaise
            if (btnTous != null) btnTous.setText("All");
            if (btnPlats != null) btnPlats.setText("Dishes");
            if (btnSnacks != null) btnSnacks.setText("Snacks");
            if (btnBoissons != null) btnBoissons.setText("Drinks");
            if (btnPayer != null) btnPayer.setText("Cart");
            if (btnRetour != null) btnRetour.setText("Return");
        }
        if (btnDesserts != null) btnDesserts.setText("Desserts");

        setActiveButton(btnTous);
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

    @FXML
    private ScrollPane menuScroll;


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

    @FXML
    private void onMenuClick(ActionEvent event) {
        setActiveButton((Button) event.getSource());
    }

    private void setActiveButton(Button clicked) {
        for (Node node : menuBox.getChildren()) {
            if (node instanceof Button btn) {
                btn.setFont(normalFont);
            }
        }
        if (clicked != null) {
            clicked.setFont(activeFont);
        }
    }

    @FXML
    public void initialize() {

        normalFont = Font.font("System", 33);
        activeFont = Font.font("Broadway", FontWeight.NORMAL, 35);


        updateTotalprice();
        refreshCartUI();


        Platform.runLater(() -> {
            if (menuScroll != null) {
                menuScroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

                var viewport = menuScroll.lookup(".viewport");
                if (viewport != null) viewport.setStyle("-fx-background-color: transparent;");

                var corner = menuScroll.lookup(".corner");
                if (corner != null) corner.setStyle("-fx-background-color: transparent;");
            }
        });

        InnerShadow clickEffect = new InnerShadow();
        clickEffect.setRadius(10.0);
        clickEffect.setOffsetX(3.0);
        clickEffect.setOffsetY(3.0);
        clickEffect.setColor(Color.rgb(0, 0, 0, 0.6));

        if (btnPayer != null) {
            btnPayer.setOnMousePressed(event -> {
                btnPayer.setEffect(clickEffect);
            });

            btnPayer.setOnMouseReleased(event -> {
                btnPayer.setEffect(null);
            });
        }
    }


    public void filterAll(ActionEvent e) {
        if (e != null) setActiveButton((Button) e.getSource());
        if (Boolean.TRUE.equals(isFrench)) {
            fetchAndDisplay(API_BASE + "/fr/menu/all");
        } else {
            fetchAndDisplay(API_BASE + "/eng/menu/all");
        }

    }

    public void filterPlats(ActionEvent e) {
        if (e != null) setActiveButton((Button) e.getSource());
        if (Boolean.TRUE.equals(isFrench)) {
            fetchAndDisplay(API_BASE + "/fr/menu/plats");
        } else {
            fetchAndDisplay(API_BASE + "/eng/menu/plats");
        }
    }

    public void filterSnacks(ActionEvent e) {
        if (e != null) setActiveButton((Button) e.getSource());
        if (Boolean.TRUE.equals(isFrench)) {
            fetchAndDisplay(API_BASE + "/fr/menu/snacks");
        } else {
            fetchAndDisplay(API_BASE + "/eng/menu/snacks");
        }
    }

    public void filterBoissons(ActionEvent e) {
        if (e != null) setActiveButton((Button) e.getSource());
        if (Boolean.TRUE.equals(isFrench)) {
            fetchAndDisplay(API_BASE + "/fr/menu/boissons");
        } else {
            fetchAndDisplay(API_BASE + "/eng/menu/boissons");
        }
    }

    public void filterDesserts(ActionEvent e) {
        if (e != null) setActiveButton((Button) e.getSource());
        if (Boolean.TRUE.equals(isFrench)) {
            fetchAndDisplay(API_BASE + "/fr/menu/desserts");
        } else {
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

                Type listType = new TypeToken<List<MenuItem>>() {
                }.getType();
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
                img.setImage(new Image(inputStream));
            } else {
                System.err.println("Image introuvable : " + imagePath);
            }
        }

        VBox texts = new VBox(4);
        HBox.setHgrow(texts, Priority.ALWAYS);

        Label namePrice = new Label(item.getName() + " - " + item.getPrice() + "0\u20AC");
        namePrice.setStyle("-fx-text-fill: black; -fx-font-size: 18px;");

        HBox titleLine = new HBox(6);
        titleLine.setAlignment(Pos.CENTER_LEFT);
        titleLine.getChildren().add(namePrice);

        if (item.isSpicy()) {
            var is = getClass().getResourceAsStream("/com/images/spicy.png");
            if (is != null) {
                ImageView spicyIcon = new ImageView(new Image(is));
                spicyIcon.setFitWidth(30);
                spicyIcon.setFitHeight(30);
                spicyIcon.setPreserveRatio(true);
                titleLine.getChildren().add(spicyIcon);
            }
        }

        if (item.isVegetarian()) {
            var is = getClass().getResourceAsStream("/com/images/vege.png");
            if (is != null) {
                ImageView vegeIcon = new ImageView(new Image(is));
                vegeIcon.setFitWidth(30);
                vegeIcon.setFitHeight(30);
                vegeIcon.setPreserveRatio(true);
                titleLine.getChildren().add(vegeIcon);
            }
        }

        Label desc = new Label(item.getDescription());
        desc.setStyle("-fx-text-fill: black; -fx-font-size: 14px;");
        desc.setWrapText(true);

        Label calories = new Label(item.getCalories() + " kcal");
        calories.setStyle("-fx-text-fill: black; -fx-font-size: 14px;");

        texts.getChildren().addAll(titleLine, desc, calories);

        if (!item.isAvailable()) {
            Label unavailable;
            if (Boolean.TRUE.equals(isFrench)) {
                unavailable = new Label("Victime de son succ\u00E8s");
            } else {
                unavailable = new Label("Victim of its success");
            }
            unavailable.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            texts.getChildren().add(unavailable);
        }

        Button btnQuickAdd = new Button("Ajouter");
        btnQuickAdd.setStyle("-fx-background-color: #60834E; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-min-width: 200; -fx-min-height: 40;");

        // Action du bouton
        btnQuickAdd.setOnAction(event -> {
            event.consume();

            String protein = null;
            if (item.isProteinRequired()) {
                protein = Boolean.TRUE.equals(isFrench) ? "Boeuf" : "Beef";
            }

            CartModel.getInstance().addItem(item, protein, 1);

            refreshCartUI();
            updateTotalprice();
        });

        InnerShadow clickEffect = new InnerShadow();
        clickEffect.setRadius(8.0);
        clickEffect.setOffsetX(2.0);
        clickEffect.setOffsetY(2.0);
        clickEffect.setColor(Color.rgb(0, 0, 0, 0.6));

        btnQuickAdd.setOnMousePressed(event -> {
            btnQuickAdd.setEffect(clickEffect);
        });

        btnQuickAdd.setOnMouseReleased(event -> {
            btnQuickAdd.setEffect(null);
        });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox card = new HBox(12, img, texts, spacer, btnQuickAdd);

        card.setAlignment(Pos.CENTER_LEFT);
        card.setMinHeight(150);
        card.setPrefHeight(150);
        card.setMaxWidth(Double.MAX_VALUE);

        card.setStyle("""
                    -fx-background-color: white;
                    -fx-background-radius: 15;
                    -fx-padding: 15;
                    -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 4);
                """);

        if (!item.isAvailable()) {
            card.setDisable(true);
            card.setOpacity(0.6);
            card.setStyle(card.getStyle() + "-fx-background-color: #f5f5f5;");
        } else {
            card.setOnMouseClicked(e -> goDetail(e, item));
            card.setCursor(javafx.scene.Cursor.HAND);
        }

        return card;
    }

}