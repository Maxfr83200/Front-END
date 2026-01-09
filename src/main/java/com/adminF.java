package com;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class adminF {

    // =========================
    // Constantes
    // =========================
    private static final String API_BASE = "http://localhost:8080";
    private static final String ACCUEIL_FXML = "/com/accueil.fxml";

    private static final String ENDPOINT_FR_PREFIX = "/fr/menu";
    private static final String ENDPOINT_EN_PREFIX = "/eng/menu";

    private static final String FR_AVAILABLE = "Dispo";
    private static final String FR_UNAVAILABLE = "Non dispo";
    private static final String EN_AVAILABLE = "Available";
    private static final String EN_UNAVAILABLE = "Unavailable";



    // =========================
    // Dépendances
    // =========================
    private final HttpClient http = HttpClient.newHttpClient();
    private final Gson gson = new Gson();



    // =========================
    // État
    // =========================
    private boolean isFrench = true;
    private MenuItem selectedItem;



    // =========================
    // Composants FXML
    // =========================
    @FXML private VBox menuContainer;

    @FXML private TextField searchID;

    @FXML private ChoiceBox<String> dispo;
    @FXML private TextField newnom;
    @FXML private TextArea newdescription;
    @FXML private TextField newprix;

    @FXML private ScrollPane menuScroll;

    @FXML private Label textRecherche;
    @FXML private Label textNom;
    @FXML private Label textDescription;
    @FXML private Label textPrix;
    @FXML private Label textDisponibilite;
    @FXML private Label textTitle;

    @FXML private Button btnEnregistrer;



    // =========================
    // Langue
    // =========================

    /**
     * Définit la langue de l'interface, met à jour les textes + la ChoiceBox de disponibilité,
     * puis recharge la liste via l'API.
     *
     * @param isFrench true = français, false = anglais
     */
    public void setLanguage(Boolean isFrench) {
        this.isFrench = Boolean.TRUE.equals(isFrench);

        applyTranslations();
        setupAvailabilityChoices();

        filterAll(null);
    }

    /**
     * Applique les traductions sur les labels et boutons en fonction de la langue.
     */
    private void applyTranslations() {
        if (isFrench) {
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
    }

    /**
     * Configure les valeurs de la ChoiceBox "dispo" selon la langue et conserve la sélection
     * si un item est déjà sélectionné.
     */
    private void setupAvailabilityChoices() {
        if (dispo == null) return;

        if (isFrench) {
            dispo.getItems().setAll(FR_AVAILABLE, FR_UNAVAILABLE);
            dispo.setValue(selectedItem != null && selectedItem.isAvailable() ? FR_AVAILABLE : FR_AVAILABLE);
        } else {
            dispo.getItems().setAll(EN_AVAILABLE, EN_UNAVAILABLE);
            dispo.setValue(selectedItem != null && selectedItem.isAvailable() ? EN_AVAILABLE : EN_AVAILABLE);
        }

        if (selectedItem != null) {
            dispo.setValue(getAvailabilityLabel(selectedItem.isAvailable()));
        }
    }



    // =========================
    // Initialisation JavaFX
    // =========================

    /**
     * Initialise le contrôleur après l'injection FXML :
     * - force le champ searchID à n'accepter que des chiffres,
     * - lance une recherche auto (vide => all, sinon => by id),
     * - rend le ScrollPane transparent,
     * - ajoute un effet de clic au bouton enregistrer.
     */
    @FXML
    private void initialize() {
        enforceDigitsOnly(searchID);
        setupAutoSearch(searchID);
        makeScrollPaneTransparent(menuScroll);
        setupButtonPressEffect(btnEnregistrer);
    }




    // =========================
    // Fonction Front-End
    // =========================
    /**
     * Force un TextField à n'accepter que des chiffres.
     *
     * @param field champ à restreindre
     */
    private void enforceDigitsOnly(TextField field) {
        if (field == null) return;

        field.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                field.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    /**
     * Configure la recherche automatique :
     * - champ vide => affiche tout
     * - sinon => filtre par ID.
     *
     * @param field champ de recherche
     */
    private void setupAutoSearch(TextField field) {
        if (field == null) return;

        field.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.isEmpty()) {
                filterAll(null);
            } else {
                filterID();
            }
        });
    }

    /**
     * Rend visuellement un ScrollPane transparent (fond + viewport + corner),
     * en utilisant Platform.runLater pour s'assurer que le CSS lookup fonctionne.
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
     * Ajoute un effet "InnerShadow" pendant l'appui (mouse pressed) sur un bouton.
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
    // Navigation
    // =========================

    /**
     * Retourne à l'écran d'accueil.
     *
     * @param event événement déclenché par le bouton retour
     */
    public void retourAcceuil(ActionEvent event) {
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




    // =========================
    // Filtrage / Chargement API
    // =========================

    /**
     * Filtre l'affichage par ID en appelant l'API correspondante (FR/EN) et rafraîchit l'UI.
     */
    public void filterID() {
        String id = searchID.getText();
        fetchAndDisplay(buildMenuUrl("/" + id));
    }

    /**
     * Charge tous les items depuis l'API (FR/EN) et rafraîchit l'UI.
     *
     * @param e événement action (peut être null)
     */
    public void filterAll(ActionEvent e) {
        fetchAndDisplay(buildMenuUrl("/all"));
    }

    /**
     * Construit l'URL complète vers l'API menu en fonction de la langue.
     *
     * @param suffix suffix du endpoint (ex: "/all" ou "/{id}")
     * @return URL complète
     */
    private String buildMenuUrl(String suffix) {
        String prefix = isFrench ? ENDPOINT_FR_PREFIX : ENDPOINT_EN_PREFIX;
        return API_BASE + prefix + suffix;
    }

    /**
     * Lance une requête HTTP GET vers l'API, parse le JSON reçu (objet ou tableau),
     * puis met à jour l'interface avec la liste d'items.
     *
     * @param url URL complète à appeler
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

                if (response.statusCode() == 404) {
                    // ID inexistant -> liste vide
                    return List.of();
                }

                if (response.statusCode() != 200) {
                    throw new RuntimeException("API error " + response.statusCode() + " : " + response.body());
                }

                JsonElement root = JsonParser.parseString(response.body());

                if (root.isJsonArray()) {
                    Type listType = new TypeToken<List<MenuItem>>() {}.getType();
                    return gson.fromJson(root, listType);
                }

                MenuItem one = gson.fromJson(root, MenuItem.class);
                return List.of(one);
            }
        };

        task.setOnSucceeded(ev -> refreshUI(task.getValue()));
        task.setOnFailed(ev -> task.getException().printStackTrace());

        new Thread(task, "api-fetch-menu").start();
    }

    /**
     * Met à jour la liste affichée dans le conteneur en générant une "carte" par item.
     *
     * @param items items à afficher
     */
    private void refreshUI(List<MenuItem> items) {
        menuContainer.getChildren().clear();
        menuContainer.setPrefHeight(VBox.USE_COMPUTED_SIZE);
        menuContainer.setMinHeight(VBox.USE_COMPUTED_SIZE);

        for (MenuItem item : items) {
            menuContainer.getChildren().add(createCard(item));
        }
    }




    // =========================
    // UI Cards + Formulaire
    // =========================

    /**
     * Crée une carte (HBox) représentant un item : image + infos (id, nom, prix, description, kcal).
     * Un clic sur la carte remplit le formulaire d'édition.
     *
     * @param item item à afficher
     * @return carte JavaFX prête à être insérée dans le VBox
     */
    private HBox createCard(MenuItem item) {
        ImageView img = buildItemImageView(item);

        VBox texts = new VBox(4);

        Label l1 = new Label("ID: " + item.getId() + " - " + item.getName() + " - " + item.getPrice() + "0€");
        Label l2 = new Label(item.getDescription());
        Label l3 = new Label(item.getCalories() + " kcal");

        l1.setStyle("-fx-text-fill: black; -fx-font-size: 18px;");
        l2.setStyle("-fx-text-fill: black; -fx-font-size: 14px;");
        l3.setStyle("-fx-text-fill: black; -fx-font-size: 14px;");

        texts.getChildren().addAll(l1, l2, l3);

        HBox card = new HBox(12, img, texts);
        card.setMinHeight(150);
        card.setPrefHeight(150);
        card.setMaxWidth(Double.MAX_VALUE);
        card.setUserData(item);
        card.setCursor(Cursor.HAND);

        card.setStyle("""
                -fx-background-color: white;
                -fx-background-radius: 15;
                -fx-padding: 15;
                -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 4);
                """);

        card.setOnMouseClicked(e -> fillForm((MenuItem) card.getUserData()));

        return card;
    }

    /**
     * Construit l'ImageView d'un item en chargeant l'image depuis les ressources si disponible.
     *
     * @param item item dont on veut afficher l'image
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
     * Remplit le formulaire d'édition (nom, description, prix, disponibilité) avec les données de l'item.
     *
     * @param item item sélectionné
     */
    private void fillForm(MenuItem item) {
        selectedItem = item;

        newnom.setText(item.getName());
        newdescription.setText(item.getDescription());
        newprix.setText(String.valueOf(item.getPrice()));
        dispo.setValue(getAvailabilityLabel(item.isAvailable()));
    }

    /**
     * Retourne le libellé de disponibilité ("Dispo"/"Non dispo" ou "Available"/"Unavailable")
     * selon la langue courante et l'état disponible.
     *
     * @param available true si disponible
     * @return libellé dans la bonne langue
     */
    private String getAvailabilityLabel(boolean available) {
        if (isFrench) {
            return available ? FR_AVAILABLE : FR_UNAVAILABLE;
        }
        return available ? EN_AVAILABLE : EN_UNAVAILABLE;
    }

    /**
     * Déduit le booléen "available" en fonction de la valeur sélectionnée dans la ChoiceBox.
     *
     * @return true si l'utilisateur a choisi l'option "disponible"
     */
    private boolean isSelectedAsAvailable() {
        String value = dispo.getValue();
        if (value == null) return false;
        return isFrench ? FR_AVAILABLE.equals(value) : EN_AVAILABLE.equals(value);
    }




    // =========================
    // Sauvegarde / API PUT
    // =========================

    /**
     * Valide le formulaire, met à jour l'objet sélectionné localement,
     * puis envoie la mise à jour à l'API via une requête PUT.
     *
     * @param e événement déclenché par le bouton enregistrer
     */
    @FXML
    private void saveItem(ActionEvent e) {
        if (selectedItem == null) {
            System.out.println("Aucun item sélectionné");
            return;
        }

        String name = newnom.getText().trim();
        String desc = newdescription.getText().trim();
        String priceText = newprix.getText().trim();

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

        boolean available = isSelectedAsAvailable();
        int id = selectedItem.getId();

        // Mise à jour locale
        selectedItem.setName(name);
        selectedItem.setDescription(desc);
        selectedItem.setPrice(price);
        selectedItem.setAvailable(available);

        updateItemOnApi(id, name, desc, price, available);
    }

    /**
     * Modèle d'objet utilisé pour sérialiser la requête JSON envoyée au PUT.
     */
    private static class MenuItemUpdateRequest {
        final String name;
        final String description;
        final double price;
        final boolean available;

        MenuItemUpdateRequest(String name, String description, double price, boolean available) {
            this.name = name;
            this.description = description;
            this.price = price;
            this.available = available;
        }
    }

    /**
     * Envoie la mise à jour d'un item à l'API (PUT), puis recharge la liste en cas de succès.
     *
     * @param id        id de l'item
     * @param name      nom
     * @param desc      description
     * @param price     prix
     * @param available disponibilité
     */
    private void updateItemOnApi(int id, String name, String desc, double price, boolean available) {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                // NOTE: ton code original envoyait toujours sur /fr/menu/{id} (même en anglais).
                // Je garde ce comportement pour ne rien casser.
                String url = API_BASE + ENDPOINT_FR_PREFIX + "/" + id;

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

        new Thread(task, "api-put-menu").start();
    }




    // =========================
    // Helpers Stage
    // =========================

    /**
     * Récupère le Stage (fenêtre) à partir d'un ActionEvent JavaFX.
     *
     * @param event événement JavaFX
     * @return Stage courant
     */
    private Stage getStageFromEvent(ActionEvent event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }
}
