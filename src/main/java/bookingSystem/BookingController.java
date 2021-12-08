package bookingSystem;

import bookingSystem.models.*;
import transactionSystem.PaymentController;
//import dummyPackage.UserDummy;

import java.sql.*;
import java.util.ArrayList;

/**
 * Class for a BookingController object which is the
 * controller for the bookingSystem package.
 * @author Tahsin Chowdhury
 */
public class BookingController {

    private ArrayList<Ticket> tickets; // list to contain all the tickets available in the database
    private ArrayList<Movie> movies; // list to contain all the movies in the database

    /**
     * Constructs a BookingController object and populates
     * the its data members by pulling data from the
     * database.
     */
    public BookingController() {
        tickets = new ArrayList<Ticket>();
        movies = new ArrayList<Movie>();
        loadMovieAndTheater();
        loadShowtimes();
        loadSeats();
        createTickets();
    }

    /**
     * Helper method that fetches the seats available for
     * every movie, showtime and theater combination and
     * populates the seats array in the corresponding
     * Showtime object.
     */
    private void loadSeats() {
        String query;

        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:sqlite.db");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        for (Movie m : movies) {
            ArrayList<Theater> theaters = m.getTheaters();
            for (Theater t : theaters) {
                ArrayList<Showtime> showtimes = t.getShowtimes();
                for (Showtime s : showtimes){
                    query = "SELECT DISTINCT seat FROM ticket WHERE movieName = '" + m.getName() + "'";
                    query += "AND theater = '" + t.getLocation() + "'";
                    query += "AND showtime = '" + s.getStartTime() + "'";

                    try{
                        Connection conn = c;
                        Statement stmt = null;
                        stmt = conn.createStatement();
                        ResultSet rs = stmt.executeQuery(query);

                        while (rs.next()){
                            int seatNumber = rs.getInt("seat");
                            System.out.println(seatNumber);
                            Seat seat = new Seat(seatNumber);
                            s.addSeat(seat);
                        }
                    } catch (SQLException e){
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * Helper method that fetches the showtimes available for
     * every movie and theater combination and
     * populates the seats array in the corresponding
     * Theater object.
     */
    private void loadShowtimes(){
        String query;

        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:sqlite.db");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        for (Movie m : movies){
            ArrayList<Theater> theaters = m.getTheaters();
            for (Theater t : theaters){
                query = "SELECT DISTINCT showtime FROM ticket WHERE movieName = '" + m.getName() + "'";
                query += "AND theater = '" + t.getLocation() + "'";
                try{
                    Connection conn = c;
                    Statement stmt = null;
                    stmt = conn.createStatement();
                    ResultSet rs = stmt.executeQuery(query);

                    while (rs.next()){
                        String theShowtime = rs.getString("showtime");
                        Showtime showtime = new Showtime(theShowtime, 36);
                        t.addShowtime(showtime);
                    }
                } catch (SQLException e){
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Helper method that fetches the movies and every
     * theater for each movie and populates
     * the theater array in the corresponding
     * Movie object before adding it to the movies array.
     */
    private void loadMovieAndTheater(){

        String query;

        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:sqlite.db");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        query = "SELECT DISTINCT movieName FROM ticket";
        try{
            Connection conn = c;
            Statement stmt = null;
            stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()){
                String movieName = rs.getString("movieName");
                Movie movie = new Movie(movieName);
                movies.add(movie);

            }
        } catch (SQLException e){
            e.printStackTrace();
        }

        for (Movie m : movies){
            query = "SELECT DISTINCT theater FROM ticket WHERE movieName = '" + m.getName() + "'";
            try{
                Connection conn = c;
                Statement stmt = null;
                stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query);

                while (rs.next()){
                    String theTheater = rs.getString("theater");
                    System.out.println(theTheater);
                    Theater theater = new Theater(theTheater);
                    m.addTheater(theater);
                }
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Helper function that creates ticket object for every
     * movie, theater, showtime and seat combination in the database
     * and populates the tickets array.
     */
    public void createTickets(){
        String query;

        Connection c = null;
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:sqlite.db");
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }

        for (Movie m : movies) {
            ArrayList<Theater> theaters = m.getTheaters();
            for (Theater t : theaters) {
                ArrayList<Showtime> showtimes = t.getShowtimes();
                for (Showtime s : showtimes){
                    ArrayList<Seat> seats = s.getSeats();
                    for (Seat st : seats){
                        query = "SELECT DISTINCT TicketID FROM ticket WHERE movieName = '" + m.getName() + "'";
                        query += "AND theater = '" + t.getLocation() + "'";
                        query += "AND showtime = '" + s.getStartTime() + "'";
                        query += "AND seat = '" + st.getSeatNumber() + "'";

                        try{
                            Connection conn = c;
                            Statement stmt = null;
                            stmt = conn.createStatement();
                            ResultSet rs = stmt.executeQuery(query);

                            while (rs.next()){
                                int ticketID = rs.getInt("TicketID");
                                Ticket ticket = new Ticket(ticketID, st, m, t, s);
                                tickets.add(ticket);
                            }
                        } catch (SQLException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns a ticket from the tickets ArrayList
     * that matches th specified ticket number
     * @param ticketNumber a value of type int representing the ticket number
     * @return a Ticket Object
     */
    public Ticket findTicket(int ticketNumber){
         for(Ticket t: tickets){
             if (t.getTicketNumber() == ticketNumber){
                 return t;
             }
         }
         return null;
    }

    /**
     * Books the specified ticket by making its seat unavailable and assigning it to a user id.
     * @param t a value of type int representing the ticket number
     * @param userID a value of type int representing userId
     */
    public void bookTicket(Ticket t, int userID){
        t.getSeat().setAvailable(false);
        t.setUserId(userID);
    }

    /**
     * Cancels a ticket by making its seat unavailable and assigning it to no user
     * @param t A Ticket object
     */
    public void cancelTicket(Ticket t){
        t.getSeat().setAvailable(true);
        t.setUserId(0);
        PaymentController p = new PaymentController();
        p.refund(p.findPayment(t));
    }

    /**
     * Find the ticket that matches the specified parameters.
     * @param m a Movie object
     * @param t a Theater object
     * @param s a Showtime object
     * @param st a Seat object
     * @return A Ticket object
     */
    public Ticket findTicket(Movie m, Theater t, Showtime s, Seat st){
        for(Ticket ticket: tickets){
            Movie ticketMovie = ticket.getMovie();
            Showtime ticketShowtime = ticket.getShowtime();
            Seat ticketSeat = ticket.getSeat();

            if (ticketMovie.getName() == m.getName() &&
                    ticketShowtime.getStartTime() == s.getStartTime() &&
                    ticketSeat.getSeatNumber() == st.getSeatNumber()){
                return ticket;
            }
        }
        return null;
    }

    /**
     * Returns a list of all the movies.
     * @return an ArrayList of Movie objects
     */
    public ArrayList<Movie> getAllMovies(){
        return movies;
    }

    /**
     * Returns a list of theaters showing the specified movie.
     * @param m a Movie object
     * @returnan ArrayList of Theater objects
     */
    public ArrayList<Theater> getAllTheaters(Movie m){
        return m.getTheaters();
    }

    /**
     * Returns a list of showtimes for the specified movie in the specified theater.
     * @param m a Movie object
     * @param t a Theater object
     * @return ArrayList of Showtime objects
     */
    public ArrayList<Showtime> getAllShowtimes(Movie m, Theater t){
        ArrayList<Showtime> showtimes = new ArrayList<Showtime>();
        for (Movie mv : movies){
            if (mv.getName().equals(m.getName())){
                ArrayList<Theater> theaters = new ArrayList<Theater>();
                theaters = mv.getTheaters();
                for (Theater th: theaters){
                    if (th.getLocation().equals(t.getLocation())){
                        showtimes = th.getShowtimes();
                    }
                }
            }
        }
        return showtimes;
    }

    /**
     * Returns a list of seats for the specified movie in the specified theater
     * at the specified showtime
     * @param m a Movie object
     * @param t a Theater object
     * @param s a Showtime object
     * @return an ArrayList of Seat objects
     */
    public ArrayList<Seat> getAllSeats(Movie m, Theater t, Showtime s){
        ArrayList<Seat> seats = new ArrayList<Seat>();
        ArrayList<Showtime> showtimes = getAllShowtimes(m, t);
        for (Showtime showtime : showtimes){
            if (showtime.getStartTime().equals(s.getStartTime())){
                seats = showtime.getSeats();
            }
        }
        return seats;
    }

    @Override
    public String toString() {
        return "BookingController{" +
                "tickets=" + tickets +
                ", movies=" + movies +
                '}';
    }
}
