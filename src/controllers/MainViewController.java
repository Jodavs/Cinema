package controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import jfx.messagebox.MessageBox;
import models.*;
import views.SeatBtn;

public class MainViewController implements Initializable {

    //Reserve View Components
    @FXML Label lblSelectShowOrReservation;
    @FXML ImageView cinemaImage;
    @FXML Pane cinemaViewPane;
    @FXML Label lblReserveViewMovieTitle;
    @FXML ImageView imgReserveViewPoster;
    @FXML TextField txtReserveViewPhoneNumber;
    @FXML ChoiceBox choiceBoxReserveViewAdults;
    @FXML ChoiceBox choiceBoxReserveViewKids;
    @FXML Label lblReserveViewTheaterNumber;
    @FXML Label lblReserveViewPrice;
    @FXML Label lblReserveViewSeatCount;
    @FXML Label lblReserveViewTimestamp;

    @FXML AnchorPane showAnchorPane;

    @FXML SplitPane splitPane;

    @FXML TextField modReservationTelephone;
    @FXML ListView modReservationList;


    private int reservableSeatCount = 1;
    private int selectedSeatCount = 0;

    // List for storing btnList
    private ArrayList<SeatBtn> btnList = new ArrayList<>();
    private Show show;

    // Variables for holding the count of adults and kids in the reservation
    private int adultCount = 0;
    private int kidCount = 0;

    /**
     * Action run when telephone number is changed in the reservation view
     * @param event
     */
    @FXML
    private void txtModNumberChanged(KeyEvent event) {
        // Set the items in the modReservationList to the search result of the partial or full phone number
        modReservationList.setItems(FXCollections.observableArrayList(Reservation.getReservationsForPartialPhoneNumber(modReservationTelephone.getText())));
    }

    // TODO: Skal den her funktion nogensinde bruges? ah ja det skal den selvf.
    @FXML
    private void btnReserveViewReserveClicked(ActionEvent event) {


        if (selectedSeatCount == reservableSeatCount && txtReserveViewPhoneNumber.getText().length() == 8) {
            txtReserveViewPhoneNumber.clear();

            ArrayList<ReservedSpot> selectedSpots = new ArrayList<>();

            for (SeatBtn seatBtn : btnList) {
                if (seatBtn.seatStatus == 1) {
                    seatBtn.seatStatus = 2;
                    seatBtn.statusChanged();
                    ReservedSpot spot = new ReservedSpot(seatBtn.seatId, 0);
                    selectedSpots.add(spot);
                }
            }

            selectedSeatCount = 0;
            Reservation.reserveSeats(selectedSpots, txtReserveViewPhoneNumber.getText(), show.id);
        }
        else if (txtReserveViewPhoneNumber.getText().length() < 8 || txtReserveViewPhoneNumber.getText().length() > 8) {
            MessageBox.show((Stage) cinemaViewPane.getScene().getWindow(),
                    "Indtast venligst et gyldigt telefon nr.",
                    "Telefon nr. ugyldigt",
                    MessageBox.ICON_INFORMATION | MessageBox.OK);
        }
        else if (selectedSeatCount < reservableSeatCount) {
            MessageBox.show((Stage) cinemaViewPane.getScene().getWindow(),
                    "Vælg venligst en eller flere pladser",
                    "Ugyldig antal pladser",
                    MessageBox.ICON_INFORMATION | MessageBox.OK);
        }
    }

