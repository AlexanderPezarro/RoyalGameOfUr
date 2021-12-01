package rgou.view.game;

import rgou.model.game.Game;
import rgou.view.Player;
import rgou.view.gui.GUIStandards;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ItemEvent;

/**
 * This class is used to represent a player of the game
 */
public class PlayerBox extends JPanel {

    // dimensions of the panel
    private int width;
    private int height;

    // player details
    private int playerNumber;
    private int piecesFinished;

    // player image details
    Image image;
    private final int imageSize;

    private JButton icon;

    // Details for the player type
    private Game.PlayerType[] types = Game.PlayerType.values();
    private JComboBox<Game.PlayerType> playerTypes;
    private JLabel piecesLabel;

    // the parent game panel
    private GamePanel gamePanel;

    private Player player;

    /**
     * Class constrcutor
     * @param playerNumber player number, 1 or 2
     * @param gamePanel parent game panel
     * @param player the player object
     * @param size the size of the screen
     * @param showOptions whether the option to change the player type should be shown or not
     */
    public PlayerBox(int playerNumber, GamePanel gamePanel, Player player, Dimension size, boolean showOptions) {
        this.width = size.width/3;
        this.height = size.height/4;

        this.player = player;
        this.gamePanel = gamePanel;
        imageSize = height;

        setBackground(GUIStandards.gamePanelsBackgroundColour);
        this.playerNumber = playerNumber;

        setOpaque(true);

        setLayout(null);

        // Set the icon image for the player based on the player type
        icon = new JButton(){
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 5, 5, imageSize - 5, imageSize - 5, null);
            }
        };
        icon.setBorder(new LineBorder(Color.white, 5));
        icon.setContentAreaFilled(false);

        // decide which side of the player box the icon should be
        int iconX = 0;
        int otherX = height;
        if(this.playerNumber == 2){
            iconX = width/2;
            otherX = 0;
        }

        int padding = width/20;
        icon.setBounds(iconX, 0, height, height);

        add(icon);
        playerTypes = new JComboBox<>(types);

        // if options can shown then display the combo box
        if(showOptions) {
            // the combo box used to decide which player type to play as

            //playerTypes = new JComboBox<>(types);

            playerTypes.setMaximumSize(playerTypes.getPreferredSize());
            playerTypes.setAlignmentX(Component.CENTER_ALIGNMENT);

            playerTypes.setSelectedItem(player.getType());

            // when the selectino is changed, update the player icon and player type
            playerTypes.addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED){
                    Game.PlayerType newType = (Game.PlayerType) e.getItem();
                    setIconImage(newType);
                    this.player.setType(newType);
                    gamePanel.playMoveIfAI();// if the new player type is an AI, then play an AI move
                }
            });


            playerTypes.setBounds(otherX + padding, height/3, 2*width/5, playerTypes.getPreferredSize().height);

            add(playerTypes);
        }

        else {// else add the player name as a label
            JLabel playerName = new JLabel(this.player.getName());
            playerName.setBounds(otherX + padding, height/3, 2*width/5, playerTypes.getPreferredSize().height);
            add(playerName);
        }

        piecesLabel = new JLabel();
        setPiecesFinished(0);

        piecesLabel.setForeground(GUIStandards.backgroundColour);
        piecesLabel.setFont(GUIStandards.buttonFont);
        piecesLabel.setHorizontalAlignment(SwingConstants.CENTER);
        piecesLabel.setBounds(otherX + padding, 2*height/3, 2*width/5, playerTypes.getPreferredSize().height);

        add(piecesLabel);

        setIconImage(player.getType());
    }

    /**
     * Increment the pieces finished counter
     */
    public void incrementPiecesFinished() {
        piecesFinished++;
        setPiecesFinished(piecesFinished);
    }

    /**
     * Sets the image icon based on the player type
     * @param typeOfPlayer the player type
     */
    private void setIconImage(Game.PlayerType typeOfPlayer) {
        // load an iage based on the player type, human is the default
        image = GamePanel.getImageFromFile("human", imageSize);
        switch (typeOfPlayer) {
            case EASY_AI:
                image = GamePanel.getImageFromFile("easy", imageSize);
                break;
            case MEDIUM_AI:
                image = GamePanel.getImageFromFile("medium", imageSize);
                break;
            case HARD_AI:
                image = GamePanel.getImageFromFile("hard", imageSize);
                break;
        }

        // update the icon
        icon.repaint();
    }

    /**
     * Sets the number of pieces finished
     * @param piecesFinished
     */
    public void setPiecesFinished(int piecesFinished) {
        this.piecesFinished = piecesFinished;
        this.piecesLabel.setText("Pieces Finished: " + piecesFinished);
    }

    /**
     * Get the player type
     * @return the player type
     */
    public Game.PlayerType getPlayerType(){
        try{
        return Game.PlayerType.valueOf(playerTypes.getSelectedItem().toString());}
        catch (Exception e){
            return null;
        }
    }
}
