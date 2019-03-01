package views;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import models.Show;

import java.io.IOException;

/**
 * Created by Anton on 24/11/2015.
 *
 * A view that displays a specific show at a given time and place
 */
public class ShowListElemView extends AnchorPane {
    // Labels accessed from the fxml file
    @FXML private Label timeLabel;
    @FXML private Label theaterLabel;
    @FXML private AnchorPane background;
    private boolean isActive;

    // Show object. Used to pass to MainViewController when changing ReservationView
    public Show show;

    /**
     * Constructor loads fxml from file and sets the labels of the view
     * @param show The show from which the other information is derived
     */
    public ShowListElemView(Show show) {
        loadFxml();

        // Set time and theater
        setTime(new String(show.timestamp.getHour() + ":" + show.timestamp.getMinute()));
        setTheater(String.valueOf(show.theaterNumber));

        // Set the show field for later reference
        this.show = show;
    }

    private void loadFxml() {
        // Create a new FXMLLoader object
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("gui_show_list_elem.fxml"));
        // Set this class to both the rootview and controller of the fxml
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        // Try to load the file
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set time to string parameter. No checks
     * @param time Should be in hh:mm format
     */
    public void setTime(String time) {
        timeLabel.setText(time);
    }

    /**
     * Set theater to parameter
     * @param theater Should be either a number or a letter
     */
    public void setTheater(String theater) {
        theaterLabel.setText(theater);
    }

    public void activate() {
        background.getStyleClass().remove("elem_normal");
        background.getStyleClass().add("elem_active");
    }
    public void deactivate() {
        background.getStyleClass().remove("elem_active");
        background.getStyleClass().add("elem_normal");
    }

    public void setLast() {
        background.getStyleClass().add("elem_bottom");
    }
}
