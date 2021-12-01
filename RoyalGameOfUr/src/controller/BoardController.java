package controller;

import model.BoardModel;
import model.Path;

public class BoardController {
    
    private BoardModel model;

    public BoardController() {
        model = new BoardModel();
    }

    public BoardModel getModel() {
        return model;
    }

    public void movePiece(int initialSquare, int destinationSquare, int totalNumberOfMoves) {
        if (initialSquare < 0 || initialSquare > 24) {
            System.out.println("Invalid move - initial square out of bounds");
            return;
        }
        if (destinationSquare < 0 || destinationSquare > 24) {
            System.out.println("Invalid move - destination square out of bounds");
            return;
        }

        int[] bored = model.getBoard();
        int piece = bored[initialSquare];

        if (piece == BoardModel.NO_PIECE) {
            System.out.println("Invalid move - no piece on initial square");
            return;
        }

        int distance = Path.getDistanceBetweenSquares(piece == BoardModel.BLACK_PIECE, initialSquare, destinationSquare);
        if (distance == -1) {
            System.out.println("Invalid move - destination square unreachable by piece on initial square");
            return;
        } else if(totalNumberOfMoves < distance) {
            System.out.println("Invalid move - Not enough moves to move piece onto destination square");
            return;
        }

        if (!model.isSquareOccupied(destinationSquare)) {
            bored[destinationSquare] = piece;
            bored[initialSquare] = BoardModel.NO_PIECE;
        } else {
            int destinationPiece = bored[destinationSquare];
            if (destinationPiece == piece) {
                System.out.println("Invalid move - destination square has piece of same colour");
                return;
            } else {
                // TODO: Replace enemy piece
            }
        }
    }
}
