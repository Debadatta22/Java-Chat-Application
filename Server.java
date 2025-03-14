import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Server {
    private static final int PORT = 7001;
    private static Set<PrintWriter> clientWriters = Collections.synchronizedSet(new HashSet<>());
    private static Set<String> activeUsers = Collections.synchronizedSet(new HashSet<>());
    private static Map<Integer, String> messageMap = new HashMap<>();
    private static int messageIdCounter = 0;

    public static void main(String[] args) {
        System.out.println("Server is running...");
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                new ClientHandler(serverSocket.accept()).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ClientHandler extends Thread {
        private Socket socket;
        private PrintWriter out;
        private BufferedReader in;
        private String username;
        private boolean hasLeft = false;

        public ClientHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                synchronized (clientWriters) {
                    clientWriters.add(out);
                }

                username = in.readLine();
                if (username != null) {
                    synchronized (activeUsers) {
                        if (!activeUsers.contains(username)) {
                            activeUsers.add(username);
                            broadcast(username + " has joined the chat.");
                        }
                    }
                }

                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith("EDIT:")) {
                        handleEditMessage(message);
                    } else if (message.startsWith("DELETE:")) {
                        handleDeleteMessage(message);
                    } else {
                        if (message.equals(username + " has left the chat.")) {
                            hasLeft = true;
                        }
                        synchronized (messageMap) {
                            messageIdCounter++;
                            messageMap.put(messageIdCounter, message);
                            broadcast("MSG:" + messageIdCounter + ":" + message);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                handleExit();
            }
        }

        private void handleEditMessage(String message) {
            String[] parts = message.split(":", 3);
            int messageId = Integer.parseInt(parts[1]);
            String newMessage = parts[2];
            synchronized (messageMap) {
                if (messageMap.containsKey(messageId)) {
                    messageMap.put(messageId, newMessage + " (edited)");
                    broadcast("EDIT:" + messageId + ":" + newMessage);
                }
            }
        }

        private void handleDeleteMessage(String message) {
            int messageId = Integer.parseInt(message.split(":")[1]);
            synchronized (messageMap) {
                if (messageMap.containsKey(messageId)) {
                    String deletedMessage = messageMap.remove(messageId);
                    broadcast("DELETE:" + messageId);
                    saveDeletedMessageToHistory(deletedMessage);
                }
            }
        }

        private void handleExit() {
            if (!hasLeft && username != null) {
                hasLeft = true;
                synchronized (activeUsers) {
                    if (activeUsers.contains(username)) {
                        activeUsers.remove(username);
                        broadcast(username + " has left the chat.");
                    }
                }
                synchronized (clientWriters) {
                    clientWriters.remove(out);
                }
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        private void broadcast(String message) {
            synchronized (clientWriters) {
                for (PrintWriter writer : clientWriters) {
                    writer.println(message);
                }
            }
        }

        private void saveDeletedMessageToHistory(String message) {
            try (FileWriter fw = new FileWriter("chat_history.txt", true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter pw = new PrintWriter(bw)) {
                pw.println("[DELETED]: " + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}