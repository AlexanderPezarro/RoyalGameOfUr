package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashSet;

import gui.BoardView;
import gui.SquareView;
import model.BoardModel;
import model.Path;
import model.PieceModel;
import model.SquareModel;

public class BoardController {

    private static final int NUM_PIECES = 5;

    private BoardModel model;
    private BoardView view;

    private int moves;
    private boolean isBlackTurn;
    private SquareModel lastPressed;
    private boolean hasRerolled;

    public BoardController() {
        model = new BoardModel(NUM_PIECES);
        view = new BoardView(NUM_PIECES);
        moves = 0;
        isBlackTurn = true;
        lastPressed = null;
        hasRerolled = false;

        addActionListerners();
        view.setTurnLabel(isBlackTurn);
    }

    private void addActionListerners() {
        view.getEndTurnButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isBlackTurn = !isBlackTurn;
                moves = 0;
                hasRerolled = false;

                view.setMovesCount(moves);
                view.getRollButton().setEnabled(true);
                view.setTurnLabel(isBlackTurn);
            }
        });

        view.getRollButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moves += (int) (Math.random() * 4);
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
        if (lastPressed != null) {
            // Checks if the square pressed is the same as the last square pressed
            if (squareModel.getID() == lastPressed.getID()) {
                view.clearHighlights();
                lastPressed = null;
                view.updateBoard(model);
            } else {
                HashSet<Integer> validMoveIDs = Path.getPossibleMoves(lastPressed.getPiece().isBlack(),
                        lastPressed.getID(),
                        moves);

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
                        view.updateBoard(model);
                    }
                } else {
                    view.clearHighlights();
                    lastPressed = null;
                    view.updateBoard(model);
                }
            }
        } else {
            // If square pressed has a piece, it's that piece's colour's turn and the piece
            // isn't finished
            if (pieceModel != null && isBlackTurn == pieceModel.isBlack() && !pieceModel.isFinished()) {
                HashSet<Integer> squareIDs = Path.getPossibleMoves(pieceModel.isBlack(), squareModel.getID(), moves);
                view.clearHighlights();
                view.hightlightSquares(squareIDs);
                lastPressed = squareModel;
                view.updateBoard(model);
            }
        }
    }
}
