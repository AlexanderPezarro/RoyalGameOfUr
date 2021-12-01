package rgou.view.panel;

import javax.swing.*;

public class CellButton extends JButton {

    public static final int cellSize = 80;
    private final int row;
    private final int col;

    private int piecePlayer;

    public CellButton(int row, int col) {
        this.row = row;
        this.col = col;

        piecePlayer = 0;
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public void setPiecePlayer(int piecePlayer) {
        this.piecePlayer = piecePlayer;
        setIcon(new ImageIcon("images/" + piecePlayer + ".png"));
    }
}
