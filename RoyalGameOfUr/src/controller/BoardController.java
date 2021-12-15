package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;

import ai.MinMax;
import gui.BoardView;
import gui.MainMenu;
import gui.SquareView;
import model.BoardModel;
import model.Move;
import model.Path;
import model.PieceModel;
import model.SquareModel;

public class BoardController {

    private static final int NUM_PIECES = 5;
    private static final int MIN_ROLL = 0;
    private static final int MAX_ROLL = 3;

    private BoardModel model;
    private BoardView view;

    private int moves;
    private boolean isBlackTurn;
    private SquareModel lastPressed;
    private boolean hasRerolled;
    private boolean isPlayerBlack;
    private boolean isAIPlaying;

    public BoardController(boolean isAIPlaying, boolean isPlayerBlack) {
        model = new BoardModel(NUM_PIECES);
        view = new BoardView(NUM_PIECES);
        this.isPlayerBlack = isPlayerBlack;
        this.isAIPlaying = isAIPlaying;
        resetBoard();

        addActionListerners();
        view.setTurnLabel(isBlackTurn);
    }

    public BoardController() {
        this(false, false);
    }

    private void addActionListerners() {
        view.getEndTurnButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                endTurn();
            }
        });

        view.getRollButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moves += (int) ((Math.random() + MIN_ROLL) * (MAX_ROLL + 1));
                view.setMovesCount(moves);
                view.getRollButton().setEnabled(false);
            }
        });

        view.getSquares().forEach(squareView -> squareView.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                squarePressed(squareView);
            }
        }));
    }

    private void squarePressed(SquareView squareViewPressed) {
        // Get the model of the square pressed
        SquareModel squareModel = model.getBoard().get(squareViewPressed.getID());

        // pieceModel will be null if square has no piece
        PieceModel pieceModel = squareModel.getPiece();
        // lastPressed will only not be null if last square pressed has a piece on it
        if (lastPressed != null) {
            // Checks if the square pressed is the same as the last square pressed
            if (squareModel.getID() == lastPressed.getID()) {
                view.clearHighlights();
                lastPressed = null;
            } else {
                // Get the valid move squares from the last square and the current moves
                // This doesn't take into consideration other pieces on the board
                HashSet<Integer> validMoveIDs = Path.getPossibleFinalSquares(lastPressed.getPiece().isBlack(),
                        lastPressed.getID(),
                        moves);

                // If the current pressed square is valid try to move the piece
                if (validMoveIDs.contains(squareModel.getID())) {
                    PieceModel piece = lastPressed.getPiece();
                    // movePiece will return true if piece is moved
                    if (model.movePiece(lastPressed.getID(), squareModel.getID(), moves)) {
                        // Only allowed 1 extra reroll per turn
                        if (squareModel.isRossete() && !hasRerolled) {
                            hasRerolled = true;
                            view.getRollButton().setEnabled(true);
                        }

                        moves -= Path.getDistanceBetweenSquares(piece.isBlack(), lastPressed.getID(),
                                squareModel.getID());
                        view.setMovesCount(moves);
                        view.clearHighlights();
                        lastPressed = null;

                        isGameOver();
                    }
                } else {
                    view.clearHighlights();
                    lastPressed = null;
                }
            }
        } else {
            // If square pressed has a piece, it's that piece's colour's turn and the piece
            // isn't finished
            if (pieceModel != null && isBlackTurn == pieceModel.isBlack() && !pieceModel.isFinished()) {
                // Then get the valid moves from that square and highlight them
                HashSet<Integer> squareIDs = Path.getPossibleFinalSquares(pieceModel.isBlack(), squareModel.getID(),
                        moves);
                view.clearHighlights();
                view.hightlightSquares(squareIDs);
                lastPressed = squareModel;
            }
        }
        // Regardless of piece moving or not, refresh the board
        view.updateBoard(model);
    }

    private void isGameOver() {
        // If one of the finish squares has 5 pieces then a player has finished
        if (model.getFinishSquare(true).getPieces().size() == 5
                || model.getFinishSquare(false).getPieces().size() == 5) {
            boolean playAgain = false;

            // If black was won "model.getFinishSquare(true).getPieces().size() == 5" will
            // be true, else white has won
            playAgain = view.showGameOverDialog(model.getFinishSquare(true).getPieces().size() == 5);

            if (playAgain) {
                model = new BoardModel(NUM_PIECES);
                resetBoard();
            } else {
                new MainMenu();
                view.dispose();
            }
        }
    }

    private void resetBoard() {
        moves = 0;
        isBlackTurn = true;
        lastPressed = null;
        hasRerolled = false;
        view.setMovesCount(moves);
        view.setTurnLabel(isBlackTurn);
        view.getRollButton().setEnabled(true);
        view.clearHighlights();
        view.updateBoard(model);
    }

    private void endTurn() {
        isBlackTurn = !isBlackTurn;
        moves = 0;
        hasRerolled = false;

        view.setMovesCount(moves);
        view.getRollButton().setEnabled(true);
        view.setTurnLabel(isBlackTurn);

        if (isAIPlaying && isBlackTurn != isPlayerBlack) {
            AITurn();
        }
    }

    private void AITurn() {
        // Press the roll button for the AI
        view.getRollButton().doClick();

        System.out.println("AI rolled a: " + moves);
        System.out.println("AI's move(s) are: ");
        HashSet<Move> bestMove = MinMax.pickMove(model, false, moves);
        if (bestMove.size() == 0) {
            System.out.println("Don't move");
        } else {
            for (Move move : bestMove) {
                System.out.println(move);
                // As the AI doesn't press the squares, manually check if a rossete square will
                // be landed on and re-enable the roll button
                if (model.getBoard().get(move.getDestinationSquareID()).isRossete()) {
                    view.getRollButton().setEnabled(true);
                }
            }
        }
        moves = model.playMoveSet(bestMove, moves);
        view.updateBoard(model);

        System.out.println("After moving the AI has: " + moves + " moves left");

        isGameOver();

        // If the AI has played its move and can't roll again, press the end turn button
        // for it
        if (!view.getRollButton().isEnabled()) {
            view.getEndTurnButton().doClick();
        } else {
            // Else roll and move again
            AITurn();
        }
    }
}
