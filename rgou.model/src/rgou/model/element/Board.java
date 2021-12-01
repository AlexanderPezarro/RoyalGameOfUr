package rgou.model.element;

import java.util.List;

public class Board {
    private final int rowsN = 3;
    private final int colsN = 8;
    private Cell[] cells;
    private List<Cell> path1;
    private List<Cell> path2;
    private List<Piece> pieces;

    public Board() {
        this.cells = new Cell[20];
        int indx = 0;
        for (int i = 0; i < rowsN; i++) {
            for (int j = 0; j < colsN; j++) {
                if ((i != 1) && ((j == 4) || (j == 5))) { continue; }
                cells[indx] = new Cell(j, i, 0, false, false, false, false, false, false);
                indx++;
            }
        }
    }

    public void addPiece(boolean player1, int roll, Cell c) {
        if ((player1)) {
            path1.get(roll).setPiece(new Piece(true, false));
        } else {
            path2.get(roll).setPiece(new Piece(false, false));
        }
    }

    //fixme ruleset
    public void movePiece(Cell start, Cell end) {
        end.setPiece(start.getPiece());
        start.setPiece(null);
    }

    public List<Cell> getPath1() {
        return path1;
    }

    public void setPath1(List<Cell> path1) {
        this.path1 = path1;
    }

    public List<Cell> getPath2() {
        return path2;
    }

    public void setPath2(List<Cell> path2) {
        this.path2 = path2;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public void setPieces(List<Piece> pieces) {
        this.pieces = pieces;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int indx = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i != 1) && ((j == 4) || (j == 5))) {
                    sb.append("  ");
                    continue;
                }
                Piece p = cells[indx].getPiece();
                if (p == null) { sb.append("* "); }
                else {
                    if (p.isOwner1()) { sb.append("1 "); }
                    else { sb.append("2 "); }
                }
                indx++;
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        Board b = new Board();
        b.cells[1].setPiece(new Piece(true, false));
        System.out.println(b);
    }

    public int getRowsN() {
        return rowsN;
    }

    public int getColsN() {
        return colsN;
    }

    public Cell[] getCells() {
        return cells;
    }

}
