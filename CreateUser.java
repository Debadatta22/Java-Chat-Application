//import javax.swing.*;
//import java.awt.*;
//
//public class CreateUser {
//    public static void main(String[] args) {
//        JFrame frame = new JFrame("Create User");
//        frame.setSize(300, 250);
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setLayout(new GridLayout(4, 1));
//
//        JTextField usernameField = new JTextField();
//        JButton bgColorButton = new JButton("Select Background Color");
//        JButton textColorButton = new JButton("Select Text Color");
//        JButton connectButton = new JButton("Connect");
//
//        final Color[] selectedBgColor = {Color.BLACK};
//        final Color[] selectedTextColor = {Color.WHITE};
//
//        bgColorButton.addActionListener(e -> {
//            Color color = JColorChooser.showDialog(frame, "Choose Background Color", Color.BLACK);
//            if (color != null) selectedBgColor[0] = color;
//        });
//
//        textColorButton.addActionListener(e -> {
//            Color color = JColorChooser.showDialog(frame, "Choose Text Color", Color.WHITE);
//            if (color != null) selectedTextColor[0] = color;
//        });
//
//        connectButton.addActionListener(e -> {
//            String username = usernameField.getText().trim();
//            if (!username.isEmpty()) {
//                frame.dispose();
//                new Client("127.0.0.1", 7001, username, selectedBgColor[0], selectedTextColor[0]);
//            } else {
//                JOptionPane.showMessageDialog(frame, "Please enter a username.");
//            }
//        });
//
//        frame.add(usernameField);
//        frame.add(bgColorButton);
//        frame.add(textColorButton);
//        frame.add(connectButton);
//        frame.setVisible(true);
//    }
//}
import javax.swing.*;
import java.awt.*;

public class CreateUser {
    private String username;

    public CreateUser(String username) {
        this.username = username;

        JFrame frame = new JFrame("Create User");
        frame.setSize(300, 250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(3, 1));

        JButton bgColorButton = new JButton("Select Background Color");
        JButton textColorButton = new JButton("Select Text Color");
        JButton connectButton = new JButton("Connect");

        final Color[] selectedBgColor = {Color.BLACK};
        final Color[] selectedTextColor = {Color.WHITE};

        bgColorButton.addActionListener(e -> {
            Color color = JColorChooser.showDialog(frame, "Choose Background Color", Color.BLACK);
            if (color != null) selectedBgColor[0] = color;
        });

        textColorButton.addActionListener(e -> {
            Color color = JColorChooser.showDialog(frame, "Choose Text Color", Color.WHITE);
            if (color != null) selectedTextColor[0] = color;
        });

        connectButton.addActionListener(e -> {
            frame.dispose();
            new Client("127.0.0.1", 7001, username, selectedBgColor[0], selectedTextColor[0]);
        });

        frame.add(bgColorButton);
        frame.add(textColorButton);
        frame.add(connectButton);
        frame.setVisible(true);
    }
}

