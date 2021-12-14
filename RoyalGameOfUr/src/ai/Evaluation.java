package ai;

import java.util.HashSet;

import model.BoardModel;
import model.Move;
import model.Path;
import model.PieceModel;
import model.SquareModel;

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

    public static int evaluate(BoardModel board, HashSet<Move> moveSet, boolean isBlack, int availableMoves) {
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

        HashSet<Move> doneMoves = new HashSet<>();
        int tries = moveSet.size() + 1;
        while (!moveSet.isEmpty()) {
            for (Move move : moveSet) {
                if (board.movePiece(move.getInitialSquareID(), move.getDestinationSquareID(), availableMoves)) {
                    doneMoves.add(move);
                }   
            }
            moveSet.removeAll(doneMoves);
            if (tries-- <= 0) {
                System.out.println("Used up tries in evaluation, invalid move in move set");
            }
        }
        return evaluate(board) + rosseteBonus;
    }
    
    // Not used
    private static int evaluate(BoardModel initialBoard, BoardModel finalBoard) {
        int rosseteSquaresBlackInitial = 0;
        int rosseteSquaresWhiteInitial = 0;
        for (PieceModel piece : initialBoard.getPieces()) {
            if (initialBoard.getBoard().get(piece.getCurrentSquareID()).isRossete()) {
                if (piece.isBlack()) {
                    rosseteSquaresBlackInitial++;
                } else {
                    rosseteSquaresWhiteInitial++;
                }
            }
        }

        int whiteScore = 0;
        int blackScore = 0;
        int rosseteSquaresBlackFinal = 0;
        int rosseteSquaresWhiteFinal = 0;
        for (PieceModel piece : finalBoard.getPieces()) {
            if (piece.isBlack()) {
                blackScore += Path.getDistanceBetweenSquares(true, finalBoard.getInitialSquare(true).getID(), piece.getCurrentSquareID());
                if (initialBoard.getBoard().get(piece.getCurrentSquareID()).isRossete()) {
                    rosseteSquaresBlackFinal++;
                }
            } else {
                whiteScore += Path.getDistanceBetweenSquares(false, finalBoard.getInitialSquare(false).getID(), piece.getCurrentSquareID());
                if (initialBoard.getBoard().get(piece.getCurrentSquareID()).isRossete()) {
                    rosseteSquaresWhiteFinal++;
                }
            }
        }
        if (rosseteSquaresBlackFinal == rosseteSquaresBlackInitial + 1) {
            blackScore += 20;
        } else if(rosseteSquaresBlackFinal > rosseteSquaresBlackInitial + 1) {
            blackScore += 15;
        }

        if (rosseteSquaresWhiteFinal == rosseteSquaresWhiteInitial + 1) {
            whiteScore += 20;
        } else if(rosseteSquaresWhiteFinal > rosseteSquaresWhiteInitial + 1) {
            whiteScore += 15;
        }
        return whiteScore - blackScore;
    }
    // Not used
    private static int evaluate(BoardModel board, int initialSquare, int finalSquare) {
        int score = 0;
        PieceModel initialPiece = board.getBoard().get(initialSquare).getPiece();
        PieceModel finalPiece = board.getBoard().get(finalSquare).getPiece();
        if (initialPiece == null) {
            return -1;
        }

        if (finalPiece != null) {
            if (finalPiece.isBlack() == initialPiece.isBlack()) {
                return -1;
            } else {
                score += Path.getDistanceBetweenSquares(finalPiece.isBlack(), finalPiece.getID(), board.getInitialSquare(finalPiece.isBlack()).getID());
            }
        }
        score += Path.getDistanceBetweenSquares(initialPiece.isBlack(), initialSquare, finalSquare);

        if (board.getBoard().get(finalSquare).isRossete()) {
            score += 20;
        }
        
        return score;
    }
    // Not used
    private static int evaluate(BoardModel board, SquareModel initialSquare, SquareModel finalSquare) {
        int score = 0;
        PieceModel initialPiece = initialSquare.getPiece();
        PieceModel finalPiece = finalSquare.getPiece();
        if (initialPiece == null) {
            return -1;
        }

        if (finalPiece != null) {
            if (finalPiece.isBlack() == initialPiece.isBlack()) {
                return -1;
            } else {
                score += Path.getDistanceBetweenSquares(finalPiece.isBlack(), finalPiece.getID(), board.getInitialSquare(finalPiece.isBlack()).getID());
            }
        }
        score += Path.getDistanceBetweenSquares(initialPiece.isBlack(), initialSquare.getID(), finalSquare.getID());

        if (finalSquare.isRossete()) {
            score += 20;
        }
        
        return score;
    }
}