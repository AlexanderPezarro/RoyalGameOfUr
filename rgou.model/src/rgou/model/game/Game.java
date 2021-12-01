package rgou.model.game;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import rgou.model.element.Board;
import rgou.model.element.Cell;
import rgou.model.rules.Ruleset;

import java.util.*;

public class Game {

    public enum PlayerType {
        HUMAN, EASY_AI, MEDIUM_AI, HARD_AI
    }

    private Board b;
    private Ruleset r;
    private int dices1;
    private int dices2;
    private boolean t1; // player1's turn

    public Game(Board b, Ruleset r) {
        this.b = b;
        this.r = r;

        dices1 = r.getDices(true);
        dices2 = r.getDices(false);

        b.getDrawPile(true).addPieces(true, r.getPieces(true));
        b.getDrawPile(false).addPieces(false, r.getPieces(false));

        t1 = true;
    }

    public ArrayList<Integer> rollDice(boolean player1) {
        Random r = new Random();
        ArrayList<Integer> dice = new ArrayList<>();

        for (int i = 0; i < getDices(player1); i++) {
            if (r.nextBoolean()) {
                dice.add(1);
            } else {
                dice.add(0);
            }
        }
        return dice;
    }


    /**
     * @param t1    is player 1's turn
     * @param roll  dice roll
     * @return empty MultiMap if no moves are valid
     */
    public Multimap<Cell, Cell> getValidMoves(boolean t1, int roll) {
        Multimap<Cell, Cell> moves = HashMultimap.create();
        if (roll == 0) {
            return moves;
        }

        List<Cell> path = b.getPath(t1);

        int maxCheck = path.size() - 2;
        if (r.isExactFinish()) {
            maxCheck = maxCheck + 1 - roll;
        }

        // moves of already placed pieces
        for (int i = 1; i <= maxCheck; i++) {
            if (path.get(i).getPieces(t1) == 0) {
                continue;
            } // nothing to move

            Cell startCell = path.get(i);
            Cell endCell = getValidEndCell(path, startCell, roll);
            if (endCell != null) {
                moves.put(startCell, endCell);
            }
        }
        if (b.getPath(t1).get(0).getPieces(t1) == 0) {
            return moves;
        }

        // place piece
        Cell drawPile = path.get(0);
        Cell endCell = getValidEndCell(path, drawPile, roll);
        if (endCell != null) {
            moves.put(drawPile, endCell);
        }
        return moves;
    }

    /**
     * Private, helper for getValidMoves(t1, roll)
     *
     * @return null if there is no possible valid move
     */
    private Cell getValidEndCell(List<Cell> path, Cell c, int roll) {
        int startI = c.getPathI();
        if (!r.isExactFinish() && (startI + roll) >= path.size()) {  // out of bounds at end
            return path.get(path.size() - 1);
        } else if (startI + roll <= 0) {    // out of bounds at start
            return path.get(0);
        } else {
            int endI = startI + roll;
            if (path.get(endI).isAvailable(t1, r.isShareSpace())) {
                return path.get(endI);
            } else if (r.isJumpOccupied()) {
                return getValidJump(path, startI, roll);
            } else {
                return null;
            }
        }
    }

    private Cell getValidJump(List<Cell> path, int startI, int roll) {
        int endI = startI + roll;
        if (path.get(endI).isAvailable(t1, r.isShareSpace())) {
            return path.get(endI);
        } else {
            return getValidJump(path, startI, roll + Integer.signum(roll));
        }
    }


    /**
     * @return follow-up moveset, empty if turn should be changed
     */
    public Multimap<Cell, Cell> playMove(Cell startCell, Cell endCell) {
        b.movePiece(t1, startCell, endCell);
        startCell.setUsed(true);

        // extra piece and dice effects
        if (endCell.isExtraDice()) {
            addDice(t1);
        }
        if (endCell.isExtraPiece()) {
            b.getDrawPile(t1).addPieces(t1, 1);
        }
        if (endCell.isExtraPieceOpp()) {
            b.getDrawPile(!t1).addPieces(!t1, 1);
        }

        Multimap<Cell, Cell> moves = HashMultimap.create();

        if (endCell.needsFollowUp()) {

            if (endCell.getMoveN() != 0) {  // move by N
                Cell c = getValidEndCell(b.getPath(t1), endCell, endCell.getMoveN());
                if (c != null) {
                    moves.put(endCell, c);
                }   // not added if invalid
            } else if (endCell.isRandMove()) { // random move
                int roll = Cell.rollRandomMove();
                Cell c = getValidEndCell(b.getPath(t1), endCell, roll);
                if (c != null) {
                    moves.put(endCell, c);
                }
            } else if (endCell.isReset()) { // reset
                moves.put(endCell, b.getPath(t1).get(0));
            } else if (endCell.isTeleport()) { // teleport
                int maxMove = r.getTeleportAmount();
                int curI = endCell.getPathI();
                for (int i = curI - maxMove; i <= curI + maxMove; i++) {
                    if (i == endCell.getPathI() // invalid teleports
                            && !(r.isTeleportSpecialHops() && b.getPath(t1).get(i).isTier1Special())) {
                        continue;
                    }
                    int roll = i - curI;
                    Cell c = getValidEndCell(b.getPath(t1), endCell, roll);
                    if (c != null) {
                        moves.put(endCell, c);
                    }
                }
            }
        }
        return moves;   //reroll shall be checked in GUI to ask for a new dice roll
    }

    public void changeTurn() {
        t1 = !t1;
        Cell[][] cells = b.getCells();
        for (int i = 0; i < cells.length; i++) {    // reset 'used' cell attribute
            for (int j = 0; j < cells[i].length; j++) {
                cells[i][j].setUsed(false);
            }
        }
    }

    public boolean won(boolean player1) {
        return b.geRemainingPieces(player1) == 0;
    }

    public boolean isPlayerOneTurn() {
        return t1;
    }

    public Board getBoard() {
        return b;
    }

    public Ruleset getRuleset() {
        return r;
    }

    public int getDices(boolean player1) {
        return (player1) ? dices1 : dices2;
    }

    public void addDice(boolean player1) {
        if (player1) {
            dices1++;
        } else {
            dices2++;
        }
    }

    // https://www.baeldung.com/java-deep-copy
    public Game deepCopy() {
        Gson g = new Gson();
        return g.fromJson(g.toJson(this), getClass());
    }
}

