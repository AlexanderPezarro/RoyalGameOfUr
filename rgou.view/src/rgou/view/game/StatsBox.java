package rgou.view.game;

import rgou.view.game.GamePanel;
import rgou.view.gui.*;
import rgou.view.networking.MessageBox;
import rgou.view.networking.NetworkPlayer;

import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;

public class StatsBox extends JPanel {

    private JButton rollDiceButton;
    private JLabel displayMessage;

    private GamePanel gamePanel;

    private MessageBox messageBox;

    public StatsBox(GamePanel gamePanel) {
        this.gamePanel = gamePanel;

        //setBorder(new LineBorder( new Color(218,165,32), 4)); uncomment this if you like
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));


        //displayMessage = new JLabel("Player 1's Turn");
        rollDiceButton = new JButton("Roll Dice");
        rollDiceButton.setAlignmentX(CENTER_ALIGNMENT);
        rollDiceButton.setFont(GUIStandards.buttonFont);
        rollDiceButton.setForeground(GUIStandards.buttonFontColour);
        rollDiceButton.setBackground(GUIStandards.backgroundColour);

        setBackground(GUIStandards.gamePanelsBackgroundColour);

        rollDiceButton.addActionListener(e -> {
            rollDiceButton.setEnabled(false);
            gamePanel.setDiceRollNumbers();
            gamePanel.rollDice();
        });

        setPreferredSize(new DimensionUIResource(470, 0));

        add(rollDiceButton);

        messageBox = new MessageBox();
        messageBox.pushMessage("Player 1's Turn");
        add(messageBox);
    }

    public void pushMessage(String msg){
        messageBox.pushMessage(msg);
        this.revalidate();
    }

    public void enableRollButton() {
        rollDiceButton.setEnabled(true);
    }

    public void disableRollButton() {
        rollDiceButton.setEnabled(false);
    }

    public void setButtonText(String text) {
        rollDiceButton.setText(text);
    }

    public void setMsgBoxPlayer(NetworkPlayer player) {
        messageBox.player = player;
    }
}
