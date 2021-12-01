package rgou.view.panel;

import rgou.model.element.Board;
import rgou.model.element.Cell;
import rgou.model.rules.Ruleset;
import rgou.view.game.CellButton;
import rgou.view.gui.GUI;
import rgou.view.gui.GUIStandards;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static rgou.view.game.GamePanel.getImageFromFile;

public class BoardEditorPanel extends JPanel {

    protected static final long serialVersionUID = 1L;
    protected JButton exitButton;
    protected static final int buttonWidth = 300;
    protected static final int buttonHeight = 100;
    protected Dimension screenSize;

    protected JPanel boardPanel;
    protected JPanel tileSelectorPanel;
    protected Board board;
    protected JButton[][] cellButtons;
    protected InputPanel saveBox;

    protected static final Color TILE_BG = new Color(255, 255, 255);

    protected enum tileType {
        normal, safe, teleport, reset, move, reroll, addStoneToMe, addDice, addStoneToOpponent, randMove
    }

    private HashMap<tileType, JButton> tileTypeButtons = new HashMap<>();
    private JSpinner moveNSpinner = new JSpinner();

    private tileType currentTileType = tileType.normal;


    public BoardEditorPanel() {
        loadWindow();
    }

    /**
     * Draws the screen and resets all fields
     */
    public void loadWindow() {
        // Reset all fields
        boardPanel = null;
        exitButton = null;
        tileSelectorPanel = null;
        board = null;
        cellButtons = null;
        tileTypeButtons = new HashMap<>();
        currentTileType = tileType.normal;

        // Add exit button and setup window
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLayout((LayoutManager) null);
        this.exitButton = new JButton("Back to menu");
        this.exitButton.setBounds((Math.round((float) screenSize.getWidth() / 2) - 90), 20, 180, 40);
        exitButton.setForeground(GUIStandards.buttonFontColour);
        this.setBackground(GUIStandards.backgroundColour);
        this.add(exitButton);

        this.revalidate();
        this.repaint();
        drawBoardPanel();
        drawTileSelectorPanel();
    }

    /**
     * Sets board as a default one
     */
    public void newBoard() {
        board = new Board();
        for (int i = 0; i < board.getCells().length; i++) {
            for (int j = 0; j < board.getCells()[i].length; j++) {
                resetCenter(i, j);
                resetCorners(i, j);
            }
        }
    }

