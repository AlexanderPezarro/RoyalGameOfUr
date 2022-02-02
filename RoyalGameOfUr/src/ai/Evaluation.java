package ai;

import java.util.HashSet;

import model.BoardModel;
import model.Move;
import model.Path;
import model.PieceModel;

public class Evaluation {
    
    private static int evaluate(BoardModel board) {
        int whiteScore = 0;
        int blackScore = 0;
        for (PieceModel piece : board.getPieces()) {
            if (piece.isBlack()) {
                blackScore += Path.getDistanceBetweenSquares(true, board.getInitialSquare(true).getID(), piece.getCurrentSquareID());
            } else {
                whiteScore += Path.getDistanceBetweenSquares(false, board.getInitialSquare(false).getID(), piece.getCurrentSquareID());
            }
        }
        return whiteScore - blackScore;
    }

    public static Evaluation evaluate(BoardModel board, HashSet<Move> moveSet, boolean isBlack, int availableMoves) {
        int rosseteBonus = 0;
        boolean landedOnRossete = false;
        for (Move move : moveSet) {
            if (board.getBoard().get(move.getDestinationSquareID()).isRossete()) {
                if (!landedOnRossete) {
                    landedOnRossete = true;
                    rosseteBonus += 20;
                } else {
                    rosseteBonus -= 10;
                }
            }
        }
        if (isBlack) {
            rosseteBonus *= -1;
        }

        board.playMoveSet(moveSet, availableMoves);
        
        return new Evaluation(evaluate(board) + rosseteBonus);
    }

    private HashSet<Move> moveSet;
    private float eval;

    public Evaluation(HashSet<Move> moveSet, float eval) {
        this.moveSet = moveSet;
        this.eval = eval;
    }

    public Evaluation(float eval) {
        this(null, eval);
    }

    public Evaluation() {
        this(null, 0);
    }

    public boolean isEvaluationGreater(Evaluation eval) {
        return (eval.getEval() > this.eval);

    }

    public HashSet<Move> getMoveSet() {
        return moveSet;
    }

    public void setMoveSet(HashSet<Move> moveSet) {
        this.moveSet = moveSet;
    }

    public float getEval() {
        return eval;
    }

    public void setEval(float eval) {
        this.eval = eval;
    }
}