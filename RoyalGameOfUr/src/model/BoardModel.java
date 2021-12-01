package model;

public class BoardModel {
    public static final int NO_PIECE = -1;
    public static final int BLACK_PIECE = 0;
    public static final int WHITE_PIECE = 1;

    private int[] squares;

    public static int getInitialSquare(boolean isBlack) {
        return isBlack ? 4 : 20;
    }

    public static int getFinishSquare(boolean isBlack) {
        return isBlack ? 5 : 21;
    }

    public BoardModel() {
        squares = new int[24];
        for (int i = 0; i < squares.length; i++) {
            squares[i] = NO_PIECE;
        }
    }

    /**
     * Checks whether the specified square has a piece on it, and if so whether that
     * piece is friendly or an enemy.
     * 
     * @param square  The index of the square that is being checked.
     * @param isBlack If the occupation is being checked from the black side or
     *                white side.
     * @return -1 if square is unoccupied, 0 if it is occupied by a friendly piece
     *         and 1 if it is occupied by an enemy piece.
     */
    public int isSquareOccupied(int square, boolean isBlack) {
        if (isBlack) {
            if (squares[square] == NO_PIECE) {
                return NO_PIECE;
            } else if (squares[square] == BLACK_PIECE) {
                return 0;
            } else {
                return 1;
            }
        } else {
            if (squares[square] == -1) {
                return NO_PIECE;
            } else if (squares[square] == BLACK_PIECE) {
                return 0;
            } else {
                return 1;
            }
        }
    }

    public int[] getBoard() {
        return squares;
    }
}
