package rgou.view.networking;

import rgou.model.profile.Profile;
import rgou.view.game.GamePanel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class LANreciever extends Thread {

    // Set Variables
    private final Socket socket;
    public NetworkPlayer owner;
    public LANHostThread interfaceOwner;
    public PeerNetworkInterface peerInterfaceOwner;
    private BufferedReader in;
    private HeartBeat heart;
    private Boolean forceStop = false;

    // Start receiver
    public LANreciever(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        System.out.println("Started reciever on port: "+socket.getPort());

        // Open Reader
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Start heartbeat
        this.heart = new HeartBeat(this, socket);
        this.heart.start();

        // Start process
        receiveMessages();
    }

    public void receiveMessages() {
        Boolean run = true;
        while (run) {
            try {
                // Time delay
                TimeUnit.SECONDS.sleep(1);

                // Get Message
                String line="";
                if (in.ready()) line = in.readLine();
                String[] parts = line.split("-:-");
                String header = parts[0];

                String content = "";
                if (parts.length > 1) {
                    content = parts[1];
                }
                else {
                    content = "";
                }

                //Do tasks based on received information here

                // Quit
                if (header.equals("quit")) {
                    run = false;
                    // Close connection
                    owner.otherQuit();
                    in.close();
                }

                // Ready up
                else if (header.equals("ready")) {
                    // Set other started
                    owner.otherStarted = true;
                }

                // Move
                else if (header.equals("move")) {
                    owner.playOpponentMove(content);
                }

                // Chat Message (DEPRECATED VERSION FOR TESTING)
                else if (header.equals("msg")) {
                    // Add Message
                    GamePanel panel = owner.getGamePanel();
                    panel.addMessageChatbox(content);
                }

                // Dice roll
                else if (header.equals("dice")) {
                    // Role dice
                    owner.playOpponentDiceRoll(content);
                }

                // Chat Message
                else if (header.equals("chat")) {
                    // Add message to chatbox
                    owner.addMessageToChatbox("other",content);
                }

                // Get name
                else if (header.equals("name")) {
                    // Set opponent name
                    if (content.isEmpty()) {
                        content = "Player 1";
                    }

                    peerInterfaceOwner.nameRecieved();
                    peerInterfaceOwner.setOpponentName(content);

                    // Send recieved message along with own name
                    LANsender sender = new LANsender(socket);
                    sender.messageHeader = "nameRecieved";
                    try {
                        sender.messageContent = Profile.getCurrentUser().getName();
                    }
                    catch (NullPointerException e) {
                        sender.messageContent = "Player 2";
                    }
                    sender.start();
                }

                // Alert name received
                else if (header.equals("nameRecieved")) {
                    // Set received name
                    if (content.isEmpty()) {
                        content = "Player 2";
                    }

                    interfaceOwner.nameRecieved();
                    interfaceOwner.setOpponentName(content);
                }

                // Get rules
                else if (header.equals("rules")) {
                    // Send received messages
                    LANsender sender = new LANsender(socket);
                    sender.messageHeader = "rulesReceived";
                    sender.messageContent = "null";
                    sender.start();

                    // Set rules
                    peerInterfaceOwner.rulesRecieved();
                    peerInterfaceOwner.setRules(content);
                    System.out.println(content);
                }

                // Alert rules received
                else if (header.equals("rulesReceived")) {
                    // Set received rules
                    interfaceOwner.rulesRecieved();
                }

                // Alert game started
                else if (header.equals("started")) {
                    // Set game started variable in interface
                    interfaceOwner.started();
                }

                // Heartbeat pulse
                else if (header.equals("heartbeat")) {
                    // Keep heart beat alive
                    heart.resetLife();
                }

                // Check force stop
                if (forceStop) {
                    run = false;
                }
            } catch (IOException | NullPointerException | ArrayIndexOutOfBoundsException | InterruptedException e) {
                // No messages to recieve
            }
        }

        // Attempt to close socket in case of error
        owner.closeSocket();
        heart.forceStop();
    }

    public void setInterfaceOwner(LANHostThread interfaceOwner) {
        this.interfaceOwner = interfaceOwner;
    }

    public void setPeerInterfaceOwner(PeerNetworkInterface peerInterfaceOwner) {
        this.peerInterfaceOwner = peerInterfaceOwner;
    }

    public void forceStop() {
        // Force stop and kill heart beat
        forceStop = true;
        heart.forceStop();
    }
}