    /**
     * Initializes the MainViewController instance
     * @param location TODO: Is this ever used?
     * @param resources TODO: Is this ever used?
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Create a new instance of ShowViewController and pass the showAnchorPane (defined in the main_view.fxml file)
        // as a parameter
        ShowViewController showViewController = new ShowViewController(showAnchorPane);
        // Set the showViewController's parent to this instance of MainViewController
        showViewController.setParent(this);

        // Call the static method fetchReservations on the Reservation class to fetch all reservations in the database
        Reservation.fetchReservations();

        // Create a list of numbers from 0 to 100 for use in the two choiceBoxes
        ArrayList<Object> list = new ArrayList<>();
        for (int i=0;i<=100;i++) {list.add(i);}
        // Add list element to choiceBoxes and select the first item (0) in each
        choiceBoxReserveViewAdults.setItems(FXCollections.observableArrayList(list));
        choiceBoxReserveViewKids.setItems(FXCollections.observableArrayList(list));
        choiceBoxReserveViewAdults.getSelectionModel().selectFirst();
        choiceBoxReserveViewKids.getSelectionModel().selectFirst();

        // Add listener to when the selection changes in the two choiceBoxes
        choiceBoxReserveViewKids.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            // Update the kidCount field to the new value
            kidCount = newValue.intValue();
            // Update the price label
            updatePrice();
        });
        choiceBoxReserveViewAdults.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            // Update the adultCount field to the new value
            adultCount = newValue.intValue();
            // Update the price label
            updatePrice();
        });

        // Add listener to when the selection changes in the phone number box above the reservation list
        modReservationList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            changeReservationViewWithReservation((Reservation)newValue);
        });
        // Add all current reservations to the reservation list
        modReservationList.setItems(FXCollections.observableArrayList(Reservation.getReservationsForPartialPhoneNumber("")));

        // Update the price field
        updatePrice();
    }

    /**
     * Updates the price label in the reservation view
     */
    private void updatePrice() {
        reservableSeatCount = kidCount + adultCount;
        String totalPrice = String.valueOf(kidCount * SeatPrices.KID + adultCount * SeatPrices.ADULT);
        lblReserveViewPrice.setText(totalPrice);
    }

    /**
     * Function that calls changeReservationViewWithNewReservation and gets the seat info from the selected reservation.
     * This function is only used when editing an existing reservation.
     * @param reservation The reservation determines which show will be passed and and which seats to show as reserved
     *                    and selected.
     */
    public void changeReservationViewWithReservation(Reservation reservation) {
        changeReservationView(reservation.getShow());
    }

    /**
     * Function that calls changeReservationView and sets the selected seat count to 0.
     * @param show The show that determines which theater will be selected and which seats are reserved.
     */
    public void changeReservationViewWithNewReservation(Show show) {
        // Set the currently selected seat count to 0 as this is a new reservation
        selectedSeatCount = 0;
        // Call the changeReservationView function
        changeReservationView(show);
    }

    /**
     * Function that sets info of the reservation view in the middle of the screen
     * @param show Determines the info to show
     */
    public void changeReservationView(Show show) {
        // Hide the placeholder text in the reservation view
        lblSelectShowOrReservation.setVisible(false);
        // Show the cinema screen in the reservation view
        cinemaImage.setVisible(true);

        // Set the title, seatCount and theaterNumber labels to the values determined by the current show and the
        // optional (determined by the caller) reservation. The reservation in this case only influences the chosen
        // amount of seats, which is set to the reservations seats
        lblReserveViewMovieTitle.setText(show.movie.movieTitle);
        lblReserveViewSeatCount.setText(String.valueOf(show.amountOfSeats));
        lblReserveViewTheaterNumber.setText(String.valueOf(show.theaterNumber));

        // Create datetime formatter for showing the show's timestamp as a localized date (i.e. 31. november 2015)
        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL);
        lblReserveViewTimestamp.setText(show.timestamp.format(formatter));

        // Set the image in the top to the show's movie
        imgReserveViewPoster.setImage(new Image(show.movie.moviePosterUrl));

        // Set the show field to the passed show parameter
        this.show = show;

