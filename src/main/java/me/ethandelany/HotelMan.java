package me.ethandelany;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
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
        frame.setBounds(0, 0, 1600, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JPanel menuPanel = new JPanel();
        menuPanel.setBorder(new LineBorder(new Color(0, 0, 0)));
        menuPanel.setBounds(0, 0, 150, 400);
        frame.getContentPane().add(menuPanel);
        menuPanel.setLayout(null);
        JButton searchButton = new JButton("Search Books");

        searchButton.setBounds(0, 20, 150, 40);
        menuPanel.add(searchButton);

        JButton ordersButton = new JButton("Search Orders");
        ordersButton.setBounds(0, 65, 150, 40);
        menuPanel.add(ordersButton);

        JButton InsertCustomers = new JButton("Insert Books");
        InsertCustomers.setBounds(0, 110, 150, 40);
        menuPanel.add(InsertCustomers);
        JPanel titlePanel = new JPanel();
        titlePanel.setBorder(null);
        titlePanel.setBounds(144, 0, 710, 41);
        frame.getContentPane().add(titlePanel);

        JLabel lblWelcomeToMy = new JLabel("HotelMan v1.0");
        lblWelcomeToMy.setFont(new Font("Tahoma", Font.BOLD, 18));
        titlePanel.add(lblWelcomeToMy);

        final JPanel mainPanel = new JPanel();
        mainPanel.setBounds(150, 50, 650, 750);
        frame.getContentPane().add(mainPanel);
        mainPanel.setLayout(null);

        JLabel lblPleaseChooseAn = new JLabel("Please choose an option from the menu");
        lblPleaseChooseAn.setFont(new Font("Tahoma", Font.PLAIN, 15));
        lblPleaseChooseAn.setBounds(192, 110, 351, 50);
        mainPanel.add(lblPleaseChooseAn);

        searchButton.addActionListener(e -> searchFunction(mainPanel));
        InsertCustomers.addActionListener(e -> insertFunction(mainPanel));
        ordersButton.addActionListener(e -> searchOrders(mainPanel));

    }

    // Create the book search interface

    public void searchFunction(final JPanel pan) {

        pan.removeAll();
        JLabel lblPleaseTypeA = new JLabel("Please type a category");
        lblPleaseTypeA.setBounds(330, 20, 200, 50);
        pan.add(lblPleaseTypeA);

        category = new JTextField();
        category.setBounds(326, 70, 200, 50);
        pan.add(category);
        category.setColumns(10);

        JButton lookup = new JButton("SUBMIT");
        lookup.setBounds(326, 133, 200, 50);
        pan.add(lookup);
        frame.repaint();
        final JTextPane displaypane = new JTextPane();
        displaypane.setBounds(50, 220, 800, 600);

        lookup.addActionListener(e -> {
            displaypane.setText(sqlSearchBooks(category.getText()));
            pan.add(displaypane);
            frame.repaint();
        });
    }

    // create the insert (admin page ) interface
    public void insertFunction(JPanel mainPanel) {

        mainPanel.removeAll();
        JLabel lblIsbn = new JLabel("ISBN");
        lblIsbn.setBounds(120, 91, 100, 50);
        mainPanel.add(lblIsbn);

        JLabel lblBookTitle = new JLabel("Book Title");
        lblBookTitle.setBounds(120, 133, 100, 50);
        mainPanel.add(lblBookTitle);

        JLabel lblNewLabel = new JLabel("Publish Date");
        lblNewLabel.setBounds(120, 173, 100, 50);
        mainPanel.add(lblNewLabel);

        JLabel lblNewLabel_1 = new JLabel("Publisher ID");
        lblNewLabel_1.setBounds(120, 219, 100, 50);
        mainPanel.add(lblNewLabel_1);

        JLabel lblCost = new JLabel("Cost");
        lblCost.setBounds(120, 258, 100, 50);
        mainPanel.add(lblCost);

        JLabel lblRetail = new JLabel("Discount");
        lblRetail.setBounds(120, 351, 100, 50);
        mainPanel.add(lblRetail);

        JLabel lblCategory = new JLabel("Category");
        lblCategory.setBounds(120, 396, 100, 50);
        mainPanel.add(lblCategory);

        ISBN = new JTextField();
        ISBN.setBounds(236, 102, 192, 28);
        mainPanel.add(ISBN);
        ISBN.setColumns(10);

        title = new JTextField();
        title.setColumns(10);
        title.setBounds(236, 144, 192, 28);
        mainPanel.add(title);

        pubDate = new JTextField();
        pubDate.setColumns(10);
        pubDate.setBounds(236, 184, 192, 28);
        mainPanel.add(pubDate);

        pubID = new JTextField();
        pubID.setColumns(10);
        pubID.setBounds(236, 230, 192, 28);
        mainPanel.add(pubID);

        Cost = new JTextField();
        Cost.setColumns(10);
        Cost.setBounds(236, 269, 192, 28);
        mainPanel.add(Cost);

        retail = new JTextField();
        retail.setColumns(10);
        retail.setBounds(236, 317, 192, 28);
        mainPanel.add(retail);

        discount = new JTextField();
        discount.setColumns(10);
        discount.setBounds(236, 362, 192, 28);
        mainPanel.add(discount);

        cat = new JTextField();
        cat.setColumns(10);
        cat.setBounds(236, 407, 192, 28);
        mainPanel.add(cat);

        JLabel lblDiscount = new JLabel("Retail");
        lblDiscount.setBounds(120, 301, 100, 50);
        mainPanel.add(lblDiscount);

        JButton btnInsertData = new JButton("Insert Data");
        btnInsertData.setBounds(236, 465, 192, 42);
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

    public void searchOrders(final JPanel pan) {

        pan.removeAll();
        final JRadioButton cname = new JRadioButton("Customer's Last Name");
        cname.setBounds(62, 47, 200, 50);
        pan.add(cname);

        final JRadioButton oid = new JRadioButton("Order ID");
        oid.setBounds(62, 84, 200, 50);
        pan.add(oid);

        searchVlue = new JTextField();
        searchVlue.setBounds(288, 97, 200, 50);
        pan.add(searchVlue);
        searchVlue.setColumns(10);

        JButton search = new JButton("SUBMIT");
        search.setBounds(288, 160, 200, 50);
        pan.add(search);

        final JRadioButton rdbtnState = new JRadioButton("State");
        rdbtnState.setBounds(62, 123, 200, 50);
        pan.add(rdbtnState);

        lblPleaseTypeSome = new JLabel("Please type some value");
        lblPleaseTypeSome.setBounds(288, 47, 200, 50);
        pan.add(lblPleaseTypeSome);
        ButtonGroup bg = new ButtonGroup();

        bg.add(cname);
        bg.add(oid);
        bg.add(rdbtnState);

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
                pan.add(jsp);
                frame.repaint();
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
