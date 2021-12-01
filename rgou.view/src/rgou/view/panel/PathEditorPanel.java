package rgou.view.panel;

import java.util.List;

import rgou.model.element.Board;
import rgou.model.element.PathBoard;
import rgou.view.gui.GUIStandards;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class PathEditorPanel extends BoardEditorPanel {

    private static final long serialVersionUID = 1L;
    private static final int buttonWidth = 300;
    private static final int buttonHeight = 100;

    private boolean player1 = true;
    private static final Color PLAYER1_COLOUR = new Color(255, 0, 0);
    private static final Color PLAYER2_COLOUR = new Color(0, 0, 255);
    private static final Color BOTHPLAYER_COLOUR = new Color(128, 0, 128);

    private List<int[]> path1 = new ArrayList<>();
    private List<int[]> path2 = new ArrayList<>();

    private JButton player1Button;
    private JButton player2Button;

    public PathEditorPanel() {
        super();
        // Replace boardeditor's save button
        JButton saveButton = saveBox.getInputButton();
        for (ActionListener al : saveButton.getActionListeners()) {
            saveButton.removeActionListener(al);
        }
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    saveToFile();
                } catch (Exception ee) {
                }
            }
        });
    }

    /**
     * returns a default path
     */
    @Override
    public void newBoard() {
        board = new PathBoard();
    }

    /**
     * This is what should happen when a tile is clicked
     */
    @Override
    public void tileClicked(JButton c, int i, int j) {
        List<int[]> currentList;
        currentList = player1 ? path1 : path2;
        int[] coords = getTileCoords(c);
        if (c.getBackground().equals(getCurrentColour())) {
            //If this tile's colour = selected colour, set background as blank
            c.setBackground(TILE_BG);
            currentList.remove(coords);
        } else if (c.getBackground().equals(getOtherPlayerColour())) {
            // If this tile's colour = OTHER colour, set background as mix
            c.setBackground(BOTHPLAYER_COLOUR);
            currentList.add(coords);
        } else if (c.getBackground().equals(BOTHPLAYER_COLOUR)) {
            // If this tile's colour = BOTH colour, set as other player's colour
            c.setBackground(getOtherPlayerColour());
            currentList.remove(coords);
        } else {
            // Set as selected colour
            c.setBackground(getCurrentColour());
            currentList.add(coords);
        }
    }

    /**
     *
     * @param b
     * @return co-ordinates of given cell button
     */
    public int[] getTileCoords(JButton b) {
        int x = -1, y = -1;
        for (int i = 0; i < cellButtons.length; i++) {
            for (int j = 0; j < cellButtons[i].length; j++) {
                if (cellButtons[i][j].equals(b)) {
                    x = i;
                    y = j;
                    break;
                }
            }
        }
        return new int[]{y, x};
    }

    /**
     * Draw the path selector buttons
     */
    @Override
    public void drawTileSelectorButtons() {

        player1Button = new JButton();
        player1Button.setBorder(new LineBorder(new Color(255, 255, 255), 4));

        player2Button = new JButton();
        player2Button.setBorder(new LineBorder(new Color(255, 255, 255), 4));

        player1Button.setText("Player 1");
        player1Button.setBackground(PLAYER1_COLOUR);

        player2Button.setText("Player 2");
        player2Button.setBackground(PLAYER2_COLOUR);


        for (JButton b : new JButton[]{player1Button, player2Button}) {
            tileSelectorPanel.add(b);
            b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    player1Button.setBorder(new LineBorder(new Color(255, 255, 255), 4));
                    player2Button.setBorder(new LineBorder(new Color(255, 255, 255), 4));
                    b.setBorder(new LineBorder(GUIStandards.gamePanelsBackgroundColour, 4));
                    player1 = e.getSource().equals(player1Button);
                }
            });
        }
    }

    @Override
    public void saveToFile() throws IOException {
        board.setPath(true, path1.toArray(new int[0][0]));
        board.setPath(false, path2.toArray(new int[0][0]));
        super.saveToFile();
        resetBoard();
    }

    public Color getCurrentColour() {
        if (player1) return PLAYER1_COLOUR;
        return PLAYER2_COLOUR;
    }

    public Color getOtherPlayerColour() {
        if (player1) return PLAYER2_COLOUR;
        return PLAYER1_COLOUR;
    }


}
