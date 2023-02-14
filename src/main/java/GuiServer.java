
import java.util.HashMap;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class GuiServer extends Application {

    Button serverChoice, clientChoice, send, sendAll;
    HashMap<String, Scene> sceneMap;
    Server serverConnection;
    Client clientConnection;
    TextArea msg;
    Label noticeLabel;
    final String str = "You are sending to: ";
    String message = str;
    ListView<String> serverList, clientList, msgList;


    public static void main(String[] args) {
        // TODO Auto-generated method stub
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        serverChoice = new Button("Server");
        clientChoice = new Button("Client");
        serverList = new ListView<String>();
        clientList = new ListView<String>();
        msgList = new ListView<String>();

        primaryStage.setTitle("The Chat Room");
        this.serverChoice.setOnAction(e -> {
            primaryStage.setScene(sceneMap.get("server"));
            primaryStage.setTitle("This is the Server");
            serverConnection = new Server(data -> {
                Platform.runLater(() -> {
                    serverList.getItems().add(data.message);
                });

            });

        });

        this.clientChoice.setOnAction(e -> {
            primaryStage.setScene(sceneMap.get("client"));
            primaryStage.setTitle("This is a client");
            clientConnection = new Client(data -> {
                Platform.runLater(() -> {
                    msgList.getItems().add(data.message);    //  update message
                    clientList.getItems().clear();  //  update client list
                    for (Integer i: data.clientList) {
                        clientList.getItems().add("Client " + i);
                    }
                });
            });

            clientConnection.start();
        });
        sceneMap = new HashMap<String, Scene>();

        sceneMap.put("server", createServerGui());
        sceneMap.put("client", createClientGui());

        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

        primaryStage.setScene(createStartGui());
        primaryStage.show();

    }

    public Scene createStartGui() {
        AnchorPane pane = new AnchorPane();
        pane.getChildren().add(serverChoice);
        pane.getChildren().add(clientChoice);
        pane.setStyle("-fx-background-color: pink");

        serverChoice.setLayoutX(151);
        serverChoice.setLayoutY(176);
        serverChoice.setPrefSize(90, 47);
        serverChoice.setStyle("-fx-background-color: #b266ff");

        clientChoice.setLayoutX(348);
        clientChoice.setLayoutY(176);
        clientChoice.setPrefSize(90, 47);
        clientChoice.setStyle("-fx-background-color: #b266ff");

        return new Scene(pane, 600, 400);
    }

    public Scene createServerGui() {
        AnchorPane pane = new AnchorPane();
        pane.setPrefSize(494, 442);
        pane.setStyle("-fx-background-color: pink");
        pane.getChildren().add(serverList);

        serverList.setLayoutX(31);
        serverList.setLayoutY(38);
        serverList.setPrefSize(426, 359);

        return new Scene(pane, 494, 442);
    }

    public Scene createClientGui() {
        AnchorPane pane = new AnchorPane();
        pane.setPrefSize(746, 592);
        pane.setStyle("-fx-background-color: pink");

        msgList.setLayoutX(47);
        msgList.setLayoutY(76);
        msgList.setPrefSize(349, 394);

        clientList.setLayoutX(473);
        clientList.setLayoutY(76);
        clientList.setPrefSize(200, 200);

        Label chatRoom = new Label("Chat Room");
        chatRoom.setLayoutX(159);
        chatRoom.setLayoutY(31);
        chatRoom.setPrefSize(134, 40);
        chatRoom.setFont(Font.font(23));
        chatRoom.setTextFill(Color.web("#4487df"));

        Label clientConnected = new Label("Client Connected");
        clientConnected.setLayoutX(506);
        clientConnected.setLayoutY(34);
        clientConnected.setPrefSize(134, 34);
        clientConnected.setFont(Font.font(16));
        clientConnected.setTextFill(Color.web("#4487df"));

        msg = new TextArea();
        msg.setLayoutX(53);
        msg.setLayoutY(496);
        msg.setPrefSize(260, 63);
        msg.setWrapText(true);
        msg.setOnMouseClicked(event -> {msg.clear();});

        send = new Button("Send");
        send.setLayoutX(341);
        send.setLayoutY(496);
        send.setPrefSize(56, 63);
        setSendBtn();

        noticeLabel = new Label(str + "all");
        noticeLabel.setAlignment(Pos.TOP_LEFT);
        noticeLabel.setLayoutX(437);
        noticeLabel.setLayoutY(303);
        noticeLabel.setPrefSize(230, 200);
        noticeLabel.setWrapText(true);
        noticeLabel.setFont(Font.font(16));

        Label prompt = new Label("Click on the name(s) you want to send message to \nor click send all button to send message to everyone");
        prompt.setLayoutX(426);
        prompt.setLayoutY(11);

        sendAll = new Button("Send All");
        sendAll.setLayoutX(673);
        sendAll.setLayoutY(78);
        setSendAll();
        setClientList();

        pane.getChildren().add(msgList);
        pane.getChildren().add(clientList);
        pane.getChildren().add(chatRoom);
        pane.getChildren().add(clientConnected);
        pane.getChildren().add(msg);
        pane.getChildren().add(send);
        pane.getChildren().add(noticeLabel);
        pane.getChildren().add(prompt);
        pane.getChildren().add(sendAll);

        return new Scene(pane, 746, 592);
    }

    //  send out the text in message box
    public void setSendBtn() {
        send.setOnAction(e -> {
            clientConnection.clientData.message = "Client " + clientConnection.clientData.clientID + ": " + msg.getText();
            clientConnection.send();
        });
    }

    //  Onclick of a client from the client list, add that client to selected client list
    public void setClientList() {
        clientList.setOnMouseClicked(e -> {
            String client = clientList.getSelectionModel().getSelectedItem();
            int id = Integer.parseInt(client.substring(7));
            if (!clientConnection.clientData.selectedClient.contains(id)) {
                message = message + client + " ";
                noticeLabel.setText(message);
                clientConnection.clientData.sendAll = false;
                clientConnection.clientData.selectedClient.add(id);
            }
        });
    }


    //  Set the message to send to all users
    public void setSendAll() {
        sendAll.setOnAction(e->{
            clientConnection.clientData.sendAll = true;
            clientConnection.clientData.selectedClient.clear();
            message = str + "all";
            noticeLabel.setText(message);
            message = str;
        });
    }

}
