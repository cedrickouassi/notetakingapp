package ci.pigier.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class Note {
    private final SimpleIntegerProperty id;
    private final SimpleStringProperty title;
    private final SimpleStringProperty description;

    // Constructeur pour une nouvelle note (sans id)
    public Note(String title, String description) {
        this.id = new SimpleIntegerProperty(0); // 0 ou tout autre valeur par défaut
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
    }

    // Constructeur pour une note existante (avec id)
    public Note(int id, String title, String description) {
        this.id = new SimpleIntegerProperty(id);
        this.title = new SimpleStringProperty(title);
        this.description = new SimpleStringProperty(description);
    }

    public int getId() {
        return id.get();
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getTitle() {
        return title.get();
    }

    public void setTitle(String title) {
        this.title.set(title);
    }

    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    @Override
    public String toString() {
        return "Note{" + "id=" + id.get() + ", title=" + title.get() + ", description=" + description.get() + '}';
    }
}
