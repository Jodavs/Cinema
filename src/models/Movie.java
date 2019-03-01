package models;


import controllers.MySQLController;
import javafx.scene.image.Image;

import java.sql.ResultSet;

public class Movie {

    public String movieTitle;
    public Image moviePoster;
    public String moviePosterUrl;
    public int movieLength;


    public Movie(String movieTitle, String moviePosterUrl, int movieLength) {
        this.movieTitle = movieTitle;
        this.moviePosterUrl = moviePosterUrl;
        moviePoster = new Image(moviePosterUrl);
        this.movieLength = movieLength;
    }

    public static Movie getById(int id) {
        String sql = "SELECT * FROM movies WHERE movieId='" + id + "'";
        ResultSet rs = MySQLController.dbConnection.fetchData(sql);
        try {
            rs.next();
            return new Movie(rs.getString("movieTitle"), rs.getString("moviePosterUrl"), rs.getInt("movieLength"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean isSameMovie(Movie other) {
        if (this.movieTitle.equals(other.movieTitle)) return true;
        return false;
    }
}
