import java.io.Serializable;
import java.util.ArrayList;

public class info implements Serializable {
    int clientID;
    ArrayList<Integer> clientList;  //  current users
    ArrayList<Integer> selectedClient;  // who is the client sending to
    boolean sendAll;
    String message;

    info() {
        clientID = 0;
        clientList = new ArrayList<>();
        selectedClient = new ArrayList<>();
        sendAll = true;
        message = "";
    }
}
