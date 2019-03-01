package models;

/**
 * Created by Lucas on 27/11/15.
 */
public class Seat {

    public int rowNumber;
    public int seatNumber;
    public int seatId;
    public boolean seatStatus;


    public Seat(int rowNumber, int seatNumber, int seatID, int seatStatus) {
        this.rowNumber = rowNumber;
        this.seatNumber = seatNumber;
        this.seatId = seatID;
        if (seatStatus == 1) {this.seatStatus = true;} else {this.seatStatus = false;}
    }

}
