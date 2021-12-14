package model;

public final class Move {
    private final int initialSquareID;
    private final int destinationSquareID;
    private final int moves;

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
}
