package me.ethandelany.views;

import java.awt.Component;
import java.awt.Font;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import me.ethandelany.HotelMan;
import me.ethandelany.SqlHandler;

public class SearchRooms {

    private final HotelMan hm;
    private final JPanel mainPanel;
    private final SqlHandler sql;
    private String reservedFilter = "Any";
    private String roomTypeFilter = "All Room Types";
    HashMap<Integer, String> features = new HashMap<>();
    private JLabel lblRoomTitle, lblReservationList;
    private JTextArea areaRoomInfo, areaRoomFeatures;
    private JList<String> reservationSelectorList;


    public SearchRooms(HotelMan hm) {
        this.hm = hm;
        mainPanel = hm.getMainPanel();
        sql = hm.getSqlHandler();

        try {
            ResultSet resultsFeatures = sql.executeQuery("SELECT * FROM `FEATURE`");

            while(resultsFeatures.next()) {
                features.put(resultsFeatures.getInt("FeatureID"), resultsFeatures.getNString("FeatureName"));
            }
        } catch(SQLException e) {
            System.out.println("[SQL ERROR] Couldn't get list of features.");
            e.printStackTrace();
        }
    }

    public void roomSearchFunction() {

        mainPanel.removeAll();

        hm.buttonSetter(1);

        JLabel lblRoomList = new JLabel("Room List");
        lblRoomList.setBounds(288, 47, 200, 50);
        mainPanel.add(lblRoomList);

        DefaultListModel<String> listOfRooms = new DefaultListModel<>();

        JLabel lblFilters = new JLabel("Filters");
        lblFilters.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblFilters.setBounds(60, 47, 200, 50);
        mainPanel.add(lblFilters);

        // Radial buttons for reservation status
        JRadioButton AnyReservations = new JRadioButton("Any Reservation Status");
        AnyReservations.setBounds(62, 87, 200, 30);
        AnyReservations.setSelected(true);
        mainPanel.add(AnyReservations);

        JRadioButton onlyReserved = new JRadioButton("Only Reserved Rooms");
        onlyReserved.setBounds(62, 117, 200, 30);
        mainPanel.add(onlyReserved);

        JRadioButton onlyAvailable = new JRadioButton("Only Available Rooms");
        onlyAvailable.setBounds(62, 147, 200, 30);
        mainPanel.add(onlyAvailable);

        ButtonGroup reservationsFilter = new ButtonGroup();
        reservationsFilter.add(AnyReservations);
        reservationsFilter.add(onlyReserved);
        reservationsFilter.add(onlyAvailable);

        JList<String> roomSelectorList = new JList<>(listOfRooms);
        roomSelectorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomSelectorList.setLayoutOrientation(JList.VERTICAL);
        roomSelectorList.setVisibleRowCount(-1);

        JScrollPane listScroller = new JScrollPane(roomSelectorList);
        listScroller.setBounds(288, 97, 400, 300);
        mainPanel.add(listScroller);

        AnyReservations.addActionListener(e -> {
            reservedFilter = "Any";
            getListOfRooms(listOfRooms, reservedFilter, roomTypeFilter);
            hm.repaintFrame();
        });
        onlyReserved.addActionListener(e -> {
            reservedFilter = "Reserved";
            getListOfRooms(listOfRooms, reservedFilter, roomTypeFilter);
            hm.repaintFrame();
        });
        onlyAvailable.addActionListener(e -> {
            reservedFilter = "Available";
            getListOfRooms(listOfRooms, reservedFilter, roomTypeFilter);
            hm.repaintFrame();
        });

        // Room Type Dropdown Selector
        String[] roomTypeOptions = {"All Room Types", "Single-Twin", "Single-Queen", "Single-King", "Double-Twin", "Double-Queen"};

        JComboBox<String> roomTypeSelector = new JComboBox<>(roomTypeOptions);
        roomTypeSelector.setSelectedIndex(0);
        roomTypeSelector.setBounds(62, 187, 200, 30);
        Component arrow = roomTypeSelector.getComponents()[0];
        arrow.setSize(20, 30);
        arrow.setLocation(180, 0);
        mainPanel.add(roomTypeSelector);

        roomTypeSelector.addActionListener(e -> {
            roomTypeFilter = roomTypeSelector.getSelectedItem().toString();
            getListOfRooms(listOfRooms, reservedFilter, roomTypeFilter);
        });

        roomSelectorList.addListSelectionListener(e -> {
            displayRoomInfo(roomSelectorList);
        });


        lblRoomTitle = new JLabel();
        lblRoomTitle.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblRoomTitle.setBounds(60, 412, 200, 50);
        lblRoomTitle.setVisible(false);
        mainPanel.add(lblRoomTitle);

        areaRoomInfo = new JTextArea();
        areaRoomInfo.setBounds(60, 457, 250, 200);
        areaRoomInfo.setOpaque(false);
        areaRoomInfo.setEditable(false);
        areaRoomInfo.setFont(new Font("Tahoma", Font.PLAIN, 13));
        areaRoomInfo.setVisible(false);
        mainPanel.add(areaRoomInfo);

        areaRoomFeatures = new JTextArea();
        areaRoomFeatures.setBounds(131, 489, 150, 200);
        areaRoomFeatures.setOpaque(false);
        areaRoomFeatures.setEditable(false);
        areaRoomFeatures.setFont(new Font("Tahoma", Font.PLAIN, 13));
        areaRoomFeatures.setVisible(false);
        mainPanel.add(areaRoomFeatures);

        lblReservationList = new JLabel("Reservation List");
        lblReservationList.setBounds(288, 407, 200, 50);
        lblReservationList.setVisible(false);
        mainPanel.add(lblReservationList);

        reservationSelectorList = new JList<>();
        reservationSelectorList.setBounds(288, 457, 400, 200);
        reservationSelectorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reservationSelectorList.setLayoutOrientation(JList.VERTICAL);
        reservationSelectorList.setVisibleRowCount(-1);
        reservationSelectorList.setVisible(false);
        mainPanel.add(reservationSelectorList);

        getListOfRooms(listOfRooms, reservedFilter, roomTypeFilter);

        hm.repaintFrame();
    }

