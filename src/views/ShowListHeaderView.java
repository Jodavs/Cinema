package views;

import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.TextField;

import java.io.IOException;

/**
 * Created by Anton on 24/11/2015.
 *
 * A view that displays a movie header with a title and runlength. It is used to provide context for the
 * ShowListElemView's that will follow
 */
public class ShowListHeaderView extends AnchorPane {
    // Component references to the fxml file
    @FXML private Label titleLabel;
    @FXML private Label lengthLabel;
    @FXML private ImageView posterImage;
    @FXML private AnchorPane background;

    /**
     * Constructor loads fxmlfile and sets fields to supplied values
     * @param title The title of the movie
     * @param length The length of the movie
     * @param image A poster image for the movie
     */
    public ShowListHeaderView(String title, int length, Image image) {
        // Create a new FXMLLoader
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("gui_show_list_header.fxml"));
        // Set this object as both the rootview and controller of the loaded view
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        // Try to actually load the file
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Set object fields
        setTitle(title);
        setLength(String.valueOf(length) + " minutter");
        posterImage.setImage(image);
    }

    /**
     * Sets the movie title label (wraps if too long)
     * @param title Supplied string title
     */
    public void setTitle(String title) {
        titleLabel.textProperty().setValue(title);
    }

    /**
     * Sets the movie length label
     * @param lenstr String with length and unit specifier
     */
    public void setLength(String lenstr) {
        lengthLabel.setText(lenstr);
    }

    public void setPosterUrl(String url) {
        posterImage.setImage(new Image(url));
    }

    public void setFirst() {
        background.getStyleClass().add("header_top");
    }
}
