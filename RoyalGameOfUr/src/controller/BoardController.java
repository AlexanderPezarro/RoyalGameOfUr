package controller;

import model.BoardModel;

public class BoardController {
    
    private BoardModel board;

    public BoardController() {
        board = new BoardModel();
    }

    public BoardModel getModel() {
        return board;
    }

    public void movePiece(int initialSquare, int destinationSquare) {
        if (initialSquare < 0 || initialSquare > 24) {
            System.out.println("Invalid move - initial square out of bounds");
            return;
        }
        if (destinationSquare < 0 || destinationSquare > 24) {
            System.out.println("Invalid move - destination square out of bounds");
            return;
        }
    }
}
