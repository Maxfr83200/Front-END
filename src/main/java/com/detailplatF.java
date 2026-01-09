package com;

import java.io.IOException;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class detailplatF {

    // =========================
    // Constantes
    // =========================
    private static final int MIN_QTY = 1;
    private static final int MAX_QTY = 9;
    private static final String MAINSCREEN_FXML = "/com/mainscreen.fxml";

    // Ces "codes" servent à choisir le suffixe d'image, indépendamment de la langue affichée
    private static final String PROTEIN_BEEF = "boeuf";
    private static final String PROTEIN_CHICKEN = "poulet";
    private static final String PROTEIN_SHRIMP = "crevette";
    private static final String PROTEIN_TOFU = "tofu";

    // =========================
    // Composants FXML
    // =========================
    @FXML private HBox proteinBox;
    @FXML private RadioButton rbPoulet;
    @FXML private RadioButton rbBoeuf;
    @FXML private RadioButton rbTofu;
    @FXML private RadioButton rbCrevette;
    @FXML private ToggleGroup proteinGroup;
    @FXML private Button btnAjoutPanier;

    @FXML private Text quantityText;
    @FXML private ImageView imagePlat;
    @FXML private Text afficheNom;
    @FXML private Text afficheDesc;
    @FXML private Text affichePrix;

    // =========================
    // État
    // =========================
    private final CartModel cart = CartModel.getInstance();

    private MenuItem item;
    private boolean isFrench = true;
    private int quantity = MIN_QTY;

    // =========================
    // Langue
    // =========================

    /**
     * Définit la langue de l'écran (textes des boutons + protéines) puis rafraîchit l'affichage.
     *
     * @param isFrench true = français, false = anglais
     */
    public void setLanguage(boolean isFrench) {
        this.isFrench = isFrench;

        // Textes des protéines (affichage)
        rbTofu.setText("Tofu");
        if (isFrench) {
            rbBoeuf.setText("Boeuf");
            rbCrevette.setText("Crevette");
            rbPoulet.setText("Poulet");
            btnAjoutPanier.setText("Ajouter au Panier");
        } else {
            rbBoeuf.setText("Beef");
            rbCrevette.setText("Shrimp");
            rbPoulet.setText("Chicken");
            btnAjoutPanier.setText("Add to Cart");
        }

        refreshQtyUI();
    }

    // =========================
    // Initialisation JavaFX
    // =========================

    /**
     * Initialise le contrôleur :
     * - initialise l'affichage quantité/prix,
     * - applique un clip arrondi à l'image et le met à jour à chaque changement de taille.
     */
    @FXML
    private void initialize() {
        refreshQtyUI();
        setupRoundedImageClip(imagePlat, 60);
    }

    /**
     * Applique un clip arrondi (coins arrondis) sur une ImageView et le met à jour quand sa taille change.
     *
     * @param view     ImageView à découper
     * @param arcSize  taille des arrondis
     */
    private void setupRoundedImageClip(ImageView view, double arcSize) {
        if (view == null) return;

        Rectangle clip = new Rectangle();
        clip.setArcWidth(arcSize);
        clip.setArcHeight(arcSize);
        view.setClip(clip);

        Runnable updateClip = () -> {
            var b = view.getLayoutBounds();
            clip.setWidth(b.getWidth());
            clip.setHeight(b.getHeight());
        };

        Platform.runLater(updateClip);

        view.layoutBoundsProperty().addListener((obs, oldB, newB) -> {
            clip.setWidth(newB.getWidth());
            clip.setHeight(newB.getHeight());
        });
    }

    // =========================
    // Quantités
    // =========================

    /**
     * Augmente la quantité (jusqu'à MAX_QTY), puis met à jour le texte et le prix total.
     */
    @FXML
    private void plusQty() {
        if (quantity < MAX_QTY) {
            quantity++;
            refreshQtyUI();
        }
    }

    /**
     * Diminue la quantité (jusqu'à MIN_QTY), puis met à jour le texte et le prix total.
     */
    @FXML
    private void minusQty() {
        if (quantity > MIN_QTY) {
            quantity--;
            refreshQtyUI();
        }
    }

    /**
     * Met à jour le texte de quantité et le prix affiché en fonction de la quantité actuelle.
     */
    private void refreshQtyUI() {
        if (quantityText != null) {
            quantityText.setText(isFrench ? ("Quantit\u00E9 : " + quantity) : ("Quantity : " + quantity));
        }

        if (item != null && affichePrix != null) {
            double total = item.getPrice() * quantity;
            affichePrix.setText(String.format("%.2f \u20AC", total));
        }
    }

    // =========================
    // Protéines
    // =========================

    /**
     * Cache le choix des protéines (et désactive/efface les RadioButton).
     */
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

    /**
     * Affiche le choix des protéines et active les RadioButton.
     * Sélectionne une valeur par défaut (boeuf) pour éviter un état "null".
     */
    private void showProteinOptions() {
        proteinBox.setVisible(true);
        proteinBox.setManaged(true);

        rbPoulet.setDisable(false);
        rbBoeuf.setDisable(false);
        rbTofu.setDisable(false);
        rbCrevette.setDisable(false);

        rbBoeuf.setSelected(true);
    }

    /**
     * Réagit au changement de protéine sélectionnée et met à jour l'image du plat.
     */
    @FXML
    private void onProteinChanged() {
        if (proteinGroup.getSelectedToggle() == null || item == null) return;

        RadioButton selected = (RadioButton) proteinGroup.getSelectedToggle();
        String proteinCode = mapProteinLabelToCode(selected.getText());

        if (proteinCode != null) {
            setProteinImage(proteinCode);
        }
    }

    /**
     * Convertit le texte affiché du RadioButton (FR/EN) en code d'image stable.
     * Exemple : "Beef" -> "boeuf", "Poulet" -> "poulet".
     *
     * @param label texte affiché dans le RadioButton
     * @return code d'image (boeuf/poulet/tofu/crevette) ou null si inconnu
     */
    private String mapProteinLabelToCode(String label) {
        if (label == null) return null;

        return switch (label) {
            case "Boeuf", "Beef" -> PROTEIN_BEEF;
            case "Poulet", "Chicken" -> PROTEIN_CHICKEN;
            case "Crevette", "Shrimp" -> PROTEIN_SHRIMP;
            case "Tofu" -> PROTEIN_TOFU;
            default -> null;
        };
    }

    /**
     * Change l'image affichée pour correspondre à la protéine choisie.
     * Convention attendue : baseName_protein.png (ex: ramen_boeuf.png).
     *
     * @param proteinCode code d'image (boeuf/poulet/tofu/crevette)
     */
    private void setProteinImage(String proteinCode) {
        if (item == null || item.getImageUrl() == null) return;

        String baseName = item.getImageUrl().replace(".png", "");
        String path = "/" + baseName + "_" + proteinCode + ".png";

        var stream = getClass().getResourceAsStream(path);
        if (stream != null) {
            imagePlat.setImage(new Image(stream));
        } else {
            System.err.println("Image protéine introuvable : " + path);
        }
    }

    // =========================
    // Panier + Navigation
    // =========================

    /**
     * Ajoute l'item au panier avec la quantité choisie et la protéine (si requise),
     * puis retourne sur l'écran principal.
     *
     * @param event événement du bouton "ajouter au panier"
     */
    @FXML
    private void addToCart(ActionEvent event) {
        if (item == null) return;

        String protein = null;

        if (item.isProteinRequired()) {
            RadioButton selected = (RadioButton) proteinGroup.getSelectedToggle();
            protein = (selected != null) ? selected.getText() : null;
        }

        cart.addItem(item, protein, quantity);
        goMain(event);
    }

    /**
     * Retourne à l'écran principal (mainscreen) en transmettant la langue.
     *
     * @param event événement qui déclenche la navigation
     */
    public void goMain(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(MAINSCREEN_FXML));

        try {
            Parent root = loader.load();
            mainscreenF mainCtrl = loader.getController();
            mainCtrl.setLanguage(isFrench);

            Stage stage = getStageFromEvent(event);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Récupère le Stage courant à partir d'un ActionEvent.
     *
     * @param event événement JavaFX
     * @return fenêtre courante
     */
    private Stage getStageFromEvent(ActionEvent event) {
        return (Stage) ((Node) event.getSource()).getScene().getWindow();
    }

    // =========================
    // Données
    // =========================

    /**
     * Injecte l'item à afficher dans l'écran détail, met à jour les textes + l'image,
     * et affiche/masque les protéines selon le flag "proteinRequired".
     *
     * @param item item à afficher
     */
    public void setItem(MenuItem item) {
        this.item = item;

        afficheNom.setText(item.getName());
        afficheDesc.setText(item.getDescription());
        affichePrix.setText(String.format("%.2f €", item.getPrice()));

        loadMainImage(item.getImageUrl());

        if (item.isProteinRequired()) {
            showProteinOptions();
        } else {
            hideProteinOptions();
        }

        refreshQtyUI();
    }

    /**
     * Charge l'image principale du plat depuis les ressources, sinon vide l'image.
     *
     * @param imagePath chemin d'image relatif (ex: "images/ramen.png")
     */
    private void loadMainImage(String imagePath) {
        if (imagePath == null || imagePath.isBlank()) {
            imagePlat.setImage(null);
            return;
        }

        var stream = getClass().getResourceAsStream("/" + imagePath);
        if (stream != null) {
            imagePlat.setImage(new Image(stream));
        } else {
            imagePlat.setImage(null);
            System.err.println("Image introuvable : " + imagePath);
        }
    }
}
