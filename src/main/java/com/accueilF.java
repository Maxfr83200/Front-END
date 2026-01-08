package com;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.scene.effect.InnerShadow;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

public class accueilF {

    @FXML
    private Label instructionLabel;

    @FXML
    private TextField codeAdmin;

    @FXML
    private Button btnAdmin;

    private Boolean isFrench = true;

    @FXML
    public void initialize() {
        //pour ajouter effet d'ombre portée sur le champ code
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setRadius(5.0);
        innerShadow.setOffsetX(2.0);
        innerShadow.setOffsetY(2.0);
        innerShadow.setColor(Color.rgb(0, 0, 0, 0.7));

        codeAdmin.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                codeAdmin.setEffect(innerShadow);
            } else {
                codeAdmin.setEffect(null);
            }
        });
    }

    @FXML
    public void switchToFrench(ActionEvent event) {
        instructionLabel.setText("Toucher pour commencer");
        btnAdmin.setText("Acc\u00E8s admin");
        System.out.println("Langue changée en Français");
        isFrench = true;
    }

    @FXML
    public void switchToEnglish(ActionEvent event) {
        instructionLabel.setText("Touch to start");
        btnAdmin.setText("Admin access");
        System.out.println("Language switched to English");
        isFrench = false;
    }

    @FXML
    public void startCommand(MouseEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/mainscreen.fxml"));
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

    @FXML
    public void goAdmin(ActionEvent event) {

        String code = codeAdmin.getText();


        if ("0000".equals(code)) {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/admin.fxml"));
            try {
                Parent root = loader.load();
                adminF mainCtrl = loader.getController();
                mainCtrl.setLanguage(isFrench);

                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // --- CODE FAUX ---
            System.out.println("Code faux ! Tentative avec : " + code);

            codeAdmin.setText("");
        }
    }

}
