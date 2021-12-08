package database;

import java.sql.*;

/**
 * binding object to jdbc src
 * https://docs.oracle.com/cd/E19182-01/821-1069/6nm3256a2/index.html
 */

public class Controller {
    private static void createSQLiteDB() {
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:sqlite.db");
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }

    public static Connection getConnection() {
        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:sqlite.db");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return c;
    }

    public static void insertIntoPayment(Integer paymentID, double amount, Integer userID, Integer ticketID) throws SQLException {
        Connection c = Controller.getConnection();
        String sql = "INSERT INTO payment(paymentID, amount, userID, ticketID) " +
                String.format("Values (%o, %f, %o, %o)",
                        paymentID,
                        amount,
                        userID,
                        ticketID);
        System.out.println(sql);
        Connection conn = c;
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(sql);
    }

    public static void insertIntoCredits(Integer userId, double credit, String expiryDate) throws SQLException {
        Connection conn = Controller.getConnection();
        String sql = "INSERT INTO credits(UserID, Credit, expiryDate) " +
                String.format("Values (%o, %f, \"%s\");", userId, credit, expiryDate);
        System.out.println(sql);
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(sql);
        conn.close();
        stmt.close();
    }

    public static void deleteFromPayment(Integer paymentID) throws SQLException {
        Connection conn = Controller.getConnection();
        String sql = "DELETE FROM Payment " +
                String.format("WHERE paymentID = %s ;", paymentID);
        System.out.println(sql);
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(sql);
        conn.close();
        stmt.close();
    }
}
