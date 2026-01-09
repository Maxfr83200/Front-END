package com;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class accueilF {

    // =========================
    // Constantes
    // =========================
    private static final String ADMIN_CODE = "0000";
    private static final String MAIN_SCREEN_FXML = "/com/mainscreen.fxml";
    private static final String ADMIN_FXML = "/com/admin.fxml";



    // =========================
    // Composants FXML
    // =========================
    @FXML private Button instructionLabel;
    @FXML private TextField codeAdmin;
    @FXML private Button btnAdmin;



    // =========================
    // État
    // =========================
    private boolean isFrench = true;



    // =========================
    // Initialisation
    // =========================
    /**
     * Initialise le contrôleur après l'injection FXML.
     * Ajoute un effet "InnerShadow" sur le champ de code admin lorsque celui-ci est focus.
     */
    @FXML
    public void initialize() {
        InnerShadow innerShadow = createInnerShadowEffect();

        codeAdmin.focusedProperty().addListener((observable, oldValue, isFocused) -> {
            codeAdmin.setEffect(isFocused ? innerShadow : null);
        });
    }



    // =========================
    // Gestion de la langue
    // =========================
    /**
     * Passe l'interface en français (textes + état interne de langue).
     *
     * @param event événement déclenché par l'action (bouton/menu)
     */
    @FXML
    public void switchToFrench(ActionEvent event) {
        instructionLabel.setText("Toucher pour commencer");
        btnAdmin.setText("Acc\u00E8s admin");
        System.out.println("Langue changée en Français");
        isFrench = true;
    }


    /**
     * Passe l'interface en anglais (textes + état interne de langue).
     *
     * @param event événement déclenché par l'action (bouton/menu)
     */
    @FXML
    public void switchToEnglish(ActionEvent event) {
        instructionLabel.setText("Touch to start");
        btnAdmin.setText("Admin access");
        System.out.println("Language switched to English");
        isFrench = false;
    }



    // =========================
    // Navigation
    // =========================
    /**
     * Ouvre l'écran principal (mainscreen) et transmet la langue au contrôleur suivant.
     *
     * @param event événement souris déclenché par l'action "commencer"
     */
    @FXML
    public void startCommand(MouseEvent event) {
        loadAndShowScene(MAIN_SCREEN_FXML, event, controller -> {
            mainscreenF mainCtrl = (mainscreenF) controller;
            mainCtrl.setLanguage(isFrench);
        });
    }


    /**
     * Vérifie le code admin, puis ouvre l'écran admin si le code est correct.
     * Si le code est incorrect, vide le champ.
     *
     * @param event événement déclenché par l'action (bouton)
     */
    @FXML
    public void goAdmin(ActionEvent event) {
        String code = codeAdmin.getText();

        if (ADMIN_CODE.equals(code)) {
            loadAndShowScene(ADMIN_FXML, event, controller -> {
                adminF adminCtrl = (adminF) controller;
                adminCtrl.setLanguage(isFrench);
            });
            return;
        }

        System.out.println("Code faux ! Tentative avec : " + code);
        codeAdmin.setText("");
    }



    // =========================
    // Méthodes utilitaires privées
    // =========================
    /**
     * Crée et configure l'effet InnerShadow utilisé sur le champ de code admin.
     *
     * @return un effet InnerShadow prêt à être appliqué à un composant JavaFX
     */
    private InnerShadow createInnerShadowEffect() {
        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setRadius(5.0);
        innerShadow.setOffsetX(2.0);
        innerShadow.setOffsetY(2.0);
        innerShadow.setColor(Color.rgb(0, 0, 0, 0.7));
        return innerShadow;
    }


    /**
     * Charge un fichier FXML, récupère son contrôleur, exécute une action optionnelle dessus,
     * puis remplace la scène actuelle par la nouvelle.
     *
     * @param fxmlPath chemin du fichier FXML à charger
     * @param event    événement (ActionEvent ou MouseEvent) servant à récupérer la fenêtre courante
     * @param onLoaded action optionnelle à exécuter sur le contrôleur (ex: passer la langue)
     */
    private void loadAndShowScene(String fxmlPath, Object event, ControllerConsumer onLoaded) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));

        try {
            Parent root = loader.load();
            Object controller = loader.getController();

            if (onLoaded != null) {
                onLoaded.accept(controller);
            }

            Stage stage = getStageFromEvent(event);
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Récupère le Stage (fenêtre) à partir d'un événement JavaFX (ActionEvent ou MouseEvent).
     *
     * @param event événement JavaFX
     * @return le Stage associé
     * @throws IllegalArgumentException si l'événement n'est pas supporté
     */
    private Stage getStageFromEvent(Object event) {
        if (event instanceof ActionEvent actionEvent) {
            return (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
        }
        if (event instanceof MouseEvent mouseEvent) {
            return (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();
        }
        throw new IllegalArgumentException("Unsupported event type: " + event.getClass());
    }


    /**
     * Interface fonctionnelle interne pour exécuter une action sur un contrôleur chargé via FXMLLoader.
     */
    @FunctionalInterface
    private interface ControllerConsumer {
        void accept(Object controller);
    }
}
