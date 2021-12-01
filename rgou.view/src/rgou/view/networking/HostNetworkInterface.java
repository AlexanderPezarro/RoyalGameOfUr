package rgou.view.networking;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class HostNetworkInterface {

    // Set Variables
    public static int port = 0; // Port 0 = auto port selection
    public ServerSocket serverSocket;
    public Boolean clientJoin = false;
    private Socket socket;
    private LANreciever reciever;
    private LANHost owner;

    public void setupConnection(LANHost owner) {
        // Setup Server with port
        try {
            this.owner = owner;
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startConnectionHandler() {
        // Start connection handler
        ServerConnectionHandler handler = new ServerConnectionHandler(serverSocket, this);
        System.out.println(serverSocket.getInetAddress().getHostName());

        // Get client
        handler.start();
    }

    public String getPort() {
        return String.valueOf(serverSocket.getLocalPort());
    }

    public String getHostName() {
        return String.valueOf(serverSocket.getInetAddress().getHostName());
    }

    public String getInetAddress() {
        return String.valueOf(serverSocket.getLocalPort());
    }

    public void closeSocket() {
        // Cancel and close socket
        try {
            serverSocket.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public void copyClipboard(String string) {
        // Copy IP to clipboard
        StringSelection stringSelection = new StringSelection(string);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public void clientConnect(Socket socket) {
        // Start Game and set player
        System.out.println("Client Connected!");
        this.clientJoin = true;
        this.socket = socket;
        this.reciever = new LANreciever(socket);
        this.reciever.start();
        owner.startGUIThread(socket);
    }

    public NetworkPlayer startGame() {
        // Create Receiver and player object
        NetworkPlayer player = new NetworkPlayer(reciever,socket, owner.opponentName);
        return player;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setThreadOwner(LANHostThread threadOwner) {
        reciever.setInterfaceOwner(threadOwner);
    }

    public boolean checkClientJoin() {
        return clientJoin;
    }

    public String getLocalIP() {
        try {
            // Get host name
            InetAddress host = InetAddress.getLocalHost();
            // Get IP from host name
            String ipString = host.getHostAddress();
            return ipString;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            return "<UnknownIP>";
        }
    }
}
