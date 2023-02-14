import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;


public class Client extends Thread {


    Socket socketClient;

    ObjectOutputStream out;
    ObjectInputStream in;

    private Consumer<info> callback;
    info clientData;

    Client(Consumer<info> call) {
        clientData = new info();
        callback = call;
    }

    public void run() {

        try {
            socketClient = new Socket("127.0.0.1", 5555);
            out = new ObjectOutputStream(socketClient.getOutputStream());
            in = new ObjectInputStream(socketClient.getInputStream());
            socketClient.setTcpNoDelay(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //synchronized (clientData) {
            try {
                info message = (info) in.readObject();
                clientData = message;
                callback.accept(clientData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        //}


        while (true) {

            try {
                info message = (info) in.readObject();
                //synchronized (message) {
                    clientData.clientList = message.clientList;
                    clientData.message = message.message;
                    callback.accept(clientData);
                //}
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public synchronized void send() {
        try {
            out.reset();
            out.writeObject(clientData);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


}
