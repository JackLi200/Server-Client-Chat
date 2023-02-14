import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

import javafx.application.Platform;
import javafx.scene.control.ListView;
/*
 * Clicker: A: I really get it    B: No idea what you are talking about
 * C: kind of following
 */

public class Server {

    int count = 1;
    ArrayList<ClientThread> clients = new ArrayList<ClientThread>();
    TheServer server;
    private Consumer<info> callback;

    info data;


    Server(Consumer<info> call) {
        data = new info();
        callback = call;
        server = new TheServer();
        server.start();
    }


    public class TheServer extends Thread {

        public void run() {

            try (ServerSocket mysocket = new ServerSocket(5555);) {
                //synchronized (data) {
                    data.message = "Server is waiting for a client!";
                    callback.accept(data);
                //}


                while (true) {

                    ClientThread c = new ClientThread(mysocket.accept(), count);
                    data.message = "client has connected to server: " + "client #" + count;
                    callback.accept(data);
                    clients.add(c);
                    c.start();
                    count++;

                }
            }//end of try
            catch (Exception e) {
                data.message = "Server socket did not launch";
                callback.accept(data);
            }
        }//end of while
    }


    class ClientThread extends Thread {


        Socket connection;
        int count;
        ObjectInputStream in;
        ObjectOutputStream out;

        ClientThread(Socket s, int count) {
            this.connection = s;
            this.count = count;
        }


        public void run() {

            try {
                in = new ObjectInputStream(connection.getInputStream());
                out = new ObjectOutputStream(connection.getOutputStream());
                connection.setTcpNoDelay(true);
            } catch (Exception e) {
                System.out.println("Streams not open");
            }

            //  First time update data
            //synchronized (data) {
                data.selectedClient.clear();
                data.clientID = count;
                data.clientList.add(count);
                data.message = "new client on server: client #" + count;
                updateClients(data);
            //}

            while (true) {
                //synchronized (data) {
                    try {
                        data = (info) in.readObject();
                        callback.accept(data);
                        updateClients(data);
                    } catch (Exception e) {
                        data.message = "OOOOPPs...Something wrong with the socket from client: " + count + "....closing down!";
                        data.clientList.removeIf(item -> item == count);
                        callback.accept(data);
                        data.message = "Client #" + count + " has left the server!";
                        updateClients(data);
                        clients.remove(this);
                        break;
                    }
                //}
            }
        }//end of run

        public synchronized void updateClients(info message) {
            for (ClientThread client: clients) {
                if (data.sendAll || data.selectedClient.contains(client.count) || client.count == data.clientID) {
                    try {
                        client.out.reset();
                        client.out.writeObject(message);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }


    }//end of client thread
}


	
	

	
