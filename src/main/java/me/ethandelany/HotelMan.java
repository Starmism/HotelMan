package me.ethandelany;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.sql.ResultSet;

import java.sql.SQLException;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import java.awt.Font;

public class HotelMan {

    private JFrame frame;
    private JTextField category;
    private JTextField searchVlue;
    private JLabel lblPleaseTypeSome;
    private JTextField ISBN;
    private JTextField title;
    private JTextField pubDate;
    private JTextField pubID;
    private JTextField Cost;
    private JTextField retail;
    private JTextField discount;
    private JTextField cat;
    public final SqlHandler sql;
    private JButton hotelsButton, roomSearchButton, ordersButton, insertButton;
    private JLabel lblSelectedHotel;
    private String selectedHotel = "";
    private String selectedHotelManager = "Hotel Manager:\nNONE SELECTED";
    private int selectedHotelID;
    private String reservedFilterGlobal = "All";
    private String roomTypeSelectorGlobal = "All Room Types";
    private Date date;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                HotelMan window = new HotelMan(args[0], args[1], args[2], args[3]);
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Create the application.
     */
    public HotelMan(String host, String database, String username, String password) {
        sql = new SqlHandler(host, database, username, password);

        initialize();
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setBounds(0, 0, 1000, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JPanel menuPanel = new JPanel();
        menuPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
        menuPanel.setBounds(0, 0, 150, 400);
        frame.getContentPane().add(menuPanel);
        menuPanel.setLayout(null);

        hotelsButton = new JButton("Select Hotel");
        hotelsButton.setBounds(0, 20, 150, 40);
        menuPanel.add(hotelsButton);

        roomSearchButton = new JButton("Search Rooms");
        roomSearchButton.setBounds(0, 65, 150, 40);
        menuPanel.add(roomSearchButton);

        ordersButton = new JButton("Search Orders");
        ordersButton.setBounds(0, 110, 150, 40);
        menuPanel.add(ordersButton);

        insertButton = new JButton("Insert Books");
        insertButton.setBounds(0, 155, 150, 40);
        menuPanel.add(insertButton);

        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(null);
        titlePanel.setBounds(174, 0, 710, 25);
        frame.getContentPane().add(titlePanel);

        JLabel lblTitle = new JLabel("HotelMan v1.0");
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 18));
        titlePanel.add(lblTitle);

        JPanel subTitlePanel = new JPanel();
        subTitlePanel.setBorder(null);
        subTitlePanel.setBounds(174, 30, 710, 41);
        frame.getContentPane().add(subTitlePanel);

        lblSelectedHotel = new JLabel("Current Hotel: NONE  ");
        lblSelectedHotel.setFont(new Font("Tahoma", Font.ITALIC, 15));
        subTitlePanel.add(lblSelectedHotel);

        final JPanel mainPanel = new JPanel();
        mainPanel.setBounds(150, 50, 650, 750);
        frame.getContentPane().add(mainPanel);
        mainPanel.setLayout(null);

        hotelFunction(mainPanel);

