package ai;

import java.util.HashSet;

import model.BoardModel;
import model.Move;

public class MinMax { 
    
    public static HashSet<Move> pickMove(BoardModel board, boolean isBlack, int moves) {
        return pickMoveHelper(board, isBlack, moves, 0);
    }

    private static HashSet<Move> pickMoveHelper(BoardModel board, boolean isBlack, int moves, int depth) {
        if (depth == 0) {
            HashSet<HashSet<Move>> possibleMoves = Move.getAllPossibleMoveSets(board, isBlack, moves);
            int maxEval = Integer.MIN_VALUE;
            HashSet<Move> bestMoveSet = null;
            for (HashSet<Move> moveSet : possibleMoves) {
                int eval = Evaluation.evaluate(new BoardModel(board), new HashSet<Move>(moveSet), isBlack, moves);
                if (eval > maxEval) {
                    maxEval = eval;
                    bestMoveSet = moveSet;
                }
            }
            return bestMoveSet;
        } else {
            return pickMoveHelper(board, isBlack, moves, depth-1);
        }
    }
}
