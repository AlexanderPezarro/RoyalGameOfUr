package rgou.view.networking;

import rgou.model.element.Board;
import rgou.model.game.Game;
import rgou.model.profile.Profile;
import rgou.model.rules.Ruleset;
import rgou.view.Player;
import rgou.view.game.GamePanel;
import rgou.view.gui.GUI;
import rgou.view.gui.GUIStandards;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.io.IOException;

public class LANPeer extends JPanel{

    // Set Variables
    private static final Color buttonColour = new Color(220, 219, 203);
    private static final Font buttonFont = new Font("Sylfaen", Font.PLAIN, 20);
    private static final Color buttonFontColour = new Color(28, 24, 1);
    private static final Color backgroundColour = new Color(243, 241, 219);

    private static final int buttonWidth = 300;
    private static final int buttonHeight = 100;

    // Buttons
    private JPanel buttonsPanel;
    public JButton joinButton;
    public JButton startButton;
    private JButton cancelConnection;
    private JButton pasteButton;

    // Labels
    private JLabel titleLabel;
    public JLabel notifLabel;
    public JLabel label;

    // Fields
    private JTextField ipField;
    private ActionListener al;

    // Data
    public String opponentName;
    private Ruleset ruleset;

    public LANPeer() {
        // Setup Server
        PeerNetworkInterface peerNetworkInterface = new PeerNetworkInterface();

        // Setup Connection Menu
        this.setLayout((LayoutManager)null);
        this.buttonsPanel = new JPanel();
        this.buttonsPanel.setBorder(BorderFactory.createLineBorder(GUIStandards.buttonFontColour, 2));

        this.titleLabel = new JLabel("Join Game");
        this.titleLabel.setHorizontalAlignment(0);
        this.label = new JLabel("Enter Server Host:");
        this.notifLabel = new JLabel("");

        this.ipField = new JTextField(20);
        this.ipField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = ipField.getText();
            }
        });

        this.pasteButton = new JButton("Paste Clipboard");
        this.pasteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Paste IP to clipboard
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                Transferable text = clipboard.getContents(this);

                try {
                    // Code from: https://stackoverflow.com/questions/6631933/how-would-i-make-a-paste-from-java-using-the-system-clipboard
                    ipField.setText((String) text.getTransferData(DataFlavor.stringFlavor));
                } catch (Exception o){
                    o.printStackTrace();
                }
            }
        });

        this.joinButton = new JButton("Connect");
        this.joinButton.putClientProperty("peerObject", this);
        this.joinButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Join Server using ip
                LANPeer owner = ((LANPeer) ((JButton) e.getSource()).getClientProperty("peerObject"));
                Boolean success = peerNetworkInterface.joinServer(ipField.getText(),owner);
                if (!success) {
                    notifLabel.setText("Server not found!");
                    notifLabel.setForeground(Color.red);
                }
                else {
                    notifLabel.setText("Connection Successful!");
                    notifLabel.setForeground(Color.green);
                    //startButton.setBackground(Color.green);
                    //startButton.setEnabled(true);
                    joinButton.setEnabled(false);
                    ipField.setEnabled(false);
                    pasteButton.setEnabled(false);
                }
            }
        });

        this.cancelConnection = new JButton("Cancel Connection");
        this.cancelConnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Reset buttons and close reciever
                resetButtons();
                peerNetworkInterface.closeReciever();
            }
        });

        this.startButton = new JButton("Start");
        this.startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetButtons();
                NetworkPlayer player2 = peerNetworkInterface.startGame();

                // Check name
                if (opponentName.isEmpty()) {
                    opponentName = "Player Two";
                }

                else if(Profile.getCurrentUser() != null) {
                    if (opponentName.equals(Profile.getCurrentUser().getName())) {
                        opponentName = opponentName+" (1)";
                    }
                }

                Player player1 = new Player(opponentName, Game.PlayerType.HUMAN);

                // Delete board config
                GUI.deleteBoardConfig();

                // Start game
                Board defaultBoard = new Board();
                GUI.setBoard(defaultBoard);
                GUI.setNetworkGame(true);
                GUI.setIsHost(false);
                GUI.setGamePlayers(player1, player2);

                al.actionPerformed(e);

                // Set chatbox config
                GamePanel panelOfGame = player2.getGamePanel();
                panelOfGame.setMsgPlayer(player2);
            }
        });

        // Add buttons to screen (same as other menus)
        this.startButton.setBackground(Color.gray);
        this.startButton.setEnabled(false);

        Component[] componentList = new Component[]{titleLabel, label,ipField,pasteButton,joinButton,startButton,cancelConnection,notifLabel};
        this.buttonsPanel.setLayout(new GridLayout(componentList.length, 1));
        Component[] var2 = componentList;
        int var3 = componentList.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Component b = var2[var4];
            if (JButton.class.isInstance(b)) {
                ((JButton)b).setAlignmentY(0.0F);
                b.setBackground(GUIStandards.buttonColour);
            }

            b.setFont(GUIStandards.buttonFont);
            b.setForeground(GUIStandards.buttonFontColour);
            this.buttonsPanel.add(b);
        }

        ipField.setHorizontalAlignment(SwingConstants.CENTER);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        notifLabel.setHorizontalAlignment(SwingConstants.CENTER);

        this.titleLabel.setFont(new Font(GUIStandards.buttonFont.getFontName(), 1, GUIStandards.buttonFont.getSize()));
        this.buttonsPanel.setBackground(GUIStandards.backgroundColour);
        this.setBackground(GUIStandards.backgroundColour);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.buttonsPanel.setBounds((Math.round((float)screenSize.getWidth()) - 300) / 2, (Math.round((float)screenSize.getHeight()) - 100 * componentList.length) / 2, 300, 100 * componentList.length);
        this.add(this.buttonsPanel);
    }

    public void addChangePanelListener(ActionListener al) {
        this.al = al;
        cancelConnection.addActionListener(al);
        cancelConnection.setActionCommand("network");
        startButton.setActionCommand("game");
    }

    public void resetButtons() {
        // Reset buttons and features to defaults
        this.startButton.setBackground(GUIStandards.buttonColour);
        this.startButton.setEnabled(false);
        this.pasteButton.setEnabled(true);
        this.joinButton.setEnabled(true);
        this.ipField.setEnabled(true);
        this.notifLabel.setText("");
    }

    public void setRules(String rules) {
        // Set ruleset json
        try {
            this.ruleset = new Ruleset(rules);
        } catch (IOException e) {
            this.ruleset = new Ruleset();
        }
        GUI.setRuleSet(this.ruleset);
        System.out.println("ruleset set");
    }
}

