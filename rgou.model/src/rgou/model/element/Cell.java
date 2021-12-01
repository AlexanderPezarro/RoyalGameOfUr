package rgou.model.element;

public class Cell {
    int id;
    private int x;
    private int y;
    private int moveN;
    private boolean safe;
    private boolean reroll;
    private boolean randomMoveByN;
    private boolean teleport;
    private boolean reset; //fixme some are mutually exclusive
    private boolean finish;
    private Piece piece;

//    protected Cell(int x, int y, int id, Ruleset rules) {
//
//    }

    protected Cell(int x, int y, int moveN, boolean safe, boolean reroll, boolean randomMoveByN, boolean teleport, boolean reset, boolean finish) {
        this.x = x;
        this.y = y;
        this.safe = safe;
        this.reroll = reroll;
        this.randomMoveByN = randomMoveByN;
        this.teleport = teleport;
        this.reset = reset;
        this.finish = finish;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getMoveN() {
        return moveN;
    }

    public void setMoveN(int moveN) {
        this.moveN = moveN;
    }

    public boolean isSafe() {
        return safe;
    }

    public void setSafe(boolean safe) {
        this.safe = safe;
    }

    public boolean isReroll() {
        return reroll;
    }

    public void setReroll(boolean reroll) {
        this.reroll = reroll;
    }

    public boolean isRandomMoveByN() {
        return randomMoveByN;
    }

    public void setRandomMoveByN(boolean randomMoveByN) {
        this.randomMoveByN = randomMoveByN;
    }

    public boolean isTeleport() {
        return teleport;
    }

    public void setTeleport(boolean teleport) {
        this.teleport = teleport;
    }

    public boolean isReset() {
        return reset;
    }

    public void setReset(boolean reset) {
        this.reset = reset;
    }

    public boolean isFinish() {
        return finish;
    }

    public void setFinish(boolean finish) {
        this.finish = finish;
    }

    public Piece getPiece() {
        return piece;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }

    public int getId() {
        return id;
    }
}
