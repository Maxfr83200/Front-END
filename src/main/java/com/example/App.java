package com.example;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {

        stage.setTitle("Borne de Commande - Cacaya");
        stage.setWidth(1024);
        stage.setHeight(768);
        stage.setFullScreen(false);
        Scene scene = createHomeScene();
        stage.setScene(scene);
        stage.show();
    }

    private Scene createHomeScene() {
        VBox root = new VBox();
        root.setStyle("-fx-background-color: #f5f5f5;");
        root.setAlignment(Pos.CENTER);
        root.setSpacing(30);
        root.setPadding(new javafx.geometry.Insets(20));

        // logo
        VBox logoSection = createLogoSection();
        VBox.setVgrow(logoSection, Priority.SOMETIMES);

        // text au centre
        VBox textSection = createTextSection();
        VBox.setVgrow(textSection, Priority.ALWAYS);

        // bouton en bas
        HBox buttonSection = createButtonSection();
        VBox.setVgrow(buttonSection, Priority.SOMETIMES);

        root.getChildren().addAll(logoSection, textSection, buttonSection);

        return new Scene(root, 1024, 768);
    }

    private VBox createLogoSection() {
        VBox logoBox = new VBox();
        logoBox.setAlignment(Pos.TOP_CENTER);
        logoBox.setPrefHeight(150);


        Label logoLabel = new Label("Pitacaca ou Cacaya");
        logoLabel.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        logoLabel.setStyle("-fx-text-fill: #e74c3c;");

        // pour image si jamais
        try {
            Image logoImage = new Image(getClass().getResource("/logo.png").toString());
            ImageView logoView = new ImageView(logoImage);
            logoView.setPreserveRatio(true);
            logoView.setFitHeight(120);
            logoBox.getChildren().add(logoView);
        } catch (Exception e) {
            logoBox.getChildren().add(logoLabel);
        }

        return logoBox;
    }

    private VBox createTextSection() {
        VBox textBox = new VBox();
        textBox.setAlignment(Pos.CENTER);

        Label touchLabel = new Label("Toucher pour commencer");
        touchLabel.setFont(Font.font("Arial", FontWeight.BOLD, 72));
        touchLabel.setStyle("-fx-text-fill: #2c3e50;");
        textBox.getChildren().add(touchLabel);

        return textBox;
    }

    private HBox createButtonSection() {
        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(40);
        buttonBox.setPrefHeight(100);

        // Bouton FR
        Button btnFr = createStyledButton("FR", "blue");
        btnFr.setOnAction(e -> handleLanguageSelection("Français"));

        // Bouton EN
        Button btnEn = createStyledButton("EN", "white");
        btnEn.setStyle(btnEn.getStyle() + "-fx-text-fill: black ;");
        btnEn.setOnAction(e -> handleLanguageSelection("English"));

        // Bouton AIDE
        Button btnAide = createStyledButton("AIDE", "red");
        btnAide.setOnAction(e -> handleAideSelection());

        buttonBox.getChildren().addAll(btnFr, btnEn, btnAide);

        return buttonBox;
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setPrefSize(120, 80);
        button.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        button.setStyle(
                "-fx-background-color: " + color + ";" +
                "-fx-text-fill: white;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 10px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);" +
                "-fx-cursor: hand;"
        );

        // Effet au survol
        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: " + lightenColor(color) + ";" +
                "-fx-text-fill: white;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 10px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 8, 0, 0, 4);" +
                "-fx-cursor: hand;" +
                "-fx-font-size: 26;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: " + color + ";" +
                "-fx-text-fill: white;" +
                "-fx-border-radius: 10;" +
                "-fx-background-radius: 10;" +
                "-fx-padding: 10px;" +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0, 0, 2);" +
                "-fx-cursor: hand;"
        ));

        return button;
    }

    private String lightenColor(String color) {
        // Fonction simple pour éclaircir les couleurs
        switch (color) {
            case "#3498db": return "#5dade2";  // Bleu plus clair
            case "#2ecc71": return "#58d68d";  // Vert plus clair
            case "#f39c12": return "#f8b739";  // Orange plus clair
            default: return color;
        }
    }

    private void handleLanguageSelection(String language) {
        System.out.println("Langue sélectionnée : " + language);
        // À implémenter : charger la page de commande dans la langue sélectionnée
    }

    private void handleAideSelection() {
        System.out.println("Aide demandée");
        // À implémenter : afficher l'écran d'aide
    }

    public static void main(String[] args) {
        launch(args);
    }
}
