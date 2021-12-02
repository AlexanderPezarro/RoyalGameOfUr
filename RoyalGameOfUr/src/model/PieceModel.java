package model;

public class PieceModel {
    private final int id;
    private final boolean isBlack;
    private boolean isFinished;
    
    public PieceModel(int id, boolean isBlack) {
        this.id = id;
        this.isBlack = isBlack;
        isFinished = false;
    }

    public int getId() {
        return id;
    }

    public boolean isBlack() {
        return isBlack;
    }

    public void setFinished() {
        isFinished = true;
    }

    public boolean isFinished() {
        return isFinished;
    }
}
