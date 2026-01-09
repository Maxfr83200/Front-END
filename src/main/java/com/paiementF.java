package com;

import java.io.IOException;
import java.util.Random;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class paiementF {

    // =========================
    // Constantes
    // =========================
    private static final String ACCUEIL_FXML = "/com/accueil.fxml";
    private static final String ORDER_PREFIX = "#CB";

    // =========================
    // État
    // =========================
    private boolean isFrench = true;
    private String orderId;

    // =========================
    // Composants FXML
    // =========================
    @FXML private Text textTitle;
    @FXML private Text textnumCommande;
    @FXML private Text textRemerciement;
    @FXML private Text textRetour;

    // =========================
    // Initialisation JavaFX
    // =========================

    /**
     * Initialise le contrôleur en générant un numéro de commande aléatoire.
     */
    @FXML
    public void initialize() {
        generateOrderId();
    }

    /**
     * Génère un identifiant de commande unique sous la forme "#CBXXXX" (XXXX entre 1000 et 9999).
     */
    private void generateOrderId() {
        int number = new Random().nextInt(9000) + 1000;
        orderId = ORDER_PREFIX + number;
    }

    // =========================
    // Langue
    // =========================

    /**
     * Définit la langue de l'écran et met à jour les textes affichés.
     * Si l'identifiant de commande n'est pas encore généré, il est généré avant l'affichage.
     *
     * @param isFrench true = français, false = anglais
     */
    public void setLanguage(Boolean isFrench) {
        this.isFrench = Boolean.TRUE.equals(isFrench);

        if (orderId == null) {
            generateOrderId();
        }

        if (this.isFrench) {
            textTitle.setText("Paiement réussi");
            textnumCommande.setText("Numéro de commande " + orderId);
            textRemerciement.setText("vous remercie de votre visite");
            textRetour.setText("Touchez pour revenir à l'accueil");
        } else {
            textTitle.setText("Payment Successful");
            textnumCommande.setText("Order Number " + orderId);
            textRemerciement.setText("Thank you for your visit");
            textRetour.setText("Touch to return to Home");
        }
    }

    // =========================
    // Navigation
    // =========================

    /**
     * Retourne à l'écran d'accueil lorsque l'utilisateur touche l'écran.
     *
     * @param event événement souris déclenché par l'action "retour"
     */
    @FXML
    public void startCommand(MouseEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(ACCUEIL_FXML));

        try {
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
