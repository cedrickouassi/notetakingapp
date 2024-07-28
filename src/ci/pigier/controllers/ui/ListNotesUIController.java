package ci.pigier.controllers.ui;

import ci.pigier.controllers.BaseController;
import ci.pigier.model.Note;
import ci.pigier.ui.FXMLPage;
import java.io.IOException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ListNotesUIController extends BaseController implements Initializable {

    @FXML
    private TableColumn<Note, String> descriptionTc;

    @FXML
    private Label notesCount;

    @FXML
    private TableView<Note> notesListTable;

    @FXML
    private TextField searchNotes;

    @FXML
    private TableColumn<Note, String> titleTc;

    private ResourceBundle bundle;

    @FXML
    void doDelete(ActionEvent event) {
        Note selectedNote = notesListTable.getSelectionModel().getSelectedItem();
        if (selectedNote != null) {
            data.remove(selectedNote);
            deleteNoteFromDatabase(selectedNote);
            updateNoteCount();
        } else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle(bundle.getString("alert.warning"));
            alert.setHeaderText(bundle.getString("alert.noSelection"));
            alert.setContentText(bundle.getString("alert.selectNote"));
            alert.showAndWait();
        }
    }

    @FXML
    void doEdit(ActionEvent event) throws IOException {
        Note selectedNote = notesListTable.getSelectionModel().getSelectedItem();
        if (selectedNote != null) {
            editNote = selectedNote;
            navigate(event, FXMLPage.ADD.getPage());
        } else {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle(bundle.getString("alert.warning"));
            alert.setHeaderText(bundle.getString("alert.noSelection"));
            alert.setContentText(bundle.getString("alert.selectNote"));
            alert.showAndWait();
        }
    }

    @FXML
    void newNote(ActionEvent event) throws IOException {
        editNote = null;
        navigate(event, FXMLPage.ADD.getPage());
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.bundle = resources;
        
        FilteredList<Note> filteredData = new FilteredList<>(data, n -> true);
        notesListTable.setItems(filteredData);
        titleTc.setCellValueFactory(new PropertyValueFactory<>("title"));
        descriptionTc.setCellValueFactory(new PropertyValueFactory<>("description"));
        
        searchNotes.setOnKeyReleased(e -> {
            filteredData.setPredicate(n -> {
                if (searchNotes.getText() == null || searchNotes.getText().isEmpty()) {
                    return true;
                }
                return n.getTitle().contains(searchNotes.getText()) || n.getDescription().contains(searchNotes.getText());
            });
            updateNoteCount(filteredData);
        });
        
        updateNoteCount(filteredData);
    }

    private void updateNoteCount(FilteredList<Note> filteredData) {
        notesCount.setText(bundle.getString("label.noteCount") + filteredData.size());
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
}
