package ma.project;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * TCPServer is the  Main class for the server
 * java TCPServer
 */
public class TCPServer extends Application {
    
    private SModel m;
    private SView v;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage priStage) {
        try {

            m = new SModel();
            v = new SView(priStage);
            
            // connection of the model and view of the server
            v.setModel(m);
            m.SetView(v);
            
            // loading the configuration file
            m.loadConfig("server.properties");
            

            v.show();
            
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    public void stop() {
        if (m != null) {
            m.StopServer();
        }
    }
}
