package me.ethandelany;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SqlHandler {
    private final String host, database, username, password;
    private Connection con;
    private Statement stmt;
    private final ExecutorService executor;

    /**
     * Initializes support for an SQL database.
     *
     * @param host     Host IP Address of the server
     * @param database The database name
     * @param username The login username
     * @param password The login password
     */
    public SqlHandler(String host, String database, String username, String password) {
        this.host = host;
        this.database = database;
        this.username = username;
        this.password = password;
        executor = Executors.newFixedThreadPool(10);

        System.out.println("Connecting to SQL database...");
        connectAndSetup();
    }


    /**
     * Connects to the SQL database.
     */
    private void connectAndSetup() {
        Runnable runnable = () -> {
            try {
                con = DriverManager
                    .getConnection("jdbc:mysql://" + host + ":3306/" + database,
                        username, password);
                stmt = con.createStatement();
                System.out.println("SQL Connected!");
                setupTables();
                insertData();
            } catch (SQLException e) {
                System.out.println(e.toString());
                System.out.println("SQL Connection Failed.");
            }
        };
        executor.execute(runnable);
    }

    /**
     * Sets up all the tables required in the database.
     */
    public void setupTables() {
        String query;

        query = "CREATE TABLE IF NOT EXISTS STAFF ("
            + "StaffID INT PRIMARY KEY AUTO_INCREMENT,"
            + "FirstName NVARCHAR(50),"
            + "LastName NVARCHAR(50),"
            + "PhoneNumber CHAR(10),"
            + "Email NVARCHAR(100),"
            + "StartDate DATE,"
            + "EmployeeType NVARCHAR(100)"
            + ");";

        executeUpdate(query);

        query = "CREATE TABLE IF NOT EXISTS HOTEL ("
            + "HotelID INT PRIMARY KEY AUTO_INCREMENT,"
            + "Name NVARCHAR(50),"
            + "Street NVARCHAR(100),"
            + "City NVARCHAR(50),"
            + "State VARCHAR(2),"
            + "ZipCode VARCHAR(5),"
            + "NumberOfRooms INT NOT NULL DEFAULT 0,"
            + "HotelManagerID INT,"
            + "CONSTRAINT hotel_manager_fk FOREIGN KEY (HotelManagerID) REFERENCES STAFF(StaffID)"
            + ");";

        executeUpdate(query);

        query = "CREATE TABLE IF NOT EXISTS AMENITY ("
            + "AmenityID INT PRIMARY KEY AUTO_INCREMENT,"
            + "AmenityName NVARCHAR(100)"
            + ");";

        executeUpdate(query);

        query = "CREATE TABLE IF NOT EXISTS HOTEL_AMENITY ("
            + "HotelID INT,"
            + "AmenityID INT,"
            + "PRIMARY KEY (HotelID,AmenityID),"
            + "CONSTRAINT hotelamenity_hotelid_fk FOREIGN KEY (HotelID) REFERENCES HOTEL(HotelID),"
            + "CONSTRAINT hotelamenity_amenityid_fk FOREIGN KEY (AmenityID) REFERENCES AMENITY(AmenityID)"
            + ");";

        executeUpdate(query);

        query = "CREATE TABLE IF NOT EXISTS SHIFT ("
            + "ShiftID INT PRIMARY KEY AUTO_INCREMENT,"
            + "HotelID INT NOT NULL,"
            + "StaffID INT NOT NULL,"
            + "ShiftPosition NVARCHAR(100),"
            + "ShiftStart DATETIME,"
            + "ShiftEnd DATETIME,"
            + "CONSTRAINT shift_hotelid_fk FOREIGN KEY (HotelID) REFERENCES HOTEL(HotelID),"
            + "CONSTRAINT shift_staffid_fk FOREIGN KEY (StaffID) REFERENCES STAFF(StaffID)"
            + ");";

        executeUpdate(query);

        query = "CREATE TABLE IF NOT EXISTS ROOM ("
            + "RoomID INT PRIMARY KEY AUTO_INCREMENT,"
            + "HotelID INT NOT NULL,"
            + "RoomNumber INT NOT NULL,"
            + "RoomType NVARCHAR(50),"
            + "RoomPrice DOUBLE,"
            + "CONSTRAINT room_hotelid_fk FOREIGN KEY (HotelID) REFERENCES HOTEL(HotelID)"
            + ");";

        executeUpdate(query);

        query = "CREATE TABLE IF NOT EXISTS FEATURE ("
            + "FeatureID INT PRIMARY KEY AUTO_INCREMENT,"
            + "FeatureName NVARCHAR(100)"
            + ");";

        executeUpdate(query);

        query = "CREATE TABLE IF NOT EXISTS ROOM_FEATURE ("
            + "RoomID INT,"
            + "FeatureID INT,"
            + "PRIMARY KEY (RoomID, FeatureID),"
            + "CONSTRAINT roomfeature_roomid_fk FOREIGN KEY (RoomID) REFERENCES ROOM(RoomID),"
            + "CONSTRAINT roomfeature_featureid_fk FOREIGN KEY (FeatureID) REFERENCES FEATURE(FeatureID)"
            + ");";

        executeUpdate(query);

        query = "CREATE TABLE IF NOT EXISTS CUSTOMER ("
            + "CustomerID INT PRIMARY KEY AUTO_INCREMENT,"
            + "FirstName NVARCHAR(50),"
            + "LastName NVARCHAR(50),"
            + "DateOfBirth DATE,"
            + "Street NVARCHAR(100),"
            + "City NVARCHAR(50),"
            + "State VARCHAR(2),"
            + "ZipCode VARCHAR(5),"
            + "PhoneNumber CHAR(10),"
            + "Email NVARCHAR(100)"
            + ");";

        executeUpdate(query);

        query = "CREATE TABLE IF NOT EXISTS RESERVATION ("
            + "ReservationID INT PRIMARY KEY AUTO_INCREMENT,"
            + "CustomerID INT NOT NULL,"
            + "CheckIn DATETIME,"
            + "CheckOut DATETIME,"
            + "CONSTRAINT reservation_customerid_fk FOREIGN KEY (CustomerID) REFERENCES CUSTOMER(CustomerID)"
            + ");";

        executeUpdate(query);

        query = "CREATE TABLE IF NOT EXISTS ROOM_RESERVATION ("
            + "RoomID INT,"
            + "ReservationID INT,"
            + "PRIMARY KEY (RoomID, ReservationID),"
            + "CONSTRAINT roomreservation_roomid_fk FOREIGN KEY (RoomID) REFERENCES ROOM(RoomID),"
            + "CONSTRAINT roomreservation_reservationid_fk FOREIGN KEY (ReservationID) REFERENCES RESERVATION(ReservationID)"
            + ");";

        executeUpdate(query);

        query = "CREATE TABLE IF NOT EXISTS PAYMENT ("
            + "PaymentID INT PRIMARY KEY AUTO_INCREMENT,"
            + "ReservationID INT NOT NULL,"
            + "PaymentType NVARCHAR(100),"
            + "PaymentInfo NVARCHAR(200),"
            + "CONSTRAINT payment_reservationid_fk FOREIGN KEY (ReservationID) REFERENCES RESERVATION(ReservationID)"
            + ");";

        executeUpdate(query);
    }

    public void insertData() {
        // TODO: Insert all the dummy data right here.
    }

    /**
     * Executes a query, returns the results of said query.
     *
     * @param query The query to execute
     * @return Results of the query
     */
    public ResultSet executeQuery(String query) {
        try {
            System.out.println("[SQL] " + query);
            return stmt.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Executes the update query provided.
     *
     * @param query The update query to execute.
     * @return Success of the update
     */
    public boolean executeUpdate(String query) {
        try {
            System.out.println("[SQL] " + query);
            stmt.executeUpdate(query);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
