package gui;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.util.ArrayList;
import java.util.HashSet;

import model.BoardModel;
import model.PieceModel;
import model.SquareModel;

public class BoardView extends JFrame {

    private static final long serialVersionUID = 1L;

    private final int numPieces;
    private ArrayList<SquareView> squares;
    private ArrayList<PieceView> pieces;
    private JLabel movesCount;
    private JLayeredPane boardPanel;
    private JButton endTurnBtn;
    private JButton rollBtn;
    private JLabel turnLabel;

    // Constructor
    public BoardView(int numPieces) {
        squares = new ArrayList<>(24);
        this.numPieces = numPieces;
        pieces = new ArrayList<>(numPieces * 2);
        createUI();
    }

    // Main method to crate GUI
    public void createUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(new Dimension(1000, 1000));
        setTitle("Royal Game of Ur");
        setResizable(false);
        setLocationRelativeTo(null);

        JPanel backgroundPanel = new JPanel(new GridBagLayout());
        boardPanel = new JLayeredPane();
        boardPanel.setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();

        addDefaultBoard();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        backgroundPanel.add(boardPanel, gbc);

        // Label to show player's turn
        turnLabel = new JLabel("It is White's turn");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 200, 0);
        backgroundPanel.add(turnLabel, gbc);

        // Label to show moves left
        movesCount = new JLabel("Current moves left: 0");
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(0, 0, 200, 0);
        backgroundPanel.add(movesCount, gbc);

        // Button to switch control to other side of the player when pushed
        endTurnBtn = new JButton("End turn");
        endTurnBtn.setFocusPainted(false);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.insets = new Insets(100, 0, 0, 0);
        backgroundPanel.add(endTurnBtn, gbc);

        rollBtn = new JButton("Roll");
        rollBtn.setFocusPainted(false);
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(100, 0, 0, 0);
        backgroundPanel.add(rollBtn, gbc);

        addDefaultPieces();

        add(backgroundPanel);
        setVisible(true);
    }

    // Method to draw Board
    private void addDefaultBoard() {
        GridBagConstraints gbc = new GridBagConstraints();
        boolean isRossete = false;
        boolean isPainted = true;

        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(1, 1, 1, 1);
        for (int i = 0; i < 24; i++) {
            gbc.gridx = i % 8;
            gbc.gridy = i / 8;

            // Set squares with Rossete to be recognize by program
            if (i == 0 || i == 6 || i == 11 || i == 16 || i == 22) {
                isRossete = true;
            } else {
                isRossete = false;
            }

            // Set Squares outside the board not to be painted same with the Squares on the
            // board
            if (i == 4 || i == 5 || i == 20 || i == 21) {
                isPainted = false;
            } else {
                isPainted = true;
            }
            SquareView square = new SquareView(isRossete, i, isPainted);

            boardPanel.add(square, gbc, 0);
            squares.add(square);
        }
    }

    // Method to add Pieces of both sides to board
    private void addDefaultPieces() {
        GridBagConstraints gbc = new GridBagConstraints();

        for (int i = 0; i < numPieces * 2; i++) {
            PieceView piece = new PieceView(i, i < numPieces);
            gbc.gridx = 4;
            gbc.gridy = i < numPieces ? 0 : 2;
            boardPanel.add(piece, gbc, 1);
            pieces.add(piece);
        }
    }

    // Return the coordinate by the squareID
    private ArrayList<Integer> getGridCoordsFromSquareID(int squareID) {
        ArrayList<Integer> gridCoords = new ArrayList<>(2);
        gridCoords.add(0, squareID % 8);
        gridCoords.add(1, squareID / 8);
        return gridCoords;
    }

    // Method to upadte the current move to the label
    public void setMovesCount(int movesCount) {
        this.movesCount.setText("Current moves left: " + movesCount);
    }

    // Return the squares on the board as ArrayList
    public ArrayList<SquareView> getSquares() {
        return squares;
    }

    public void updateBoard(BoardModel model) {
        GridBagConstraints gbc = new GridBagConstraints();

        for (SquareModel square : model.getBoard()) {
            ArrayList<Integer> gridCoords = getGridCoordsFromSquareID(square.getID());
            gbc.gridx = gridCoords.get(0);
            gbc.gridy = gridCoords.get(1);
            for (PieceModel piece : square.getPieces()) {
                if (!piece.isBlack() && gridCoords.get(0) == 7 && gridCoords.get(1) == 2) {
                    boardPanel.add(pieces.get(piece.getId()), gbc, 0);    
                } else {
                    boardPanel.add(pieces.get(piece.getId()), gbc, 1);
                }
            }
        }

        boardPanel.validate();
        repaint();
    }

    // Method to generate the window for outputing result when either side has won
    public int showGameOverDialog(boolean isBlackWon) {
        Object[] options = { "Replay", "Back" };

        String msg = "\nYou can play again or go back to the main menu.";
        if (isBlackWon) {
            msg = "Black won." + msg;
        } else {
            msg = "White won." + msg;
        }
        int n = JOptionPane.showOptionDialog(this,
                msg,
                "End game options",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[1]);

        // Allow user to choose between replay, back to main menu or close the game
        switch (n) {
            case JOptionPane.YES_OPTION:
                return 1;
            case JOptionPane.NO_OPTION:
                return 0;
            case JOptionPane.CLOSED_OPTION:
                return 0;
            default:
                return 0;
        }
    }

    public JButton getEndTurnButton() {
        return endTurnBtn;
    }

    public JButton getRollButton() {
        return rollBtn;
    }

    // Toggle player's turn and output on Frame
    public void setTurnLabel(boolean isBlackTurn) {
        if (isBlackTurn) {
            turnLabel.setText("It is Black's turn");
        } else {
            turnLabel.setText("It is White's turn");
        }
    }

    public void clearHighlights() {
        squares.forEach(square -> square.setHighlighted(false));
    }

    public void hightlightSquares(HashSet<Integer> squareIDs) {
        for (SquareView square : squares) {
            if (squareIDs.contains(square.getID())) {
                square.setHighlighted(true);
            }
        }
    }
}