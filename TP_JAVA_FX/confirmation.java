

import javafx.scene.control.Label;
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

public class confirmation {

    private Boolean isFrench;

    public void setLanguage(Boolean isFrench) {
        this.isFrench = isFrench;
    }

    public void retourAcceuil(ActionEvent event) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("acceuil.fxml"));
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