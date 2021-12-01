package rgou.view.networking;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class PeerNetworkInterface extends Thread {
    // Set Variables
    public Socket socket;
    private LANreciever reciever;
    public Boolean nameRecieved = false;
    public Boolean rulesRecieved = false;
    private LANPeer owner;
    public Boolean forceStop = false;

    public boolean joinServer(String ip,LANPeer owner) {
        // Default network config
        String hostName="localhost";
        int port=80;

        this.owner = owner;

        // Connection
        try {
            forceStop = false;

            // Override network config
            String[] details = ip.split(":");
            if (details.length == 2) {
                hostName = details[0];
                port = Integer.parseInt(details[1]);
            }
            else {
                port = Integer.parseInt(details[0]);
            }
            System.out.println("Attempting connection on: "+ip);
            Socket socket = new Socket(hostName, port);
            this.socket = socket;

            // Create receiver
            this.reciever = new LANreciever(socket);
            this.reciever.setPeerInterfaceOwner(this);
            this.reciever.start();

            System.out.println("Connection Successful!");

            // Start package recieved checker
            this.start();

            // Return
            return true;

        } catch (IOException | IndexOutOfBoundsException | NumberFormatException e) {
            e.printStackTrace();
            System.out.println("Connection Failed");
            return false;
        }
    }

    public NetworkPlayer startGame() {
        // Start game and create Player
        NetworkPlayer player = new NetworkPlayer(reciever, socket, owner.opponentName);

        // Send started message
        LANsender sender = new LANsender(socket);
        sender.messageHeader = "started";
        sender.messageContent = "null";
        sender.start();

        return player;
    }

    public void rulesRecieved() {
        this.rulesRecieved = true;
    }

    public void nameRecieved() {
        this.nameRecieved = true;
    }

    @Override
    public void run() {
        while (!nameRecieved && !rulesRecieved) {
            // Delay checks
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (forceStop) {
                break;
            }
        }

        // Allow game start
        owner.startButton.setBackground(Color.green);
        owner.startButton.setEnabled(true);
    }

    public void setOpponentName(String name) {
        owner.opponentName = name;
    }

    public void setRules(String rules) {
        owner.setRules(rules);
    }

    public void closeReciever() {
        reciever.forceStop();
        forceStop = true;
    }
}
