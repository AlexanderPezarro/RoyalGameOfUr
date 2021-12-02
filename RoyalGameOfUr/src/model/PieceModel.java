package model;

public class PieceModel {
    private final int id;
    private final boolean isBlack;
    private boolean isFinished;
    private int currentSquareID;
    
    public PieceModel(int id, boolean isBlack) {
        this.id = id;
        this.isBlack = isBlack;
        isFinished = false;
        currentSquareID = -1;
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

    public int getCurrentSquareID() {
        return currentSquareID;
    }

    public void setCurrentSquareID(int currentSquareID) {
        this.currentSquareID = currentSquareID;
    }

}
