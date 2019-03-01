package models;

import controllers.MySQLController;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 27/11/15.
 */
public class Theater {

    public ArrayList<Seat> seats;


    public Theater(ArrayList<Seat> seats) {
        this.seats = seats;
    }


    public static Theater getTheater(int theaterNumber) {
        try {
            String sql = "SELECT seats.seatId, seats.seatStatus, rows.rowNumber, seats.seatNumber\n" +
                    "FROM seats, rows\n" +
                    "WHERE seats.rowId = rows.rowId AND rows.theaterId = " + theaterNumber + "\n" +
                    "GROUP BY seatId";

            ResultSet rs = MySQLController.dbConnection.fetchData(sql);

            ArrayList<Seat> seats = new ArrayList<>();

            while (rs.next()) {
                int rowNumber = rs.getInt("rowNumber");
                int seatNumber = rs.getInt("seatNumber");
                int seatId = rs.getInt("seatId");
                int seatStatus = rs.getInt("seatStatus");
                Seat seat = new Seat(rowNumber, seatNumber, seatId, seatStatus);
                seats.add(seat);
            }
            rs.close();
            Theater theater = new Theater(seats);
            return theater;
        } catch(Exception e) {
            System.out.println("An error occured: " + e);
        }
        return null;
    }

}
