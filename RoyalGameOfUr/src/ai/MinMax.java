package ai;

import java.util.HashSet;

import controller.BoardController;
import model.BoardModel;
import model.Move;

public class MinMax {

    public static HashSet<Move> pickMove(BoardModel board, boolean isBlack, int moves) {
        return pickMoveHelper(board, isBlack, moves, 1).getMoveSet();
    }

    private static Evaluation pickMoveHelper(BoardModel board, boolean isBlack, int moves, int depth) {
        HashSet<HashSet<Move>> possibleMoves = Move.getAllPossibleMoveSets(board, isBlack, moves);
        if (depth == 0) {
            Evaluation baseEval = new Evaluation(isBlack ? Integer.MAX_VALUE : Integer.MIN_VALUE);
            for (HashSet<Move> moveSet : possibleMoves) {
                Evaluation eval = Evaluation.evaluate(new BoardModel(board), new HashSet<Move>(moveSet), isBlack,
                        moves);
                eval.setMoveSet(moveSet);
                if (!isBlack == baseEval.isEvaluationGreater(eval)) {
                    baseEval = eval;
                }
            }
            return baseEval;
        } else {
            Evaluation bestMove = new Evaluation(isBlack ? Integer.MAX_VALUE : Integer.MIN_VALUE);
            for (HashSet<Move> moveSet : possibleMoves) {
                Evaluation baseEval = new Evaluation(new HashSet<Move>(moveSet), 0);
                // Create a copy of the current board
                BoardModel tempBoard = new BoardModel(board);
                // Apply a move set
                tempBoard.playMoveSet(moveSet, moves);
                
                // Find the best move adjusted for how likely it is to roll that move
                for (int i = BoardController.MIN_ROLL; i <= BoardController.MAX_ROLL; i++) {
                    Evaluation tempEval = pickMoveHelper(tempBoard, !isBlack, i, depth - 1);
                    tempEval.setEval(tempEval.getEval() * ((100 - 25f*i) / 100f));
                    // Add each roll to base eval to get average eval for this position
                    baseEval.setEval(baseEval.getEval() + tempEval.getEval());
                }
                // Gets the best move out of all the move sets
                if (!isBlack == bestMove.isEvaluationGreater(baseEval)) {
                    bestMove = baseEval;
                }
            }
            return bestMove;

        }
    }
}