import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

public class LoginUI {
    private JFrame frame;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton, createAccountButton;

    public LoginUI() {
        frame = new JFrame("User Login");
        frame.setSize(300, 200);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(4, 1));

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");
        createAccountButton = new JButton("Create New Account");

        frame.add(new JLabel("Username:"));
        frame.add(usernameField);
        frame.add(new JLabel("Password:"));
        frame.add(passwordField);
        frame.add(loginButton);
        frame.add(createAccountButton);

        // ✅ Handle Login Button Click
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            if (validateLogin(username, password)) {
                JOptionPane.showMessageDialog(frame, "Login Successful! ✅");
                frame.dispose();  // Close Login UI
                new CreateUser(); // Open Chat
            } else {
                JOptionPane.showMessageDialog(frame, "Invalid Username or Password! ❌");
            }
        });

        // ✅ Handle Create New Account Button Click
        createAccountButton.addActionListener(e -> {
            String newUsername = usernameField.getText().trim();
            String newPassword = new String(passwordField.getPassword());
            if (createNewAccount(newUsername, newPassword)) {
                JOptionPane.showMessageDialog(frame, "Account Created Successfully! ✅");
            } else {
                JOptionPane.showMessageDialog(frame, "Username Already Exists! ❌");
            }
        });

        frame.setVisible(true);
    }

    // ✅ Validate Login from users.txt file
    private boolean validateLogin(String username, String password) {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts.length == 2 && parts[0].equals(username) && parts[1].equals(password)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ✅ Create New Account and Save in users.txt file
    private boolean createNewAccount(String username, String password) {
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "Username and Password cannot be empty!");
            return false;
        }
        if (isUserExists(username)) {
            return false; // User already exists
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("users.txt", true))) {
            writer.write(username + " " + password);
            writer.newLine();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ✅ Check if User Already Exists in users.txt
    private boolean isUserExists(String username) {
        try (BufferedReader reader = new BufferedReader(new FileReader("users.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts[0].equals(username)) {
                    return true; // User already exists
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(LoginUI::new);
    }
}
