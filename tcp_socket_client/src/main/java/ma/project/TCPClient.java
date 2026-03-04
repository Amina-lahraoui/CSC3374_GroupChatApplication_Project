package ma.project;

import javafx.application.Application;
import javafx.stage.Stage;

/// TCPClient is the  Main class for the client
/// use of Java TCPClient via <ServerIPAddress> <PortNumber>
/// in this project the localhost is 3000
public class TCPClient extends Application {
    
    private CModel Model;
    private CView View;
    private static String serverIP;
    private static int serverPort;
    //(Section 3.3) specify loading network settings from a configuration file at runtime.
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Java TCPClient <ServerIPAddress> <PortNumber>");
            System.out.println("Java TCPClient localhost 3000 as an example");
            System.exit(1);
        }
        
        try {
            serverIP = args[0];
            serverPort = Integer.parseInt(args[1]);
            
            if (serverPort < 1 || serverPort > 65535) {
                System.out.println("sorry the valid port numbers are between 1 and 65535");
                System.exit(1);
            }
            
            launch(args);
        } catch (NumberFormatException e) {
            System.out.println("err: port number entered is invalid");
            System.exit(1);
        }
    }
    
    @Override
    public void start(Stage PriStage) {
        try {
            Model = new CModel();
            View = new CView(PriStage);
            
            View.setM(Model);
            Model.setView(View);
            View.setServerInfo(serverIP, serverPort);
            
            View.show();
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void stop() {
        if (Model != null) {
            Model.disconnect();
        }
    }
}
