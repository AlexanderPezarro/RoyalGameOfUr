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

public class Board extends JFrame {

    private static final long serialVersionUID = 1L;

    private ArrayList<Square> squares;
    private ArrayList<PlayingPiece> blackPieces;
    private ArrayList<PlayingPiece> whitePieces;
    private JLabel movesCount;
    private JLayeredPane boardPanel;
    private JButton endTurnBtn;
    private JLabel turnLabel;

    // Constructor
    public Board() {
        squares = new ArrayList<>(24);
        blackPieces = new ArrayList<>(5);
        whitePieces = new ArrayList<>(5);
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
            Square square = new Square(isRossete, i, isPainted);

            boardPanel.add(square, gbc, 0);
            squares.add(square);
        }
    }

    // Method to add Pieces of both sides to board
    private void addDefaultPieces() {
        GridBagConstraints gbc = new GridBagConstraints();
        // Initialise Black piece
        for (int i = 0; i < 5; i++) {
            PlayingPiece blackPiece = new PlayingPiece(i, true);
            gbc.gridx = 4;
            gbc.gridy = 0;
            boardPanel.add(blackPiece, gbc, 1);
            blackPieces.add(blackPiece);
        }
        // Initialise White piece
        for (int i = 0; i < 5; i++) {
            PlayingPiece whitePiece = new PlayingPiece(i, false);
            gbc.gridx = 4;
            gbc.gridy = 2;
            boardPanel.add(whitePiece, gbc, 1);
            whitePieces.add(whitePiece);
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
    public ArrayList<Square> getSquares() {
        return squares;
    }

    // Return all Black pieces as ArrayList
    public ArrayList<PlayingPiece> getBlackPieces() {
        return blackPieces;
    }

    // Return all White pieces as ArrayList
    public ArrayList<PlayingPiece> getWhitePieces() {
        return whitePieces;
    }

    // TODO: Redo the update method

    // public void updateBoard(Board board) {
    //     GridBagConstraints gbc = new GridBagConstraints();

    //     // Place Black Piece
    //     blackPieces.forEach(f -> boardPanel.remove(f));
    //     for (int i = 0; i < blackPieces.size(); i++) {
    //         ArrayList<Integer> gridCoords = getGridCoordsFromSquareID(
    //                 board.getBlackPieces().get(i).getSquare().getSquareID());
    //         gbc.gridx = gridCoords.get(0);
    //         gbc.gridy = gridCoords.get(1);
    //         boardPanel.add(blackPieces.get(i), gbc, 1);
    //     }

    //     // Place White Piece
    //     whitePieces.forEach(f -> boardPanel.remove(f));
    //     for (int i = 0; i < whitePieces.size(); i++) {
    //         ArrayList<Integer> gridCoords = getGridCoordsFromSquareID(
    //                 board.getWhitePieces().get(i).getSquare().getSquareID());
    //         gbc.gridx = gridCoords.get(0);
    //         gbc.gridy = gridCoords.get(1);
    //         if (gridCoords.get(0) == 7 && gridCoords.get(1) == 2) {
    //             boardPanel.add(whitePieces.get(i), gbc, 0);
    //         } else {
    //             boardPanel.add(whitePieces.get(i), gbc, 1);
    //         }

    //     }

    //     boardPanel.validate();
    //     repaint();
    // }

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

    // Toggle player's turn and output on Frame
    public void setTurnLabel(boolean isBlackTurn) {
        if (isBlackTurn) {
            turnLabel.setText("It is Black's turn");
        } else {
            turnLabel.setText("It is White's turn");
        }
    }

}