        hotelsButton.addActionListener(e -> hotelFunction(mainPanel));
        roomSearchButton.addActionListener(e -> roomSearchFunction(mainPanel));
        ordersButton.addActionListener(e -> searchOrders(mainPanel));
        insertButton.addActionListener(e -> insertFunction(mainPanel));
    }

    public void hotelFunction(JPanel mainPanel) {

        mainPanel.removeAll();

        hotelsButton.setEnabled(false);
        roomSearchButton.setEnabled(true);
        ordersButton.setEnabled(true);
        insertButton.setEnabled(true);

        JLabel lblPleaseChooseAHotel = new JLabel("Please select a hotel");
        lblPleaseChooseAHotel.setBounds(288, 47, 200, 50);
        mainPanel.add(lblPleaseChooseAHotel);

        DefaultListModel<String> listOfHotels = new DefaultListModel<>();
        ResultSet rs = sql.executeQuery("SELECT * FROM `HOTEL`;");

        try {
            while (rs.next())
                listOfHotels.addElement(
                    rs.getNString("Name") + " @ "
                    + rs.getNString("Street") + ", "
                    + rs.getNString("City") + ", "
                    + rs.getString("State") + " "
                    + rs.getString("ZipCode")
                );
        } catch (SQLException e) {
            System.out.println("[SQL ERROR] Error getting list of hotels.");
            e.printStackTrace();
        }

        JList<String> hotelSelectorList = new JList<>(listOfHotels);
        // This needs to be bigger but I don't know why it won't get bigger!!!
        hotelSelectorList.setBounds(288, 97, 500, 200);
        hotelSelectorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hotelSelectorList.setLayoutOrientation(JList.VERTICAL);
        hotelSelectorList.setVisibleRowCount(-1);
        hotelSelectorList.setSelectedValue(selectedHotel, false);
        mainPanel.add(hotelSelectorList);

        JTextArea areaHotelManager = new JTextArea(selectedHotelManager);
        areaHotelManager.setBounds(20, 97, 240, 150);
        areaHotelManager.setOpaque(false);
        areaHotelManager.setEditable(false);
        areaHotelManager.setFont(new Font("Tahoma", Font.PLAIN, 13));
        mainPanel.add(areaHotelManager);

        frame.repaint();



        hotelSelectorList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if (!(hotelSelectorList.getSelectedIndex() == -1)) {
                    // Sets the hotel to the selected one
                    selectedHotel = hotelSelectorList.getSelectedValue();
                    lblSelectedHotel.setText("Current Hotel: " + selectedHotel + "  ");

                    // Get the Hotel Manager information
                    try {
                        // Set the ResultSet row to the correct one
                        rs.absolute(hotelSelectorList.getSelectedIndex() + 1);

                        // Set the HotelID to the correct one
                        selectedHotelID = rs.getInt("HotelID");

                        // Get the row corresponding to the hotel manager
                        ResultSet rs2 = sql.executeQuery("SELECT * FROM `STAFF` WHERE `STAFFID` = " + rs.getInt("HotelManagerID"));

                        // If they exist
                        if (rs2.next()) {
                            // Get the phone number, then prettify it
                            String phoneNum = rs2.getString("PhoneNumber");
                            phoneNum = "(" + phoneNum.substring(0,3) + ") " + phoneNum.substring(3, 6) + "-" + phoneNum.substring(6,10);

                            // Set the current Hotel Manager string to the full format
                            selectedHotelManager = "Hotel Manager:"
                                + "\nName: " + rs2.getNString("FirstName") + " " + rs2.getNString("LastName")
                                + "\nEmail: " + rs2.getNString("Email")
                                + "\nPhone Number: " + phoneNum;

                            // Update the actual text in the panel to the new string
                            areaHotelManager.setText(selectedHotelManager);

                        } else {
                            areaHotelManager.setText("Hotel Manager:\nNONE FOUND");
                        }
                    } catch (SQLException ex) {
                        System.out.println("[SQL ERROR] Couldn't get Manager info.");
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    // Create the room search interface
    public void roomSearchFunction(JPanel mainPanel) {

        mainPanel.removeAll();

        hotelsButton.setEnabled(true);
        roomSearchButton.setEnabled(false);
        ordersButton.setEnabled(true);
        insertButton.setEnabled(true);

        JLabel lblRoomList = new JLabel("Room List");
        lblRoomList.setBounds(288, 47, 200, 50);
        mainPanel.add(lblRoomList);

        DefaultListModel<String> listOfRooms = new DefaultListModel<>();
        getListOfRooms(listOfRooms, mainPanel);

        JRadioButton allReservations = new JRadioButton("All Reservation Status");
        allReservations.setBounds(62, 47, 200, 50);
        allReservations.setSelected(true);
        mainPanel.add(allReservations);

        JRadioButton onlyReserved = new JRadioButton("Only Reserved Rooms");
        onlyReserved.setBounds(62, 87, 200, 50);
        mainPanel.add(onlyReserved);

        JRadioButton onlyAvailable = new JRadioButton("Only Available Rooms");
        onlyAvailable.setBounds(62, 127, 200, 50);
        mainPanel.add(onlyAvailable);

        ButtonGroup reservationsFilter = new ButtonGroup();
        reservationsFilter.add(allReservations);
        reservationsFilter.add(onlyReserved);
        reservationsFilter.add(onlyAvailable);

        String[] roomTypeOptions = {"All Room Types", "Single-Twin", "Single-Queen", "Single-King", "Double-Twin", "Double-Queen"};

        JComboBox<String> roomTypeSelector = new JComboBox<>(roomTypeOptions);
        roomTypeSelector.setSelectedIndex(0);
        roomTypeSelector.setBounds(62, 187, 200, 50);
        mainPanel.add(roomTypeSelector);

        roomTypeSelector.addActionListener(e -> {
            roomTypeSelectorGlobal = (String) roomTypeSelector.getSelectedItem();
            getListOfRooms(listOfRooms, mainPanel);
        });

        frame.repaint();


        allReservations.addActionListener(e -> {
            reservedFilterGlobal = "All";
            getListOfRooms(listOfRooms, mainPanel);
            frame.repaint();
        });
        onlyReserved.addActionListener(e -> {
            reservedFilterGlobal = "Reserved";
            getListOfRooms(listOfRooms, mainPanel);
            frame.repaint();
        });
        onlyAvailable.addActionListener(e -> {
            reservedFilterGlobal = "Available";
            getListOfRooms(listOfRooms, mainPanel);
            frame.repaint();
        });
    }

    private void getListOfRooms(DefaultListModel<String> listOfRooms, JPanel mainPanel) {
        ResultSet rs = sql.executeQuery("SELECT * FROM `ROOM` WHERE `HotelID` = " + selectedHotelID + " ORDER BY `RoomNumber`");
        ResultSet rs2;
        ResultSet rs3;
        String reserved, roomType = "All";
        int roomNumber, roomID;
        listOfRooms.clear();

        try {
            while (rs.next()) {
                roomNumber = rs.getInt("RoomNumber");
                roomID = rs.getInt("RoomID");
                date = new Date();
                reserved = "<font color='green'>AVAILABLE</font>";

                // Check if the room is currently reserved
                rs2 = sql.executeQuery("SELECT r.CheckIn, r.CheckOut "
                    + "FROM `ROOM_RESERVATION` rr "
                    + "INNER JOIN RESERVATION R "
                    + "ON rr.ReservationID = R.ReservationID "
                    + "WHERE `RoomID` = " + roomID);

                while (rs2.next()) {
                    if (date.after(rs2.getDate("CheckIn")) && date.before(rs2.getDate("CheckOut"))) {
                        reserved = "<font color='red'>RESERVED</font>";
                    }
                }

                // Get the room type of the current room
                rs3 = sql.executeQuery("SELECT * FROM `ROOM` WHERE `RoomID` = " + roomID);
                if (rs3.next()) {
                    roomType = rs3.getNString("RoomType");
                }


                switch (reservedFilterGlobal) {
                    case "All":
                        satisfyRoomTypeCheck(listOfRooms, reserved, roomType, roomNumber);
                        break;
                    case "Reserved":
                        if (reserved.equals("<font color='red'>RESERVED</font>")) {
                            System.out.println(roomID);
                            satisfyRoomTypeCheck(listOfRooms, reserved, roomType, roomNumber);
                        }
                        break;
                    case "Available":
                        if (reserved.equals("<font color='green'>AVAILABLE</font>")) {
                            System.out.println(roomID);
                            satisfyRoomTypeCheck(listOfRooms, reserved, roomType, roomNumber);
                        }
                        break;
                }
            }
        } catch (SQLException e) {
            System.out.println("[SQL ERROR] Error getting list of rooms.");
            e.printStackTrace();
        }

        JList<String> roomSelectorList = new JList<>(listOfRooms);
        roomSelectorList.setBounds(288, 97, 500, 300);
        roomSelectorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomSelectorList.setLayoutOrientation(JList.VERTICAL);
        roomSelectorList.setVisibleRowCount(-1);
        mainPanel.add(roomSelectorList);
    }

    private void satisfyRoomTypeCheck(DefaultListModel<String> listOfRooms, String reserved, String roomType, int roomNumber) {
        switch (roomTypeSelectorGlobal) {
            case "All Room Types":
                listOfRooms.addElement(
                    "<html>"
                        + roomNumber + " -> "
                        + reserved
                        + " - "
                        + roomType
                        + "</html>"
                );
                break;
            case "Single-Twin":
                if (roomType.equals("Single-Twin")) {
                    listOfRooms.addElement(
                        "<html>"
                            + roomNumber + " -> "
                            + reserved
                            + " - "
                            + roomType
                            + "</html>"
                    );
                }
                break;
            case "Single-Queen":
                if (roomType.equals("Single-Queen")) {
                    listOfRooms.addElement(
                        "<html>"
                            + roomNumber + " -> "
                            + reserved
                            + " - "
                            + roomType
                            + "</html>"
                    );
                }
                break;
            case "Single-King":
                if (roomType.equals("Single-King")) {
                    listOfRooms.addElement(
                        "<html>"
                            + roomNumber + " -> "
                            + reserved
                            + " - "
                            + roomType
                            + "</html>"
                    );
                }
                break;
            case "Double-Twin":
                if (roomType.equals("Double-Twin")) {
                    listOfRooms.addElement(
                        "<html>"
                            + roomNumber + " -> "
                            + reserved
                            + " - "
                            + roomType
                            + "</html>"
                    );
                }
                break;
            case "Double-Queen":
                if (roomType.equals("Double-Queen)")) {
                    listOfRooms.addElement(
                        "<html>"
                            + roomNumber + " -> "
                            + reserved
                            + " - "
                            + roomType
                            + "</html>"
                    );
                }
                break;
        }
    }

    public void searchOrders(JPanel mainPanel) {

        mainPanel.removeAll();

        hotelsButton.setEnabled(true);
        roomSearchButton.setEnabled(true);
        ordersButton.setEnabled(false);
        insertButton.setEnabled(true);

        JRadioButton cname = new JRadioButton("Customer's Last Name");
        cname.setBounds(62, 47, 200, 50);
        mainPanel.add(cname);

        JRadioButton oid = new JRadioButton("Order ID");
        oid.setBounds(62, 84, 200, 50);
        mainPanel.add(oid);

        JRadioButton rdbtnState = new JRadioButton("State");
        rdbtnState.setBounds(62, 123, 200, 50);
        mainPanel.add(rdbtnState);

        ButtonGroup bg = new ButtonGroup();
        bg.add(cname);
        bg.add(oid);
        bg.add(rdbtnState);

        lblPleaseTypeSome = new JLabel("Please type some value");
        lblPleaseTypeSome.setBounds(288, 47, 200, 50);
        mainPanel.add(lblPleaseTypeSome);

        searchVlue = new JTextField();
        searchVlue.setBounds(288, 97, 200, 50);
        searchVlue.setColumns(10);
        mainPanel.add(searchVlue);

        JButton search = new JButton("SUBMIT");
        search.setBounds(288, 160, 200, 50);
        mainPanel.add(search);

        frame.repaint();



        final JTextPane displaypane = new JTextPane();
        displaypane.setBounds(50, 225, 1600, 800);

        final JScrollPane jsp = new JScrollPane(displaypane);
        jsp.setBounds(50, 225, 1600, 800);

        search.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {

                cname.addActionListener(this);
                oid.addActionListener(this);
                rdbtnState.addActionListener(this);

                String column = "";

                if (cname.isSelected()) {
                    column = "CustomerLastName";
                    lblPleaseTypeSome.setText("Please Enter Last Name");
                } else if (rdbtnState.isSelected()) {
                    column = "ShipState";
                    lblPleaseTypeSome.setText("Please Enter State");
                } else if (oid.isSelected()) {
                    column = "OrderID";
                    lblPleaseTypeSome.setText("Please Enter Order ID");
                }

                JLabel hidden = new JLabel(column);
                displaypane.setText(sqlSearchOrders(hidden.getText(), searchVlue.getText()));
                mainPanel.add(jsp);

                frame.repaint();
            }
        });
    }

    // create the insert (admin page ) interface
    public void insertFunction(JPanel mainPanel) {

        mainPanel.removeAll();

        hotelsButton.setEnabled(true);
        roomSearchButton.setEnabled(true);
        ordersButton.setEnabled(true);
        insertButton.setEnabled(false);

        JLabel lblIsbn = new JLabel("ISBN");
        lblIsbn.setBounds(160, 58, 100, 50);
        mainPanel.add(lblIsbn);

        JLabel lblBookTitle = new JLabel("Book Title");
        lblBookTitle.setBounds(160, 98, 100, 50);
        mainPanel.add(lblBookTitle);

        JLabel lblNewLabel = new JLabel("Publish Date");
        lblNewLabel.setBounds(160, 138, 100, 50);
        mainPanel.add(lblNewLabel);

        JLabel lblNewLabel_1 = new JLabel("Publisher ID");
        lblNewLabel_1.setBounds(160, 178, 100, 50);
        mainPanel.add(lblNewLabel_1);

        JLabel lblCost = new JLabel("Cost");
        lblCost.setBounds(160, 218, 100, 50);
        mainPanel.add(lblCost);

        JLabel lblDiscount = new JLabel("Retail");
        lblDiscount.setBounds(160, 258, 100, 50);
        mainPanel.add(lblDiscount);

        JLabel lblRetail = new JLabel("Discount");
        lblRetail.setBounds(160, 298, 100, 50);
        mainPanel.add(lblRetail);

        JLabel lblCategory = new JLabel("Category");
        lblCategory.setBounds(160, 338, 100, 50);
        mainPanel.add(lblCategory);

        ISBN = new JTextField();
        ISBN.setBounds(288, 70, 192, 28);
        ISBN.setColumns(10);
        mainPanel.add(ISBN);

        title = new JTextField();
        title.setBounds(288, 110, 192, 28);
        title.setColumns(10);
        mainPanel.add(title);

        pubDate = new JTextField();
        pubDate.setBounds(288, 150, 192, 28);
        pubDate.setColumns(10);
        mainPanel.add(pubDate);

        pubID = new JTextField();
        pubID.setBounds(288, 190, 192, 28);
        pubID.setColumns(10);
        mainPanel.add(pubID);

        Cost = new JTextField();
        Cost.setBounds(288, 230, 192, 28);
        Cost.setColumns(10);
        mainPanel.add(Cost);

        retail = new JTextField();
        retail.setBounds(288, 270, 192, 28);
        retail.setColumns(10);
        mainPanel.add(retail);

        discount = new JTextField();
        discount.setBounds(288, 310, 192, 28);
        discount.setColumns(10);
        mainPanel.add(discount);

        cat = new JTextField();
        cat.setBounds(288, 350, 192, 28);
        cat.setColumns(10);
        mainPanel.add(cat);

        JButton btnInsertData = new JButton("Insert Data");
        btnInsertData.setBounds(288, 390, 192, 42);
        mainPanel.add(btnInsertData);

        frame.repaint();



        btnInsertData.addActionListener(e -> {
            boolean success = sqlInsertBook(ISBN.getText(), title.getText(), pubDate.getText(),
                Integer.parseInt(pubID.getText()), Double.parseDouble(Cost.getText()),
                Double.parseDouble(retail.getText()), Double.parseDouble(discount.getText()),
                cat.getText());

            if (success) {
                JOptionPane.showMessageDialog(null, "Book inserted successfully.");
                ISBN.setText("");
                title.setText("");
                pubDate.setText("");
                pubID.setText("");
                Cost.setText("");
                retail.setText("");
                discount.setText("");
                cat.setText("");
                frame.repaint();
            } else {
                JOptionPane.showMessageDialog(null, "Book insertion failed.");
            }
        });
    }




    public String sqlSearchBooks(String search) {
        String query;
        ResultSet rs;
        int resultCount = 0;

        if (search.equalsIgnoreCase("all")) {
            query = "SELECT b.ISBN, b.Title, b.Retail FROM BOOKS b";
        } else {
            // TODO: Setup PreparedStatement to stop SQL Injections
            query = "SELECT b.ISBN, b.Title, b.Retail FROM BOOKS b WHERE `Category` = '" + search + "'";
        }

        StringBuilder results = new StringBuilder("Books found in the database: \n \n");
        rs = sql.executeQuery(query);

        try {
            while (rs.next()) {
                results.append(rs.getString(1)).append("\t | \t").append(rs.getString(2))
                    .append("\t |\t").append(rs.getString(3)).append("\n");
                resultCount++;
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            results.delete(0, results.length());
            results.append("Database access error.");
        }

        if (resultCount == 0) {
            results.delete(0, results.length());
            results.append("Sorry!!\nNo books were found");
        }

        return results.toString();

    }

    public String sqlSearchOrders(String col, String search) {
        String query;
        StringBuilder results;
        ResultSet rs;
        int resultCount = 0;

        if (search.equalsIgnoreCase("all")) {
            query = "SELECT c.lastname, "
                + "o.OrderID,"
                + "o.OrderDate,"
                + "o.ShipStreet,"
                + "o.ShipCity,"
                + "o.ShipState"
                + " FROM orders o"
                + " INNER JOIN customers c ON o.CustomerID = c.CustomerID";
        } else if (col.equalsIgnoreCase("CustomerLastName")) {
            query = "SELECT o.OrderID,"
                + "concat(c.FirstName, \"  \", c.LastName),"
                + "o.OrderDate,"
                + "o.ShipStreet,"
                + "o.ShipCity,"
                + "o.ShipState"
                + " FROM orders o"
                + " INNER JOIN customers c ON o.CustomerID = c.CustomerID"
                + " WHERE c.customerID IN"
                + " (SELECT customerid FROM customers WHERE lastname LIKE \"%" + search + "%\")";
        } else if (col.equalsIgnoreCase("OrderID")) {
            query = "SELECT o.OrderID,"
                + "concat(c.FirstName, \"  \", c.LastName),"
                + "o.OrderDate,"
                + "o.ShipStreet,"
                + "o.ShipCity,"
                + "o.ShipState"
                + " FROM orders o"
                + " INNER JOIN customers c ON o.CustomerID = c.CustomerID"
                + " WHERE o.OrderID = " + search;
        } else {
            query = "SELECT o.OrderID,"
                + "concat(c.FirstName, \"  \", c.LastName),"
                + "o.OrderDate,"
                + "o.ShipStreet,"
                + "o.ShipCity,"
                + "o.ShipState"
                + " FROM orders o"
                + " INNER JOIN customers c ON o.CustomerID = c.CustomerID"
                + " WHERE `" + col + "` = '" + search + "'";
        }

        results = new StringBuilder("Orders found in the database: \n \n");
        try {
            rs = sql.executeQuery(query);

            while (rs.next()) {
                results.append(rs.getString(1)).append("\t |   ").append(rs.getString(2))
                    .append("\t |   ").append(rs.getString(3)).append("\t |   ")
                    .append(rs.getString(4)).append("\t |   ").append(rs.getString(5))
                    .append("\t |   ").append(rs.getString(6)).append("\n");
                resultCount++;
            }

        } catch (Exception e) {
            System.out.println(e.toString());
            results.delete(0, results.length());
            results.append("Database error.");
        }

        if (resultCount == 0) {
           results.delete(0, results.length());
           results.append("Sorry, no orders were found.");
        }

        return results.toString();
    }

    public boolean sqlInsertBook(String ISBN, String title, String pubDate, int pubID, double cost,
        double retail, double discount, String cat) {

        String query = "INSERT INTO Books VALUES ('" + ISBN + "','" + title + "', '" + pubDate + "',"
                + pubID + "," + cost + "," + retail + "," + discount + ",'" + cat + "')";
        return sql.executeUpdate(query);
    }

    private static class __Tmp {

        private static void __tmp() {
            javax.swing.JPanel __wbp_panel = new javax.swing.JPanel();
        }
    }
}
