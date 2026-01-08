package com;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class paiementF {

    private Boolean isFrench = true;


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
