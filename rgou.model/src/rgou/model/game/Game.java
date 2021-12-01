package rgou.model.game;

import rgou.model.element.Board;

public class Game {
    private Board b;
    //    private Ruleset r;
    private boolean playerOneTurn;

    public Game() {
        initialiseBoard();
        playerOneTurn = true;
    }

    // Change to make tiles...
    private void initialiseBoard() {
        b = new Board();
    }

    public void changeTurns() {
        playerOneTurn = !playerOneTurn;
    }

    public boolean isPlayerOneTurn() {
        return playerOneTurn;
    }

    public Board getBoard() {
        return b;
    }
}