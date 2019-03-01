package controllers;

import javax.swing.plaf.nimbus.State;
import javax.xml.transform.Result;
import java.sql.*;

public class MySQLController {

    //  Database credentials
    private static final String DB = "AntonsDB";
    private static final String USER = "anton";
    private static final String PASS = "esp3218lak"; //TODO: Gør private

    // JDBC driver name and database URL
    private static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://mysql.itu.dk/" + DB;

    private Connection connection;

    public static MySQLController dbConnection = new MySQLController();

    public MySQLController() {
        try {
            DriverManager.registerDriver(new com.mysql.jdbc.Driver());
            connection = DriverManager.getConnection(DB_URL, USER, PASS);
            System.out.println("Succesfully connected to AntonsDB");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public ResultSet fetchData(String query) {
        try {
            Statement statement = connection.createStatement();
            String sql = query; // implicit semi-colon!
            ResultSet rs = statement.executeQuery(sql);
            return rs;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void executeQuery(String query) {
        try {
            Statement statement = connection.createStatement();
            String sql = query; // implicit semi-colon!
            statement.execute(sql);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    public int insertAndReturnId(String query) {
        try {
            Statement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
            stmt.executeUpdate(query);
            ResultSet rs = stmt.getGeneratedKeys();

            int result = 0;

            if (rs.next()){
                result = rs.getInt(1);
            }

            rs.close();
            return result;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


}