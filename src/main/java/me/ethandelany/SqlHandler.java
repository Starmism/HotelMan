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
                setup();
            } catch (SQLException e) {
                System.out.println(e.toString());
                System.out.println("SQL Connection Failed.");
            }
        };
        executor.execute(runnable);
    }

    public void setup() {
        // TODO: Get the actual SQL from Part 3 of the Team Project
        /*String query;

        // Test table
        query = "SELECT `UUID` FROM `BL_PLAYER` LIMIT 1";

        try {
            stmt.executeQuery(query);
        } catch (SQLException e) {
            // If it doesn't exist, create the table
            query = "CREATE TABLE IF NOT EXISTS `BL_PLAYER` ("
                + "`UUID` VARCHAR(36) PRIMARY KEY,"
                + "`TrailToggle` BOOL DEFAULT false,"
                + "`Trail` NVARCHAR(30),"
                + "`RoadBoostToggle` BOOL DEFAULT false"
                + ")";
            System.out.println("Creating Player table.");
            executeUpdate(query);
        }

        query = "SELECT `UUID` FROM `BL_HOME` LIMIT 1";

        try {
            stmt.executeQuery(query);
        } catch (SQLException e) {
            query = "CREATE TABLE IF NOT EXISTS `BL_HOME` ("
                + "`UUID` VARCHAR(36),"
                + "`Home` NVARCHAR(30),"
                + "`X` DOUBLE,"
                + "`Y` DOUBLE,"
                + "`Z` DOUBLE,"
                + "`World` NVARCHAR(30),"
                + "`Yaw` FLOAT,"
                + "`Pitch` FLOAT,"
                + "PRIMARY KEY (UUID,Home),"
                + "FOREIGN KEY (UUID) REFERENCES BL_PLAYER (UUID)"
                + ")";
            Main.doBukkitLog(ChatColor.LIGHT_PURPLE + "Creating Home table.");
            executeUpdate(query);
        }

        query = "SELECT `ZoneID` FROM `BL_ZONE` LIMIT 1";

        try {
            stmt.executeQuery(query);
        } catch (SQLException e) {
            query = "CREATE TABLE IF NOT EXISTS `BL_ZONE` ("
                + "`ZoneID` INT PRIMARY KEY AUTO_INCREMENT,"
                + "`AX` DOUBLE,"
                + "`AY` DOUBLE,"
                + "`AZ` DOUBLE,"
                + "`BX` DOUBLE,"
                + "`BY` DOUBLE,"
                + "`BZ` DOUBLE,"
                + "`World` NVARCHAR(30),"
                + "`OwnerUUID` VARCHAR(36),"
                + "FOREIGN KEY (OwnerUUID) REFERENCES BL_PLAYER (UUID)"
                + ")";
            Main.doBukkitLog(ChatColor.LIGHT_PURPLE + "Creating Zone table.");
            executeUpdate(query);
        }

        query = "SELECT `ZoneID` FROM `BL_ZONE_MEMBER` LIMIT 1";

        try {
            stmt.executeQuery(query);
        } catch (SQLException e) {
            query = "CREATE TABLE IF NOT EXISTS `BL_ZONE_MEMBER` ("
                + "`ZoneID` INT,"
                + "`MemberUUID` VARCHAR(36),"
                + "PRIMARY KEY (ZoneID, MemberUUID),"
                + "FOREIGN KEY (MemberUUID) REFERENCES BL_PLAYER (UUID)"
                + ")";
            Main.doBukkitLog(ChatColor.LIGHT_PURPLE + "Creating Zone Members table.");
            executeUpdate(query);
        }*/
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
