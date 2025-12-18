import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class app extends Application {
    private Label welcomeText;

    @Override
    public void start(Stage stage) {
        welcomeText = new Label("Welcome to JavaFX Application!");
        Button printButton = new Button("Print Text");
        
        printButton.setOnAction(e -> {
            try {
                fetchDataAndUpdate();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        VBox root = new VBox(10);
        root.getChildren().addAll(welcomeText, printButton);
        
        Scene scene = new Scene(root, 400, 200);
        stage.setTitle("JavaFX App");
        stage.setScene(scene);
        stage.show();
    }

    private void fetchDataAndUpdate() throws Exception {
        java.net.URL url = new java.net.URL("http://localhost:7000/users");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        int status = con.getResponseCode();
        System.out.println(status);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        welcomeText.setText("Welcome to JavaFX Application! " + content);
    }

    public static void main(String[] args) {
        launch(args);
    }
}