package com;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

public class mainscreenF {

    // =========================
    // Constantes
    // =========================
    private static final String API_BASE = "http://localhost:8080";

    private static final String ENDPOINT_FR_PREFIX = "/fr/menu";
    private static final String ENDPOINT_EN_PREFIX = "/eng/menu";

    private static final String ACCUEIL_FXML = "/com/accueil.fxml";
    private static final String DETAIL_FXML = "/com/detailplat.fxml";
    private static final String PANIER_FXML = "/com/panier.fxml";

    private static final String ICON_SPICY = "/com/images/spicy.png";
    private static final String ICON_VEGE = "/com/images/vege.png";

    // =========================
    // Dépendances
    // =========================
    private final HttpClient http = HttpClient.newHttpClient();
    private final Gson gson = new Gson();
    private final CartModel cart = CartModel.getInstance();

    // =========================
    // État
    // =========================
    private boolean isFrench = true;

    private Font normalFont;
    private Font activeFont;

    // =========================
    // Composants FXML
    // =========================
    @FXML private VBox menuBox;

    @FXML private Button btnTous;
    @FXML private Button btnPlats;
    @FXML private Button btnSnacks;
    @FXML private Button btnBoissons;
    @FXML private Button btnDesserts;
    @FXML private Button btnPayer;
    @FXML private Button btnRetour;

    @FXML private VBox menuContainer;
    @FXML private VBox cartContainer;

    @FXML private Label price;

    @FXML private ScrollPane menuScroll;

    // =========================
    // Langue
    // =========================

    /**
     * Définit la langue de l'interface (labels de boutons) puis charge la liste complète.
     *
     * @param isFrench true = français, false = anglais
     */
    public void setLanguage(Boolean isFrench) {
        this.isFrench = Boolean.TRUE.equals(isFrench);

        applyTranslations();
        setActiveButton(btnTous);
        filterAll(null);
    }

    /**
     * Applique les traductions sur les boutons en fonction de la langue.
     */
    private void applyTranslations() {
        if (this.isFrench) {
            if (btnTous != null) btnTous.setText("Tout");
            if (btnPlats != null) btnPlats.setText("Plats");
            if (btnSnacks != null) btnSnacks.setText("Snacks");
            if (btnBoissons != null) btnBoissons.setText("Boissons");
            if (btnPayer != null) btnPayer.setText("Panier");
            if (btnRetour != null) btnRetour.setText("Retour");
        } else {
            if (btnTous != null) btnTous.setText("All");
            if (btnPlats != null) btnPlats.setText("Dishes");
            if (btnSnacks != null) btnSnacks.setText("Snacks");
            if (btnBoissons != null) btnBoissons.setText("Drinks");
            if (btnPayer != null) btnPayer.setText("Cart");
            if (btnRetour != null) btnRetour.setText("Return");
        }

        if (btnDesserts != null) btnDesserts.setText("Desserts");
    }

    // =========================
    // Initialisation JavaFX
    // =========================

    /**
     * Initialise le contrôleur :
     * - initialise les polices (normal / actif),
     * - rafraîchit l'UI panier + total,
     * - rend le ScrollPane transparent,
     * - ajoute un effet de clic sur le bouton "Panier".
     */
    @FXML
    public void initialize() {
        normalFont = Font.font("System", 33);
        activeFont = Font.font("Broadway", FontWeight.NORMAL, 35);

        updateTotalPrice();
        refreshCartUI();

        makeScrollPaneTransparent(menuScroll);
        setupButtonPressEffect(btnPayer);
    }

    /**
     * Rend visuellement un ScrollPane transparent (fond + viewport + corner).
     *
     * @param scroll ScrollPane à styliser
     */
    private void makeScrollPaneTransparent(ScrollPane scroll) {
        if (scroll == null) return;

        Platform.runLater(() -> {
            scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

            var viewport = scroll.lookup(".viewport");
            if (viewport != null) viewport.setStyle("-fx-background-color: transparent;");

            var corner = scroll.lookup(".corner");
            if (corner != null) corner.setStyle("-fx-background-color: transparent;");
        });
    }

