package rgou.view.networking;

import rgou.model.profile.Profile;
import rgou.model.rules.Ruleset;
import rgou.view.panel.RulesEditorPanel;

import java.awt.*;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class LANHostThread extends Thread {
    // Set Variables
    public LANHost owner;
    public Socket socket;
    public Boolean nameRecieved = false;
    public Boolean rulesRecieved = false;
    public Boolean canStart = false;

    public LANHostThread(LANHost owner, Socket socket) {
        this.owner = owner;
        this.socket = socket;
    }

    @Override
    public void run() {
        // Check Playable for GUI
        Boolean flag = false;
        while (flag==false) {
            flag = owner.hostInterface.checkClientJoin();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        // Allow game start
        owner.label.setText("Client joined!");

        Boolean sent = false;
        while (!sent) {
            // Send username
            if (!nameRecieved) {
                LANsender sender = new LANsender(socket);
                sender.messageHeader = "name";
                try {
                    sender.messageContent = Profile.getCurrentUser().getName();
                }
                catch (NullPointerException e) {
                    sender.messageContent = "Player 1";
                }
                sender.start();
            }

            // Send ruleset
            else if (!rulesRecieved) {
                LANsender sender = new LANsender(socket);
                sender.messageHeader = "rules";
                String rules = new Ruleset().getCurrentPath();
                if (rules==null) {
                    rules = new Ruleset().toJsonString();
                }
                sender.messageContent = rules;
                sender.start();
            }

            // Check if both recieved
            if (nameRecieved && rulesRecieved) {
                sent = true;
            }
            else {
                // Delay Sending to prevent double issues
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        // Set playable
        while (!canStart) {
            // Delay Start check
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        owner.startButton.setBackground(Color.green);
        owner.startButton.setEnabled(true);
    }

    public void rulesRecieved() {
        this.rulesRecieved = true;
    }

    public void nameRecieved() {
        this.nameRecieved = true;
    }

    public void setOpponentName(String name) {
        owner.opponentName = name;
    }

    public void started() {
        this.canStart = true;
    }

}
