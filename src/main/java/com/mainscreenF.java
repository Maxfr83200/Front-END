package com;

import javafx.stage.Stage;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

public class mainscreenF {

    private Boolean isFrench;

    public void setLanguage(Boolean isFrench) {
        this.isFrench = isFrench;
    }

    public void goDetail(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/detailProduit.fxml"));
        try {
            Parent root = loader.load();
            mainscreenF mainCtrl = loader.getController();
            mainCtrl.setLanguage(isFrench);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
            
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
}