    /**
     * Ajoute un effet "InnerShadow" pendant l'appui sur un bouton.
     *
     * @param button bouton à décorer
     */
    private void setupButtonPressEffect(Button button) {
        if (button == null) return;

        InnerShadow clickEffect = new InnerShadow();
        clickEffect.setRadius(10.0);
        clickEffect.setOffsetX(3.0);
        clickEffect.setOffsetY(3.0);
        clickEffect.setColor(Color.rgb(0, 0, 0, 0.6));

        button.setOnMousePressed(ev -> button.setEffect(clickEffect));
        button.setOnMouseReleased(ev -> button.setEffect(null));
        button.setOnMouseExited(ev -> button.setEffect(null));
    }

    // =========================
    // UI Panier
    // =========================

    /**
     * Met à jour l'affichage du total panier.
     */
    private void updateTotalPrice() {
        if (price != null) {
            price.setText(String.format("Total : %.2f \u20AC", cart.getTotal()));
        }
    }

    /**
     * Rafraîchit la liste d'items affichée dans le panier (colonne de droite).
     */
    private void refreshCartUI() {
        if (cartContainer == null) return;

        cartContainer.getChildren().clear();

        for (String key : cart.getQuantities().keySet()) {
            MenuItem item = cart.getItem(key);
            Integer qty = cart.getQuantities().get(key);
            String protein = cart.getProteinFromKey(key);

            if (item == null || qty == null) continue;

            String line = qty + " x " + item.getName();
            if (protein != null) line += " (" + protein + ")";
            line += " - " + String.format("%.2f \u20AC", item.getPrice() * qty);

            Label label = new Label(line);
            label.setStyle("-fx-font-size: 18px; -fx-text-fill: #4a3c3c;");
            cartContainer.getChildren().add(label);
        }

        updateTotalPrice();
    }

    // =========================
    // Navigation
    // =========================

