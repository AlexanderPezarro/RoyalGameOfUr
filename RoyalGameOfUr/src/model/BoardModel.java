package model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

public class BoardModel {

    private final ArrayList<SquareModel> squares;
    private final ArrayList<SquareModel> rosseteSquares;
    private ArrayList<PieceModel> pieces;
    private ArrayList<PieceModel> blackPieces;
    private ArrayList<PieceModel> whitePieces;
    private HashSet<Integer> occupiedBlackSquareIDs;
    private HashSet<Integer> occupiedWhiteSquareIDs;

    public BoardModel(int numPieces) {
        squares = new ArrayList<>(24);
        // Add board squares with IDs from 0 to 23
        for (int i = 0; i < 24; i++) {
            squares.add(new SquareModel(i));
        }
        // Adds the predifined squares to the rossete square list
        rosseteSquares = new ArrayList<SquareModel>(
                Arrays.asList(squares.get(0), squares.get(6), squares.get(11), squares.get(16), squares.get(22)));

        // Sets the initial square models of both sides to be starting squares
        getInitialSquare(true).setStarting();
        getInitialSquare(false).setStarting();

        // Sets the final square models of both sides to be starting squares
        getFinishSquare(true).setFinishing();
        getFinishSquare(false).setFinishing();

        // Sets the rossete square models to be rossete squares
        getRosseteSquares().forEach(square -> square.setRossete());

        pieces = new ArrayList<>(numPieces * 2);
        blackPieces = new ArrayList<>(numPieces);
        whitePieces = new ArrayList<>(numPieces);

        for (int i = 0; i < numPieces * 2; i++) {
            // Creates half black and adds them to black initial square then other half
            // white and adds them to white initial square
            PieceModel piece = new PieceModel(i, i < numPieces);
            getInitialSquare(i < numPieces).addPiece(piece);

            pieces.add(piece);
            if (i < numPieces) {
                blackPieces.add(piece);
            } else {
                whitePieces.add(piece);
            }
        }

        occupiedBlackSquareIDs = new HashSet<>();
        occupiedWhiteSquareIDs = new HashSet<>();

        // Adds the initial square models IDs for both sides as all pieces start on the
        // same square
        occupiedBlackSquareIDs.add(getInitialSquare(true).getID());
        occupiedWhiteSquareIDs.add(getInitialSquare(false).getID());
    }

    public BoardModel(BoardModel board) {
        squares = new ArrayList<>(24);
        // Add board squares with IDs from 0 to 23
        for (int i = 0; i < 24; i++) {
            squares.add(new SquareModel(i));
        }
        // Adds the predifined squares to the rossete square list
        rosseteSquares = new ArrayList<SquareModel>(
                Arrays.asList(squares.get(0), squares.get(6), squares.get(11), squares.get(16), squares.get(22)));

        // Sets the initial square models of both sides to be starting squares
        getInitialSquare(true).setStarting();
        getInitialSquare(false).setStarting();

        // Sets the final square models of both sides to be starting squares
        getFinishSquare(true).setFinishing();
        getFinishSquare(false).setFinishing();

        // Sets the rossete square models to be rossete squares
        getRosseteSquares().forEach(square -> square.setRossete());

        pieces = new ArrayList<>(board.getPieces().size());
        blackPieces = new ArrayList<>(board.getPieces().size()/2);
        whitePieces = new ArrayList<>(board.getPieces().size()/2);
        occupiedBlackSquareIDs = new HashSet<>();
        occupiedWhiteSquareIDs = new HashSet<>();

        for (PieceModel pieceModel : board.getPieces()) {
            PieceModel piece = new PieceModel(pieceModel.getID(), pieceModel.isBlack());
            squares.get(pieceModel.getCurrentSquareID()).addPiece(piece);

            if (piece.isBlack()) {
                blackPieces.add(piece);
                occupiedBlackSquareIDs.add(pieceModel.getCurrentSquareID());
            } else {
                whitePieces.add(piece);
                occupiedWhiteSquareIDs.add(pieceModel.getCurrentSquareID());
            }
        }

    }

    public SquareModel getInitialSquare(boolean isBlack) {
        return isBlack ? squares.get(4) : squares.get(20);
    }

