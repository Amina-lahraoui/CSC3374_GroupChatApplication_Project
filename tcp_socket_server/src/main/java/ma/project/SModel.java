package ma.project;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * SModel to control the communication between sockets and the logic of the server.
 */
//The application must strictly adhere to the principle of 'Separation of Concerns'
// This class is to separate business logic from UI layer;
// Change in the view layer won't affect  the model layer.
public class SModel {
    private ServerSocket sSocket;
    private List<ClientHandler> Clients;
    private boolean running = false;
    private String serverIPAddr;
    private int server_Port;
    private SView View;
    
    public SModel() { Clients = Collections.synchronizedList(new ArrayList<>());
        //to ensure safety used synchronized list.
        }
    //to connect the model to View to update UI when an event happens like client connection.
    public void SetView(SView v) { this.View = v;}
    

    public void loadConfig(String FILEname) throws IOException {
        Properties p = new Properties();
        InputStream input = getClass().getClassLoader().getResourceAsStream(FILEname);
        if (input == null) {
            throw new FileNotFoundException(" SORRY, Configuration file is not found: " + FILEname);
        }
        p.load(input);
        serverIPAddr = p.getProperty("server.ip", "0.0.0.0");
        server_Port = Integer.parseInt(p.getProperty("server.port", "3000"));
    }
    
    // the starting of the server

    public void StartServer() {
        if (running) {
            return;
        }
        
        running = true;
        
        // FOR THE UI TO NOT FREEZE I will start the server in new thread
        new Thread(() -> {
            try {
                sSocket = new ServerSocket(server_Port);
                updateLog(" the Server Started on " + serverIPAddr + ":" + server_Port);
                updateLog("NONE,Waiting for Client to connect...");
                
                // connections acceptance using the accept method
                while (running) {
                    Socket clSocket = sSocket.accept();
                    ClientHandler h = new ClientHandler(clSocket, this);
                    Clients.add(h);
                    new Thread(h).start();
                    // this shows  every new client connection will create a ClientHandler and start it in a new thread.
                }
            } catch (IOException e) {
                if (running) {
                    updateLog("Server ERROR: " + e.getMessage());
                }
            }
        }).start();
    }
    
    // server STOPPING
    public void StopServer() {
        running = false;
        
        // DISCONNECT  all clients
        for (ClientHandler c : Clients) {
            c.disconnect();
        }
        Clients.clear();
        
        // server socket CLOSED
        try {
            if (sSocket != null) {
                sSocket.close();
            }
        } catch (IOException e) {
            updateLog("ERROR closing server: " + e.getMessage());
        }
        
        updateLog("Server Stopped");
    }
    
    // Broadcasting method so that one message can be received by many clients
    public void broadcastMessage(String msg, String Uname) {
        // Format with time and username
        String time = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String formatted = "[" + time + "] " + Uname + ": " + msg;
        
        updateLog("Broadcasting: " + formatted);
        
        // message sent to all clients
        for (ClientHandler c : Clients) {
            c.sendMessage(formatted);
        }
    }
    
    // the process of removing a client from list
    public void RemoveClient(ClientHandler c) {
        Clients.remove(c);
        updateUList();
    }
    
    // NEW CLIENT CONNECTION
    public void clientConnected(ClientHandler c, String Uname) {

        Random rnd = new Random();
        int r = 100 + rnd.nextInt(156);
        var g = 100 + rnd.nextInt(156);
        int b = 100 + rnd.nextInt(156);
        String col = String.format("#%02X%02X%02X", r, g, b);
        c.setColor(col);
        
        // welcome message
        c.sendMessage("Hello, Welcome " + Uname + "! You are connected.");
        
        // NOTIFICATION SENT TO OTHER USERS
        String t = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String joinMsg = "[" + t + "] " + Uname + "  joined the chat.";
        for (ClientHandler cl : Clients) {
            if (c != cl) {
                c.sendMessage(joinMsg);
            }
        }
        
        updateLog("Welcome " + Uname);
        View.addUser(Uname, col);
        updateUList();
    }
    
    // disconnection
    public void clientDisconnected(String Uname) {
        if (Uname != null && !Uname.isEmpty()) {
            String t = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
            String leaveMsg = "[" + t + "] " + Uname + "  left the chat.";
            broadcastMessage(leaveMsg, "System");
            View.removeUser(Uname);
            updateUList();
            updateLog(Uname + " is disconnected");
        }
    }
    
    // acquire the list of all users
    public List<String> getAllUnames() {
        List<String> Unames = new ArrayList<>();
        for (ClientHandler c : Clients) {
            if (c.getUname() != null && !c.getUname().isEmpty()) {
                Unames.add(c.getUname());
            }
        }
        return Unames;
    }
    
    // UPDATING user list in view
    private void updateUList() {
        if (View != null) {
            View.updateUserList(getAllUnames());
        }
    }
    
    // UPDATING  log in view
    private void updateLog(String msg) {
        if (View != null) {
            View.updateLog(msg);
        }
    }
    
    public boolean isRunning() {
        return running;
    }
}
