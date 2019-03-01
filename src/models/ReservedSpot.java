package models;


import controllers.MySQLController;

import java.sql.ResultSet;
import java.util.ArrayList;

public class ReservedSpot {

    public int seatId;
    public int reservationId;


    public ReservedSpot(int seatId, int reservationId) {
        this.seatId = seatId;
        this.reservationId = reservationId;
    }

    public static ArrayList<ReservedSpot> getAllReservedSpots() {
        ArrayList<ReservedSpot> reservedSpots = new ArrayList<>();
        try {
            String sql = "SELECT * from reservedSpots";

            ResultSet rs = MySQLController.dbConnection.fetchData(sql);

            while (rs.next()) {
                int seatId = rs.getInt("seatId");
                int reservationId = rs.getInt("reservationId");
                reservedSpots.add(new ReservedSpot(seatId, reservationId));
            }
            rs.close();
        }
        catch(Exception e) {
            System.out.println("An error occured: " + e);
        }
        return reservedSpots;
    }

}
