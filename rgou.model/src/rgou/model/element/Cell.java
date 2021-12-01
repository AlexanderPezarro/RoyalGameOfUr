package rgou.model.element;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Random;


public class Cell {
    public enum Type {
        Pile, Normal
    }

    private int x;
    private int y;
    private boolean beenSet = false;
    private int pathI;
    private Type type;
    private int pieces1;
    private int pieces2;

    private boolean used;

    // tier1
    @Expose
    @SerializedName("a")
    private boolean safe;
    @Expose
    @SerializedName("b")
    private boolean reset;
    @Expose
    @SerializedName("c")
    private boolean teleport;
    @Expose
    @SerializedName("d")
    private int moveN;
    @Expose
    @SerializedName("e")
    private boolean randMove;
    // tier 2
    @Expose
    @SerializedName("f")
    private boolean reroll;
    @Expose
    @SerializedName("g")
    private boolean extraPiece;
    @Expose
    @SerializedName("h")
    private boolean extraDice;
    @Expose
    @SerializedName("i")
    private boolean extraPieceOpp;

    @Expose
    @SerializedName("z")
    private boolean surprise;

    protected Cell(int x, int y) {
        this.x = x;
        this.y = y;
    }

    protected void setType(int x, int y) {
        type = (y != 1 && (x == 4 || x == 5)) ? Type.Pile : Type.Normal;
    }

    public static int rollRandomMove() {
        Random r = new Random();
        return (int) Math.round(r.nextGaussian() * 14 / 3); // [-14, 14]
    }

    public boolean isAvailable(boolean t1, boolean shareSpace) {
        return (!safe || getPieces(!t1) == 0)  // capture but is safe
                && (shareSpace || getPieces(t1) == 0 || type == Type.Pile);  // no sharing
    }

    // tier 1 specials can not be teleported to if Ruleset.teleportSpecialHops is false
    public boolean isTier1Special() {
        return safe || teleport || moveN != 0 || randMove;
    }

    public boolean needsFollowUp() {
        return !used && (moveN != 0 || randMove || reset || teleport || reroll);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Cell) {
            Cell c = (Cell) o;
            return x == c.x && y == c.y;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 * x + 7 * y;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        sb.append("x:");
        sb.append(String.format("% 2d", x));
        sb.append("y:");
        sb.append(String.format("% 2d", y));
        sb.append("|");
        sb.append("p1:");
        sb.append(pieces1);
        sb.append("|p2:");
        sb.append(pieces2);
        sb.append("]");

        return sb.toString();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getPathI() {
        return pathI;
    }

    public void setPathI(int pathI) {
        this.pathI = pathI;
    }

    public Type getType() {
        return type;
    }

    public int getMoveN() {
        return moveN;
    }

    public void setMoveN(int moveN) {
        this.moveN = moveN;
        safe = false;
        reset = false;
        teleport = false;
        randMove = false;
    }

    public boolean isSafe() {
        return safe;
    }

    public void setSafe(boolean safe) {
        this.safe = safe;
        reset = false;
        teleport = false;
        moveN = 0;
        randMove = false;
    }

    public boolean isReroll() {
        return reroll;
    }

    public void setReroll(boolean reroll) {
        this.reroll = reroll;
    }

    public boolean isRandMove() {
        return randMove;
    }

    public void setRandMove(boolean randMove) {
        this.randMove = randMove;
        safe = false;
        reset = false;
        teleport = false;
        moveN = 0;
    }

    public boolean isTeleport() {
        return teleport;
    }

    public void setTeleport(boolean teleport) {
        this.teleport = teleport;
        safe = false;
        reset = false;
        moveN = 0;
        randMove = false;
    }

    public boolean isReset() {
        return reset;
    }

    public void setReset(boolean reset) {
        this.reset = reset;
        safe = false;
        teleport = false;
        moveN = 0;
        randMove = false;
    }

    public boolean isSurprise() {
        return surprise;
    }

    public void setSurprise(boolean surprise) {
        this.surprise = surprise;   //fixme other abilities can be set, but should be ignored
    }

    public boolean isExtraPiece() {
        return extraPiece;
    }

    public void setExtraPiece(boolean extraPiece) {
        this.extraPiece = extraPiece;
    }

    public boolean isExtraDice() {
        return extraDice;
    }

    public void setExtraDice(boolean extraDice) {
        this.extraDice = extraDice;
    }

    public boolean isExtraPieceOpp() {
        return extraPieceOpp;
    }

    public void setExtraPieceOpp(boolean extraPieceOpp) {
        this.extraPieceOpp = extraPieceOpp;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public int getPieces(boolean owner1) {
        return (owner1) ? pieces1 : pieces2;
    }

    // fixme temporarily made it public (change back to protected)
    public void addPieces(boolean player1, int n) {
        if (player1) {
            pieces1 += n;
        } else {
            pieces2 += n;
        }
    }

    public void removePieces(boolean player1, int n) {
        addPieces(player1, -n);
    }

    public void setXY(int x, int y) {
        if (beenSet) return;
        this.x = x;
        this.y = y;
        beenSet = true;
    }
}