        // Calls this function to update the seats in the middle to show the correct seat status
        updateCinemaView(show);
    }

    /**
     * Updates the actual seat views in the reservation view
     * @param show
     */
    private void updateCinemaView(Show show) {

        // If there's old buttons delete them from both the btnList and the actual cinemaViewPane (which is displayed
        // in the gui)
        if (!btnList.isEmpty()) {
            btnList.clear();
            cinemaViewPane.getChildren().clear();
        }

        // Get all reservations for the current show
        ArrayList<Reservation> result = Reservation.fetchReservationsForShow(show);
        Theater theater = Theater.getTheater(show.theaterNumber);

        // Find the max row and seat number for the selected theater
        int maxSeatNumber = 0;
        int maxRowNumber = 0;
        for (Seat seat : theater.seats) {
            if (seat.rowNumber > maxRowNumber) maxRowNumber = seat.rowNumber;
            if (seat.seatNumber > maxSeatNumber) maxSeatNumber = seat.seatNumber;
        }

        // Define padding variables
        int xPadding = 6;
        int yPadding = 15;
        // Get the cinemaViewPane width and subtract the total padding for both x and y directions
        double programWidth =  cinemaViewPane.getWidth() - (xPadding*maxSeatNumber);
        double programHeight =  cinemaViewPane.getHeight() - (yPadding*maxRowNumber);

        // Calculate the size of each seat by dividing programWidth with the maxSeatNumber and maxRowNumber variables
        double sizeX = (double) programWidth/maxSeatNumber;
        double sizeY = (double) programHeight/maxRowNumber;
        double size = 0;
        // Use whichever value is the smallest for defining the size variable. This is to ensure all seats can fit on
        // screen while keeping them square
        if (sizeX < sizeY) size = sizeX;
        if (sizeY < sizeX) size = sizeY;

        double spacingLeft = (double)((programWidth-(size*maxSeatNumber))/2);
        double spacingTop = (double)(programHeight-(size*maxRowNumber))/2;

        cinemaImage.setFitWidth(xPadding*maxSeatNumber + maxSeatNumber*size);

        // Loop through each seat in the theater
        for (Seat seat : theater.seats) {

            boolean seatReserved = false;

            // Check each reservation for a mach with the current seats id
            for (Reservation reservation : result) {
                for (ReservedSpot reservedSpot : reservation.reservedSpots) {
                    if (reservedSpot.seatId == seat.seatId) {
                        seatReserved = true;
                    }
                }
            }

            // Make a new SeatBtn instance
            SeatBtn btn = new SeatBtn(seat.seatId);
            // Define some properties of this button, setting the size to the calculated size variable from before
            btn.setMaxSize(size, size);
            btn.setPrefSize(size, size);
            btn.setMinSize(size, size);
            btn.setLayoutX(xPadding*(seat.seatNumber) + spacingLeft + ((seat.seatNumber -1) * size));
            btn.setLayoutY(yPadding*(seat.rowNumber) + spacingTop + ((seat.rowNumber - 1) * size));
            // If the seat is either out of function or already reserved, set the seatStatus to 2. This will make
            // the seat not selectable and appear red
            if (seat.seatStatus == false || seatReserved == true) {
                btn.seatStatus = 2;
                btn.statusChanged();
            }
            // Add click event handler to the button
            btn.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> {
                SeatBtn ebtn = (SeatBtn)event.getSource();
                if (ebtn.seatStatus == 2) return;
                if (selectedSeatCount < reservableSeatCount && ebtn.seatStatus == 0) {
                    ebtn.seatStatus = 1;
                    ebtn.statusChanged();
                    selectedSeatCount++;
                }
                else if (btn.seatStatus == 1) {
                    ebtn.seatStatus = 0;
                    ebtn.statusChanged();
                    selectedSeatCount--;
                }
            });
            // Add the button to the buttons list
            btnList.add(btn);
            // Also add the button to the cinemaViewPane
            cinemaViewPane.getChildren().add(btn);
        }
    }



    // Inner private class that is used for handling seat selection events
    /*private class SeatSelectedHandler implements EventHandler<Event> {
        @Override
        public void handle(Event evt) {
            SeatBtn btn = (SeatBtn)evt.getSource();
            if (btn.seatStatus == 2) return;
            if (selectedSeatCount < reservableSeatCount && btn.seatStatus == 0) {
                btn.seatStatus = 1;
                btn.statusChanged();
                selectedSeatCount++;
            }
            else if (btn.seatStatus == 1) {
                btn.seatStatus = 0;
                btn.statusChanged();
                selectedSeatCount--;
            }
        }
    }*/

}
