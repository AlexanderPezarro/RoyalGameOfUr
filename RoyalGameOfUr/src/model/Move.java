package model;

import java.util.ArrayList;
import java.util.HashSet;

public final class Move {
    private final int initialSquareID;
    private final int destinationSquareID;
    private final int moves;

    public static HashSet<HashSet<Move>> getAllPossibleMoveSets(BoardModel board, boolean isBlack, int moves) {
        HashSet<HashSet<Move>> possibleMoveSets = new HashSet<>();
        if (moves == 0) {
            possibleMoveSets.add(new HashSet<Move>(0));
            return possibleMoveSets;
        }

        // Get the correct colour of pieces
        ArrayList<PieceModel> pieces = isBlack ? board.getPieces(true) : board.getPieces(false);
        HashSet<Integer> startingSqaureIDs = new HashSet<>();
        
        // Gets the set of squares the pieces are currently on
        pieces.forEach(piece -> startingSqaureIDs.add(piece.getCurrentSquareID()));

        HashSet<Move> possibleMoves = new HashSet<>();
        for (Integer startSquareID : startingSqaureIDs) {
            possibleMoves.addAll(Path.getPossibleMoves(isBlack, startSquareID, moves));
        }

        possibleMoveSets = powerSetWithinMoves(possibleMoves, moves);
        HashSet<HashSet<Move>> validMoveSet = new HashSet<>();
        
        for (HashSet<Move> moveSet : possibleMoveSets) {
            if (isMoveSetValid(board, isBlack, moveSet)) {
                validMoveSet.add(moveSet);
            }
        }
        return validMoveSet;
    }

    public static HashSet<HashSet<Move>> powerSetWithinMoves(HashSet<Move> originalSet, int moves) {
        HashSet<HashSet<Move>> sets = new HashSet<HashSet<Move>>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<Move>());
            return sets;
        }
        ArrayList<Move> list = new ArrayList<Move>(originalSet);
        Move head = list.get(0);
        HashSet<Move> rest = new HashSet<Move>(list.subList(1, list.size())); 
        for (HashSet<Move> set : powerSetWithinMoves(rest, moves)) {
            HashSet<Move> newSet = new HashSet<Move>();
            newSet.add(head);
            newSet.addAll(set);
            int moveCount = 0;
            for (Move move : newSet) {
                moveCount+= move.getMoves();
            }
            if (moveCount <= moves) sets.add(newSet);
            sets.add(set);
        }       
        return sets;
    } 
    
    public static boolean isMoveSetValid(BoardModel board, boolean isBlack, HashSet<Move> moveSet) {
        for (Move move : moveSet) {
            boolean isDestinationFree;
            if (board.getFinishSquare(isBlack).getID() == move.getDestinationSquareID()) {
                isDestinationFree = true;
            } else {
                if (board.isSquareOccupied(move.getDestinationSquareID())) {
                    isDestinationFree = board.getBoard().get(move.getDestinationSquareID()).getPiece().isBlack() != isBlack;
                } else {
                    isDestinationFree = true;
                }  
            }
            int movesFromSameSquare = 1;
            for (Move move2 : moveSet) {
                if (move == move2) continue;
                if (move.getDestinationSquareID() == move2.getDestinationSquareID() && !board.getBoard().get(move.getDestinationSquareID()).isFinishing()) {
                    return false;
                }
                if (move.getInitialSquareID() == move2.getInitialSquareID()) {
                    movesFromSameSquare++;
                }
                isDestinationFree = isDestinationFree ? true : move2.getInitialSquareID() == move.getDestinationSquareID();
            }
            if (!isDestinationFree) {
                return false;
            }
            if (board.getBoard().get(move.getInitialSquareID()).getPieces().size() < movesFromSameSquare) {
                return false;
            }
        }
        return true;
    }

    public Move(int initialSquareID, int destinationSquareID, int moves) {
        this.initialSquareID = initialSquareID;
        this.destinationSquareID = destinationSquareID;
        this.moves = moves;
    } 

    public int getInitialSquareID() {
        return initialSquareID;
    }

    public int getDestinationSquareID() {
        return destinationSquareID;
    }

    public int getMoves() {
        return moves;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Move) {
            Move move = (Move)obj;
            return initialSquareID == move.getInitialSquareID() && destinationSquareID == move.getDestinationSquareID() && moves == move.getMoves();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 3 * initialSquareID + 5 * destinationSquareID + 7 * moves;
    }

    @Override
    public String toString() {
        return "initial square: " + initialSquareID + ", destination square: " + destinationSquareID;
    }
}