    /**
     * Setup the given list of rooms with rooms matching the given filters.
     * @param listOfRooms The list of rooms to populate
     * @param reservedFilter The status of the reservation status filter
     * @param roomTypeFilter The status of the room type filter
     */
    private void getListOfRooms(DefaultListModel<String> listOfRooms, String reservedFilter, String roomTypeFilter) {
        Date date = new Date();
        ResultSet reservations, rooms = sql.executeQuery("SELECT * FROM `ROOM` WHERE `HotelID` = " + hm.selectedHotelID + " ORDER BY `RoomNumber`");
        String reserved, roomType;
        int roomNumber, roomID;

        listOfRooms.clear();

        try {
            while (rooms.next()) {
                roomNumber = rooms.getInt("RoomNumber");
                roomID = rooms.getInt("RoomID");
                reserved = "<font color='green'>AVAILABLE</font>";

                // Check if the room is currently reserved
                reservations = sql.executeQuery("SELECT r.CheckIn, r.CheckOut "
                    + "FROM `ROOM_RESERVATION` rr "
                    + "INNER JOIN RESERVATION R "
                    + "ON rr.ReservationID = R.ReservationID "
                    + "WHERE `RoomID` = " + roomID);

                while (reservations.next()) {
                    if (date.after(reservations.getDate("CheckIn")) && date.before(reservations.getDate("CheckOut"))) {
                        reserved = "<font color='red'>RESERVED</font>";
                    }
                }

                roomType = rooms.getNString("RoomType");

                switch (reservedFilter) {
                    case "Any":
                        satisfyRoomTypeCheck(listOfRooms, reserved, roomType, roomNumber, roomTypeFilter);
                        break;
                    case "Reserved":
                        if (reserved.equals("<font color='red'>RESERVED</font>")) {
                            System.out.println(roomID);
                            satisfyRoomTypeCheck(listOfRooms, reserved, roomType, roomNumber, roomTypeFilter);
                        }
                        break;
                    case "Available":
                        if (reserved.equals("<font color='green'>AVAILABLE</font>")) {
                            System.out.println(roomID);
                            satisfyRoomTypeCheck(listOfRooms, reserved, roomType, roomNumber, roomTypeFilter);
                        }
                        break;
                }
            }
        } catch (SQLException e) {
            System.out.println("[SQL ERROR] Couldn't get list of rooms.");
            e.printStackTrace();
        }
    }

