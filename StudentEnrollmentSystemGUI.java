package javaapplication31;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class StudentEnrollmentSystemGUI {

    private static final double UNIVERSITY_FEE = 5000; // Replace with actual university fee in pesos
    private static ArrayList<Course> courseCatalog = new ArrayList<>();
    private static ArrayList<Course> selectedCourses = new ArrayList<>();

    public static void main(String[] args) {
        initializeCourseCatalog();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = createMainFrame();
            frame.setVisible(true);
        });
    }

    private static void initializeCourseCatalog() {
        // Initialize course catalog (replace with actual course data)
        courseCatalog.add(new Course("COMP101", "Introduction to Programming", 3, 3000)); // Fee in pesos
        courseCatalog.add(new Course("MATH120", "Calculus", 4, 4500)); // Fee in pesos
    }

    private static JFrame createMainFrame() {
        JFrame frame = new JFrame("Student Enrollment System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new CardLayout());

        JPanel loginPanel = createLoginPanel(frame);
        JPanel selectionPanel = createSelectionPanel(frame);
        JPanel paymentPanel = createPaymentPanel(frame);

        frame.add(loginPanel, "Login");
        frame.add(selectionPanel, "Selection");
        frame.add(paymentPanel, "Payment");

        return frame;
    }

    private static JPanel createLoginPanel(JFrame frame) {
        JPanel loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField studentIdField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");

        // Set preferred sizes for the text fields and button
        Dimension fieldDimension = new Dimension(200, 24);
        studentIdField.setPreferredSize(fieldDimension);
        passwordField.setPreferredSize(fieldDimension);
        loginButton.setPreferredSize(new Dimension(100, 24));

        loginButton.addActionListener(e -> handleLogin(frame, studentIdField.getText(), new String(passwordField.getPassword())));

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

        return loginPanel;
    }

    private static void handleLogin(JFrame frame, String studentId, String password) {
        if (studentId.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Please enter your student ID and password.");
        } else if (isValidCredentials(studentId, password)) {
            
            showPanel(frame, "Selection");
        } else {
            JOptionPane.showMessageDialog(frame, "Invalid login credentials.");
        }
    }

    private static boolean isValidCredentials(String studentId, String password) {
        // Replace with actual student data validation logic
        return studentId.equals("12345") && password.equals("password");
    }

    private static JPanel createSelectionPanel(JFrame frame) {
        JPanel selectionPanel = new JPanel(new BorderLayout());
        JTextArea courseCatalogArea = new JTextArea();
        courseCatalogArea.setEditable(false);

        for (Course course : courseCatalog) {
            courseCatalogArea.append(course.toString() + "\n");
        }

        JTextField courseCodeField = new JTextField();
        JButton addButton = new JButton("Add Course");
        JButton doneButton = new JButton("Done");

        addButton.addActionListener(e -> handleAddCourse(courseCodeField.getText(), frame));
        doneButton.addActionListener(e -> showPanel(frame, "Payment"));

        JPanel inputPanel = new JPanel(new GridLayout(2, 2));
        inputPanel.add(new JLabel("Enter course code:"));
        inputPanel.add(courseCodeField);
        inputPanel.add(addButton);
        inputPanel.add(doneButton);

        selectionPanel.add(new JScrollPane(courseCatalogArea), BorderLayout.CENTER);
        selectionPanel.add(inputPanel, BorderLayout.SOUTH);

        return selectionPanel;
    }

    private static void handleAddCourse(String courseCode, JFrame frame) {
        Course selectedCourse = findCourse(courseCode.trim().toUpperCase());
        if (selectedCourse != null) {
            selectedCourses.add(selectedCourse);
        } else {
            JOptionPane.showMessageDialog(frame, "Invalid course code.");
        }
    }

    private static JPanel createPaymentPanel(JFrame frame) {
        JPanel paymentPanel = new JPanel(new BorderLayout());
        JTextArea selectedCoursesArea = new JTextArea();
        selectedCoursesArea.setEditable(false);

        for (Course course : selectedCourses) {
            selectedCoursesArea.append(course.toString() + "\n");
        }

        double totalFee = calculateTotalFee();
        selectedCoursesArea.append("\nTotal fee (including university fee): " + totalFee + " pesos\n");

        JRadioButton onlinePaymentButton = new JRadioButton("Online Payment");
        JRadioButton cashPaymentButton = new JRadioButton("Cash Payment");
        ButtonGroup paymentGroup = new ButtonGroup();
        paymentGroup.add(onlinePaymentButton);
        paymentGroup.add(cashPaymentButton);

        JButton payButton = new JButton("Pay");
        payButton.addActionListener(e -> handlePayment(frame, onlinePaymentButton.isSelected(), cashPaymentButton.isSelected()));

        JPanel paymentMethodPanel = new JPanel(new GridLayout(2, 1));
        paymentMethodPanel.add(onlinePaymentButton);
        paymentMethodPanel.add(cashPaymentButton);

        paymentPanel.add(new JScrollPane(selectedCoursesArea), BorderLayout.CENTER);
        paymentPanel.add(paymentMethodPanel, BorderLayout.NORTH);
        paymentPanel.add(payButton, BorderLayout.SOUTH);

        return paymentPanel;
    }

    private static void handlePayment(JFrame frame, boolean onlinePaymentSelected, boolean cashPaymentSelected) {
        if (onlinePaymentSelected) {
            JOptionPane.showMessageDialog(frame, "Redirecting to secure payment gateway...");
        } else if (cashPaymentSelected) {
            JOptionPane.showMessageDialog(frame, "Payment slip generated. Please pay at designated locations.");
        } else {
            JOptionPane.showMessageDialog(frame, "Please select a payment method.");
            return;
        }

        JOptionPane.showMessageDialog(frame, "Payment received. Enrollment confirmed!");
        JOptionPane.showMessageDialog(frame, "Updating enrollment information and payment details...");
        JOptionPane.showMessageDialog(frame, "Thank you for using the Student Enrollment System!");
        System.exit(0);
    }

    private static Course findCourse(String courseCode) {
        for (Course course : courseCatalog) {
            if (course.getCode().equalsIgnoreCase(courseCode)) {
                return course;
            }
        }
        return null;
    }

    private static double calculateTotalFee() {
        double total = UNIVERSITY_FEE;
        for (Course course : selectedCourses) {
            total += course.getFee();
        }
        return total;
    }

    private static void showPanel(JFrame frame, String panelName) {
        CardLayout cl = (CardLayout) frame.getContentPane().getLayout();
        cl.show(frame.getContentPane(), panelName);
    }
}

class Course {
    private final String code;
    private final String title;
    private final int credits;
    private final double fee;

    public Course(String code, String title, int credits, double fee) {
        this.code = code;
        this.title = title;
        this.credits = credits;
        this.fee = fee;
    }

    public String getCode() {
        return code;
    }

    public String getTitle() {
        return title;
    }

    public int getCredits() {
        return credits;
    }

    public double getFee() {
        return fee;
    }

    @Override
    public String toString() {
        return code + ": " + title + " (" + credits + " credits) - " + fee + " pesos";
    }
}
