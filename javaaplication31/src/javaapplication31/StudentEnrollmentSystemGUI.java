package javaapplication31;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.Queue;

public class StudentEnrollmentSystemGUI {
    private static final double UNIVERSITY_FEE = 5000.0;  // Example fee, ensure this is defined correctly
    private static LinkedList<Course> courseCatalog = new LinkedList<>();
    private static LinkedList<Course> selectedCourses = new LinkedList<>();
    private static Queue<String> paymentQueue = new LinkedList<>();
    private static final String USER_DATA_FILE = "user_data.txt";
    private static final String ADMIN_USERNAME = "admin";
    private static final String ADMIN_PASSWORD = "admin";

    // Declare the fields globally
    private static JTextField studentIdField;
    private static JPasswordField passwordField;

    public static void main(String[] args) {
        initializeCourseCatalog();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = createMainFrame();
            frame.setVisible(true);
        });
    } 

    

    private static void processPaymentQueue() {
        new Thread(() -> {
            synchronized (paymentQueue) {
                while (!paymentQueue.isEmpty()) {
                    String paymentMethod = paymentQueue.poll();
                    System.out.println("Processing payment for: " + paymentMethod);
                    try {
                        Thread.sleep(1000);  // Simulate payment delay
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    
    @SuppressWarnings("unused")
    private static void updatePaymentPanelContent(JPanel paymentPanel) {
        paymentPanel.removeAll(); // Clear existing components
    
        JTextArea selectedCoursesArea = new JTextArea();
        selectedCoursesArea.setEditable(false);
    
        // Clear the previous text and append selected courses dynamically
        selectedCoursesArea.setText(""); // Clear previous content
        for (Course course : selectedCourses) {
            selectedCoursesArea.append(course.toString() + "\n");
        }
    
        // Debugging: Print selected courses and their fees
        System.out.println("Selected Courses: ");
        for (Course course : selectedCourses) {
            System.out.println(course.getName() + " - " + course.getFee());
        }
    
        // Calculate the total fee based on selected courses
        double courseFee = calculateCourseFee(); // Calculate the course fees only
        System.out.println("Calculated Course Fee: " + courseFee); // Debugging output
    
        double totalFee = calculateCourseFee() + UNIVERSITY_FEE; // Total fee including university fee
        System.out.println("University Fee: " + UNIVERSITY_FEE); // Debugging output
    
        // Debugging: Output total fee to the terminal
        System.out.println("Total Course Fee: " + courseFee + ", University Fee: " + UNIVERSITY_FEE + ", Total Fee: " + totalFee);
    
        // Append total fee to the JTextArea
        selectedCoursesArea.append("\nTotal Fee: " + UNIVERSITY_FEE);
    
        // Payment method selection buttons
        JRadioButton onlinePaymentButton = new JRadioButton("Online Payment");
        JRadioButton cashPaymentButton = new JRadioButton("Cash Payment");
        ButtonGroup paymentGroup = new ButtonGroup();
        paymentGroup.add(onlinePaymentButton);
        paymentGroup.add(cashPaymentButton);
    
        JButton payButton = new JButton("Pay");
        payButton.addActionListener(e -> handlePayment(
                (JFrame) SwingUtilities.getWindowAncestor(paymentPanel),
                onlinePaymentButton.isSelected(),
                cashPaymentButton.isSelected()
        ));
    
        // Panel for payment method selection
        JPanel paymentMethodPanel = new JPanel(new GridLayout(2, 1));
        paymentMethodPanel.add(onlinePaymentButton);
        paymentMethodPanel.add(cashPaymentButton);
    
        // Add components to the payment panel
        paymentPanel.setLayout(new BorderLayout()); // Ensure the panel has a layout
        paymentPanel.add(new JScrollPane(selectedCoursesArea), BorderLayout.CENTER);
        paymentPanel.add(paymentMethodPanel, BorderLayout.NORTH);
        paymentPanel.add(payButton, BorderLayout.SOUTH);
    
        // Refresh the panel
        paymentPanel.revalidate();
        paymentPanel.repaint();
    }
   
    private static void initializeCourseCatalog() {
        courseCatalog.add(new Course("COMP101", "Introduction to Programming", 3, 3000));
        courseCatalog.add(new Course("MATH120", "Calculus", 4, 4500));
        courseCatalog.add(new Course("PHYS101", "Physics", 3, 3200));
        Collections.sort(courseCatalog, Comparator.comparing(Course::getCode));
    }

    private static JFrame createMainFrame() {
        JFrame frame = new JFrame("Student Enrollment System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new CardLayout());
    
        JPanel loginPanel = createLoginPanel(frame);
        JPanel signUpPanel = createSignUpPanel(frame);
        JPanel selectionPanel = createSelectionPanel(frame);
        JPanel paymentPanel = createPaymentPanel(frame);  // Create payment panel here
        JPanel adminPanel = createAdminPanel(frame);
    
        frame.add(loginPanel, "Login");
        frame.add(signUpPanel, "SignUp");
        frame.add(selectionPanel, "Selection");
        frame.add(paymentPanel, "Payment");  // Add payment panel to the frame
        frame.add(adminPanel, "Admin");
    
        return frame;
    }
    

    private static JPanel createPaymentPanel(JFrame frame) {
        JPanel paymentPanel = new JPanel(new BorderLayout());
        JTextArea selectedCoursesArea = new JTextArea();
        selectedCoursesArea.setEditable(false);
        
        // Clear the previous text and append selected courses dynamically
        selectedCoursesArea.setText("");  // Clear previous content
        for (Course course : selectedCourses) {
            selectedCoursesArea.append(course.toString() + "\n");
        }
        
        // Calculate the total fee based on selected courses
        double totalCourseFee = calculateCourseFee();
        double totalFee = totalCourseFee + UNIVERSITY_FEE;
        
        // Display the total fee breakdown
        selectedCoursesArea.append("\nTotal Fee: " + totalFee + " pesos\n");
        
        // Payment method selection buttons
        JRadioButton onlinePaymentButton = new JRadioButton("Online Payment");
        JRadioButton cashPaymentButton = new JRadioButton("Cash Payment");
        ButtonGroup paymentGroup = new ButtonGroup();
        paymentGroup.add(onlinePaymentButton);
        paymentGroup.add(cashPaymentButton);
        
        JButton payButton = new JButton("Pay");
        payButton.addActionListener(e -> handlePayment(frame, onlinePaymentButton.isSelected(), cashPaymentButton.isSelected()));
        
        // Panel for payment method selection
        JPanel paymentMethodPanel = new JPanel(new GridLayout(2, 1));
        paymentMethodPanel.add(onlinePaymentButton);
        paymentMethodPanel.add(cashPaymentButton);
        
        // Add components to the payment panel
        paymentPanel.add(new JScrollPane(selectedCoursesArea), BorderLayout.CENTER);
        paymentPanel.add(paymentMethodPanel, BorderLayout.NORTH);
        paymentPanel.add(payButton, BorderLayout.SOUTH);
        
        return paymentPanel;
    }
    
   
    private static void handlePayment(JFrame frame, boolean onlinePaymentSelected, boolean cashPaymentSelected) {
        if (onlinePaymentSelected) {
            JOptionPane.showMessageDialog(frame, "Redirecting to secure payment gateway...");
            processPayment("Online Payment");
        } else if (cashPaymentSelected) {
            JOptionPane.showMessageDialog(frame, "Payment slip generated. Please pay at designated locations.");
            processPayment("Cash Payment");
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a payment method.");
            return;
        }
    
        JOptionPane.showMessageDialog(frame, "Payment received. Enrollment confirmed!");
        JOptionPane.showMessageDialog(frame, "Updating enrollment information and payment details...");
        JOptionPane.showMessageDialog(frame, "Thank you for using the Student Enrollment System!");
        selectedCourses.clear(); // Clear selected courses after payment
        showPanel(frame, "Selection");  // Redirect back to the Selection Panel after payment
    }
    

    private static void processPayment(String paymentMethod) {
        paymentQueue.add(paymentMethod);  // Add payment method to the queue
        processPaymentQueue();  // Process the queue in a separate thread
    }

    private static double calculateCourseFee() {
        double totalCourseFee = 0;
        for (Course course : selectedCourses) {
            totalCourseFee += course.getFee();
        }
        return totalCourseFee;
    }

    private static JPanel createLoginPanel(JFrame frame) {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Initialize the login fields
        studentIdField = new JTextField();
        passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        JButton signUpButton = new JButton("Sign Up");

        Dimension fieldDimension = new Dimension(200, 24);
        studentIdField.setPreferredSize(fieldDimension);
        passwordField.setPreferredSize(fieldDimension);
        loginButton.setPreferredSize(new Dimension(100, 24));

        loginButton.addActionListener(e -> handleLogin(frame, studentIdField.getText(), new String(passwordField.getPassword())));
        signUpButton.addActionListener(e -> showPanel(frame, "SignUp"));

        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(new JLabel("Student ID:"), gbc);

        gbc.gridx = 1;
        loginPanel.add(studentIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        loginPanel.add(passwordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        loginPanel.add(loginButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        loginPanel.add(signUpButton, gbc);

        return loginPanel;
    }

    private static void handleLogin(JFrame frame, String studentId, String password) {
        if (studentId.equals(ADMIN_USERNAME) && password.equals(ADMIN_PASSWORD)) {
            // Admin login
            JOptionPane.showMessageDialog(frame, "Admin login successful.");
            showPanel(frame, "Admin");
        } else if (isValidCredentials(studentId, password)) {
            // Student login
            JOptionPane.showMessageDialog(frame, "Login successful.");
            showPanel(frame, "Selection");  // Proceed to course selection
        } else {
            JOptionPane.showMessageDialog(frame, "Invalid login credentials.");
            studentIdField.setText("");
            passwordField.setText("");
        }
    }


    private static boolean isValidCredentials(String studentId, String password) {
        File file = new File(USER_DATA_FILE);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(null, "User data file not found. Please contact admin.");
            return false;
        }
    
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts[0].equals(studentId) && parts[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    

    private static JPanel createSignUpPanel(JFrame frame) {
        JPanel signUpPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField studentIdField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton registerButton = new JButton("Register");
        JButton backButton = new JButton("Back");

        Dimension fieldDimension = new Dimension(200, 24);
        studentIdField.setPreferredSize(fieldDimension);
        passwordField.setPreferredSize(fieldDimension);
        registerButton.setPreferredSize(new Dimension(100, 24));

        registerButton.addActionListener(e -> {
            String studentId = studentIdField.getText();
            String password = new String(passwordField.getPassword());
            if (!studentId.isEmpty() && !password.isEmpty()) {
                registerUser(studentId, password);
                JOptionPane.showMessageDialog(frame, "Registration successful. You can now log in.");
                showPanel(frame, "Login");
            } else {
                JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
            }
        });

        backButton.addActionListener(e -> showPanel(frame, "Login"));

        gbc.gridx = 0;
        gbc.gridy = 0;
        signUpPanel.add(new JLabel("Student ID:"), gbc);

        gbc.gridx = 1;
        signUpPanel.add(studentIdField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        signUpPanel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        signUpPanel.add(passwordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.EAST;
        signUpPanel.add(registerButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        signUpPanel.add(backButton, gbc);

        return signUpPanel;
    }

    private static void registerUser(String studentId, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_DATA_FILE, true))) {
            writer.write(studentId + "," + password);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static JPanel createSelectionPanel(JFrame frame) {
        JPanel selectionPanel = new JPanel(new BorderLayout());
        JTextArea courseCatalogArea = new JTextArea();
        courseCatalogArea.setEditable(false);
        
        // Display available courses in the catalog
        for (Course course : courseCatalog) {
            courseCatalogArea.append(course.toString() + "\n");
        }
        
        JTextField courseCodeField = new JTextField();
        JButton addButton = new JButton("Add Course");
        JButton enterButton = new JButton("Enter");
        JButton logoutButton = new JButton("Logout");
        
        // Action listener for the Add Course button
        addButton.addActionListener(e -> {
            String courseCode = courseCodeField.getText().trim();  // Get the entered course code
            if (courseCode.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please enter a course code.", "Input Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        
            // Add the course if it exists in the catalog
            boolean found = false;
            for (Course course : courseCatalog) {
                if (course.getCode().equals(courseCode)) {
                    selectedCourses.add(course);  // Add the course to the selectedCourses list
                    found = true;
                    JOptionPane.showMessageDialog(frame, "Course added: " + course.getCode());
                    System.out.println("Selected Courses: " + selectedCourses);  // Debugging line to check selected courses
                    break;
                }
            }
        
            if (!found) {
                JOptionPane.showMessageDialog(frame, "Course code not found.");
            } else {
                // Clear the course code field after adding a course
                courseCodeField.setText("");
            }
        });
        
        
        // Action listener for the Enter button that proceeds to payment
        enterButton.addActionListener(e -> {
            if (selectedCourses.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "No courses selected! Please add courses first.");
            } else {
                // Proceed to Payment Panel after adding the course
                showPanel(frame, "Payment");
            }
        });
        
        // Action listener for the Logout button
        logoutButton.addActionListener(e -> {
            selectedCourses.clear();  // Clear selected courses before logout
            showPanel(frame, "Login");  // Redirect to the login screen
        });
        
        // Create input panel for course code and action buttons
        JPanel inputPanel = new JPanel(new GridLayout(1, 5));  // Changed to GridLayout with 5 columns for 4 buttons and input
        inputPanel.add(new JLabel("Enter course code:"));
        inputPanel.add(courseCodeField);
        inputPanel.add(addButton);
        inputPanel.add(enterButton);
        inputPanel.add(logoutButton);  // Add the logout button to the same panel
        
        // Add course catalog area and buttons to the selection panel
        selectionPanel.add(new JScrollPane(courseCatalogArea), BorderLayout.CENTER);
        selectionPanel.add(inputPanel, BorderLayout.SOUTH);
        
        return selectionPanel;
    }
    
    

    @SuppressWarnings("unused")
    private static void handleAddCourse(String courseCode, JFrame frame) {
        for (Course course : courseCatalog) {
            if (course.getCode().equals(courseCode)) {
                if (!selectedCourses.contains(course)) {
                    selectedCourses.add(course);
                    JOptionPane.showMessageDialog(frame, "Course added: " + course.getCode());
    
                    // Debug: Print selected courses
                    System.out.println("Selected Courses:");
                    for (Course selectedCourse : selectedCourses) {
                        System.out.println(selectedCourse);
                    }
    
                    // Recalculate total fee and update payment panel
                    double totalCourseFee = calculateCourseFee();
                    double totalFee = totalCourseFee + UNIVERSITY_FEE;
                    updatePaymentPanel(frame, totalCourseFee, totalFee);
                } else {
                    JOptionPane.showMessageDialog(frame, "Course already selected.");
                }
                return;
            }
        }
        JOptionPane.showMessageDialog(frame, "Course code not found.");
    }
    
    private static void updatePaymentPanel(JFrame frame, double totalCourseFee, double totalFee) {
        // Get the current payment panel
        JPanel paymentPanel = (JPanel) frame.getContentPane().getComponent(3); // Assuming payment panel is the 4th component
    
        // Update the total fee display
        JTextArea selectedCoursesArea = (JTextArea) paymentPanel.getComponent(0);
        selectedCoursesArea.setText("");
        for (Course course : selectedCourses) {
            selectedCoursesArea.append(course.toString() + "\n");
        }
        selectedCoursesArea.append("\nTotal Fee: " + totalCourseFee + " + " + UNIVERSITY_FEE + " = " + totalFee + " pesos\n");
    
        // Revalidate and repaint the panel to reflect the changes
        paymentPanel.revalidate();
        paymentPanel.repaint();
    }

    private static void showPanel(JFrame frame, String panelName) {
        CardLayout cardLayout = (CardLayout) frame.getContentPane().getLayout();
        cardLayout.show(frame.getContentPane(), panelName);
    }
    

    private static JPanel createAdminPanel(JFrame frame) {
        JPanel adminPanel = new JPanel(new BorderLayout());
    
        // Upper panel to display the course catalog
        JTextArea courseCatalogArea = new JTextArea();
        courseCatalogArea.setEditable(false);
    
        // Display the course catalog in the text area
        for (Course course : courseCatalog) {
            courseCatalogArea.append(course.toString() + "\n");
        }
    
        JScrollPane scrollPane = new JScrollPane(courseCatalogArea);
    
        // Create fields for course details
        JTextField courseCodeField = new JTextField(10);
        JTextField courseNameField = new JTextField(20);
        JTextField unitsField = new JTextField(5);
        JTextField feeField = new JTextField(10);
    
        // Add and Remove course buttons
        JButton addCourseButton = new JButton("Add Course");
        JButton removeCourseButton = new JButton("Remove Course");
    
        // Action listeners for buttons
        addCourseButton.addActionListener(e -> {
            String code = courseCodeField.getText().trim();
            String name = courseNameField.getText().trim();
            String unitsText = unitsField.getText().trim();
            String feeText = feeField.getText().trim();
    
            if (code.isEmpty() || name.isEmpty() || unitsText.isEmpty() || feeText.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Please fill in all fields.");
                return;
            }
    
            try {
                int units = Integer.parseInt(unitsText);
                double fee = Double.parseDouble(feeText);
                Course newCourse = new Course(code, name, units, fee);
                courseCatalog.add(newCourse);
                Collections.sort(courseCatalog, Comparator.comparing(Course::getCode));
                refreshCourseCatalog(courseCatalogArea);  // Refresh the catalog display
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame, "Invalid input for units or fee.");
            }
        });
    
        removeCourseButton.addActionListener(e -> {
            String courseCodeToRemove = JOptionPane.showInputDialog(frame, "Enter the course code to remove:");
            if (courseCodeToRemove != null && !courseCodeToRemove.isEmpty()) {
                Course courseToRemove = null;
                for (Course course : courseCatalog) {
                    if (course.getCode().equals(courseCodeToRemove)) {
                        courseToRemove = course;
                        break;
                    }
                }
    
                if (courseToRemove != null) {
                    courseCatalog.remove(courseToRemove);
                    refreshCourseCatalog(courseCatalogArea);  // Refresh the catalog display
                } else {
                    JOptionPane.showMessageDialog(frame, "Course not found.");
                }
            }
        });
    
        // Panel for course details input (lower half of the admin panel)
        JPanel inputPanel = new JPanel(new GridLayout(5, 2));  // Create a grid layout for input fields
        inputPanel.add(new JLabel("Course Code:"));
        inputPanel.add(courseCodeField);
        inputPanel.add(new JLabel("Course Name:"));
        inputPanel.add(courseNameField);
        inputPanel.add(new JLabel("Units:"));
        inputPanel.add(unitsField);
        inputPanel.add(new JLabel("Fee:"));
        inputPanel.add(feeField);
        inputPanel.add(addCourseButton);
        inputPanel.add(removeCourseButton);
    
        // Log Out button
        JButton logOutButton = new JButton("Log Out");
        logOutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to log out?", "Log Out", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                // Clear selected courses before logging out
                selectedCourses.clear();  
        
                // Redirect back to the Login screen (Home page)
                showPanel(frame, "Login");
            }
        });
    
        // Panel for log out button at the top-right corner (or wherever desired)
        JPanel logOutPanel = new JPanel(new BorderLayout());
        logOutPanel.add(logOutButton, BorderLayout.EAST);
    
        // Add components to the admin panel
        adminPanel.add(logOutPanel, BorderLayout.NORTH);  // Log out button at the top
        adminPanel.add(scrollPane, BorderLayout.CENTER);  // Upper half for catalog
        adminPanel.add(inputPanel, BorderLayout.SOUTH);   // Lower half for input fields and buttons
    
        return adminPanel;
    }
    
    private static void refreshCourseCatalog(JTextArea courseCatalogArea) {
        courseCatalogArea.setText("");  // Clear the current text
        for (Course course : courseCatalog) {
            courseCatalogArea.append(course.toString() + "\n");
        }
    }
   
}
