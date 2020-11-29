package me.ethandelany;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListModel;
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
    private JButton hotelsButton, searchButton, ordersButton, insertButton;
    private JLabel lblSelectedHotel;
    private String selectedHotel = null;

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
        initialize();

        sql = new SqlHandler(host, database, username, password);
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

        hotelsButton = new JButton("Manage Hotels");
        hotelsButton.setBounds(0, 20, 150, 40);
        menuPanel.add(hotelsButton);

        searchButton = new JButton("Search Books");
        searchButton.setBounds(0, 65, 150, 40);
        menuPanel.add(searchButton);

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
        searchButton.addActionListener(e -> searchFunction(mainPanel));
        ordersButton.addActionListener(e -> searchOrders(mainPanel));
        insertButton.addActionListener(e -> insertFunction(mainPanel));
    }

    public void hotelFunction(JPanel mainPanel) {

        mainPanel.removeAll();

        hotelsButton.setEnabled(false);
        searchButton.setEnabled(true);
        ordersButton.setEnabled(true);
        insertButton.setEnabled(true);

        JLabel lblPleaseChooseAHotel = new JLabel("Please select a hotel");
        lblPleaseChooseAHotel.setBounds(288, 47, 200, 50);
        mainPanel.add(lblPleaseChooseAHotel);

        DefaultListModel<String> listOfHotels = new DefaultListModel<>();
        listOfHotels.addElement("Hampton Inn");
        listOfHotels.addElement("Hilton Double Tree");
        listOfHotels.addElement("Motel One");

        JList<String> hotelSelectorList = new JList<>(listOfHotels);
        hotelSelectorList.setBounds(288, 97, 200, 200);
        hotelSelectorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hotelSelectorList.setLayoutOrientation(JList.VERTICAL);
        hotelSelectorList.setVisibleRowCount(-1);
        mainPanel.add(hotelSelectorList);

        frame.repaint();

        hotelSelectorList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                if (!(hotelSelectorList.getSelectedIndex() == -1)) {
                    selectedHotel = hotelSelectorList.getSelectedValue();
                    lblSelectedHotel.setText("Current Hotel: " + selectedHotel + "  ");
                }
            }
        });
    }

    // Create the book search interface
    public void searchFunction(JPanel mainPanel) {

        mainPanel.removeAll();

        hotelsButton.setEnabled(true);
        searchButton.setEnabled(false);
        ordersButton.setEnabled(true);
        insertButton.setEnabled(true);

        JLabel lblPleaseTypeA = new JLabel("Please type a category");
        lblPleaseTypeA.setBounds(288, 47, 200, 50);
        mainPanel.add(lblPleaseTypeA);

        category = new JTextField();
        category.setBounds(288, 97, 200, 50);
        mainPanel.add(category);
        category.setColumns(10);

        JButton lookup = new JButton("SUBMIT");
        lookup.setBounds(288, 160, 200, 50);
        mainPanel.add(lookup);

        frame.repaint();



        final JTextPane displaypane = new JTextPane();
        displaypane.setBounds(50, 220, 800, 600);

        lookup.addActionListener(e -> {
            displaypane.setText(sqlSearchBooks(category.getText()));
            mainPanel.add(displaypane);
            frame.repaint();
        });
    }

    public void searchOrders(JPanel mainPanel) {

        mainPanel.removeAll();

        hotelsButton.setEnabled(true);
        searchButton.setEnabled(true);
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
        searchButton.setEnabled(true);
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
