package rgou.view.networking;

import rgou.model.element.Board;
import rgou.model.game.Game;
import rgou.model.profile.Profile;
import rgou.view.Player;
import rgou.view.game.GamePanel;
import rgou.view.gui.GUI;
import rgou.view.gui.GUIStandards;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.Socket;

public class LANHost extends JPanel {

    private static final Color buttonColour = new Color(220, 219, 203);
    private static final Font buttonFont = new Font("Sylfaen", Font.PLAIN, 20);
    private static final Color buttonFontColour = new Color(28, 24, 1);
    private static final Color backgroundColour = new Color(243, 241, 219);

    private static final int buttonWidth = 300;
    private static final int buttonHeight = 100;

    public HostNetworkInterface hostInterface;

    // Buttons
    private JPanel buttonsPanel;
    private JButton cancelConnection;
    private JButton copyButton;

    // Labels
    private JLabel ipLabel;
    private JLabel titleLabel;

    // Public buttons that need access
    public JButton startButton;
    public JLabel label;
    private ActionListener al;

    // String
    public String opponentName;

    public LANHost() {
        this.setLayout((LayoutManager)null);
        this.buttonsPanel = new JPanel();
        this.buttonsPanel.setBorder(BorderFactory.createLineBorder(GUIStandards.buttonFontColour, 2));
        this.titleLabel = new JLabel("Host");
        this.titleLabel.setHorizontalAlignment(0);

        // Setup Connection Menu
        this.label = new JLabel("Waiting for a peer to join...");
        this.ipLabel = new JLabel("[Port designation failed]");

        this.copyButton= new JButton("Copy Address");
        this.copyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Copy IP to clipboard
                String hostName = hostInterface.getHostName();
                String hostIP = hostInterface.getLocalIP();
                String port = hostInterface.getPort();
                hostInterface.copyClipboard(hostIP + ":" + port);
            }
        });

        this.cancelConnection = new JButton("Cancel Connection");
        this.cancelConnection.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Reset buttons and close socket
                resetButtons();
                hostInterface.closeSocket();
            }
        });

        this.startButton = new JButton("Start");
        this.startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetButtons();

                // Start connections and create player
                NetworkPlayer player1 = hostInterface.startGame();

                // Check name
                if (opponentName.isEmpty()) {
                    opponentName = "Player Two";
                }
                else if (Profile.getCurrentUser() != null) {
                    if (opponentName.equals(Profile.getCurrentUser().getName())) {
                        opponentName = opponentName+" (2)";
                    }
                }

                Player player2 = new Player(opponentName, Game.PlayerType.HUMAN);

                // Destroy selected board
                GUI.deleteBoardConfig();

                // Start game
                Board defaultBoard = new Board();
                GUI.setBoard(defaultBoard);
                GUI.setNetworkGame(true);
                GUI.setIsHost(true);
                GUI.setGamePlayers(player1, player2);

                // Wait for other player to start
                al.actionPerformed(e);

                // Set chatbox config
                GamePanel panelOfGame = player1.getGamePanel();
                panelOfGame.setMsgPlayer(player1);
            }
        });
        this.startButton.setEnabled(false);

        // Add buttons to screen (same as other menus)
        Component[] componentList = new Component[]{this.titleLabel,this.label,this.ipLabel,this.copyButton,this.startButton,this.cancelConnection};
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

        ipLabel.setHorizontalAlignment(SwingConstants.CENTER);
        label.setHorizontalAlignment(SwingConstants.CENTER);

        this.titleLabel.setFont(new Font(GUIStandards.buttonFont.getFontName(), 1, GUIStandards.buttonFont.getSize()));
        this.buttonsPanel.setBackground(GUIStandards.backgroundColour);
        this.setBackground(GUIStandards.backgroundColour);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.buttonsPanel.setBounds((Math.round((float)screenSize.getWidth()) - 300) / 2, (Math.round((float)screenSize.getHeight()) - 100 * componentList.length) / 2, 300, 100 * componentList.length);
        this.add(this.buttonsPanel);
    }

    // Start Hosting Connection
    public void startHost() {
        this.hostInterface = new HostNetworkInterface();

        // Start Connection
        hostInterface.setupConnection(this);

        // Update GUI
        this.ipLabel.setText("Hosted on: "+hostInterface.getLocalIP()+":"+hostInterface.getPort());

        // Open Start Listener
        hostInterface.startConnectionHandler();
    }

    public void resetButtons() {
        // Reset buttons and labels
        this.label.setText("Waiting for a peer to join...");
        this.ipLabel.setText("[Port designation failed]");

        this.startButton.setBackground(GUIStandards.buttonColour);
        this.startButton.setEnabled(false);
    }

    public void addChangePanelListener(ActionListener al) {
        this.al = al;
        cancelConnection.addActionListener(al);
        cancelConnection.setActionCommand("network");
        startButton.setActionCommand("game");
    }

    public void startGUIThread(Socket socket) {
        // Open GUI updating thread
        LANHostThread hostThread = new LANHostThread(this, socket);
        hostInterface.setThreadOwner(hostThread);
        hostThread.start();
    }
}
