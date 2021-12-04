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
    private boolean isPlayerBlack;
    private SquareModel lastPressed;

    public BoardController() {
        model = new BoardModel(NUM_PIECES);
        view = new BoardView(NUM_PIECES);
        moves = 0;
        isBlackTurn = true;
        lastPressed = null;
        isPlayerBlack = true;

        addActionListerners();
        view.setTurnLabel(isBlackTurn);
    }

    public void setPlayerColour(boolean isPlayerBlack) {
        this.isPlayerBlack = isPlayerBlack;
    }

    private void addActionListerners() {
        view.getEndTurnButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isBlackTurn = !isBlackTurn;
                moves = 0;

                view.setMovesCount(moves);
                view.getRollButton().setEnabled(true);
                view.setTurnLabel(isBlackTurn);
            }
        });

        view.getRollButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                moves = (int)(Math.random()*4);
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
        SquareModel squareModel = model.getBoard().get(squareViewPressed.getID());
        if (lastPressed != null) {
            if (squareViewPressed.isHighlighted()) {
                model.movePiece(lastPressed.getID(), squareModel.getID(), moves);
            }   
        }

        PieceModel pieceModel = squareModel.getPiece();
        if (pieceModel != null && isBlackTurn == pieceModel.isBlack() && isBlackTurn == isPlayerBlack && !pieceModel.isFinished()) {
            HashSet<Integer> squareIDs = Path.getPossibleMoves(pieceModel.isBlack(), squareModel.getID(), moves);
            view.getSquares().forEach(s -> s.setHighlighted(false));
            for (SquareView squareView : view.getSquares()) {
                if (squareIDs.contains(squareView.getID())) {
                    squareView.setHighlighted(true);
                }
            }
            lastPressed = squareModel;
            view.updateBoard(model);
        }
    }
}
