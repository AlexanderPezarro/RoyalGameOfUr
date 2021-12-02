package model;

import java.util.ArrayList;

public class BoardModel {

    private ArrayList<SquareModel> squares;
    private ArrayList<PieceModel> pieces;

    public BoardModel(int numPieces) {
        squares = new ArrayList<>(24);
        for (int i = 0; i < 24; i++) {
            squares.add(new SquareModel(i));
        }
        getInitialSquare(true).setStarting();
        getInitialSquare(false).setStarting();

        getFinishSquare(true).setFinishing();
        getFinishSquare(false).setFinishing();

        pieces = new ArrayList<>(numPieces * 2);
        for (int i = 0; i < numPieces * 2; i++) {
            // Creates half black and adds them to black initial square then other half
            // white and adds them to white initial square
            PieceModel tempPiece = new PieceModel(i, i < numPieces);
            getInitialSquare(i < numPieces).addPiece(tempPiece);

            pieces.add(tempPiece);
        }
    }

    public SquareModel getInitialSquare(boolean isBlack) {
        return isBlack ? squares.get(4) : squares.get(20);
    }

    public SquareModel getFinishSquare(boolean isBlack) {
        return isBlack ? squares.get(5) : squares.get(21);
    }

    public boolean isSquareOccupied(int squareID) {
        for (SquareModel square : squares) {
            if (square.getID() == squareID) {
                return square.isOccupied();
            }
        }
        System.out.println("isSquareOccupied - Invalid squareID given");
        return false;
    }

    public ArrayList<SquareModel> getBoard() {
        return squares;
    }

    public ArrayList<PieceModel> getPieces() {
        return pieces;
    }

    public void movePiece(int initialSquareID, int destinationSquareID, int availableMoves) {
        if (initialSquareID < 0 || initialSquareID > 24) {
            System.out.println("Invalid move - initial square out of bounds");
            return;
        }
        if (destinationSquareID < 0 || destinationSquareID > 24) {
            System.out.println("Invalid move - destination square out of bounds");
            return;
        }
        SquareModel initialSquare = squares.get(initialSquareID);
        SquareModel destinationSquare = squares.get(destinationSquareID);

        PieceModel piece = initialSquare.getPiece();

        if (piece == null) {
            System.out.println("Invalid move - no piece on initial square");
            return;
        }

        int distance = Path.getDistanceBetweenSquares(piece.isBlack(), initialSquareID, destinationSquareID);
        if (distance == -1) {
            System.out.println("Invalid move - destination square unreachable by piece on initial square");
            return;
        } else if (availableMoves < distance) {
            System.out.println("Invalid move - Not enough moves to move piece onto destination square");
            return;
        }

        if (!isSquareOccupied(destinationSquareID)) {
            destinationSquare.addPiece(piece);
            initialSquare.removePiece(piece);
        } else {
            PieceModel destinationPiece = destinationSquare.getPiece();
            if (destinationPiece.isBlack() == piece.isBlack()) {
                System.out.println("Invalid move - destination square has piece of same colour");
                return;
            } else {
                destinationSquare.addPiece(piece);
                initialSquare.removePiece(piece);
                getInitialSquare(destinationPiece.isBlack()).addPiece(piece);
            }
        }
    }
}
