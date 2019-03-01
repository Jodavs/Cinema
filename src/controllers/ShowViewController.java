package controllers;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

import models.*;
import views.ShowListElemView;
import views.ShowListHeaderView;

/**
 * Created by Anton on 24/11/2015.
 *
 * A controller class for the showView, which is composed of the showListView (a VBox) and the controls for setting
 * the date. There's no class called ShowView or ShowListView because they're just containers for other views.
 */
public class ShowViewController {
    // Views needed for updating the showList
    private DatePicker datePicker;
    private VBox showListView;
    private ScrollPane scrollPane;
    private AnchorPane containerPane;

    // Reference to the controllers parent. Used for accessing functions in this controller
    private MainViewController parent;

    // Reference to active element. Used to deactivate it later
    private ShowListElemView activeElem;

    private ArrayList<Show> shows;

    /**
     * Constructor
     * @param pane Will become the base container of the other views
     */
    public ShowViewController(AnchorPane pane) {
        createSceneObjects(pane);
    }

    /**
     * Set the parent field to a controller.
     * @param controller The controller to which the parent will be set.
     */
    public void setParent(MainViewController controller) {
        parent = controller;
    }

    /**
     * Creates the objects needed for the scene and makes the necessary layout preparations/changes for them to be
     * correctly shown.
     * @param pane The AnchorPane to which the scene objects will be added.
     */
    public void createSceneObjects(AnchorPane pane) {
        // Custom creation of view instead of loading an fxml file. Used this method because we need a lot of dynamic
        // and/or custom properties.

        // Make new vbox for the list object
        showListView = new VBox(0.0);
        // Put showListView into a scrollPane for scrolling
        scrollPane = new ScrollPane(showListView);
        scrollPane.setMinHeight(520.0);
        // Never show the scrollbars
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        // Remove background color
        scrollPane.setStyle("-fx-background-color: none;");
        // Embed in anchorpane for snapping to parents edges
        AnchorPane scrollViewAnchor = new AnchorPane(scrollPane);
        // Set anchors to 0
        scrollViewAnchor.setTopAnchor(scrollPane, 0.0);
        scrollViewAnchor.setLeftAnchor(scrollPane, 0.0);
        scrollViewAnchor.setRightAnchor(scrollPane, 0.0);
        scrollViewAnchor.setBottomAnchor(scrollPane, 0.0);

        //Make control buttons
        datePicker = new DatePicker(LocalDate.now());
        Button leftButton = new Button("<");
        Button rightButton = new Button(">");

        // Add action events for each button
        datePicker.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Update the list with the selected date
                updateShowListView();
            }
        });
        leftButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Subtract one day from the date then update the list
                datePicker.setValue(datePicker.getValue().minusDays(1));
                updateShowListView();
            }
        });
        rightButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Add one day to the date then update the list
                datePicker.setValue(datePicker.getValue().plusDays(1));
                updateShowListView();
            }
        });

        // Make HBox for the control buttons and datePicker
        HBox controlsBox = new HBox(leftButton, datePicker, rightButton);
        // Embed HBox in Anchorpane
        AnchorPane controlsAnchor = new AnchorPane(controlsBox);
        // Set anchors
        controlsAnchor.setTopAnchor(controlsBox, 20.0);
        controlsAnchor.setLeftAnchor(controlsBox, 50.0);
        controlsAnchor.setRightAnchor(controlsBox, 50.0);
        // Set height
        controlsAnchor.setMinHeight(70.0);
        controlsAnchor.setPrefHeight(70.0);

        // Create new VBox with the controls on top and the scrollView with the shows beneath it
        VBox showView = new VBox(controlsAnchor, scrollViewAnchor);

        // Add the finished scene (a VBox node with all our other nodes inside it) to the anchor pane
        // given by the parameter
        pane.getChildren().add(showView);

        pane.setTopAnchor(showView, 0.0);
        pane.setLeftAnchor(showView, 0.0);
        pane.setRightAnchor(showView, 0.0);
        pane.setBottomAnchor(showView, 0.0);

        // Set the controllers containerPane to the anchorpane parameter.
        containerPane = pane;

        // Update the showList
        updateShowListView();
    }

    /**
     * Get shows for the date specified by the parameter
     * @param date
     */
    public ArrayList<Show> getShowsAtDate(LocalDate date) {
        return Show.getShowsAtDate(date);
    }

    /**
     * Updates the showListView with the shows on the date currently selected in the datePicker
     */
    public void updateShowListView() {
        // Get selected date from datePicker
        LocalDate cDate = datePicker.getValue();

        // Get shows for selected date
        ArrayList<Show> shows = getShowsAtDate(cDate);

        // Sort the list by time of day using the ShowDateComparator
        shows.sort(new ShowDateComparator());

        // Add each movie on display the current date exactly once
        ArrayList<Movie> cMovies = new ArrayList<>();
        for (Show s : shows) {
            boolean isInList = false;
            // Loop to check if movie was already added
            for (Movie m : cMovies) {
                if (m.isSameMovie(s.movie)) {
                    isInList = true;
                    break;
                }
            }
            if (!isInList) {
                cMovies.add(s.movie);
            }
        }


        // Clear the current childnodes of showListView. Clears all the currently displayed shows
        showListView.getChildren().clear();

        // Calculated height of the view. Used to set the scrollPane height correctly
        double viewportHeight = 0.0;

        // Go through each movie shown the current date
        for (int i=0; i<cMovies.size(); i++) {
            // Get current movie
            Movie m = cMovies.get(i);
            // Create a new ShowListHeaderView with the current movie
            ShowListHeaderView head = new ShowListHeaderView(m.movieTitle, m.movieLength, m.moviePoster);
            // Add style to header if it's the first. Adds rounded corners to the top
            if (i == 0) head.setFirst();
            // Set view width. Subtract two because of border width
            head.setPrefWidth(containerPane.getWidth()-2.0);
            // Add head to a new VBox
            VBox movieBox = new VBox(head);
            // Each ShowListHeaderView is 150.0 high so add that to total
            viewportHeight += 150.0;

            // Go through each show
            for (int j=0; j<shows.size(); j++) {
                // Get current show
                Show s = shows.get(j);

                // Only get shows that have the current movie
                if (s.movie.isSameMovie(m)) {
                    // Create new show view object
                    ShowListElemView showElem = new ShowListElemView(s);
                    // Set view width. Subtract two because of border width
                    showElem.setPrefWidth(containerPane.getWidth()-2.0);

                    // Add new click event to ShowListElemView.
                    showElem.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {
                            // Get the source view of the event object
                            ShowListElemView view = (ShowListElemView)event.getSource();

                            // Activate element
                            view.activate();
                            // Deactivate last selected element if there is any
                            if (activeElem != null) {
                                activeElem.deactivate();
                            }
                            // Make this element the active element
                            activeElem = view;

                            // Tell the parent controller to change its ReservationView to the selected show
                            parent.changeReservationViewWithNewReservation(view.show);
                        }
                    });

                    // Adds style to element if it's the very last. Adds rounded corners to the bottom
                    if (i == cMovies.size()-1 && j == shows.size()-1) showElem.setLast();

                    // Add each specific show underneath the movie header
                    movieBox.getChildren().add(showElem);
                    // Add ShowListElemView's height to total viewport height
                    viewportHeight += 40.0;
                }
            }

            // Add the movieBox (which contains the movie header and all its shows the current date) to the
            // showListView
            showListView.getChildren().add(movieBox);
            // Set scrollPane's height to the calculated height of showListView
            scrollPane.setPrefViewportHeight(viewportHeight);
        }

    }


}

/**
 * A class that implements Comparator<Show> in order to sort shows by time
 */
class ShowDateComparator implements Comparator<Show> {
    @Override
    public int compare(Show s1, Show s2) {
        // Compare the timestamp fields of each Show object
        return s1.timestamp.compareTo(s2.timestamp);
    }
}