    /**
     * Draws the current board
     */
    public void drawBoardPanel() {
        // Initialise and setup panel
        boardPanel = new JPanel();
        boardPanel.setLayout(new GridLayout(3, 8));
        Dimension boardPanelSize = new Dimension(6 * (int) screenSize.getWidth() / 8, (int) screenSize.getHeight() / 2);
        boardPanel.setBounds((int) (screenSize.getWidth() - boardPanelSize.getWidth()) / 2, (int) (screenSize.getHeight() - boardPanelSize.getHeight()) / 2, (int) boardPanelSize.getWidth(), (int) boardPanelSize.getHeight());
        boardPanel.setBackground(GUIStandards.buttonColour);

        //Set board as new baord
        newBoard();
        cellButtons = new JButton[3][8];

        // Draw, colour and add action listeners to each cell button
        for (int i = 0; i < cellButtons.length; i++) {
            for (int j = 0; j < cellButtons[i].length; j++) {
                int final_i = i;
                int final_j = j;
                JButton c;
                c = new JButton();
                c.revalidate();
                c.repaint();
                if (!((i == 0 || i == 2) && (j == 4 || j == 5))) {
                    c.setBackground(TILE_BG);
                    c.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            tileClicked(c, final_i, final_j);
                        }
                    });
                } else {
                    c.setBackground(new Color(0, 0, 0));
                    c.setEnabled(false);
                }
                boardPanel.add(c);
                cellButtons[i][j] = c;
            }
        }
        this.add(boardPanel);
    }

    /**
     * Draws the tile selector buttons
     */
    public void drawTileSelectorButtons() {

        // Add buttons for each tile type
        for (tileType t : tileType.values()) {
            JButton b = new JButton(t.toString());
            b.setBorder(new LineBorder(new Color(255, 255, 255), 4));
            tileTypeButtons.put(t, b);
        }

        // Add action listener for each tile type
        for (Map.Entry<tileType, JButton> e : tileTypeButtons.entrySet()) {
            tileSelectorPanel.add(e.getValue());
            e.getValue().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ee) {
                    currentTileType = e.getKey();
                    for (JButton b : tileTypeButtons.values()) {
                        b.setBorder(new LineBorder(new Color(255, 255, 255), 4));
                    }
                    // Set different border colour for selected tile type
                    ((JButton) e.getValue()).setBorder(new LineBorder(GUIStandards.gamePanelsBackgroundColour, 4));
                }
            });
        }

        // This for setting the N value of a move N tile
        tileSelectorPanel.add(new JLabel("Move N Value: "));
        moveNSpinner = new JSpinner();
        SpinnerNumberModel nm = new SpinnerNumberModel();
        nm.setMinimum(1);
        nm.setValue(3);
        moveNSpinner.setModel(nm);
        tileSelectorPanel.add(moveNSpinner);

        // Add button that resets whole board
        JButton resetBoardButton = new JButton("Reset board");
        resetBoardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetBoard();
            }
        });
        resetBoardButton.setBackground(GUIStandards.deleteColour);
        tileSelectorPanel.add(resetBoardButton);
    }

    /**
     * Draws panel where current tile type is selected
     */
    public void drawTileSelectorPanel() {
        // This for setting up the tile selector panel
        tileSelectorPanel = new JPanel();

        FlowLayout f = new FlowLayout();
        f.setHgap(40);

        tileSelectorPanel.setLayout(f);
        tileSelectorPanel.setBounds(boardPanel.getX(), (int) (boardPanel.getY() + boardPanel.getHeight()), (int) boardPanel.getSize().getWidth(), (int) boardPanel.getSize().getHeight() / 4);

        drawTileSelectorButtons();


        // Initialise the save box
        saveBox = new InputPanel();
        saveBox.getInputButton().setText("Save");
        saveBox.getInputBox().setPreferredSize(new Dimension(180, 40));
        saveBox.getInputButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    board.setName(saveBox.getInputBox().getText());
                    saveToFile();
                } catch (Exception ee) {
                }
            }
        });
        tileSelectorPanel.add(saveBox);

        this.add(tileSelectorPanel);
    }

    public void addChangePanelListener(ActionListener al) {
        exitButton.addActionListener(al);
        exitButton.setActionCommand("settings");
    }

    /**
     * @param t
     * @return true if tileType t is a "center" (instead of corner)
     */
    private boolean isCenter(tileType t) {
        return t.equals(tileType.normal) || t.equals(tileType.reset) ||
                t.equals(tileType.safe) || t.equals(tileType.teleport) ||
                t.equals(tileType.move) || t.equals(tileType.randMove);
    }

    /**
     * Resets center of Cell[i][j]
     *
     * @param i
     * @param j
     */
    public void resetCenter(int i, int j) {
        board.getCells()[i][j].setSafe(false);
        board.getCells()[i][j].setReset(false);
        board.getCells()[i][j].setTeleport(false);
        board.getCells()[i][j].setRandMove(false);
        board.getCells()[i][j].setMoveN(0);
    }

    /**
     * Resets corner of Cell[i][j]
     *
     * @param i
     * @param j
     */
    public void resetCorners(int i, int j) {
        board.getCells()[i][j].setReroll(false);
        board.getCells()[i][j].setExtraPiece(false);
        board.getCells()[i][j].setExtraPieceOpp(false);
        board.getCells()[i][j].setExtraDice(false);
    }

    /**
     * Sets cell[i][j] to have tileType t
     *
     * @param i
     * @param j
     * @param t
     */
    public void setCell(int i, int j, tileType t) {
        // Tile can have one center and 0 - 4 corner
        // This is for setting a cell in board
        Cell c = board.getCells()[i][j];

        if (isCenter(t)) resetCenter(i, j);

        if (t.equals(tileType.move)) {
            c.setMoveN((int) moveNSpinner.getValue());
        } else if (t.equals(tileType.randMove)) {
            c.setRandMove(true);
        } else if (t.equals(tileType.reset)) {
            c.setReset(true);
        } else if (t.equals(tileType.safe)) {
            c.setSafe(true);
        } else if (t.equals(tileType.teleport)) {
            c.setTeleport(true);
        }

        if (t.equals(tileType.reroll)) c.setReroll(!c.isReroll());
        if (t.equals(tileType.addStoneToMe)) c.setExtraPiece(!c.isExtraPiece());
        if (t.equals(tileType.addStoneToOpponent)) c.setExtraPieceOpp(!c.isExtraPieceOpp());
        if (t.equals(tileType.addDice)) c.setExtraDice(!c.isExtraDice());

    }

    /**
     * Gets cell[i][j]
     *
     * @param i
     * @param j
     * @return
     */
    private Cell getCell(int i, int j) {
        return board.getCells()[i][j];
    }

    /**
     * Resets board and redraws it
     */
    public void resetBoard() {
        newBoard();
        int i = 0;
        for (JButton[] row : cellButtons) {
            int j = 0;
            for (JButton c : row) {
                if (!((i == 0 || i == 2) && (j == 4 || j == 5))) {
                    c.setBackground(TILE_BG);
                } else {
                    c.setBackground(new Color(0, 0, 0));
                }
                c.setText("");
                j++;
            }
            i++;
        }
    }

    /**
     * @param i
     * @param j
     * @return center tileType of Cell[i][j]
     */
    private tileType getCenter(int i, int j) {
        Cell c = getCell(i, j);
        if (c.isSafe()) return tileType.safe;
        if (c.isTeleport()) return tileType.teleport;
        if (c.isReset()) return tileType.reset;
        if (c.getMoveN() != 0) return tileType.move;
        if (c.isRandMove()) return tileType.randMove;
        return tileType.normal;
    }

    /**
     * What should happen when a tile is clicked
     *
     * @param c
     * @param i
     * @param j
     */
    public void tileClicked(JButton c, int i, int j) {
        //Display a cell's center and the boolean value of each corner
        setCell(i, j, currentTileType);
        String s = "";
        Cell cell = getCell(i, j);
        s += "Center: " + getCenter(i, j);
        if (cell.getMoveN() != 0) s += " (" + cell.getMoveN() + ")";
        s += "\n Reroll: " + cell.isReroll();
        s += "\n +1 Stone: " + cell.isExtraPiece();
        s += "\n +1 Stone Opp: " + cell.isExtraPieceOpp();
        s += "\n +1 Dice: " + cell.isExtraDice();
        // Next line taken from https://stackoverflow.com/questions/15746970/how-to-add-a-multi-line-text-to-a-jbutton-with-the-line-unknown-dynamically
        c.setText("<html>" + s.replaceAll("\\n", "<br>") + "</html>");
    }

    public void saveToFile() throws IOException {
        board.saveToFile(board.toJsonString());
        resetBoard();
    }
}
