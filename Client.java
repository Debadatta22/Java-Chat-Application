import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Client {
    private JFrame frame;
    private JTextArea chatArea;
    private JTextField messageField;
    private PrintWriter out;
    private String username;
    private Socket socket;
    private final String CHAT_HISTORY_FILE = "chat_history.txt";

    public Client(String serverAddress, int port, String username, Color backgroundColor, Color textColor) {
        this.username = username;
        try {
            socket = new Socket(serverAddress, port);
            out = new PrintWriter(socket.getOutputStream(), true);

            frame = new JFrame("Chat - " + username);
            frame.setSize(600, 500);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            // Send username first
            out.println(username);

            // Send user left message when closing the window
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    out.println(username + " has left the chat.");
                }
            });

            // Create Top Bar with Buttons
            JPanel topBar = new JPanel(new FlowLayout());
            JButton clearChatButton = new JButton("Clear Chat");
            JButton showHistoryButton = new JButton("Show History");
            JButton clearHistoryButton = new JButton("Clear History");
            JButton searchButton = new JButton("Search");

            // Clear Chat (Only clears the chat screen, doesn't delete history)
            clearChatButton.addActionListener(e -> chatArea.setText(""));

            // Show History (Opens a new window to display saved chat history)
            showHistoryButton.addActionListener(e -> showChatHistory());

            // ✅ Clear History and Notify Other Users
            clearHistoryButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(frame, "Are you sure you want to clear chat history?",
                        "Confirm Clear History", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    clearChatHistory();

                    // ✅ Send message to other users that this user cleared the chat
                    out.println("⚡ " + username + " cleared the chat history.");
                }
            });

            // ✅ Search Message Functionality
            searchButton.addActionListener(e -> searchMessage());

            topBar.add(clearChatButton);
            topBar.add(showHistoryButton);
            topBar.add(clearHistoryButton);
            topBar.add(searchButton);
            frame.add(topBar, BorderLayout.NORTH);

            chatArea = new JTextArea();
            chatArea.setEditable(false);
            chatArea.setBackground(backgroundColor);
            chatArea.setForeground(textColor);
            chatArea.setFont(new Font("Arial", Font.PLAIN, 14));
            JScrollPane scrollPane = new JScrollPane(chatArea);
            frame.add(scrollPane, BorderLayout.CENTER);

            JPanel bottomPanel = new JPanel(new BorderLayout());
            messageField = new JTextField();
            JButton sendButton = new JButton("Send");

            sendButton.addActionListener(e -> sendMessage());

            bottomPanel.add(messageField, BorderLayout.CENTER);
            bottomPanel.add(sendButton, BorderLayout.EAST);
            frame.add(bottomPanel, BorderLayout.SOUTH);

            frame.setVisible(true);

            new Thread(() -> {
                try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                    String message;
                    while ((message = in.readLine()) != null) {
                        chatArea.append(message + "\n");
                        saveMessageToHistory(message);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage() {
        String text = messageField.getText().trim();
        if (!text.isEmpty()) {
            String timestamp = new SimpleDateFormat("HH:mm:ss").format(new Date());
            String formattedMessage = username + " [" + timestamp + "]: " + text;
            out.println(formattedMessage);
            messageField.setText("");
        }
    }

    // ✅ Search Message Functionality
    private void searchMessage() {
        String searchText = JOptionPane.showInputDialog(frame, "Enter message to search:");
        if (searchText != null && !searchText.isEmpty()) {
            String chatContent = chatArea.getText();
            int index = chatContent.indexOf(searchText);
            if (index != -1) {
                chatArea.setCaretPosition(index); // Scroll to the found message
                chatArea.requestFocus();
                chatArea.select(index, index + searchText.length()); // Highlight the text
            } else {
                JOptionPane.showMessageDialog(frame, "Message not found.");
            }
        }
    }

    // Save message to chat history file
    private void saveMessageToHistory(String message) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CHAT_HISTORY_FILE, true))) {
            writer.write(message + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Show chat history in a new window
    private void showChatHistory() {
        JFrame historyFrame = new JFrame("Chat History");
        historyFrame.setSize(500, 400);
        JTextArea historyArea = new JTextArea();
        historyArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(historyArea);
        historyFrame.add(scrollPane);

        try (BufferedReader reader = new BufferedReader(new FileReader(CHAT_HISTORY_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                historyArea.append(line + "\n");
            }
        } catch (IOException e) {
            historyArea.setText("No chat history found.");
        }

        historyFrame.setVisible(true);
    }

    // ✅ Clear Chat History File
    private void clearChatHistory() {
        try (PrintWriter writer = new PrintWriter(CHAT_HISTORY_FILE)) {
            writer.print("");
        } catch (IOException e) {
            e.printStackTrace();
        }
        JOptionPane.showMessageDialog(frame, "Chat history has been cleared.");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Client("127.0.0.1", 7001, "User", Color.BLACK, Color.WHITE));
    }
}
