import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;
import javax.swing.table.DefaultTableModel;

public class BookBlissGUI extends JFrame {
    JFrame viewCartFrame;
    JTable bookTable;
    final double[] totalAmount = {0};
    static boolean logged = false;
    static int id = 3;
    int userId;
    JPanel panel = new JPanel(new GridBagLayout());
    private CardLayout cardLayout;
    private JPanel cardPanel;

    public BookBlissGUI() {
        setTitle("BookBliss");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(300, 450);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        // Create main page
        JPanel mainPagePanel = createMainPagePanel();
        cardPanel.add(mainPagePanel, "mainPage");

        // Create login page
        JPanel loginPagePanel = createLoginPagePanel();
        cardPanel.add(loginPagePanel, "loginPage");

        // Create signup page
        JPanel signupPagePanel = createSignupPagePanel();
        cardPanel.add(signupPagePanel, "signupPage");
        add(cardPanel);
        setVisible(true);
    }
    private JPanel createMainPagePanel() {

        panel.setBackground(new Color(255, 255, 240));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(20, 10, 20, 10);

        JLabel headingLabel = new JLabel("BOOKBLISS");
        headingLabel.setFont(new Font("Segoe Script", Font.BOLD, 24));
        headingLabel.setForeground(new Color(101, 144, 100));
        headingLabel.setHorizontalAlignment(JLabel.CENTER);

        ImageIcon logoIcon = new ImageIcon("C:\\Users\\swath\\Desktop\\JAVAPROJECT\\images.png"); // Replace with the actual path
        ImageIcon resizedIcon = resizeIcon(logoIcon, 100, 100);
        JLabel logoLabel = new JLabel(resizedIcon);
        logoLabel.setHorizontalAlignment(JLabel.CENTER);

        JButton loginButton = createStyledButton("Login", 200, 40);
        JButton signupButton = createStyledButton("Signup", 200, 40);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "loginPage");
            }
        });

        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "signupPage");
            }
        });

        gbc.gridwidth = 2;
        panel.add(headingLabel, gbc);
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 10, 10, 10); // Adjusted padding
        panel.add(logoLabel, gbc);
        gbc.gridy++;
        panel.add(loginButton, gbc);
        gbc.gridy++;
        panel.add(signupButton, gbc);
        return panel;
    }

    private JPanel createLoginPagePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(255, 255, 240));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(2, 10, 2, 10);

        JLabel loginHeadingLabel = new JLabel("LOGIN");
        loginHeadingLabel.setFont(new Font("Segoe Script", Font.BOLD, 18));
        loginHeadingLabel.setForeground(new Color(101, 144, 100));
        loginHeadingLabel.setHorizontalAlignment(JLabel.CENTER);

        JTextField emailField = new JTextField();
        emailField.setPreferredSize(new Dimension(200, 30));
        JPasswordField passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, 30));

        JButton loginButton = createStyledButton("Login", 100, 30);
        JButton backButton = createStyledButton("Back", 100, 30);


        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String email = emailField.getText();
                char[] pass = passwordField.getPassword();
                ArrayList<String> errormsg = new ArrayList<>();
                if (email.isEmpty() || pass.length == 0) {
                    errormsg.add("No fields should be empty.");
                }
                if (!errormsg.isEmpty()) {
                    StringBuilder errorMessageBuilder = new StringBuilder();
                    errorMessageBuilder.append(errormsg);
                    JOptionPane.showMessageDialog(null, "<html><body><p style='width: 200px;'>" + errorMessageBuilder.toString() + "</p></body></html>", "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    String url = "jdbc:mysql://localhost:3306/bookstore";
                    String user = "root";
                    String password = "root";

                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection connection = DriverManager.getConnection(url, user, password);
                        System.out.println("Connected to the database!");
                        Statement statement = connection.createStatement();
                        String sql = "SELECT * FROM user WHERE email=? AND password=?";
                        PreparedStatement ps = connection.prepareStatement(sql);
                        ps.setString(1, email);
                        String p = new String(pass);
                        ps.setString(2, p);
                        ResultSet rs = ps.executeQuery();

                        if (rs.next()) {
                            userId = rs.getInt("id");
                            JOptionPane.showMessageDialog(null, "Login Successful", "Success", JOptionPane.INFORMATION_MESSAGE);
                            logged = true;
                            emailField.setText("");
                            passwordField.setText("");
                            viewStore();

                        } else {
                            JOptionPane.showMessageDialog(null, "Invalid Credentials", "Error", JOptionPane.ERROR_MESSAGE);
                        }

                        // Close ResultSet, PreparedStatement, and Connection
                        rs.close();
                        ps.close();
                        connection.close();


                    } catch (ClassNotFoundException | SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.show(cardPanel, "mainPage");
                emailField.setText("");
                passwordField.setText("");
            }
        });

        gbc.gridwidth = 2;
        panel.add(loginHeadingLabel, gbc);
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(5, 10, 5, 10);
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridy++;
        panel.add(emailField, gbc);
        gbc.gridy++;
        panel.add(new JLabel("Password:"), gbc);
        gbc.gridy++;
        panel.add(passwordField, gbc);
        gbc.gridy++;
        panel.add(loginButton, gbc);
        gbc.gridy++;
        panel.add(backButton, gbc);

        return panel;
    }

    private JPanel createSignupPagePanel() {
        JPanel panel = new JPanel(new GridLayout(9, 2, 10, 10));
        panel.setBackground(new Color(255, 255, 240));

        JLabel signupHeadingLabel = new JLabel("SIGNUP");
        signupHeadingLabel.setFont(new Font("Segoe Script", Font.BOLD, 18));
        signupHeadingLabel.setForeground(new Color(101, 144, 100));
        signupHeadingLabel.setHorizontalAlignment(JLabel.CENTER);

        JButton signupButton = createStyledButton("Signup", 150, 30);
        JButton backButton = createStyledButton("Back", 150, 30);

        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();
        JTextField addressField = new JTextField();

        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                ArrayList<String> errormsg = new ArrayList<>();
                String name = nameField.getText();
                String email = emailField.getText();
                String phne = phoneField.getText();
                char[] pswd = passwordField.getPassword();
                char[] cpswd = confirmPasswordField.getPassword();
                String addr = addressField.getText();
                if (name.isEmpty() || email.isEmpty() || phne.isEmpty() || pswd.length == 0 || cpswd.length == 0 || addr.isEmpty()) {

                    errormsg.add("* No fields should be left empty.");
                }

                if (name.matches(".*\\d.*")) {
                    errormsg.add("* Name should only contain characters.");
                }
                if (!isValidEmail(email)) {
                    errormsg.add("* Invalid email format.");
                }
                if (!isValidPhoneNumber(phne)) {
                    errormsg.add("* Phone number should only contain digits.");
                }
                if (!Arrays.equals(pswd, cpswd)) {
                    errormsg.add("* Password and confirm password should be same.");
                }
                if (!errormsg.isEmpty()) {
                    StringBuilder errorMessageBuilder = new StringBuilder();
                    for (String errorMessage : errormsg) {
                        errorMessageBuilder.append(errorMessage).append("<br>");
                    }
                    JOptionPane.showMessageDialog(null, "<html><body><p style='width: 200px;'>" + errorMessageBuilder.toString() + "</p></body></html>", "Error",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Signed up successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    logged = true;
                    nameField.setText("");
                    emailField.setText("");
                    phoneField.setText("");
                    passwordField.setText("");
                    confirmPasswordField.setText("");
                    addressField.setText("");
                    String url = "jdbc:mysql://localhost:3306/bookstore";
                    String user = "root";
                    String password = "root";
                    try {
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        Connection connection = DriverManager.getConnection(url, user, password);
                        System.out.println("Connected to the database!");
                        // Create a statement
                        Statement statement = connection.createStatement();
                        String sql = "INSERT INTO user(id,name, email, phone_number, password, address)" + "values(?,?,?,?,?,?)";
                        PreparedStatement ps = connection.prepareStatement(sql);
                        ps.setInt(1, id);
                        ps.setString(2, name);
                        ps.setString(3, email);
                        ps.setString(4, phne);
                        String p = new String(pswd);
                        ps.setString(5, p);
                        ps.setString(6, addr);
                        userId = id;
                        id++;
                        int addedrows = ps.executeUpdate();
                        viewStore();

                    } catch (ClassNotFoundException | SQLException ex) {
                        ex.printStackTrace();
                    }

                }
            }
        });
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nameField.setText("");
                emailField.setText("");
                phoneField.setText("");
                passwordField.setText("");
                confirmPasswordField.setText("");
                addressField.setText("");
                cardLayout.show(cardPanel, "mainPage");
            }

        });

        panel.add(signupHeadingLabel);
        panel.add(new JLabel());
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Phone Number:"));
        panel.add(phoneField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmPasswordField);
        panel.add(new JLabel("Address:"));
        panel.add(addressField);
        panel.add(signupButton);
        panel.add(backButton);

        return panel;
    }

    private JButton createStyledButton(String text, int width, int height) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBackground(new Color(144, 195, 137));
        button.setForeground(new Color(255, 255, 240));
        button.setPreferredSize(new Dimension(width, height));
        button.setBorder(BorderFactory.createLineBorder(new Color(101, 144, 100), 4));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        return button;
    }

    private ImageIcon resizeIcon(ImageIcon icon, int width, int height) {
        Image image = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

    private boolean isValidEmail(String email) {
        String regex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String regex = "^[0-9]+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(phoneNumber);
        return matcher.matches();
    }

    private void viewStore() {
        // Close the current JFrame
        this.setVisible(false);

        // Create a new JFrame for the viewStorePanel
        JFrame viewStoreFrame = new JFrame("BookBliss - View Store");
        viewStoreFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        viewStoreFrame.setSize(900, 700);
        viewStoreFrame.setLocationRelativeTo(null);

        // Create the main panel for the viewStorePanel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(255, 255, 240));

        // Heading Label
        JLabel headingLabel = new JLabel("BOOKBLISS");
        headingLabel.setFont(new Font("Segoe Script", Font.BOLD, 24));
        headingLabel.setForeground(new Color(101, 144, 100));
        // headingLabel.setHorizontalAlignment(JLabel.CENTER);

        JButton viewCartButton = createStyledButton("View Cart", 90, 30);
        viewCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewCart();
            }
        });
        // Logo Label
        ImageIcon logoIcon = new ImageIcon("C:\\Users\\swath\\Desktop\\JAVAPROJECT\\images.png");
        ImageIcon resizedIcon = resizeIcon(logoIcon, 50, 40);
        JLabel logoLabel = new JLabel(resizedIcon);

        // Add to Cart Button
        JButton addToCartButton = createStyledButton("Logout", 90, 30);
        addToCartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewStoreFrame.setVisible(false);
                setTitle("BookBliss");
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setSize(300, 450);
                setLocationRelativeTo(null);

                add(cardPanel);

                setVisible(true);

            }
        });

        // Header Panel containing logo and heading
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBackground(new Color(255, 255, 240));
        headerPanel.add(logoLabel);
        headerPanel.add(headingLabel);

        // Button Panel containing "Add to Cart" button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(255, 255, 240));
        buttonPanel.add(addToCartButton);
        JPanel buttonPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel2.setBackground(new Color(255, 255, 240));
        buttonPanel2.add(viewCartButton);

        // Add components to the mainPanel
        mainPanel.add(headerPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.EAST);
        mainPanel.add(buttonPanel2, BorderLayout.WEST);

        viewStoreFrame.setLayout(new BorderLayout());
        viewStoreFrame.add(mainPanel, BorderLayout.NORTH);

        JPanel booklist = new JPanel(new GridLayout(0, 2, 10, 10)); // Two columns
        booklist.setBackground(new Color(255, 255, 240));
        GridBagConstraints gbc = new GridBagConstraints();
        bookD p = new bookD();
        p.fetchBooks();

        for (int i = 0; i < p.books.length; i++) {
            if (p.books[i] != null) {
                // Create a panel for each book
                JPanel bookPanel = new JPanel(new BorderLayout());
                bookPanel.setBackground(new Color(255, 255, 240));

                // Use a specific image for each book
                String imagePath = "C:\\Users\\swath\\Desktop\\JAVAPROJECT\\download" + (i) + ".jpeg";
                ImageIcon imageIcon = new ImageIcon(imagePath);
                Image scaledImage = imageIcon.getImage().getScaledInstance(70, 100, Image.SCALE_SMOOTH);
                ImageIcon resizedImageIcon = new ImageIcon(scaledImage);
                JLabel imageLabel = new JLabel(resizedImageIcon);
                bookPanel.add(imageLabel, BorderLayout.WEST);

                // Create a details panel for book information
                JPanel detailsPanel = new JPanel(new GridLayout(0, 1));
                detailsPanel.setBackground(new Color(255, 255, 240));

                // Add book details to the details panel
                detailsPanel.add(new JLabel("  Book Name: " + p.books[i].bookName));
                detailsPanel.add(new JLabel("  Author: " + p.books[i].author));
                detailsPanel.add(new JLabel("  Price: " + p.books[i].price));
                JLabel availabilityLabel = new JLabel("  Availability: ");
                if (p.books[i].availability) {
                    // If available, set text to "Available" in green and italic
                    availabilityLabel.setText("<html><i><font color='green'>Available</font></i></html>");
                } else {
                    // If not available, set text to "Not Available" in red and italic
                    availabilityLabel.setText("<html><i><font color='red'>Not Available</font></i></html>");
                }
                detailsPanel.add(availabilityLabel);
                int finalI = i;
                int finalI1 = i;
                detailsPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        // Handle the click event
                        // Here, you can add the clicked book to the "Add to Cart" button or perform any other action

                        // Create a custom dialog
                        JDialog dialog = new JDialog();
                        dialog.setTitle("Book Selected");

                        // Create a panel for the dialog content
                        JPanel dialogPanel = new JPanel(new BorderLayout());
                        dialogPanel.setBackground(new Color(255, 255, 240));

                        // Display the selected book details
                        String selectedBook = "<html><body><p style='width: 200px;'>" +
                                "Book Name: " + p.books[finalI1].bookName + "<br>" +
                                "Author: " + p.books[finalI1].author + "<br>" +
                                "Price: " + p.books[finalI1].price + "<br>" +
                                "Availability: " + (p.books[finalI1].availability ? "<font color='green'><i>Available</i></font>" : "<font color='red'><i>Not Available</i></font>") +
                                "</p></body></html>";
                        String bookID = p.books[finalI1].bid;
                        JLabel bookDetailsLabel = new JLabel(selectedBook);
                        dialogPanel.add(bookDetailsLabel, BorderLayout.CENTER);
                        dialogPanel.setBorder(BorderFactory.createEmptyBorder(10, 40, 10, 10));
                        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                        buttonPanel.setBackground(new Color(255, 255, 240));
                        if (p.books[finalI1].availability) {
                            JButton addToCartButton = createStyledButton("Add to Cart", 90, 30);
                            addToCartButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    // Add the book to the cart or perform any other action
                                   // JOptionPane.showMessageDialog(null, "Book added to cart!", "Success", JOptionPane.INFORMATION_MESSAGE);
//                                    // Close the dialog after adding to the cart
                                    String url = "jdbc:mysql://localhost:3306/bookstore";
                                    String user = "root";
                                    String password = "root";
                                    try {
                                        Class.forName("com.mysql.cj.jdbc.Driver");
                                        Connection connection = DriverManager.getConnection(url, user, password);

                                        // Create a statement
                                        Statement statement = connection.createStatement();
                                        String sql1 = "SELECT * from user where id=?";
                                        PreparedStatement ps1 = connection.prepareStatement(sql1);
                                        ps1.setInt(1, userId);
                                        ResultSet rs1 = ps1.executeQuery();
                                        String l = null;
                                        if (rs1.next()) {
                                            l = rs1.getString("cart");
                                        }
                                        if (l != null) {
                                            String sql = "UPDATE user SET cart=CONCAT(cart, ?) WHERE id=?";
                                            PreparedStatement ps = connection.prepareStatement(sql);

                                            //   ps.setInt(1,bookId);
                                            ps.setString(1, bookID + ",");
                                            ps.setInt(2, userId);
                                            int rs = ps.executeUpdate();
                                        } else {
                                            String sql = "UPDATE user SET cart=? WHERE id=?";
                                            PreparedStatement ps = connection.prepareStatement(sql);
                                            //   ps.setInt(1,bookId);
                                            ps.setString(1, bookID + ",");
                                            ps.setInt(2, userId);
                                            int rs = ps.executeUpdate();
                                        }
                                        String sql2 = "SELECT * from books where book_id=?";
                                        PreparedStatement ps2 = connection.prepareStatement(sql2);
                                        ps2.setString(1, bookID);
                                        ResultSet rs2 = ps2.executeQuery();

                                        int avail=0;

                                        if (rs2.next()) {

                                            avail = rs2.getInt("num_of_books_available");

                                            avail = avail - 1;
                                            String sql3 = "UPDATE books SET num_of_books_available=? WHERE book_id=?";
                                            PreparedStatement ps3 = connection.prepareStatement(sql3);

                                            ps3.setInt(1, avail);
                                            ps3.setString(2, bookID);
                                            int rs3 = ps3.executeUpdate();
                                        }

                                    } catch (ClassNotFoundException | SQLException ex) {
                                        ex.printStackTrace();
                                    }
                                    dialog.dispose();
                                }
                            });

                            buttonPanel.add(addToCartButton);
                        } else {
                            JButton backButton = createStyledButton("Back", 90, 30);
                            backButton.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e) {
                                    // Add the book to the cart or perform any other action
                                    dialog.dispose();
                                }
                            });

                            buttonPanel.add(backButton);
                        }
                        // Create the "Add to Cart" button

                        dialogPanel.add(buttonPanel, BorderLayout.SOUTH);

                        dialog.add(dialogPanel, BorderLayout.CENTER);
                        dialog.setSize(300, 200);
                        dialog.setLocationRelativeTo(null);
                        dialog.setVisible(true);
                    }
                });
                // Add the details panel to the book panel
                bookPanel.add(detailsPanel, BorderLayout.CENTER);

                // Add the book panel to the main grid layout
                booklist.add(bookPanel);
            }
        }

        viewStoreFrame.add(new JScrollPane(booklist), BorderLayout.CENTER);

        viewStoreFrame.setVisible(true);

    }
    private void removeBookFromCart(String bookId) {
        String ul = "jdbc:mysql://localhost:3306/bookstore";
        String ur = "root";
        String passd = "root";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(ul, ur, passd);

            // Remove the book from the user's cart in the database
            String removeFromCartSql = "UPDATE user SET cart = REPLACE(cart, ?, '') WHERE id = ?";
            try (PreparedStatement removeFromCartPs = connection.prepareStatement(removeFromCartSql)) {
                removeFromCartPs.setString(1, bookId + ",");
                removeFromCartPs.setInt(2, userId);
                removeFromCartPs.executeUpdate();
            }

            // Close the database connection
            connection.close();

        } catch (ClassNotFoundException | SQLException ex) {
            ex.printStackTrace();
        }


    }
     void viewCart() {
         viewCartFrame = new JFrame("View Cart");
         viewCartFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         viewCartFrame.setSize(400, 370); // Adjusted frame size
         viewCartFrame.setLocationRelativeTo(null);
         viewCartFrame.setLayout(new BorderLayout());
         viewCartFrame.setVisible(true);

         DefaultTableModel tableModel = new DefaultTableModel(new Object[]{"ID", "Book Name", "Price"}, 0);
         bookTable = new JTable(tableModel);

         JButton removeButton = createStyledButton("Remove", 90, 30);
         // Heading Panel


         // Book Details Panel
         JPanel bookDetailsPanel = new JPanel();
         bookDetailsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 5)); // Adjusted layout
         bookDetailsPanel.setBackground(new Color(255, 255, 240));

         // Create a table for displaying book details

         bookTable.setBackground(new Color(255, 255, 240));
         JScrollPane scrollPane = new JScrollPane(bookTable);
         scrollPane.setBackground(new Color(255, 255, 240));
         bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow selecting only one row

         String url = "jdbc:mysql://localhost:3306/bookstore";
         String user = "root";
         String password = "root";


         try {
             Class.forName("com.mysql.cj.jdbc.Driver");
             Connection connection = DriverManager.getConnection(url, user, password);

             String sql1 = "SELECT * from user where id=?";
             PreparedStatement ps1 = connection.prepareStatement(sql1);
             ps1.setInt(1, userId);
             ResultSet rs1 = ps1.executeQuery();
             String cartDetails = null;
             if (rs1.next()) {
                 cartDetails = rs1.getString("cart");
             }

             // Check if cartDetails is not null
             if (cartDetails != null) {
                 // Split the cartDetails string into an array based on commas
                 String[] bookIds = cartDetails.split(",");
                 //int slNo = 1; // To track the serial number in the table
                 // Now you have an array of bookIds, you can fetch and display book details
                 for (String bookId : bookIds) {
                     String sql2 = "SELECT * FROM books WHERE book_id=?";
                     try (PreparedStatement ps2 = connection.prepareStatement(sql2)) {
                         ps2.setInt(1, Integer.parseInt(bookId.trim()));
                         ResultSet rs2 = ps2.executeQuery();

                         if (rs2.next()) {
                             int bookID = rs2.getInt("book_id");
                             String bookName = rs2.getString("name");
                             double price = rs2.getDouble("price");
                             totalAmount[0] += price;

                             // Add data to the table model
                             tableModel.addRow(new Object[]{bookID, bookName, "Rs." + price});

                             //slNo++;
                         }
                     }
                 }
             }
             // Set the table as non-editable
             bookTable.setDefaultEditor(Object.class, null);
             bookTable.setBackground(new Color(255, 255, 240));
             // Add the table to the scroll pane
             scrollPane.setPreferredSize(new Dimension(350, 150));
             bookDetailsPanel.add(scrollPane);
             // Total Panel
             JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
             totalPanel.setBackground(new Color(255, 255, 240));
             JLabel totalLabel = new JLabel("Total Amount: Rs." + totalAmount[0]);
             totalLabel.setFont(new Font("Arial", Font.BOLD, 14)); // Larger font size
             totalPanel.add(totalLabel);
             bookDetailsPanel.add(totalPanel);
             JRadioButton cashOnDeliveryRadioButton = new JRadioButton("Cash on Delivery");
             JRadioButton netBankingRadioButton = new JRadioButton("Net Banking");
             ButtonGroup paymentGroup = new ButtonGroup();
             paymentGroup.add(cashOnDeliveryRadioButton);
             paymentGroup.add(netBankingRadioButton);
             JPanel BPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
             BPanel.setBackground(new Color(255, 255, 240));
             BPanel.add(cashOnDeliveryRadioButton);
             BPanel.add(netBankingRadioButton);
             bookDetailsPanel.add(BPanel);
// Add BPanel just above the buttons
             // Remove Button
             removeButton.addActionListener(new ActionListener() {
                 @Override
                 public void actionPerformed(ActionEvent e) {
                     int selectedRow = bookTable.getSelectedRow();
                     if (selectedRow != -1) {
                         String bookId = bookTable.getValueAt(selectedRow, 0).toString();
                         double priceToRemove = Double.parseDouble(bookTable.getValueAt(selectedRow, 2).toString().substring(3));

                         // Update total amount
                         totalAmount[0] -= priceToRemove;
                         totalLabel.setText("Total Amount: Rs." + totalAmount[0]);

                         // Remove the selected row from the table
                         tableModel.removeRow(selectedRow);

                         // Remove the book from the user's cart in the database
                         removeBookFromCart(bookId);

                         // Update the table display
                         bookTable.repaint();
                     }
                 }
             });
             // Add the heading and book details panel to the frame
             viewCartFrame.add(bookDetailsPanel, BorderLayout.CENTER);
             viewCartFrame.add(removeButton, BorderLayout.NORTH);

             // Add Buy Now and Back buttons
             JButton backButton = createStyledButton("Back", 90, 30);
             backButton.addActionListener(new ActionListener() {
                 @Override
                 public void actionPerformed(ActionEvent e) {
                     viewCartFrame.dispose();
                 }

             });
             JButton buyNowButton = createStyledButton("Buy Now", 90, 30);
             buyNowButton.addActionListener(new ActionListener() {
                 @Override
                 public void actionPerformed(ActionEvent e) {
                     String paymentOption = cashOnDeliveryRadioButton.isSelected() ? "Cash on Delivery" : "Net Banking";
                     generateReceipt(paymentOption);
                     totalAmount[0]=0;
                     String url = "jdbc:mysql://localhost:3306/bookstore";
                     String user = "root";
                     String password = "root";


                     try {
                         Class.forName("com.mysql.cj.jdbc.Driver");
                         Connection connection = DriverManager.getConnection(url, user, password);

                         // Remove the book from the user's cart in the database
                         String removeFromCartSql = "UPDATE user SET cart = null WHERE id = ?";
                         try (PreparedStatement removeFromCartPs = connection.prepareStatement(removeFromCartSql)) {
                             removeFromCartPs.setInt(1, userId);
                             removeFromCartPs.executeUpdate();
                         }
                         // Close the database connection
                         connection.close();
                         viewCartFrame.dispose();
                     }
                     catch(Exception ex)
                     {
                         ex.printStackTrace();
                     }

                 }
             });

             JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
             buttonsPanel.setBackground(new Color(255, 255, 240));
             buttonsPanel.add(backButton);
             buttonsPanel.add(buyNowButton);
             viewCartFrame.add(buttonsPanel, BorderLayout.SOUTH);


         } catch (ClassNotFoundException | SQLException ex) {
             ex.printStackTrace();
         }
     }

    private void generateReceipt(String paymentOption) {
        List<String> purchasedBooks = new ArrayList<>();

        for (int i = 0; i < bookTable.getRowCount(); i++) {
            String bookName = bookTable.getValueAt(i, 2).toString();
            purchasedBooks.add(bookName);
        }

        StringBuilder receiptMessage = new StringBuilder();
        receiptMessage.append("\t    Receipt\n\n");
        receiptMessage.append("Payment Option: \t").append(paymentOption).append("\n");
        receiptMessage.append("Books Purchased:");

        double total = totalAmount[0];
        boolean p=false;
        for (String book : purchasedBooks) {
            if(p==false)
            {
                receiptMessage.append("\t- ").append(book).append("\n");
                p=true;
            }
           else{
                receiptMessage.append("\t\t- ").append(book).append("\n");
            }
        }

        receiptMessage.append("----------------------------------------------------\n");
        receiptMessage.append("Total Amount:\t\tRs.").append(total).append("\n");
        receiptMessage.append("Payment Status: \t").append(paymentOption.equals("Net Banking") ? "Paid" : "Not paid");
        receiptMessage.append("\n\n      ORDER SUCCESSFULLY PLACED");

        JTextArea receiptTextArea = new JTextArea(receiptMessage.toString());
        receiptTextArea.setEditable(false);
        receiptTextArea.setFont(new Font("Arial", Font.PLAIN, 14));
        receiptTextArea.setForeground(Color.BLACK);
        receiptTextArea.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(receiptTextArea);
        scrollPane.setPreferredSize(new Dimension(300, 200));
        JDialog receiptDialog = new JDialog(viewCartFrame, "Receipt", true);
        receiptDialog.getContentPane().add(scrollPane, BorderLayout.CENTER);

        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> receiptDialog.dispose());

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(okButton);

        receiptDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Center the dialog relative to the main frame
        receiptDialog.setLocationRelativeTo(viewCartFrame);

        receiptDialog.pack();
        receiptDialog.setVisible(true);
    }


    public static void main(String[] args) {
       BookBlissGUI in= new BookBlissGUI();
    }
}
