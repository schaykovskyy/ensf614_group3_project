package userSystem;
import userSystem.models.*;
import java.util.ArrayList;
import java.sql.*;

public class UserController{
    public static void main(String[] args) {
        UserController uc = new UserController();
    }

    private ArrayList<RegisteredUser> listOfRegisteredUsers;
    private ArrayList<User> listOfUsers;
    /**
     * Constructor. creates listOfRegisteredUsers and listOfUsers.
     *  loads all users from db
     */ 
    public UserController(){
        listOfRegisteredUsers = new ArrayList<RegisteredUser>();
        listOfUsers = new ArrayList<User>();
        try {
            loadAllUsers();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }    

    /**
     *  Creates registeredUser and adds it to listOfRegisteredUsers 
     *  Adds it to DB
     * @param userId 
     * @param eMail
     * @param name
     * @param address
     */
    public void addRegisteredUser(int userId,String eMail, String name, String address){
        RegisteredUser ru = new RegisteredUser(name, address, userId, eMail);
        listOfRegisteredUsers.add(ru);
        try {
            addRegisteredUserToDB(userId, eMail, name, address);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Creates regular user and adds it to listOfUsers
     * Adds it to DB
     * @param userId
     * @param eMail
     */
    public void addUser(int userId, String eMail){
        User u = new User(userId, eMail);
        listOfUsers.add(u);
        try {
            addUserToDB(userId, eMail);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Finds and delets user object.
     * Deletes it from DB
     * @param userId
     */
    public void deleteUser(int userId){
        //find and remove user object
        for (RegisteredUser ru: listOfRegisteredUsers){
            if(ru.getUserId()==userId){
                listOfRegisteredUsers.remove(ru);
                break;
            }
        }
        for (User u: listOfUsers){
            if(u.getUserId()==userId){
                listOfUsers.remove(u);
                break;
            }
        }
        //remove user from database
        try{
            deleteUserFromDB(userId);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Adds ticket to users listOfTickets
     * Adds ticket booking to DB
     * @param user
     * @param ticket
     */
    public void addTicketBooking(User user, Ticket ticket){
        user.getListOfTickets().add(ticket);
        try {
            addTicketBookingToDB(user.getUserId(), ticket.getTicketId());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes ticket from users listOfTickets
     * Removes ticket booking from DB
     * @param user
     * @param ticket
     */
    public void removeTicketBooking(User user, Ticket ticket){
        user.getListOfTickets().remove(ticket);
        try {
            removeTicketBookingFromDB(user.getUserId(), ticket.getTicketId());
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    /**
     * Getter for listOfRegisteredUsers
     * @return listOfRegisteredUsers
     */
    public ArrayList<RegisteredUser> getListOfRegisteredUsers() {
        return this.listOfRegisteredUsers;
    }
    /**
     * Setter for list of registeredUsers
     * @param listOfRegisteredUsers
     */
    public void setListOfRegisteredUsers(ArrayList<RegisteredUser> listOfRegisteredUsers) {
        this.listOfRegisteredUsers = listOfRegisteredUsers;
    }

    /**
     * Getter for listOfUsers
     * @return listOfUsers
     */
    public ArrayList<User> getListOfUsers() {
        return this.listOfUsers;
    }
    /**
     * Setter for listOfUsers
     * @param listOfUsers
     */ 
    public void setListOfUsers(ArrayList<User> listOfUsers) {
        this.listOfUsers = listOfUsers;
    }
    //--------------------------------------Methods to work with DB----------------------------------------------
    /**
     * Connects to sqlite.db 
     * @return Connection
     */
    public Connection connect() {
        Connection conn = null;
        try {
            // db parameters
            String url = "jdbc:sqlite:sqlite.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }
    /**
     * Load all users from DB to listOfUsers and listOfRegiesteredUsers
     * @throws SQLException
     */
    private void loadAllUsers() throws SQLException{
        Connection conn = connect();
        String sql = String.format("SELECT * FROM Users"); 

        Statement stmt  = conn.createStatement();
        ResultSet rs    = stmt.executeQuery(sql);
        while (rs.next()) {
            int userID = rs.getInt("UserID");
            String email = rs.getString("email");
            String name = rs.getString("name");
            String address = rs.getString("name");
            Boolean isRegistered = rs.getBoolean("isRegistered");
            if(isRegistered){
                RegisteredUser ru = new RegisteredUser(name, address, userID, email);
                listOfRegisteredUsers.add(ru);
            }else{
                User u = new User(userID, email);
                listOfUsers.add(u);
            }
        }
        conn.close();
    }
    /**
     * Adds user to DB
     * @param userId
     * @param eMail
     * @throws SQLException
     */
    public void addUserToDB(int userId, String eMail) throws SQLException{
        Connection conn = connect();

        String sql = String.format("INSERT INTO users (UserID, email, isRegistered) VALUES (%d, '%s', 0)", userId, eMail); 
        Statement stmt = conn.createStatement();
        int rs = stmt.executeUpdate(sql);
        conn.close();
    }
    /**
     * Adds registeredUser to DB
     */
    public void addRegisteredUserToDB(int userId, String eMail, String name, String address) throws SQLException{
        Connection conn = connect();

        String sql = String.format("INSERT INTO users (UserID, email, name, address, isRegistered) VALUES (%d, '%s', '%s', '%s', 1)", userId, eMail, name, address); 
        Statement stmt = conn.createStatement();
        int rs = stmt.executeUpdate(sql);
        conn.close();
    }
    /**
     * Deletes User or RegisteredUser from DB
     * @param userId
     * @throws SQLException
     */
    public void deleteUserFromDB(int userId) throws SQLException{
        Connection conn = connect();

        String sql = String.format("DELETE FROM users WHERE UserID='%s'", userId); 
        Statement stmt = conn.createStatement();
        int rs = stmt.executeUpdate(sql);
        conn.close();
    }
    /**
     * Adds ticket booking to DB
     * @param userId
     * @param ticketId
     * @throws SQLException
     */
    public void addTicketBookingToDB(int userId, int ticketId)throws SQLException{
        Connection conn = connect();
        long bookingId= System.currentTimeMillis();

        String sql = String.format("INSERT INTO bookings (BookingId, userId, ticketId) VALUES (%d ,%d,%d)",bookingId, userId, ticketId); 
        Statement stmt = conn.createStatement();
        int rs = stmt.executeUpdate(sql);
        conn.close();
    }
    /**
     * Remove ticket booking from DFB
     * @param userId
     * @param ticketId
     * @throws SQLException
     */
    public void removeTicketBookingFromDB(int userId, int ticketId) throws SQLException{
        Connection conn = connect();

        String sql = String.format("DELETE FROM bookings WHERE userId= '%s' AND ticketId = '%s'", userId, ticketId); 
        Statement stmt = conn.createStatement();
        int rs = stmt.executeUpdate(sql);
        conn.close();
    }

    @Override
    public String toString() {
        return "UserController{" +
                "listOfRegisteredUsers=" + listOfRegisteredUsers +
                ", listOfUsers=" + listOfUsers +
                '}';
    }
}

