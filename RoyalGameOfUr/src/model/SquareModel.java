package model;

import java.util.ArrayList;

public class SquareModel {
    private final int id;
    private ArrayList<PieceModel> pieces;
    private boolean isStarting;
    private boolean isFinishing;
    private boolean isRossete;

    public SquareModel(int id) {
        this.id = id;
        pieces = new ArrayList<>();
        isStarting = false;
        isFinishing = false;
    }

    public int getID() {
        return id;
    }

    public ArrayList<PieceModel> getPieces() {
        return pieces;
    }

    public PieceModel getPiece() {
        if (isOccupied()) {
            return pieces.get(0);
        }
        return null;
    }

    public void removePiece(int pieceID) {
        if (!isStarting && !isFinishing) {
            pieces.clear();
        } else {
            pieces.removeIf(piece -> piece.getID() == pieceID);
        }
    }

    public void removePiece(PieceModel piece) {
        if (!isStarting && !isFinishing) {
            pieces.clear();
        } else {
            pieces.remove(piece);
        }
    }

    public void addPiece(PieceModel piece) {
        if (!isStarting && !isFinishing) {
            if (isOccupied()) {
                System.out.println("addPiece - Can't add piece as square is occupied");
            } else {
                piece.setCurrentSquareID(id);
                pieces.add(piece); 
            }
        } else {
            piece.setCurrentSquareID(id);
            pieces.add(piece);
        }
    }

    public boolean isStarting() {
        return isStarting;
    }

    public void setStarting() {
        isStarting = true;
    }

    public boolean isFinishing() {
        return isFinishing;
    }

    public void setFinishing() {
        isFinishing = true;
    }

    public boolean isRossete() {
        return isRossete;
    }

    public void setRossete() {
        isRossete = true;
    }

    public boolean isOccupied() {
        return pieces.size() != 0;
    }

    @Override
    protected Object clone() {
        SquareModel newModel = new SquareModel(id);
        if (isFinishing) {
            newModel.setFinishing();
        }
        if (isStarting) {
            newModel.setStarting();
        }
        if (isRossete) {
            newModel.setRossete();
        }
        return newModel;
    }
}
