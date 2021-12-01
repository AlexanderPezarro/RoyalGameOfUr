package rgou.view.panel;

import rgou.model.game.Game;

import javax.swing.*;
import java.awt.event.*;

public class GamePanel extends JPanel {
    
    private static final long serialVersionUID = 1L;

    private Game model;

    private final BoardPanel boardPanel;
    private final JButton homeButton;
    private final JLabel currentTurn;

    private final JLabel tester = new JLabel();

    public GamePanel() {

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.model = new Game();
        boardPanel = new BoardPanel(this, model.getBoard());
        homeButton = new JButton("Back to Home");
        currentTurn = new JLabel("Player 1's Turn");


        add(homeButton);
        add(new JLabel("This is the Game Page"));
        add(currentTurn);
        add(boardPanel);
        add(tester);
    }

    public void addChangePanelListener(ActionListener al) {
        homeButton.addActionListener(al);
        homeButton.setActionCommand("home");
    }

    public void setCurrentTurn(String player) {
        currentTurn.setText(player);
    }

    public void playMove(int startRow, int startCol, int endRow, int endCol) {
        tester.setText(startRow + ", " + startCol + ", " + endRow + ", " + endCol);
        // play the move...
        model.changeTurns();

        String currentPlayer;

        if (model.isPlayerOneTurn()) {
            currentPlayer = "Player 1's Turn";
        }
        else {
            currentPlayer = "Player 2's Turn";
        }

        setCurrentTurn(currentPlayer);

        boardPanel.updateBoard(model.getBoard());
    }
}
