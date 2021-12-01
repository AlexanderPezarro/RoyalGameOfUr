package rgou.view.networking;

import rgou.model.game.Game;
import rgou.view.Player;
import rgou.view.gui.GUI;

import javax.swing.*;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class NetworkPlayer extends Player {
    // Network Specifics
    private Socket socket;
    LANreciever receiver;
    ChatHub chatBox;
    private String opponentName;

    public boolean otherStarted = false;

    public NetworkPlayer(LANreciever receiver, Socket socket, String opponentName) {
        super();
        //this.chatBox = new ChatHub(this);
        this.receiver = receiver;
        this.socket = socket;
        this.opponentName = opponentName;
        receiver.owner = this;
        //receiver.start();
    }

    public void sendMessage(String header, String message) {
        // Create send object and set confid
        LANsender sender = new LANsender(socket);
        sender.messageHeader = header;
        sender.messageContent = message;
        sender.start();
    }

    public void setOtherStarted() {
        otherStarted = true;
    }

    @Override
    public void sendDiceRollToOpponent(ArrayList<Integer> numbers) {
        // Send dice roll to opponent
        String message = numbers.stream().map(i -> i.toString()).collect(Collectors.joining(""));
        sendMessage("dice", message);
    }

    @Override
    public void sendMoveToOpponent(int[][] move) {
        // Send moves to opponent
        String coords = "" + move[0][0] + move[0][1] + move[1][0] + move[1][1];
        sendMessage("move", coords);
    }

    public void addMessageToChatbox(String sender, String message) {
        try {
            chatBox.addMessage(opponentName, message);
        }
        catch (NullPointerException e) {
            // Socket closed
        }
    }

    public void otherQuit() {
        // Show quit pop-up and close socket
        JOptionPane.showMessageDialog(new JFrame(), opponentName+" has quit the game!");
        addMessageToChatbox(opponentName,"has quit!");
        closeSocket();
    }

    @Override
    public void quitGame() {
        // Close socket and send quit to other player
        if (!socket.isClosed()) {
            sendMessage("quit", "null");
            receiver.forceStop();
            closeSocket();
        }
    }

    public void closeSocket() {
        try {
            TimeUnit.SECONDS.sleep(3);
            socket.close();
        } catch (IOException | InterruptedException e) {
            // Socket already closed!
        }
    }
}