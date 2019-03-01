package models;

import controllers.MySQLController;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;


/**
 * Class that models the shows table in the database
 */
public class Show {

    public Movie movie;
    public LocalDateTime timestamp;
    public int length;
    public int theaterNumber;
    public int id;
    public int amountOfSeats;
    public static ArrayList<Show> shows = new ArrayList<>();
    public static ArrayList<LocalDate> datesCached = new ArrayList<>(); // List for easy lookup of whether a dates shows has been cached


    public Show(Movie movie, LocalDateTime timestamp, int length, int theaterNumber, int id, int amountOfSeats) {
        this.movie = movie;
        this.timestamp = timestamp;
        this.length = length;
        this.theaterNumber = theaterNumber;
        this.id = id;
        this.amountOfSeats = amountOfSeats;
    }


    public static Show getById(int id) {
        for (Show s : shows) {
            if (id == s.id) {
                return s;
            }
        }

        System.out.println("called get id");
        String sql= "SELECT * FROM shows WHERE showId='" + id + "'";
        ResultSet rs = MySQLController.dbConnection.fetchData(sql);

        try {
            rs.next();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");
            LocalDateTime showTimestamp = LocalDateTime.parse(rs.getString("showTimestamp"), formatter);

            Show newShow = new Show(Movie.getById(rs.getInt("movieId")), showTimestamp, rs.getInt("showLength"), rs.getInt("theaterId"), rs.getInt("showId"), 1);
            shows.add(newShow);
            return newShow;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get shows at a date already cached. Function is unchecked which means it will return an empty list if the shows on
     * the specified date have not been cached.
     * @param d Specified date
     * @return A list of shows
     */
    private static ArrayList<Show> getCachedShowsAtDate(LocalDate d) {
        ArrayList<Show> result = new ArrayList<>();
        // Run through each show in cached ArrayList<Show> shows
        for (Show s : shows) {
            // If the shows date matches the parameter date add the show to result
            if (s.timestamp.toLocalDate().equals(d)) {
                result.add(s);
            }
        }
        return result;
    }

    /**
     * Get shows not yet cached from the database.
     * @param d Specified date
     * @return A list of shows
     */
    private static ArrayList<Show> fetchShowsAtDate(LocalDate d) {
        ArrayList<Show> result = new ArrayList<>();

        // Format the parameter to a date format accepted by the sql query language
        String date = d.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        try {
            String sql = "SELECT *, (SELECT movieTitle from movies where movies.movieId = shows.movieId LIMIT 1) AS movieTitle, (SELECT moviePosterUrl from movies where movies.movieId = shows.movieId LIMIT 1) AS moviePosterUrl, (select COUNT(seatId) FROM seats where seats.theaterId = shows.theaterId LIMIT 1) AS amountOfSeats, (select movieLength FROM movies where movies.movieId = shows.movieId LIMIT 1) AS movieLength FROM shows where DATE(`showTimestamp`) = DATE('" + date + "')";

            ResultSet rs = MySQLController.dbConnection.fetchData(sql);

            // Run while theres still rows in the query results
            while (rs.next()) {
                // Get the shows id
                int showId = rs.getInt("showId");
                // Get the movies title, posterUrl and movieId
                String movieTitle = rs.getString("movieTitle");
                String moviePosterUrl = rs.getString("moviePosterUrl");
                int movieId = rs.getInt("movieId"); //TODO: Slet hvis det ikke skal bruges aligevel. Det bruges dog i querien.

                // Make a new datetime formatter to convert the sql datetime string into a LocalDateTime object
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S");

                LocalDateTime showTimestamp = LocalDateTime.parse(rs.getString("showTimestamp"), formatter);

                //Get the shows length, amount of seats, movie length and theater number
                int showLength = rs.getInt("showLength");
                int amountOfSeats = rs.getInt("amountOfSeats");
                int movieLength = rs.getInt("movieLength");
                int theaterNumber = rs.getInt("theaterId"); //TODO: Ændre til theaterID og sæt theaterNumber til theanumber som i oversigten.

                // Create new Movie object
                Movie movie = new Movie(movieTitle, moviePosterUrl, movieLength);
                // Create new Show object with the movie as a parameter and add it to the result ArrayList
                result.add(new Show(movie, showTimestamp, showLength, theaterNumber, showId, amountOfSeats));
            }

            // Close resultset
            rs.close();
        }
        catch(Exception e) {
            System.out.println("An error occured: " + e);
        }
        // Return the result
        return result;
    }

    /**
     * Gets the shows on the date specified by the parameter either by reading the cache or by fetching new results
     * from the database.
     * @param d The desired date.
     * @return An ArrayList<Show> containing all the shows on the date specified.
     */
    public static ArrayList<Show> getShowsAtDate(LocalDate d) {
        ArrayList<Show> result;

        // Check if cache already has the shows for the date
        if (datesCached.contains(d)) {
            // Get cached shows
            result = getCachedShowsAtDate(d);
        } else {
            // Else get the shows from the database
            result = fetchShowsAtDate(d);
            // and add them to the cache
            shows.addAll(result);
            // add the date to list of cached dates
            datesCached.add(d);
        }
        // Return shows
        return result;
    }

}
