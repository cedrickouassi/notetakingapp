package ci.pigier;

import ci.pigier.ui.FXMLPage;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class NoteTakingApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        // Chargement de la page initiale (Liste des notes)
        Parent root = FXMLLoader.load(FXMLPage.LIST.getPage());
        
        // Création de la scène principale
        Scene scene = new Scene(root);
     
        // Configuration de la scène principale
        stage.setScene(scene);
        stage.setTitle("P'Note-Taking Application v1.0.0");
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Point d'entrée de l'application
     * @param args les arguments de la ligne de commande
     */
    public static void main(String[] args) {
        launch(args);
    }
}
