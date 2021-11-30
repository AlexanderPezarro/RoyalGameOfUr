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
}
