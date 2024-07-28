package ci.pigier.controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.SQLException;

import ci.pigier.model.Note;

public class BaseController {
    protected Alert alert;
    protected static Note editNote = null;
    
    protected static ObservableList<Note> data = FXCollections.<Note>observableArrayList();

    protected void navigate(Event event, URL fxmlDocName) throws IOException {
        // Chargement du nouveau document FXML de l'interface utilisateur
        Parent pageParent = FXMLLoader.load(fxmlDocName);
        // Création d'une nouvelle scène
        Scene scene = new Scene(pageParent);
        // Obtention de la scène actuelle
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        // Masquage de l'ancienne scène (facultatif)
        appStage.hide(); // facultatif
        // Définition de la nouvelle scène pour la scène
        appStage.setScene(scene);
        // Affichage de la scène
        appStage.show();
    }

    // Méthode pour se connecter à la base de données MySQL
    protected Connection connect() {
        String url = "jdbc:mysql://localhost:3306/notesdb";
        String user = "user";
        String password = "password";
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    // Méthode pour charger les notes depuis la base de données
    protected void loadNotesFromDatabase() {
        String sql = "SELECT id, title, description FROM notes";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            data.clear();
            while (rs.next()) {
                data.add(new Note(
                    rs.getInt("id"),
                    rs.getString("title"),
                    rs.getString("description")
                ));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Méthode pour ajouter une note à la base de données
    protected void addNoteToDatabase(Note note) {
        String sql = "INSERT INTO notes(title, description) VALUES(?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, note.getTitle());
            pstmt.setString(2, note.getDescription());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Méthode pour mettre à jour une note dans la base de données
    protected void updateNoteInDatabase(Note note) {
        String sql = "UPDATE notes SET title = ?, description = ? WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, note.getTitle());
            pstmt.setString(2, note.getDescription());
            pstmt.setInt(3, note.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // Méthode pour supprimer une note de la base de données
    protected void deleteNoteFromDatabase(Note note) {
        String sql = "DELETE FROM notes WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, note.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
