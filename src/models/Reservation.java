package models;

import controllers.MySQLController;
import sample.Main;

import java.lang.reflect.Array;
import java.sql.ResultSet;
import java.util.ArrayList;

public class Reservation {

    public int id;
    public String phoneNumber;
    public ArrayList<ReservedSpot> reservedSpots;
    private int showId;
    public static ArrayList<Reservation> reservations = new ArrayList<>();


    public Show getShow() {
        return Show.getById(showId);
    }

    public Reservation(int id, String phoneNumber, ArrayList<ReservedSpot> reservedSpots, int showId) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.reservedSpots = reservedSpots;
        this.showId = showId;
    }


    public boolean deleteReservation() {
        //TODO: Insert MySQL stuff
        return true;
    }


    public boolean editReservation() {
        //TODO: Insert MySQL stuff
        return true;
    }


    public static void fetchReservations() {
        try {
            String sql = "SELECT * from reservations";

            ResultSet rs = MySQLController.dbConnection.fetchData(sql);

            ArrayList<ReservedSpot> allReservedSpots = ReservedSpot.getAllReservedSpots();

            int counter = 0;
            while (rs.next()) {
                counter++;
                System.out.println("Loading reservation #" + counter);
                int reservationId = rs.getInt("reservationId");
                int showId = rs.getInt("showId");
                String phoneNumber = rs.getString("phoneNumber");
                //ArrayList<ReservedSpot> spots = fetchReservedSpots(reservationId);
                ArrayList<ReservedSpot> spots = fetchReservedSpots(reservationId, allReservedSpots);
                reservations.add(new Reservation(reservationId, phoneNumber, spots, showId));
            }
            rs.close();
        } catch(Exception e) {
            System.out.println("An error occured: " + e);
        }
    }


    public static ArrayList<Reservation> getReservationsForPartialPhoneNumber(String number) {
        ArrayList<Reservation> result = new ArrayList<>();

        for (Reservation r : reservations) {
            if (r.phoneNumber.contains(number)) {
                result.add(r);
            }
        }
        return result;
    }


    public static ArrayList<Reservation> fetchReservationsForShow(Show show) {
        ArrayList<Reservation> result = new ArrayList<>();

        for (Reservation r : reservations) {
            if (show.id == r.showId) {
                result.add(r);
            }
        }
        return result;
    }


    public static ArrayList<ReservedSpot> fetchReservedSpots(int reservationId, ArrayList<ReservedSpot> allReservedSpots) {
        ArrayList<ReservedSpot> arrReservedSpots = new ArrayList<>();

        for (ReservedSpot reservedSpot : allReservedSpots) {
            if (reservedSpot.reservationId == reservationId) {arrReservedSpots.add(reservedSpot);}
        }

        return arrReservedSpots;
    }


    @Override
    public String toString() {
        return "Telefon: " + phoneNumber + "\t\tAntal: " + reservedSpots.size();
    }


    public static boolean reserveSeats(ArrayList<ReservedSpot> reservedSpots, String phoneNumber, int showId) {

        try {
            int reservId = MySQLController.dbConnection.insertAndReturnId("INSERT INTO `AntonsDB`.`reservations` (`showId`, `phoneNumber`) VALUES ('" + showId + "', '" + phoneNumber + "')"); //Vi kan ikke bruge SQL last_insert_id pga. der ikke supporteres ml queries

            for (ReservedSpot reservedSpot : reservedSpots) {
                reservedSpot.reservationId = reservId;
                MySQLController.dbConnection.executeQuery("INSERT INTO `AntonsDB`.`reservedSpots` (`reservationId`, `seatId`) VALUES ('" + reservId + "', '" + reservedSpot.seatId + "')");
            }

            Reservation reservation = new Reservation(reservId, phoneNumber, reservedSpots, showId);
            reservations.add(reservation);
        }
        catch(Exception e) {
            System.out.println("An error occured: " + e);
        }
        return true;
    }


}
