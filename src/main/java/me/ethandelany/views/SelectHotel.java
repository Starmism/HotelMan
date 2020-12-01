package me.ethandelany.views;

import java.awt.Font;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import me.ethandelany.HotelMan;
import me.ethandelany.SqlHandler;

public class SelectHotel {

    private final HotelMan hm;
    private final JPanel mainPanel;
    private final SqlHandler sql;
    private String selectedHotel = "";
    private String selectedHotelManager = "";

    public SelectHotel(HotelMan hm) {
        this.hm = hm;
        mainPanel = hm.getMainPanel();
        sql = hm.getSqlHandler();
    }

    public void hotelFunction() {

        mainPanel.removeAll();

        hm.buttonSetter(0);

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
        hotelSelectorList.setBounds(288, 97, 450, 300);
        hotelSelectorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hotelSelectorList.setLayoutOrientation(JList.VERTICAL);
        hotelSelectorList.setVisibleRowCount(-1);
        hotelSelectorList.setSelectedValue(selectedHotel, false);
        mainPanel.add(hotelSelectorList);

        JLabel lblHotelManager = new JLabel("Hotel Manager");
        lblHotelManager.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblHotelManager.setBounds(30, 67, 200, 50);
        mainPanel.add(lblHotelManager);

        JTextArea areaHotelManager = new JTextArea(selectedHotelManager);
        areaHotelManager.setBounds(30, 97, 240, 150);
        areaHotelManager.setOpaque(false);
        areaHotelManager.setEditable(false);
        areaHotelManager.setFont(new Font("Tahoma", Font.PLAIN, 13));
        mainPanel.add(areaHotelManager);

        hm.repaintFrame();


        // When a hotel is clicked in the list
        hotelSelectorList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if (!(hotelSelectorList.getSelectedIndex() == -1)) {
                    selectedHotel = hotelSelectorList.getSelectedValue();
                    int atI = selectedHotel.indexOf("@");
                    int comI = selectedHotel.indexOf(",");
                    hm.lblSelectedHotel.setText("Current Hotel: "
                        + selectedHotel.substring(0, atI - 1)
                        + " - "
                        + selectedHotel.substring(comI + 2, selectedHotel.length() - 6)
                        + "  ");


                    try {
                        rs.absolute(hotelSelectorList.getSelectedIndex() + 1);

                        hm.selectedHotelID = rs.getInt("HotelID");

                        ResultSet rs2 = sql.executeQuery("SELECT * FROM `STAFF` WHERE `STAFFID` = " + rs.getInt("HotelManagerID"));

                        if (rs2.next()) {
                            String phoneNum = rs2.getString("PhoneNumber");
                            phoneNum = "(" + phoneNum.substring(0,3) + ") " + phoneNum.substring(3, 6) + "-" + phoneNum.substring(6,10);

                            selectedHotelManager =
                                "\nName: " + rs2.getNString("FirstName") + " " + rs2.getNString("LastName")
                                    + "\nEmail: " + rs2.getNString("Email")
                                    + "\nPhone Number: " + phoneNum;

                            areaHotelManager.setText(selectedHotelManager);

                        } else {
                            areaHotelManager.setText("None");
                        }
                    } catch (SQLException ex) {
                        System.out.println("[SQL ERROR] Couldn't get Manager info.");
                        ex.printStackTrace();
                    }
                }
            }
        });
    }
}
