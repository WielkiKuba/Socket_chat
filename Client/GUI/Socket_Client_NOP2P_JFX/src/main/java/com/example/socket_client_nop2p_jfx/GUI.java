package com.example.socket_client_nop2p_jfx;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class GUI extends Application {
    SQL sql = new SQL();
    public static Server server = new Server();
    public static Files files = new Files();
    public static File file = files.main();
    String myIp = server.myIp();
    public static VBox root = new VBox();
    public static Listener listener = new Listener();
    public static Thread listenerThread = new Thread(listener);
    private void clearRoot() {
        root.getChildren().clear();
    }
    private Button createButton(String text,double width){
        Button button = new Button(text);
        button.setMinWidth(width);
        return button;
    }
    private TextField createTextField(String promptText, double maxWidth) {
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        textField.setMaxWidth(maxWidth);
        return textField;
    }
    @Override
    public void start(Stage primaryStage) {
        double globalWidth = 250.0;
        Stage stage = new Stage();
        stage.setTitle("Messengerex");
        root.setSpacing(10);
        root.setPadding(new javafx.geometry.Insets(30));
        root.setAlignment(Pos.CENTER);
//        textFields
        TextField textFieldPassword = createTextField("Enter a password",globalWidth);
        TextField textFieldLogin = createTextField("Enter a login",globalWidth);
        TextField textFieldNickName = createTextField("Enter a nickname for this session",globalWidth);
        TextField textFieldIp = createTextField("Enter an ip address of receiver",globalWidth);
        TextField textFieldServerIp = createTextField("Enter an ip or dns of server",globalWidth);
        TextField textFieldMessage = createTextField("Enter a message",globalWidth);
//        labels
        Label wrongSql = new Label("Wrong login or password");
        Label wrongBio = new Label("This nickname is already occupied or server is not responding");
        Label messageSent = new Label("Message sent!");
        Label noUsers = new Label("There's no online users");
        Label changed = new Label("Server has been changed!");
        Label restart = new Label("Please restart the application!");
//        buttons
        Button sendButton = createButton("Send",globalWidth);
        Button loginButton = createButton("Enter",globalWidth);
        Button messageButton = createButton("SendMessage",150.0);
        Button readButton = createButton("Read messages",150);
        Button changeServerButton = createButton("Change server",150.0);
        Button addServerButton = createButton("Add a new server",globalWidth);
        Button deleteServerButton = createButton("Delete a server",globalWidth);
        Button confirmButton3 = createButton("Confirm",globalWidth);
        Button confirmButton = createButton("Confirm",globalWidth);
        Button confirmButton2 = createButton("Confirm",globalWidth);
        Button back1 = createButton("Back",globalWidth);
        Button back2 = createButton("Back",globalWidth);
        Button back3 = createButton("Back",globalWidth);
//        lists
        ListView<String> listView = new ListView<>();
        ComboBox<String> comboBox = new ComboBox<>(FXCollections.observableArrayList());

        TextArea textArea = new TextArea();
        comboBox.setMinWidth(250.0);
        textArea.setMaxHeight(400.0);
        textArea.setMinHeight(400.0);
        textArea.setMaxWidth(450.0);
        textArea.setWrapText(true);
        textArea.setEditable(false);

        if(!(server.getList()==null)){
            listView.getItems().addAll(server.getList());
        }
        listView.setMaxWidth(250.0);
        listView.setMaxHeight(100.0);

        root.getChildren().addAll(textFieldLogin,textFieldPassword,textFieldNickName, loginButton);

        Scene scene = new Scene(root, 500, 450);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();

//        actions
        stage.setOnCloseRequest(event -> {
            if(listenerThread!=null&&listenerThread.isAlive()){
                listenerThread.interrupt();
            }
            System.exit(0);
        });
        confirmButton3.setOnAction(e ->{
            String ip = textFieldServerIp.getText();
            files.deleteFile(files.osPath()+Files.fs+"Servers",ip);
            root.getChildren().add(restart);
        });
        back1.setOnAction(e->{
            clearRoot();
            root.getChildren().addAll(messageButton,readButton,changeServerButton);
        });
        back2.setOnAction(e->{
            clearRoot();
            root.getChildren().addAll(comboBox,confirmButton,addServerButton,deleteServerButton,back1);
        });
        back3.setOnAction(e->{
            clearRoot();
        });
        loginButton.setOnAction(e -> {
            boolean successfulLogin = sql.Login(textFieldLogin.getText(),textFieldPassword.getText());
            String description = textFieldNickName.getText();
            if(successfulLogin){
                boolean bio = server.bio(description);
                if(bio){
                    root.getChildren().removeAll(textFieldLogin,textFieldNickName,textFieldPassword,loginButton,wrongBio,wrongSql);
                    root.getChildren().addAll(messageButton,readButton,changeServerButton);
                }
                else{
                    root.getChildren().add(0,wrongBio);
                }
            }else {
                root.getChildren().add(0,wrongSql);
            }
        });
        messageButton.setOnAction(e -> {
            root.getChildren().removeAll(changeServerButton,readButton,messageButton);
            if(listView.getItems().isEmpty()){
                root.getChildren().add(noUsers);
            }else {
                root.getChildren().add(listView);
            }
            root.getChildren().addAll(textFieldIp,textFieldMessage,sendButton,back1);
        });
        readButton.setOnAction(e -> {
            clearRoot();
            root.getChildren().addAll(textArea,back1);
            textArea.setText(history());
        });
        sendButton.setOnAction(e -> {
            String ip = textFieldIp.getText();
            String message = textFieldMessage.getText();
            String convertedMessage = "MESSAGE#"+ip+"#"+message+"#"+myIp;
            listener.pause();
            server.send(convertedMessage);
            listener.resume();
            root.getChildren().add(0,messageSent);
            Timeline messageSentVanish = new Timeline(
                    new KeyFrame(Duration.seconds(2), i -> {
                        root.getChildren().remove(messageSent);
                    })
            );
            messageSentVanish.play();
        });
        changeServerButton.setOnAction(e -> {
            root.getChildren().removeAll(messageButton,readButton,changeServerButton);
            comboBox.getItems().clear();
            comboBox.getItems().add("default");
            for(String serverIp:servers()){
                comboBox.getItems().add(serverIp);
            }
            root.getChildren().addAll(comboBox,confirmButton,addServerButton,deleteServerButton,back1);
        });
        confirmButton.setOnAction(h ->{
            String ip = comboBox.getValue();
            if((ip.toUpperCase()).toUpperCase().equals("DEFAULT")){
                server.changeServer("jakubdomain.ddns.net");
            }else {
                server.changeServer(ip);
            }
            root.getChildren().add(0,changed);
            Timeline changedVanish = new Timeline(
                    new KeyFrame(Duration.seconds(2), i -> {
                        root.getChildren().remove(changed);
                    })
            );
            changedVanish.play();
        });
        addServerButton.setOnAction(e ->{
            clearRoot();
            root.getChildren().addAll(textFieldServerIp,confirmButton2,back2);
        });
        confirmButton2.setOnAction(e ->{
            String ip = textFieldServerIp.getText();
            files.folderCreate(files.osPath()+Files.fs+"Servers");
            files.createFile(files.osPath()+Files.fs+"Servers",ip);
            root.getChildren().add(0,restart);
        });
        deleteServerButton.setOnAction(e->{
            clearRoot();
            root.getChildren().addAll(textFieldServerIp,confirmButton3,back2);
        });
    }
    public static String history(){
        String history = files.fileReader(file,false);
        return history;
    }
    public static void main(String[] args) {
        listenerThread.start();
        launch();
    }
    public static String chatMemory = "";
    public static ArrayList<String> servers(){
        ArrayList<String> servers = new ArrayList<>();
        File folderWithIps = new File(files.osPath()+Files.fs+"Servers");
        if(!folderWithIps.exists()){
            files.folderCreate(Files.osPath()+files.fs+"Servers");
        }
        File[] filesInFolder = folderWithIps.listFiles();
        for(File inFolder:filesInFolder){
            servers.add(inFolder.getName());
        }
        return servers;
    }
    public static class Listener implements Runnable{
        private final Object lock = new Object();
        private volatile boolean paused = false;
        public void pause() {
            paused = true;
        }
        public void resume() {
            synchronized (lock) {
                paused = false;
                lock.notify();
            }
        }
        @Override
        public void run(){
            while(!(listenerThread.isInterrupted())){
                try{
                    if(server.getInputStream().available()!=0){
                        String read = server.simpleRead();
                        if(read!=null){
                            String[] readSplited = read.split("#");
                            if(readSplited[0].equals("RMESSAGE")){
                                if(chatMemory.equals("")){
                                    chatMemory = "Message: "+readSplited[1]+" FROM "+readSplited[2];
                                    files.writeFile(file,chatMemory,false);
                                }
                                else {
                                    chatMemory = chatMemory + "\nMessage: "+readSplited[1]+" FROM "+readSplited[2];
                                    files.writeFile(file,"Message: "+readSplited[1]+" FROM "+readSplited[2],false);
                                }
                            }else {
                                server.waitThread(100);
                                server.send("PING");
                            }
                        }
                    }
                }catch (IOException e){
                    System.out.println("ERROR 101");
                }
            }
        }
    }
}