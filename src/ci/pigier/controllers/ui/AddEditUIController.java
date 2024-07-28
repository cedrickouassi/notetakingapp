package ci.pigier.controllers.ui;

import ci.pigier.controllers.BaseController;
import ci.pigier.model.Note;
import ci.pigier.ui.FXMLPage;
import java.io.IOException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;

public class AddEditUIController extends BaseController implements Initializable {

    @FXML
    private TextArea descriptionTxtArea;

    @FXML
    private Button saveBtn, clearBtn, deleteBtn, editBtn;

    @FXML
    private TextField titleTxtFld, searchField;

    @FXML
    private Label noteCountLabel;

    @FXML
    private ImageView logoImageView;

    private ResourceBundle bundle;

    @FXML
    void doBack(ActionEvent event) throws IOException {
        navigate(event, FXMLPage.LIST.getPage());
    }

    @FXML
    void doClear(ActionEvent event) {
        titleTxtFld.clear();
        descriptionTxtArea.clear();
    }

    @FXML
    void doSave(ActionEvent event) throws IOException {
        if (Objects.nonNull(editNote)) {
            data.remove(editNote);
            updateNoteInDatabase(editNote);
        } else {
            if (titleTxtFld.getText().trim().isEmpty() || descriptionTxtArea.getText().trim().isEmpty()) {
                alert = new Alert(AlertType.WARNING);
                alert.setTitle(bundle.getString("alert.warning"));
                alert.setHeaderText(bundle.getString("alert.invalidData"));
                alert.setContentText(bundle.getString("alert.noteEmpty"));
                alert.showAndWait();
                return;
            }

            Note newNote = new Note(titleTxtFld.getText(), descriptionTxtArea.getText());
            data.add(newNote);
            saveNoteToDatabase(newNote);
        }

        navigate(event, FXMLPage.LIST.getPage());
    }

    @FXML
    void doDelete(ActionEvent event) {
        Note selectedNote = notesTable.getSelectionModel().getSelectedItem();
        if (selectedNote != null) {
            data.remove(selectedNote);
            deleteNoteFromDatabase(selectedNote);
            updateNoteCount();
        } else {
            alert = new Alert(AlertType.WARNING);
            alert.setTitle(bundle.getString("alert.warning"));
            alert.setHeaderText(bundle.getString("alert.noSelection"));
            alert.setContentText(bundle.getString("alert.selectNote"));
            alert.showAndWait();
        }
    }

    @FXML
    void doEdit(ActionEvent event) {
        Note selectedNote = notesTable.getSelectionModel().getSelectedItem();
        if (selectedNote != null) {
            titleTxtFld.setText(selectedNote.getTitle());
            descriptionTxtArea.setText(selectedNote.getDescription());
            saveBtn.setText(bundle.getString("button.update"));
        } else {
            alert = new Alert(AlertType.WARNING);
            alert.setTitle(bundle.getString("alert.warning"));
            alert.setHeaderText(bundle.getString("alert.noSelection"));
            alert.setContentText(bundle.getString("alert.selectNote"));
            alert.showAndWait();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        setTexts(bundle);

        if (Objects.nonNull(editNote)) {
            titleTxtFld.setText(editNote.getTitle());
            descriptionTxtArea.setText(editNote.getDescription());
            saveBtn.setText(bundle.getString("button.update"));
        }

        Image logoImage = new Image("file:path/to/logo.png");
        logoImageView.setImage(logoImage);

        updateNoteCount();
    }

    private void saveNoteToDatabase(Note note) {
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

    private void updateNoteInDatabase(Note note) {
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

    private void deleteNoteFromDatabase(Note note) {
        String sql = "DELETE FROM notes WHERE id = ?";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, note.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private Connection connect() {
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

    private void updateNoteCount() {
        int count = data.size();
        noteCountLabel.setText(bundle.getString("label.noteCount") + count);
    }

    private void setTexts(ResourceBundle bundle) {
        saveBtn.setText(bundle.getString("button.save"));
        clearBtn.setText(bundle.getString("button.clear"));
        deleteBtn.setText(bundle.getString("button.delete"));
        editBtn.setText(bundle.getString("button.edit"));
        noteCountLabel.setText(bundle.getString("label.noteCount"));
    }

    @FXML
    private void switchToEnglish() {
        bundle = ResourceBundle.getBundle("messages", Locale.ENGLISH);
        setTexts(bundle);
    }

    @FXML
    private void switchToFrench() {
        bundle = ResourceBundle.getBundle("messages", Locale.FRENCH);
        setTexts(bundle);
    }

    private void filterNotes(String searchText) {
        ObservableList<Note> filteredList = FXCollections.observableArrayList();
        for (Note note : data) {
            if (note.getTitle().contains(searchText) || note.getDescription().contains(searchText)) {
                filteredList.add(note);
            }
        }
        notesTable.setItems(filteredList);
        updateNoteCount();
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText();
        filterNotes(searchText);
    }
}