    private void satisfyRoomTypeCheck(DefaultListModel<String> listOfRooms, String reserved, String roomType, int roomNumber, String roomTypeFilter) {
        switch (roomTypeFilter) {
            case "All Room Types":
                listOfRooms.addElement(
                    "<html>"
                        + roomNumber + " > "
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
                            + roomNumber + " > "
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
                            + roomNumber + " > "
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
                            + roomNumber + " > "
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
                            + roomNumber + " > "
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
                            + roomNumber + " > "
                            + reserved
                            + " - "
                            + roomType
                            + "</html>"
                    );
                }
                break;
        }
    }

    private void displayRoomInfo(JList<String> roomSelectorList) {
        int curRoomNumber, curRoomID = 0;
        String prettyRoomPrice, curRoomType = "";
        double curRoomPrice = 0.0;
        ResultSet resultsRoomFeatures, reservations;
        DefaultListModel<String> listOfReservations = new DefaultListModel<>();
        StringBuilder curRoomFeatures = new StringBuilder();


        try {
            curRoomNumber = Integer.parseInt(roomSelectorList.getSelectedValue().substring(6, 9));
        } catch (NullPointerException e) {
            return;
        }

        ResultSet room = sql.executeQuery("SELECT * FROM `ROOM` WHERE `RoomNumber` = " + curRoomNumber + " AND `HotelID` = " + hm.selectedHotelID);

        try {
            if(!room.next()) {
                System.out.println("Room does not exist.");
            }
        } catch (SQLException e) {
            System.out.println("[SQL ERROR]");
            e.printStackTrace();
        }

        try {
            curRoomID = room.getInt("RoomID");
            curRoomType = room.getNString("RoomType");
            curRoomPrice = room.getDouble("RoomPrice");



            listOfReservations.clear();
            reservations = sql.executeQuery("SELECT r.ReservationID, r.CheckIn, r.CheckOut"
                + " FROM `ROOM_RESERVATION` rr"
                + " INNER JOIN RESERVATION R"
                + " ON rr.ReservationID = R.ReservationID"
                + " INNER JOIN room r2 on rr.RoomID = r2.RoomID"
                + " WHERE rr.`RoomID` = " + curRoomID
                + " AND `HotelID` = " + hm.selectedHotelID
                + " ORDER BY r.CheckIn");

            while (reservations.next()) {
                listOfReservations.addElement(
                    reservations.getDate("CheckIn").toString()
                    + " > " + reservations.getDate("CheckOut").toString()
                    + " - Checkout Time: " + reservations.getTime("CheckOut").toString().substring(0, 5)
                    + " - ID: " + reservations.getInt("ReservationID")
                );
            }



            resultsRoomFeatures = sql.executeQuery("SELECT * FROM `ROOM_FEATURE` WHERE `RoomID` = " + curRoomID);

            while (resultsRoomFeatures.next()) {
                if (resultsRoomFeatures.getRow() != 1) {
                    curRoomFeatures.append(",\n");
                }
                curRoomFeatures.append(features.get(resultsRoomFeatures.getInt("FeatureID")));
            }
            if (curRoomFeatures.length() == 0) {
                curRoomFeatures.append("None");
            }

        } catch (SQLException e) {
            System.out.println("[SQL ERROR] Couldn't get room info.");
            e.printStackTrace();
        }

        prettyRoomPrice = NumberFormat.getCurrencyInstance().format(curRoomPrice);
        prettyRoomPrice = prettyRoomPrice.replaceAll("\\.00", "");

        lblRoomTitle.setText("Room #" + curRoomNumber);
        lblRoomTitle.setVisible(true);

        areaRoomInfo.setText("Type -> " + curRoomType
            + "\nPrice -> " + prettyRoomPrice
            + "\nFeatures -> ");
        areaRoomInfo.setVisible(true);

        areaRoomFeatures.setText(curRoomFeatures.toString());
        areaRoomFeatures.setVisible(true);

        lblReservationList.setVisible(true);

        reservationSelectorList.setModel(listOfReservations);
        reservationSelectorList.setVisible(true);

        hm.repaintFrame();
    }
}
