package ma.project;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * I USED  JavaFX UI FOR SERVER VIEW
 */
public class SView {
    private Stage st;
    private SModel m;
    
    private Label statusL;
    private Circle statusC;
    private ListView<String> UserList;
    private TextArea logArea;
    private Button startBtn;
    private Button stopBtn;
    
    private ObservableList<String> Users;
    private Map<String, String> UColors;
    
    public SView(Stage s) {
        this.st = s;
        this.Users = FXCollections.observableArrayList();
        this.UColors = new HashMap<>();
        setupUI();
    }
    
    public void setModel(SModel mod) {
        this.m = mod;
    }
    
    private void setupUI() {
        st.setTitle("TCP GroupChat Server");
        
        GridPane g = new GridPane();
        g.setPadding(new Insets(20));
        g.setHgap(10);
        g.setVgap(10);
        // Light pink background with galaxy theme
        g.setStyle("-fx-background-color: #FFE5F1; -fx-background-radius: 10;");
        
        // Status
        Label statusTitle = new Label("Server Status:");
        statusTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #6B2C91; -fx-font-size: 14px;");
        statusL = new Label("Offline");
        statusL.setStyle("-fx-text-fill: #4A148C; -fx-font-size: 12px;");
        statusC = new Circle(8);
        statusC.setFill(Color.web("#E91E63"));
        
        HBox statusBox = new HBox(10);
        statusBox.getChildren().addAll(statusC, statusL);
        
        g.add(statusTitle, 0, 0);
        g.add(statusBox, 1, 0);
        
        // Buttons with galaxy theme
        startBtn = new Button("Start Server");
        startBtn.setStyle("-fx-background-color: linear-gradient(to bottom, #9C27B0, #673AB7); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16;");
        startBtn.setOnAction(e -> {
            if (m != null) {
                m.StartServer();
                startBtn.setDisable(true);
                stopBtn.setDisable(false);
                statusC.setFill(Color.web("#00E5FF"));
                statusL.setText("Online");
            }
        });
        
        stopBtn = new Button("Stop Server");
        stopBtn.setStyle("-fx-background-color: linear-gradient(to bottom, #E91E63, #AD1457); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8; -fx-padding: 8 16;");
        stopBtn.setDisable(true);
        stopBtn.setOnAction(e -> {
            if (m != null) {
                m.StopServer();
                startBtn.setDisable(false);
                stopBtn.setDisable(true);
                statusC.setFill(Color.web("#E91E63"));
                statusL.setText("Offline");
            }
        });
        
        HBox btnBox = new HBox(10);
        btnBox.getChildren().addAll(startBtn, stopBtn);
        g.add(btnBox, 0, 1, 2, 1);
        

        Label UTitle = new Label("Connected Users:");
        UTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #6B2C91; -fx-font-size: 14px;");
        
        UserList = new ListView<>(Users);
        UserList.setPrefHeight(200);
        UserList.setPrefWidth(300);
        UserList.setStyle("-fx-background-color: #FFF0F8; -fx-border-color: #E1BEE7; -fx-border-radius: 5;");
        

        UserList.setCellFactory(list -> new ListCell<String>() {
            @Override
            protected void updateItem(String username, boolean empty) {
                super.updateItem(username, empty);
                if (empty || username == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(username);
                    String color = UColors.get(username);
                    if (color != null) {
                        setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-padding: 5;");
                    } else {
                        setStyle("-fx-background-color: #E1BEE7; -fx-text-fill: #4A148C;");
                    }
                }
            }
        });
        
        VBox usersBox = new VBox(5);
        usersBox.getChildren().addAll(UTitle, UserList);
        g.add(usersBox, 0, 2);
        

        Label logTitle = new Label("Server Log:");
        logTitle.setStyle("-fx-font-weight: bold; -fx-text-fill: #6B2C91; -fx-font-size: 14px;");
        
        logArea = new TextArea();
        logArea.setPrefHeight(200);
        logArea.setPrefWidth(400);
        logArea.setEditable(false);
        logArea.setWrapText(true);
        logArea.setStyle("-fx-background-color: #FFF0F8; -fx-text-fill: #4A148C; -fx-border-color: #E1BEE7; -fx-border-radius: 5; -fx-font-family: 'Courier New';");
        
        VBox logBox = new VBox(5);
        logBox.getChildren().addAll(logTitle, logArea);
        g.add(logBox, 1, 2);
        
        Scene scene = new Scene(g, 750, 350);
        scene.setFill(Color.web("#FFE5F1")); // Light pink background

        st.setScene(scene);
        st.setResizable(false);
    }
    
    public void addUser(String Uname, String col) {
        Platform.runLater(() -> {
            if (!Users.contains(Uname)) {
                Users.add(Uname);
                UColors.put(Uname, col);
            }
        });
    }
    
    public void removeUser(String Uname) {
        Platform.runLater(() -> {
            Users.remove(Uname);
            UColors.remove(Uname);
        });
    }
    
    public void updateUserList(List<String> Unames) {
        Platform.runLater(() -> {
            Users.clear();
            Users.addAll(Unames);
        });
    }
    
    public void updateLog(String msg) {
        Platform.runLater(() -> {
            String t = java.time.LocalTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
            logArea.appendText("[" + t + "] " + msg + "\n");
            logArea.setScrollTop(Double.MAX_VALUE);
        });
    }
    
    public void show() {
        st.show();
    }
}
