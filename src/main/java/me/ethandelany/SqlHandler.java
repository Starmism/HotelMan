package me.ethandelany;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.xml.transform.Result;

public class SqlHandler {
    private HotelMan hm;
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
                System.out.println("[SQL] FAILED TO CONNECT.");
                System.out.println("PROGRAM WILL NOT FUNCTION!!!!");
                e.printStackTrace();
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
            + "CONSTRAINT room_hotelid_fk FOREIGN KEY (HotelID) REFERENCES HOTEL(HotelID),"
            + "CONSTRAINT room_hotelnum_unique UNIQUE (HotelID, RoomNumber)"
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

    /**
     * Inserts all dummy test data if there is no data in the table.
     */
    public void insertData() {
        String query;

        query = "INSERT INTO STAFF VALUES (1, 'Reagen', 'Hryskiewicz', '2436043858', 'rhryskiewicz0@techcrunch.com', '2017-04-02', 'Housekeeping'),"
            + " (2, 'Carroll', 'Forker', '4609583031', 'cforker1@over-blog.com', '2020-02-17', 'Hotel-Manager'),"
            + " (3, 'Charlotte', 'Scogin', '8029578005', 'cscogin2@engadget.com', '2013-04-07', 'Room-Attendant'),"
            + " (4, 'Cass', 'Poyner', '7134724310', 'cpoyner3@bandcamp.com', '2018-05-06', 'Housekeeping'),"
            + " (5, 'Joannes', 'Doleman', '2197719589', 'jdoleman4@yahoo.com', '2011-12-15', 'Room-Attendant'),"
            + " (6, 'Raul', 'Thalmann', '2959143066', 'rthalmann5@pcworld.com', '2018-07-21', 'Room-Attendant'),"
            + " (7, 'Winona', 'Racher', '7026315941', 'wracher6@joomla.org', '2016-04-16', 'Room-Attendant'),"
            + " (8, 'Meagan', 'Kinkead', '2121063878', 'mkinkead7@google.com.au', '2017-04-01', 'Housekeeping'),"
            + " (9, 'Brandais', 'Ratcliff', '5884168025', 'bratcliff8@istockphoto.com', '2016-09-09', 'Concierge'),"
            + " (10, 'Alleen', 'Liverseege', '3715786459', 'aliverseege9@taobao.com', '2015-08-12', 'Front-Desk'),"
            + " (11, 'Wilie', 'Gardener', '1043882463', 'wgardenera@constantcontact.com', '2014-02-07', 'Room-Attendant'),"
            + " (12, 'Dominick', 'MacGeaney', '8854383987', 'dmacgeaneyb@opensource.org', '2012-02-21', 'Hotel-Manager'),"
            + " (13, 'Sherm', 'Mary', '4294885739', 'smaryc@webs.com', '2016-06-24', 'Hotel-Manager'),"
            + " (14, 'Emlyn', 'Jansey', '3683999725', 'ejanseyd@naver.com', '2014-04-30', 'Housekeeping'),"
            + " (15, 'Jourdain', 'Standage', '6471151552', 'jstandagee@freewebs.com', '2017-04-09', 'Concierge'),"
            + " (16, 'Perren', 'Ainsby', '3224627637', 'painsbyf@java.com', '2015-06-18', 'Room-Attendant'),"
            + " (17, 'Marsiella', 'Pensom', '3466867074', 'mpensomg@deliciousdays.com', '2019-11-27', 'Housekeeping'),"
            + " (18, 'Suellen', 'Jorden', '5993180591', 'sjordenh@yahoo.co.jp', '2017-11-22', 'Front-Desk'),"
            + " (19, 'Denyse', 'Thomtson', '9468180636', 'dthomtsoni@illinois.edu', '2015-01-13', 'Front-Desk'),"
            + " (20, 'Alistair', 'Langtree', '2129087766', 'alangtreej@rediff.com', '2014-07-06', 'Room-Attendant');";
        insertSql("STAFF", query);

        query = "INSERT INTO HOTEL VALUES (1, 'Double Tree', '8756 Helena Court', 'Charleston', 'WV', '25362', 0, 2),"
            + " (2, 'Four Seasons', '70 Londonderry Point', 'Yakima', 'WA', '98907', 0, 13),"
            + " (3, 'Hyatt', '648 Cardinal Center', 'Milwaukee', 'WI', '53277', 0, 12),"
            + " (4, 'Westin', '29335 Westend Lane', 'Aurora', 'CO', '80045', 0, 12),"
            + " (5, 'Embassy Suites', '988 Sunnyside Drive', 'Port Saint Lucie', 'FL', '34985', 0, 2);";
        insertSql("HOTEL", query);

        query = "INSERT INTO AMENITY (AmenityName) VALUES ('Free Parking'),"
            + " ('Free Breakfast'),"
            + " ('Bar'),"
            + " ('Gym'),"
            + " ('High-Speed Internet'),"
            + " ('Free Wi-Fi');";
        insertSql("AMENITY", query);

        query = "INSERT INTO HOTEL_AMENITY VALUES (1, 1),"
            + " (1, 3),"
            + " (1, 5),"
            + " (1, 6),"
            + " (2, 2),"
            + " (2, 3),"
            + " (2, 4),"
            + " (3, 1),"
            + " (3, 3),"
            + " (3, 5),"
            + " (3, 6),"
            + " (4, 5),"
            + " (4, 6),"
            + " (5, 1),"
            + " (5, 2),"
            + " (5, 3),"
            + " (5, 4),"
            + " (5, 5),"
            + " (5, 6);";
        insertSql("HOTEL_AMENITY", query);

        query = "INSERT INTO SHIFT VALUES (1, 2, 13, 'Hotel-Manager', '2021-04-11 22:03:10', '2021-04-12 06:03:10'),"
            + " (2, 2, 4, 'Concierge', '2022-09-04 07:08:34', '2022-09-04 15:08:34'),"
            + " (3, 4, 11, 'Housekeeping', '2021-12-06 05:06:55', '2021-12-06 13:06:55'),"
            + " (4, 2, 6, 'Concierge', '2022-09-28 04:19:04', '2022-09-28 12:19:04'),"
            + " (5, 1, 10, 'Front-Desk', '2021-01-20 23:45:31', '2021-01-21 07:45:31'),"
            + " (6, 2, 14, 'Front-Desk', '2020-06-27 00:59:02', '2020-06-27 08:59:02'),"
            + " (7, 1, 2, 'Hotel-Manager', '2022-07-08 00:38:15', '2022-07-08 08:38:15'),"
            + " (8, 4, 16, 'Housekeeping', '2020-10-21 18:53:30', '2020-10-22 02:53:30'),"
            + " (9, 1, 16, 'Room-Attendant', '2022-02-09 18:40:23', '2022-02-10 02:40:23'),"
            + " (10, 5, 4, 'Room-Attendant', '2021-06-15 00:18:42', '2021-06-15 08:18:42'),"
            + " (11, 2, 14, 'Front-Desk', '2021-04-10 05:42:18', '2021-04-10 13:42:18'),"
            + " (12, 1, 15, 'Front-Desk', '2021-07-07 14:33:47', '2021-07-07 22:33:47'),"
            + " (13, 1, 11, 'Room-Attendant', '2020-07-04 19:42:13', '2020-07-05 03:42:13'),"
            + " (14, 4, 11, 'Room-Attendant', '2021-05-13 10:09:01', '2021-05-13 18:09:01'),"
            + " (15, 3, 20, 'Front-Desk', '2022-09-21 23:45:28', '2022-09-22 07:45:28'),"
            + " (16, 1, 8, 'Concierge', '2022-04-05 13:13:11', '2022-04-05 21:13:11'),"
            + " (17, 1, 2, 'Hotel-Manager', '2020-01-16 02:22:01', '2020-01-16 10:22:01'),"
            + " (18, 4, 2, 'Hotel-Manager', '2020-05-27 12:12:11', '2020-05-27 20:12:11'),"
            + " (19, 4, 14, 'Housekeeping', '2021-04-26 06:05:20', '2021-04-26 14:05:20'),"
            + " (20, 3, 18, 'Front-Desk', '2020-06-19 09:50:46', '2020-06-19 17:50:46'),"
            + " (21, 3, 14, 'Concierge', '2021-10-11 19:47:28', '2021-10-12 03:47:28'),"
            + " (22, 3, 12, 'Hotel-Manager', '2022-04-13 11:51:07', '2022-04-13 19:51:07'),"
            + " (23, 5, 13, 'Hotel-Manager', '2022-05-29 19:03:12', '2022-05-30 03:03:12'),"
            + " (24, 3, 13, 'Hotel-Manager', '2021-08-30 20:01:06', '2021-08-31 04:01:06'),"
            + " (25, 3, 18, 'Concierge', '2022-04-25 21:48:00', '2022-04-26 05:48:00'),"
            + " (26, 3, 20, 'Room-Attendant', '2020-03-23 23:01:12', '2020-03-24 07:01:12'),"
            + " (27, 2, 13, 'Hotel-Manager', '2021-08-16 05:32:55', '2021-08-16 13:32:55'),"
            + " (28, 2, 4, 'Front-Desk', '2021-02-26 17:56:48', '2021-02-27 01:56:48'),"
            + " (29, 5, 6, 'Front-Desk', '2021-08-11 23:59:15', '2021-08-12 07:59:15'),"
            + " (30, 5, 13, 'Hotel-Manager', '2019-12-23 10:42:08', '2019-12-23 18:42:08'),"
            + " (31, 3, 6, 'Housekeeping', '2021-09-04 12:35:01', '2021-09-04 20:35:01'),"
            + " (32, 5, 19, 'Housekeeping', '2021-04-07 11:30:45', '2021-04-07 19:30:45'),"
            + " (33, 1, 9, 'Front-Desk', '2020-08-26 12:05:48', '2020-08-26 20:05:48'),"
            + " (34, 4, 12, 'Hotel-Manager', '2022-04-26 23:45:16', '2022-04-27 07:45:16'),"
            + " (35, 3, 9, 'Room-Attendant', '2020-10-24 06:38:08', '2020-10-24 14:38:08'),"
            + " (36, 5, 2, 'Hotel-Manager', '2021-11-23 13:20:20', '2021-11-23 21:20:20'),"
            + " (37, 5, 7, 'Room-Attendant', '2022-09-11 05:00:13', '2022-09-11 13:00:13'),"
            + " (38, 2, 5, 'Concierge', '2022-09-01 02:17:48', '2022-09-01 10:17:48'),"
            + " (39, 4, 5, 'Room-Attendant', '2022-04-27 02:51:08', '2022-04-27 10:51:08'),"
            + " (40, 1, 4, 'Concierge', '2022-02-25 21:59:44', '2022-02-26 05:59:44'),"
            + " (41, 4, 3, 'Room-Attendant', '2022-08-27 14:59:52', '2022-08-27 22:59:52'),"
            + " (42, 3, 15, 'Front-Desk', '2020-02-21 19:30:39', '2020-02-22 03:30:39'),"
            + " (43, 3, 4, 'Concierge', '2021-07-04 15:57:27', '2021-07-04 23:57:27'),"
            + " (44, 1, 2, 'Hotel-Manager', '2022-01-26 08:43:29', '2022-01-26 16:43:29'),"
            + " (45, 4, 20, 'Room-Attendant', '2020-01-30 10:25:28', '2020-01-30 18:25:28'),"
            + " (46, 4, 15, 'Front-Desk', '2022-01-19 12:02:46', '2022-01-19 20:02:46'),"
            + " (47, 4, 19, 'Room-Attendant', '2020-11-07 07:33:39', '2020-11-07 15:33:39'),"
            + " (48, 5, 12, 'Hotel-Manager', '2022-05-08 02:45:07', '2022-05-08 10:45:07'),"
            + " (49, 3, 12, 'Hotel-Manager', '2020-08-08 07:54:01', '2020-08-08 15:54:01'),"
            + " (50, 3, 10, 'Front-Desk', '2020-10-23 00:35:10', '2020-10-23 08:35:10');";
        insertSql("SHIFT", query);

        query = "INSERT INTO ROOM VALUES (1, 3, '220', 'Single-King', '101.40'),"
            + " (2, 2, '320', 'Single-King', '326.95'),"
            + " (3, 3, '510', 'Double-Twin', '445.37'),"
            + " (4, 2, '403', 'Single-King', '282.33'),"
            + " (5, 1, '500', 'Double-Queen', '381.42'),"
            + " (6, 2, '206', 'Double-Queen', '436.65'),"
            + " (7, 3, '329', 'Single-Queen', '268.69'),"
            + " (8, 5, '105', 'Single-Twin', '387.44'),"
            + " (9, 5, '219', 'Single-Twin', '431.39'),"
            + " (10, 4, '502', 'Single-Queen', '382.43'),"
            + " (11, 3, '208', 'Double-Queen', '77.26'),"
            + " (12, 3, '328', 'Single-Queen', '154.02'),"
            + " (13, 1, '301', 'Double-Queen', '376.45'),"
            + " (14, 1, '501', 'Single-Queen', '142.60'),"
            + " (15, 2, '103', 'Single-Queen', '494.56'),"
            + " (16, 1, '321', 'Double-Queen', '457.50'),"
            + " (17, 1, '315', 'Double-Twin', '261.66'),"
            + " (18, 5, '325', 'Single-Queen', '492.36'),"
            + " (19, 4, '227', 'Single-Queen', '465.67'),"
            + " (20, 1, '316', 'Single-Queen', '61.54'),"
            + " (21, 3, '200', 'Single-Queen', '474.98'),"
            + " (22, 3, '218', 'Single-Queen', '85.45'),"
            + " (23, 2, '407', 'Double-Queen', '439.61'),"
            + " (24, 2, '200', 'Single-Queen', '376.81'),"
            + " (25, 2, '203', 'Single-Queen', '431.63'),"
            + " (26, 1, '204', 'Double-Queen', '264.10'),"
            + " (27, 4, '519', 'Double-Queen', '457.07'),"
            + " (28, 5, '405', 'Double-Twin', '349.94'),"
            + " (29, 4, '421', 'Single-Twin', '261.99'),"
            + " (30, 2, '129', 'Single-Queen', '261.09'),"
            + " (31, 4, '228', 'Single-King', '117.94'),"
            + " (32, 1, '213', 'Single-Twin', '315.37'),"
            + " (33, 2, '221', 'Single-King', '336.34'),"
            + " (34, 1, '525', 'Single-King', '104.44'),"
            + " (35, 3, '313', 'Double-Queen', '433.37'),"
            + " (36, 1, '304', 'Single-Queen', '313.38'),"
            + " (37, 3, '110', 'Single-King', '443.38'),"
            + " (38, 2, '106', 'Double-Queen', '157.73'),"
            + " (39, 4, '113', 'Single-Queen', '206.26'),"
            + " (40, 5, '402', 'Single-Queen', '388.70'),"
            + " (41, 1, '307', 'Double-Twin', '338.92'),"
            + " (42, 1, '312', 'Single-Queen', '498.29'),"
            + " (43, 1, '404', 'Double-Twin', '431.73'),"
            + " (44, 4, '105', 'Single-Twin', '91.31'),"
            + " (45, 3, '420', 'Double-Queen', '304.30'),"
            + " (46, 3, '106', 'Single-King', '42.53'),"
            + " (47, 4, '215', 'Double-Queen', '436.60'),"
            + " (48, 2, '416', 'Double-Twin', '458.86'),"
            + " (49, 1, '205', 'Single-King', '253.60'),"
            + " (50, 3, '124', 'Single-King', '237.45');";
        insertSql("ROOM", query);

        query = "INSERT INTO FEATURE (FeatureName) VALUES ('Disability-Accessible'),"
            + " ('Non-Smoking'),"
            + " ('Balcony'),"
            + " ('Mini-Bar'),"
            + " ('Smart-TV'),"
            + " ('Coffee-Machine');";
        insertSql("FEATURE", query);

        query = "INSERT INTO ROOM_FEATURE VALUES (44, 2),"
            + " (25, 1),"
            + " (32, 4),"
            + " (5, 2),"
            + " (46, 5),"
            + " (23, 3),"
            + " (45, 5),"
            + " (23, 6),"
            + " (26, 5),"
            + " (34, 6),"
            + " (45, 4),"
            + " (44, 1),"
            + " (29, 2),"
            + " (32, 5),"
            + " (1, 1),"
            + " (45, 2),"
            + " (43, 1),"
            + " (24, 4),"
            + " (11, 6),"
            + " (19, 1),"
            + " (5, 1),"
            + " (49, 1),"
            + " (14, 2),"
            + " (43, 2),"
            + " (14, 4),"
            + " (11, 1),"
            + " (48, 1),"
            + " (1, 5),"
            + " (28, 2),"
            + " (10, 5),"
            + " (45, 1),"
            + " (10, 6),"
            + " (36, 1),"
            + " (5, 3),"
            + " (29, 5),"
            + " (25, 5),"
            + " (26, 1),"
            + " (20, 4),"
            + " (34, 3),"
            + " (5, 4),"
            + " (8, 3),"
            + " (11, 4),"
            + " (34, 5),"
            + " (14, 3),"
            + " (11, 2),"
            + " (44, 4),"
            + " (30, 6),"
            + " (9, 6),"
            + " (19, 2),"
            + " (26, 4),"
            + " (46, 3),"
            + " (14, 5),"
            + " (4, 5),"
            + " (28, 1),"
            + " (8, 6),"
            + " (8, 1),"
            + " (32, 3),"
            + " (7, 2),"
            + " (47, 5),"
            + " (9, 4),"
            + " (27, 6),"
            + " (8, 2),"
            + " (45, 3),"
            + " (9, 3),"
            + " (35, 5),"
            + " (30, 2),"
            + " (32, 1),"
            + " (50, 4),"
            + " (36, 2),"
            + " (10, 2),"
            + " (29, 3),"
            + " (20, 2),"
            + " (35, 1),"
            + " (44, 3),"
            + " (42, 1),"
            + " (10, 3),"
            + " (31, 3),"
            + " (32, 6),"
            + " (39, 2),"
            + " (7, 6),"
            + " (29, 6),"
            + " (24, 6),"
            + " (40, 5),"
            + " (33, 2),"
            + " (32, 2),"
            + " (48, 5),"
            + " (15, 3),"
            + " (20, 5),"
            + " (48, 6),"
            + " (20, 3),"
            + " (48, 2),"
            + " (15, 4),"
            + " (12, 3),"
            + " (33, 3),"
            + " (3, 6),"
            + " (50, 5),"
            + " (8, 5),"
            + " (12, 1),"
            + " (10, 1),"
            + " (48, 3);";
        insertSql("ROOM_FEATURE", query);

        query = "INSERT INTO CUSTOMER VALUES (1, 'Archy', 'von Nassau', '1984-09-03', '83182 Forest Point', 'Miami', 'FL', '33124', '7866318688', 'avonnassau0@51.la'),"
            + " (2, 'Mufinella', 'Pedroli', '1990-06-04', '942 Fremont Plaza', 'New Orleans', 'LA', '70154', '5048198732', 'mpedroli1@fastcompany.com'),"
            + " (3, 'Salome', 'Hebblewhite', '1958-10-23', '903 Waywood Center', 'Lancaster', 'CA', '93584', '6616272759', 'shebblewhite2@adobe.com'),"
            + " (4, 'Bethena', 'Ratie', '1985-05-30', '50 Ohio Way', 'Washington', 'DC', '20260', '2025325702', 'bratie3@dailymotion.com'),"
            + " (5, 'Nara', 'Reubens', '1990-01-26', '1572 Glendale Drive', 'Houston', 'TX', '77228', '7138634639', 'nreubens4@dagondesign.com'),"
            + " (6, 'Spike', 'McCalister', '1997-02-05', '23585 Granby Plaza', 'Tulsa', 'OK', '74103', '9187164062', 'smccalister5@163.com'),"
            + " (7, 'Estrellita', 'Pady', '1963-07-22', '328 Bonner Park', 'Pasadena', 'TX', '77505', '7131652850', 'epady6@java.com'),"
            + " (8, 'Glynda', 'Laneham', '1961-12-27', '27 Summit Plaza', 'Macon', 'GA', '31296', '4781561840', 'glaneham7@microsoft.com'),"
            + " (9, 'Cazzie', 'Lancaster', '1994-12-24', '7 Dapin Avenue', 'Tallahassee', 'FL', '32304', '8501887060', 'clancaster8@virginia.edu'),"
            + " (10, 'Jennifer', 'Likly', '1970-12-22', '38582 Jenifer Crossing', 'Lubbock', 'TX', '79452', '8064228176', 'jlikly9@vkontakte.ru'),"
            + " (11, 'Leonore', 'Alden', '2011-06-20', '6 Stang Junction', 'San Diego', 'CA', '92132', '6196219143', 'laldena@people.com.cn'),"
            + " (12, 'Delaney', 'Golsthorp', '1997-04-13', '59 Old Shore Avenue', 'Hartford', 'CT', '06183', '8604607269', 'dgolsthorpb@bbc.co.uk'),"
            + " (13, 'Elfreda', 'Garm', '2006-09-06', '55533 Ludington Pass', 'El Paso', 'TX', '79984', '9155784840', 'egarmc@newsvine.com'),"
            + " (14, 'Pate', 'Bockmann', '1979-07-05', '31703 Golf Avenue', 'Spokane', 'WA', '99260', '5099037579', 'pbockmannd@chron.com'),"
            + " (15, 'Torin', 'Fochs', '2020-10-18', '2017 Melrose Lane', 'Kansas City', 'MO', '64101', '8165873193', 'tfochse@senate.gov'),"
            + " (16, 'Kamila', 'Gamlyn', '1955-05-10', '6 8th Junction', 'Olympia', 'WA', '98506', '3603806709', 'kgamlynf@google.com'),"
            + " (17, 'Merv', 'Whitlaw', '1993-04-15', '03965 Vahlen Street', 'Miami', 'FL', '33134', '7869753830', 'mwhitlawg@scribd.com'),"
            + " (18, 'Arleyne', 'Burnup', '1997-05-12', '79862 Burning Wood Point', 'Burbank', 'CA', '91505', '3233404030', 'aburnuph@paginegialle.it'),"
            + " (19, 'Alexi', 'Maffin', '1968-08-17', '036 Buhler Hill', 'Lubbock', 'TX', '79452', '8066439680', 'amaffini@irs.gov'),"
            + " (20, 'Rhett', 'Arlett', '2006-09-14', '336 Butterfield Park', 'Hollywood', 'FL', '33023', '3054776163', 'rarlettj@alibaba.com');";
        insertSql("CUSTOMER", query);

        query = "INSERT INTO RESERVATION VALUES (1, 15, '2022-02-10 12:05:39', '2022-02-15 12:05:39'),"
            + " (2, 1, '2022-07-20 01:16:18', '2022-07-22 01:16:18'),"
            + " (3, 3, '2021-09-19 00:34:41', '2021-09-25 00:34:41'),"
            + " (4, 1, '2019-12-29 06:09:46', '2020-01-04 06:09:46'),"
            + " (5, 14, '2021-07-30 06:45:48', '2021-08-01 06:45:48'),"
            + " (6, 8, '2020-10-10 02:32:12', '2020-10-17 02:32:12'),"
            + " (7, 11, '2021-08-27 10:57:54', '2021-08-30 10:57:54'),"
            + " (8, 19, '2020-07-07 09:29:09', '2020-07-08 09:29:09'),"
            + " (9, 2, '2021-12-27 10:26:34', '2021-12-28 10:26:34'),"
            + " (10, 16, '2022-10-22 13:30:20', '2022-10-26 13:30:20'),"
            + " (11, 3, '2020-12-06 14:33:51', '2020-12-09 14:33:51'),"
            + " (12, 10, '2022-07-09 13:12:05', '2022-07-10 13:12:05'),"
            + " (13, 7, '2021-08-18 00:12:04', '2021-08-24 00:12:04'),"
            + " (14, 16, '2022-11-28 17:17:25', '2022-12-04 17:17:25'),"
            + " (15, 19, '2020-05-28 04:23:18', '2020-05-31 04:23:18'),"
            + " (16, 6, '2020-12-31 19:46:30', '2021-01-01 19:46:30'),"
            + " (17, 20, '2020-07-01 15:06:12', '2020-07-08 15:06:12'),"
            + " (18, 11, '2020-03-31 15:01:40', '2020-04-04 15:01:40'),"
            + " (19, 16, '2021-07-28 00:49:05', '2021-08-01 00:49:05'),"
            + " (20, 12, '2020-03-07 10:26:20', '2020-03-12 10:26:20'),"
            + " (21, 11, '2020-11-25 10:26:20', '2020-12-25 10:26:20'),"
            + " (22, 9, '2020-11-15 10:26:20', '2020-12-25 10:26:20'),"
            + " (23, 5, '2020-11-1 10:26:20', '2020-12-25 10:26:20'),"
            + " (24, 3, '2020-11-28 10:26:20', '2020-12-25 10:26:20'),"
            + " (25, 2, '2020-11-30 10:26:20', '2020-12-25 10:26:20');";
        insertSql("RESERVATION", query);

        query = "INSERT INTO ROOM_RESERVATION VALUES (12, 1),"
            + " (15, 1),"
            + " (23, 2),"
            + " (28, 3),"
            + " (41, 4),"
            + " (45, 5),"
            + " (36, 6),"
            + " (12, 6),"
            + " (14, 7),"
            + " (8, 8),"
            + " (5, 9),"
            + " (2, 10),"
            + " (3, 11),"
            + " (12, 12),"
            + " (36, 12),"
            + " (30, 13),"
            + " (20, 14),"
            + " (36, 15),"
            + " (22, 16),"
            + " (29, 16),"
            + " (46, 17),"
            + " (14, 18),"
            + " (18, 19),"
            + " (36, 20),"
            + " (35, 20),"
            + " (5, 21),"
            + " (10, 22),"
            + " (11, 23),"
            + " (30, 24),"
            + " (50, 25),"
            + " (38, 21),"
            + " (49, 24),"
            + " (42, 21),"
            + " (36, 24);";
        insertSql("ROOM_RESERVATION", query);

        query = "INSERT INTO PAYMENT VALUES (1, 6, 'Mastercard', '7617645136768180'),"
            + " (2, 9, 'Amex', '5223390377336935'),"
            + " (3, 12, 'Amex', '7560467894211843'),"
            + " (4, 18, 'Visa', '5900756539277347'),"
            + " (5, 1, 'Cash', null),"
            + " (6, 16, 'Visa', '2664265117938224'),"
            + " (7, 1, 'Visa', '3241809144167831'),"
            + " (8, 2, 'Mastercard', '1682205597287630'),"
            + " (9, 2, 'Cash', null),"
            + " (10, 20, 'Mastercard', '8615541226123430'),"
            + " (11, 11, 'Visa', '1817030795550201'),"
            + " (12, 1, 'Visa', '4137037892048257'),"
            + " (13, 9, 'Cash', null),"
            + " (14, 5, 'Mastercard', '7991303837573804'),"
            + " (15, 1, 'Visa', '1206950446238705'),"
            + " (16, 7, 'Visa', '5310660316487036'),"
            + " (17, 17, 'Amex', '1771288738109261'),"
            + " (18, 14, 'Visa', '7445710110185707'),"
            + " (19, 9, 'Cash', null),"
            + " (20, 2, 'Mastercard', '9994248835072802');";
        insertSql("PAYMENT", query);
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
        } catch (SQLException | NullPointerException e) {
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
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void insertSql(String table, String insertQuery) {
        ResultSet rs = executeQuery("SELECT * FROM " + table + " LIMIT 1");

        try {
            if (!rs.next()) {
                executeUpdate(insertQuery);
            }
        } catch (SQLException ignored) {
        }
    }
}
