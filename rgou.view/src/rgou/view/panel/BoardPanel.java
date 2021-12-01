package rgou.view.panel;

import rgou.model.element.Board;
import rgou.model.element.Cell;
import rgou.model.element.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class BoardPanel extends JPanel {

    private final GamePanel gamePanel;

    private CellButton[][] cells;
    private final int rowsN;
    private final int colsN;

    private final ActionListener cellActionListener;

    private int[] startCell = null;
    private int[] endCell = null;

    public BoardPanel(GamePanel gamePanel, Board board) {
        this.gamePanel = gamePanel;
        this.cellActionListener = makeCellActionListener();
        rowsN = board.getRowsN();
        colsN = board.getColsN();
        setLayout(new GridBagLayout());
        drawBoard(board);
    }

    private ActionListener makeCellActionListener() {
        return e -> {
            CellButton cellButton = (CellButton) e.getSource();
            if(startCell == null) {
                startCell = new int[]{cellButton.getRow(), cellButton.getCol()};
            }
            else {
                endCell = new int[]{cellButton.getRow(), cellButton.getCol()};
                // now we have both coordinates

                gamePanel.playMove(startCell[0], startCell[1], endCell[0], endCell[1]);

                // now set both to null
                startCell = null;
                endCell = null;
            }
        };
    }

    public void drawBoard(Board board) {
        cells = new CellButton[rowsN][colsN];

        for (Cell cell : board.getCells()) {
            setUpCell(cell);
        }
    }

    private void setUpCell(Cell cell) {
        int row = cell.getY();
        int col = cell.getX();

        CellButton cellButton = new CellButton(row, col);

        updateCell(cellButton, cell);

        // add action listener
        cellButton.addActionListener(cellActionListener);

        cells[row][col] = cellButton;
        addButton(cellButton);
    }

    private void addButton(CellButton button) {
        int x = button.getCol();
        int y = button.getRow();

        GridBagConstraints constraints = new GridBagConstraints();

        constraints.gridx = x * CellButton.cellSize;
        constraints.gridy = y * CellButton.cellSize;
        constraints.ipadx = CellButton.cellSize;
        constraints.ipady = CellButton.cellSize;

        add(button, constraints);

    }

    private void updateCell(CellButton cellButton, Cell cell) {
        int cellId = cell.getId();
        Piece piece = cell.getPiece();

        if(piece == null) {
            cellButton.setText(cellId + ": *");
        }
        else {
            if(piece.isOwner1()) {
                cellButton.setText(cellId + ": 1");
            }
            else {
                cellButton.setText(cellId + ": 2");
            }
        }
    }

    public void updateBoard(Board board) {
        for (Cell cell : board.getCells()) {
            int row = cell.getY();
            int col = cell.getX();

            updateCell(cells[row][col], cell);
        }
    }
}
