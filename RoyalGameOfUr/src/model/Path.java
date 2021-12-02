package model;

import java.util.HashSet;

public class Path {
    private final static int[] BLACK_PATH = { 4, 3, 2, 1, 0, 8, 9, 10, 11, 12, 13, 14, 15, 7, 6, 5 };
    private final static int[] WHITE_PATH = { 20, 19, 18, 17, 16, 8, 9, 10, 11, 12, 13, 14, 15, 23, 22, 21 };

    public static HashSet<Integer> getPossibleMoves(boolean isBlack, int currentSquare, int moves) {
        if (moves == 0) {
            return new HashSet<Integer>(0);
        }
        if (isBlack) {
            for (int i = 0; i < BLACK_PATH.length; i++) {
                if (BLACK_PATH[i] == currentSquare) {
                    HashSet<Integer> possibleMoves = new HashSet<>(moves);
                    for (int j = 1; j <= moves; j++) {
                        if (i + j < BLACK_PATH.length) {
                            possibleMoves.add(BLACK_PATH[i + j]);
                        } else {
                            break;
                        }
                    }
                    return possibleMoves;
                }
            }
        } else {
            for (int i = 0; i < WHITE_PATH.length; i++) {
                if (WHITE_PATH[i] == currentSquare) {
                    HashSet<Integer> possibleMoves = new HashSet<>(moves);
                    for (int j = 1; j <= moves; j++) {
                        if (i + j < WHITE_PATH.length) {
                            possibleMoves.add(WHITE_PATH[i + j]);
                        } else {
                            break;
                        }
                    }
                    return possibleMoves;
                }
            }
        }
        return new HashSet<Integer>(0);
    }

    public static int getDistanceBetweenSquares(boolean isBlack, int square1, int square2) {
        if (square1 == square2) {
            return 0;
        }

        int square1Index = -1;
        int square2Index = -1;

        if (isBlack) {
            for (int i = 0; i < BLACK_PATH.length; i++) {
                if (i == square1) {
                    square1Index = i;
                }
                if (i == square2) {
                    square2Index = i;
                }
                if (square1Index != -1 && square2Index != -1) {
                    break;
                }
            }

        } else {
            for (int i = 0; i < WHITE_PATH.length; i++) {
                if (i == square1) {
                    square1Index = i;
                }
                if (i == square2) {
                    square2Index = i;
                }
                if (square1Index != -1 && square2Index != -1) {
                    break;
                }
            }
        }

        if (square1Index == -1 || square2Index == -1) {
            return -1;
        }
        return Math.abs(square1Index - square2Index);
    }
}
