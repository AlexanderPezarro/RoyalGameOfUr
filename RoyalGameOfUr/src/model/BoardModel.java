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

    public boolean isSquareOccupied(int square) {
        return squares[square] == -1;
    }

    public int[] getBoard() {
        return squares;
    }
}
