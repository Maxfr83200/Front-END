package com;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Random;

public class paiementF {

    private Boolean isFrench = true;

    @FXML private Text textTitle;
    @FXML private Text textnumCommande;
    @FXML private Text textRemerciement;
    @FXML private Text textRetour;

    private String orderId;

    @FXML
    public void initialize() {
        Random random = new Random();
        int number = random.nextInt(9000) + 1000;
        orderId = "#CB" + number;
    }

    public void setLanguage(Boolean isFrench) {
        this.isFrench = isFrench;

        if (orderId == null) {
            initialize();
        }
        if(Boolean.TRUE.equals(isFrench)){
            textTitle.setText("Paiement r\u00E9ussi");
            textnumCommande.setText("Num\u00E9ro de commande " + orderId);
            textRemerciement.setText("vous remercie de votre visite");
            textRetour.setText("Touchez pour revenir \u00E0 l'accueil");
        }
        else {
            textTitle.setText("Payment Successful");
            textnumCommande.setText("Order Number " + orderId);
            textRemerciement.setText("Thank you for your visit");
            textRetour.setText("Touch to return to Home");
        }
    }

    @FXML
    public void startCommand(MouseEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/accueil.fxml"));
        try {
            Parent root = loader.load();
            accueilF mainCtrl = loader.getController();


            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
