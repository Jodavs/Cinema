package views;

import javafx.scene.control.Button;

/**
 * Created by Lucas on 24/11/15.
 */
public class SeatBtn extends Button {

    public int seatStatus; // 0 = availiable, 1 = selected for reservation, 2 = already reservated
    public int seatId;

    public SeatBtn(int seatId) {
        seatStatus = 0;
        this.seatId = seatId;
        statusChanged();
    }

    public void statusChanged() {
       // String greenImage = Main.class.getResource("awd.jpg").toExternalForm();
        if (this.seatStatus == 0) this.setStyle("-fx-background-color: #5ab953;");
        if (this.seatStatus == 1) this.setStyle("-fx-background-color: #ff9900;");
        if (this.seatStatus == 2) this.setStyle("-fx-background-color: #ff3333");

    }
}