    public SquareModel getFinishSquare(boolean isBlack) {
        return isBlack ? squares.get(5) : squares.get(21);
    }

    public ArrayList<SquareModel> getRosseteSquares() {
        return rosseteSquares;
    }

    public boolean isSquareOccupied(int squareID) {
        for (SquareModel square : squares) {
            if (square.getID() == squareID) {
                return square.isOccupied();
            }
        }
        System.out.println("isSquareOccupied - Invalid squareID given");
        return false;
    }

    public ArrayList<SquareModel> getBoard() {
        return squares;
    }

    public ArrayList<PieceModel> getPieces() {
        return pieces;
    }

    public ArrayList<PieceModel> getPieces(boolean isBlack) {
        return isBlack ? blackPieces : whitePieces;
    }

    public HashSet<Integer> getOccupiedSquareIDs(boolean isBlack) {
        return isBlack ? occupiedBlackSquareIDs : occupiedWhiteSquareIDs;
    }

    public boolean movePiece(int initialSquareID, int destinationSquareID, int availableMoves) {
        if (initialSquareID < 0 || initialSquareID > 24) {
            System.out.println("Invalid move - initial square out of bounds");
            return false;
        }
        if (destinationSquareID < 0 || destinationSquareID > 24) {
            System.out.println("Invalid move - destination square out of bounds");
            return false;
        }
        SquareModel initialSquare = squares.get(initialSquareID);
        SquareModel destinationSquare = squares.get(destinationSquareID);

        PieceModel piece = initialSquare.getPiece();

        if (piece == null) {
            System.out.println("Invalid move - no piece on initial square");
            return false;
        }

        int distance = Path.getDistanceBetweenSquares(piece.isBlack(), initialSquareID, destinationSquareID);
        if (distance == -1) {
            System.out.println("Invalid move - destination square unreachable by piece on initial square");
            return false;
        } else if (availableMoves < distance) {
            System.out.println("Invalid move - Not enough moves to move piece onto destination square");
            return false;
        }

        if (!isSquareOccupied(destinationSquareID)) {
            destinationSquare.addPiece(piece);
            initialSquare.removePiece(piece);

            if (piece.isBlack()) {
                if (!initialSquare.isOccupied()) {
                    occupiedBlackSquareIDs.remove(initialSquareID);
                }
                occupiedBlackSquareIDs.add(destinationSquareID);
            } else {
                if (!initialSquare.isOccupied()) {
                    occupiedWhiteSquareIDs.remove(initialSquareID);
                }
                occupiedWhiteSquareIDs.add(destinationSquareID);
            }

        } else {
            PieceModel destinationPiece = destinationSquare.getPiece();
            if (destinationPiece.isBlack() == piece.isBlack()) {
                if (destinationSquare.isFinishing()) {
                    destinationSquare.addPiece(piece);
                    initialSquare.removePiece(piece);

                    if (piece.isBlack()) {
                        occupiedBlackSquareIDs.add(destinationSquareID);
                    } else {
                        occupiedWhiteSquareIDs.add(destinationSquareID);
                    }

                } else {
                    System.out.println("Invalid move - destination square has piece of same colour");
                    return false;
                }
            } else {
                destinationSquare.removePiece(destinationPiece);
                destinationSquare.addPiece(piece);
                initialSquare.removePiece(piece);
                getInitialSquare(destinationPiece.isBlack()).addPiece(destinationPiece);

                if (piece.isBlack()) {
                    occupiedBlackSquareIDs.add(destinationSquareID);
                    occupiedWhiteSquareIDs.remove(destinationSquareID);
                } else {
                    occupiedWhiteSquareIDs.add(destinationSquareID);
                    occupiedBlackSquareIDs.remove(destinationSquareID);
                }
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() == this.getClass()) {
            BoardModel board = (BoardModel) obj;
            return occupiedBlackSquareIDs.equals(board.getOccupiedSquareIDs(true)) && occupiedWhiteSquareIDs.equals(board.getOccupiedSquareIDs(false));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return occupiedBlackSquareIDs.hashCode() + occupiedWhiteSquareIDs.hashCode();
    }
    
    @Override
    protected Object clone() {
        return new BoardModel(this);
    }
}
