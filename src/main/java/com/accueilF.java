package com;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

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

    private Boolean isFrench = true;

    @FXML
    public void switchToFrench(ActionEvent event) {
        instructionLabel.setText("Touche pour commencer");
        System.out.println("Langue changée en Français");
        isFrench = true;
    }

    @FXML
    public void switchToEnglish(ActionEvent event) {
        instructionLabel.setText("Touch to start");
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


        if ("1012".equals(code)) {

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
