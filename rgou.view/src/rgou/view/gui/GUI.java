package rgou.view.gui;

import rgou.model.element.PathBoard;
import rgou.model.game.Game;
import rgou.model.profile.Profile;
import rgou.view.Player;
import rgou.model.element.Board;
import rgou.view.game.GamePanel;
import rgou.view.networking.LANHost;
import rgou.view.networking.LANPeer;
import rgou.view.panel.HomePanel;
import rgou.view.networking.NetworkPanel;
import rgou.view.profile.ProfilePanel;
import rgou.view.panel.SettingsPanel;
import rgou.model.rules.Ruleset;
import rgou.view.panel.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class GUI extends JFrame {

    private static final long serialVersionUID = 1L;

    private Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final JPanel container;

    private final HomePanel home;
    private static GamePanel game;
    private final SettingsPanel settings;
    private final NetworkPanel network;
    private final ProfilePanel profile;
    private final SettingsEditorPanel settingsEditorPanel;
    private final BoardEditorPanel boardEditorPanel;
    private final PathEditorPanel pathEditorPanel;
    private final RulesEditorPanel rulesEditorPanel;
    private static LANHost lanHostPanel;
    private static LANPeer lanPeerPanel;
    private static ProfilePanel profilePanel;

    private final ActionListener cpl;

    private CardLayout cardLayout;

    private static boolean isNetworkGame;
    private static boolean isHost;
    private static Board board = new Board();
    private static Ruleset ruleSet = new Ruleset();
    private static Player player1 = new Player("P1", Game.PlayerType.HUMAN);
    private static Player player2 = new Player("P2", Game.PlayerType.EASY_AI);

    public GUI() {
        //this.setUndecorated(true); //fullscreen

        setExtendedState(MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        container = new JPanel();

        cardLayout = new CardLayout();
        container.setLayout(cardLayout);

        try { board.setBothPaths("initial_paths.json"); }
        catch (IOException e) { e.printStackTrace(); }

        player1.setOpponent(player2);
        player2.setOpponent(player1);

        isNetworkGame = false;
        isHost = true;

        home = new HomePanel();
        settings = new SettingsPanel();
        network = new NetworkPanel();
        profile = new ProfilePanel();
        boardEditorPanel = new BoardEditorPanel();
        pathEditorPanel = new PathEditorPanel();
        rulesEditorPanel = new RulesEditorPanel();
        lanHostPanel = new LANHost();
        lanPeerPanel = new LANPeer();
        profilePanel = new ProfilePanel();
        settingsEditorPanel = new SettingsEditorPanel();

        cpl = makeChangePanelListener();

        setUpActionListeners();

        container.add(home, "home");
        container.add(settings, "settings");
        container.add(network, "network");
        container.add(profile, "profile");
        container.add(boardEditorPanel, "boardEditor");
        container.add(pathEditorPanel, "pathEditor");
        container.add(rulesEditorPanel, "rulesEditor");
        container.add(lanHostPanel, "host");
        container.add(lanPeerPanel, "peer");
        container.add(profilePanel, "profile");
        container.add(settingsEditorPanel, "settingsEditor");

        add(container);


    }

    private void setUpActionListeners() {

        home.addChangePanelListener(cpl);
        network.addChangePanelListener(cpl);
        settings.addChangePanelListener(cpl);
        boardEditorPanel.addChangePanelListener(cpl);
        pathEditorPanel.addChangePanelListener(cpl);
        rulesEditorPanel.addChangePanelListener(cpl);
        lanHostPanel.addChangePanelListener(cpl);
        lanPeerPanel.addChangePanelListener(cpl);
        profilePanel.addChangePanelListener(cpl);
        settingsEditorPanel.addChangePanelListener(cpl);
    }

    private ActionListener makeChangePanelListener() {

        return e -> {
            String panelName = e.getActionCommand();
            System.out.println("Going to: " + panelName);

            if(panelName.equals("home")) {
                if(isHost) {
                    player1.quitGame();
                }

                else {
                    player2.quitGame();
                }

                setNetworkGame(false);
                player1.setType(Game.PlayerType.HUMAN);
                player2.setType(Game.PlayerType.HUMAN);
            }
            if(panelName.equals("game")) {
                try {
                    board = new Board(new Board().getCurrentPath());
                }catch (Exception ee){
                    board = new Board();
                }

                try {
                    board.setBothPaths(new PathBoard().getCurrentPath());
                }
                catch (Exception ee){
                    try { board.setBothPaths("initial_paths.json"); }
                    catch (IOException eee) { eee.printStackTrace(); }
                }

                try{
                    ruleSet = new Ruleset(new Ruleset().getCurrentPath());
                    System.out.println("New rules");
                } catch (Exception ee) {
                    ruleSet = new Ruleset();
                }

                if(!isNetworkGame && Profile.getCurrentUser() != null){
                    player1.setName(Profile.getCurrentUser().getName());
                }

                game = new GamePanel(screenSize, isNetworkGame, isHost, board, ruleSet, player1, player2);
                game.addChangePanelListener(cpl);
                container.add(game, "game");

                player1.setGamePanel(game);
                player2.setGamePanel(game);

                game.prepareToPlay();
            }
            else if(panelName.equals("settingsEditor")){
                //FIXME need to refresh settingsEditor each time it is opened
                settingsEditorPanel.update();
            }
            cardLayout.show(container, panelName);
        };
    }

    public static LANHost getHostPanel() {
        return lanHostPanel;
    }
    public static LANPeer getLanPanel() {
        return lanPeerPanel;
    }

    public static void setIsHost(boolean isHost) {
        GUI.isHost = isHost;
    }

    public static void setNetworkGame(boolean networkGame) {
        isNetworkGame = networkGame;
    }

    public static void setGamePlayers(Player p1, Player p2) {
        player1 = p1;
        player2 = p2;

        player1.setOpponent(player2);
        player2.setOpponent(player1);
    }

    public static void setRuleSet(Ruleset set) {
        ruleSet = set;
    }

    public static void setBoard(Board set) {
        board = set;
    }

    public static void deleteBoardConfig() {
        // Delete "CURRENT!!!.txt" file
        File currentFile = new File("Boards/CURRENT!!!.txt");
        currentFile.delete();
    }
}
