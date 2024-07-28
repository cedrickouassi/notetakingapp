module NoteTakingApp09072024 {
    requires transitive javafx.graphics;
    requires javafx.base;
    requires javafx.fxml;
    requires javafx.controls;

    // Ouverture des packages aux modules JavaFX
    opens ci.pigier to javafx.fxml;
    opens ci.pigier.controllers.ui to javafx.fxml;
    opens ci.pigier.model to javafx.fxml;

    // Exportation des packages pour permettre l'accès aux autres modules
    exports ci.pigier;
    exports ci.pigier.controllers.ui;
    exports ci.pigier.model;
}