    /**
     * Ouvre l'écran détail d'un item (detailplat.fxml) en transmettant la langue et l'item.
     *
     * @param event événement clic sur une carte menu
     * @param item  item sélectionné
     */
    public void goDetail(javafx.scene.input.MouseEvent event, MenuItem item) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(DETAIL_FXML));

        try {
            Parent root = loader.load();
            detailplatF ctrl = loader.getController();
            ctrl.setLanguage(isFrench);
            ctrl.setItem(item);

            Stage stage = getStageFromEvent(event);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ouvre l'écran panier (panier.fxml) en transmettant la langue.
     *
     * @param event événement du bouton panier
     */
    public void goPanier(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(PANIER_FXML));

        try {
            Parent root = loader.load();
            panierF ctrl = loader.getController();
            ctrl.setLanguage(isFrench);

            Stage stage = getStageFromEvent(event);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retourne à l'accueil et vide le panier.
     *
     * @param event événement du bouton retour
     */
    public void retourAccueil(ActionEvent event) {
        cart.clear();

        FXMLLoader loader = new FXMLLoader(getClass().getResource(ACCUEIL_FXML));

        try {
            Parent root = loader.load();
            Stage stage = getStageFromEvent(event);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Récupère le Stage (fenêtre) à partir d'un événement JavaFX.
     *
     * @param event événement JavaFX
     * @return Stage courant
     */
    private Stage getStageFromEvent(Object event) {
        if (event instanceof ActionEvent ae) {
            return (Stage) ((Node) ae.getSource()).getScene().getWindow();
        }
        if (event instanceof javafx.scene.input.MouseEvent me) {
            return (Stage) ((Node) me.getSource()).getScene().getWindow();
        }
        throw new IllegalArgumentException("Unsupported event type: " + event.getClass());
    }

    // =========================
    // Menu : boutons actifs
    // =========================

    /**
     * Handler générique de clic sur un bouton menu (met simplement la police active).
     *
     * @param event événement du clic
     */
    @FXML
    private void onMenuClick(ActionEvent event) {
        setActiveButton((Button) event.getSource());
    }

    /**
     * Met en évidence le bouton sélectionné en changeant sa police.
     *
     * @param clicked bouton cliqué
     */
    private void setActiveButton(Button clicked) {
        if (menuBox == null) return;

        for (Node node : menuBox.getChildren()) {
            if (node instanceof Button btn) {
                btn.setFont(normalFont);
            }
        }

        if (clicked != null) {
            clicked.setFont(activeFont);
        }
    }

    // =========================
    // Filtres (API)
    // =========================

    /**
     * Charge tous les items.
     *
     * @param e événement (peut être null)
     */
    public void filterAll(ActionEvent e) {
        filterByCategory("all", e);
    }

    /**
     * Charge la catégorie "plats".
     *
     * @param e événement (peut être null)
     */
    public void filterPlats(ActionEvent e) {
        filterByCategory("plats", e);
    }

    /**
     * Charge la catégorie "snacks".
     *
     * @param e événement (peut être null)
     */
    public void filterSnacks(ActionEvent e) {
        filterByCategory("snacks", e);
    }

    /**
     * Charge la catégorie "boissons".
     *
     * @param e événement (peut être null)
     */
    public void filterBoissons(ActionEvent e) {
        filterByCategory("boissons", e);
    }

    /**
     * Charge la catégorie "desserts".
     *
     * @param e événement (peut être null)
     */
    public void filterDesserts(ActionEvent e) {
        filterByCategory("desserts", e);
    }

    /**
     * Lance un appel API pour une catégorie donnée et met à jour le bouton actif si besoin.
     *
     * @param category slug de catégorie côté API
     * @param e        événement du clic (peut être null)
     */
    private void filterByCategory(String category, ActionEvent e) {
        if (e != null) setActiveButton((Button) e.getSource());
        fetchAndDisplay(buildMenuUrl("/" + category));
    }

    /**
     * Construit l'URL complète vers l'API en fonction de la langue.
     *
     * @param suffix suffix endpoint (ex: "/all", "/plats"...)
     * @return URL complète
     */
    private String buildMenuUrl(String suffix) {
        String prefix = isFrench ? ENDPOINT_FR_PREFIX : ENDPOINT_EN_PREFIX;
        return API_BASE + prefix + suffix;
    }

    /**
     * Appelle l'API (GET) pour récupérer une liste d'items, puis rafraîchit l'UI menu.
     *
     * @param url URL complète
     */
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

        task.setOnSucceeded(ev -> refreshMenuUI(task.getValue()));
        task.setOnFailed(ev -> task.getException().printStackTrace());

        new Thread(task, "api-fetch-menu").start();
    }

    /**
     * Rafraîchit la liste des cartes menu affichées dans le ScrollPane.
     *
     * @param items items reçus depuis l'API
     */
    private void refreshMenuUI(List<MenuItem> items) {
        if (menuContainer == null) return;

        menuContainer.getChildren().clear();
        menuContainer.setPrefHeight(VBox.USE_COMPUTED_SIZE);
        menuContainer.setMinHeight(VBox.USE_COMPUTED_SIZE);

        for (MenuItem item : items) {
            menuContainer.getChildren().add(createMenuCard(item));
        }
    }

    // =========================
    // Cards menu
    // =========================

    /**
     * Crée une carte pour un item du menu (image, infos, icônes, bouton ajout rapide).
     * Si l'item n'est pas disponible, la carte est grisée et désactivée.
     *
     * @param item item à afficher
     * @return HBox carte prête à être ajoutée à la liste
     */
    private HBox createMenuCard(MenuItem item) {
        ImageView img = buildItemImageView(item);

        VBox texts = new VBox(4);
        HBox.setHgrow(texts, Priority.ALWAYS);

        Label namePrice = new Label(item.getName() + " - " + String.format("%.2f \u20AC", item.getPrice()));
        namePrice.setStyle("-fx-text-fill: black; -fx-font-size: 18px;");

        HBox titleLine = new HBox(6);
        titleLine.setAlignment(Pos.CENTER_LEFT);
        titleLine.getChildren().add(namePrice);

        addIconIf(titleLine, item.isSpicy(), ICON_SPICY);
        addIconIf(titleLine, item.isVegetarian(), ICON_VEGE);

        Label desc = new Label(item.getDescription());
        desc.setStyle("-fx-text-fill: black; -fx-font-size: 14px;");
        desc.setWrapText(true);

        Label calories = new Label(item.getCalories() + " kcal");
        calories.setStyle("-fx-text-fill: black; -fx-font-size: 14px;");

        texts.getChildren().addAll(titleLine, desc, calories);

        if (!item.isAvailable()) {
            Label unavailable = new Label(isFrench ? "Victime de son succès" : "Victim of its success");
            unavailable.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
            texts.getChildren().add(unavailable);
        }

        Button btnQuickAdd = new Button(isFrench ? "Ajouter" : "Add");
        btnQuickAdd.setStyle("-fx-background-color: #60834E; -fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-min-width: 200; -fx-min-height: 40;");
        setupQuickAdd(btnQuickAdd, item);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox card = new HBox(12, img, texts, spacer, btnQuickAdd);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setMinHeight(150);
        card.setPrefHeight(150);
        card.setMaxWidth(Double.MAX_VALUE);

        String baseStyle = """
                -fx-background-color: white;
                -fx-background-radius: 15;
                -fx-padding: 15;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 4);
                """;
        card.setStyle(baseStyle);

        if (!item.isAvailable()) {
            card.setDisable(true);
            card.setOpacity(0.6);
            card.setStyle(baseStyle + "-fx-background-color: #f5f5f5;");
        } else {
            card.setOnMouseClicked(e -> goDetail(e, item));
            card.setCursor(Cursor.HAND);
        }

        return card;
    }

    /**
     * Crée et configure l'image d'un item en chargeant sa ressource depuis le classpath.
     *
     * @param item item dont on veut l'image
     * @return ImageView configurée
     */
    private ImageView buildItemImageView(MenuItem item) {
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

        return img;
    }

    /**
     * Ajoute une icône dans une ligne de titre si la condition est vraie.
     *
     * @param container HBox où ajouter l'icône
     * @param condition condition d'affichage
     * @param iconPath  chemin de l'icône dans les ressources
     */
    private void addIconIf(HBox container, boolean condition, String iconPath) {
        if (!condition) return;

        var is = getClass().getResourceAsStream(iconPath);
        if (is == null) return;

        ImageView icon = new ImageView(new Image(is));
        icon.setFitWidth(30);
        icon.setFitHeight(30);
        icon.setPreserveRatio(true);
        container.getChildren().add(icon);
    }

    /**
     * Configure le bouton "ajout rapide" : ajoute 1 item au panier et rafraîchit le panier + total.
     *
     * @param btn  bouton "Ajouter/Add"
     * @param item item à ajouter
     */
    private void setupQuickAdd(Button btn, MenuItem item) {
        if (btn == null) return;

        // Effet de clic (comme ailleurs)
        InnerShadow clickEffect = new InnerShadow();
        clickEffect.setRadius(8.0);
        clickEffect.setOffsetX(2.0);
        clickEffect.setOffsetY(2.0);
        clickEffect.setColor(Color.rgb(0, 0, 0, 0.6));

        btn.setOnMousePressed(ev -> btn.setEffect(clickEffect));
        btn.setOnMouseReleased(ev -> btn.setEffect(null));
        btn.setOnMouseExited(ev -> btn.setEffect(null));

        // Action ajout rapide
        btn.setOnAction(event -> {
            event.consume();

            String protein = null;
            if (item.isProteinRequired()) {
                // choix par défaut (comme ton code initial)
                protein = isFrench ? "Boeuf" : "Beef";
            }

            cart.addItem(item, protein, 1);
            refreshCartUI();
            updateTotalPrice();
        });
    }
}
