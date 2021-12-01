package rgou.view.game;

import rgou.model.element.Board;
import rgou.model.element.Cell;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.Collection;

import com.google.common.collect.Multimap;

/**
 * This class is used to represent the board
 */
public class BoardPanel extends JPanel {

    private final GamePanel gamePanel;// the parent game panel

    // dimensions of the screen
    private int width;
    private int height;

    // These are used to configure the actual grid of buttons
    private CellButton[][] cells;// the 3 by 8 grid of buttons
    private final int rowsN;
    private final int colsN;
    private int padding = 20;

    // the action listener used to decide when and what move to play
    private final ActionListener cellActionListener;

    // The keep track of the start and end cell for a move
    private Cell startCell = null;
    private Cell endCell = null;
    private boolean startSelected;

    /**
     * Class constructor
     * @param gamePanel the game panel
     * @param board the model board
     */
    public BoardPanel(GamePanel gamePanel, Board board) {
        super();

        //set dimesions
        this.width = gamePanel.getPanelSize().width;
        this.height = gamePanel.getPanelSize().height;

        this.gamePanel = gamePanel;
        this.cellActionListener = makeCellActionListener();

        rowsN = board.getRowsN();
        colsN = board.getColsN();

        //set size of the buttons
        CellButton.buttonSize = (int)(0.7 * width)/colsN;
        CellButton.imageSize = CellButton.buttonSize;

        setOpaque(false);
        setLayout(null);

        startSelected = false;

        // draw the grid of buttons
        drawBoard(board);
    }

    /**
     * This method converts coordinates to a cell
     * @param x x position
     * @param y y position
     * @return the cell at that position
     */
    public Cell getCellAt(int x, int y) {
        return cells[y][x].getCell();
    }

    /**
     * This method converts a cell into its coordinates
     * @param cell the cell
     * @retur an array of coordinates
     */
    public int[] getCoordsOfCell(Cell cell) {
        return new int[] { cell.getX(), cell.getY()};
    }

    /**
     * This method creates the action listener used by all grid buttons to play a move
     * @return an action listener
     */
    private ActionListener makeCellActionListener() {
        return e -> {
            // get the cell button clicked and its cell
            CellButton cellButton = (CellButton) e.getSource();
            Cell cell = cellButton.getCell();

            // if it is a disabled button then de-select the user choice
            if(cellButton.isDisabled()) {
                startSelected = false;
                // clear the selection visually
                disableAllButtons();
                showAllStartButtons();// diaply all available moves again
            }

            // else this button is part of a move
            else {
                // if the cell is marked a an end cell, and the start has already been selected , and it is not the
                // //start cell
                if(cellButton.isEnd() && startSelected && cell != startCell) {
                    // then play the move

                    endCell = cell;// set the end cell
                    // now we have both cells
                    resetAllButtons();// reset all buttons for the nxt move
                    startSelected = false;// set to false for next move
                    gamePanel.playMove(startCell, endCell);// call the game panel to play the move
                }
                // else if it is marked as a start cell
                else if(cellButton.isStart()){
                    // set the start cell
                    startCell = cell;
                    startSelected = true;// set it to true

                    //disable all buttons to prevent selecting someting else as the end cell
                    disableAllButtons();
                    cellButton.showStartColors();
                    showEndButtons(cellButton);// show the end cells for the start cell clicked
                }
            }
        };
    }

    /**
     * This method draw the inital grid of buttnos
     * @param board the model board
     */
    public void drawBoard(Board board) {
        cells = new CellButton[rowsN][colsN];
        for (Cell[] cellRow : board.getCells()) {
            for (Cell cell : cellRow) {
                addCellButton(cell);// add the cell as a button
            }
        }
    }

    /**
     * This method creates a button for the cell
     * and adds an action listener for it
     * @param cell the cell
     */
    private void addCellButton(Cell cell) {

        CellButton cellButton = new CellButton(cell);

        // add action listener
        cellButton.addActionListener(cellActionListener);
        addButton(cellButton);// add button to grid
    }

    /**
     * This method adds the button to the grid
     * @param button the button
     */
    private void addButton(CellButton button) {
        // get its x and y positions and scale it using the button size
        int y = button.getRow() * CellButton.buttonSize;
        int x = button.getCol() * CellButton.buttonSize;

        // set the bounds for the button
        button.setBounds(padding + x, padding +  y, CellButton.buttonSize, CellButton.buttonSize);

        // add the button to the 2D array of buttons
        cells[button.getRow()][button.getCol()] = button;
        add(button);// add button to the panel

    }

    /**
     * This method updates the buttons of the board
     * @param board the board
     */
    public void updateBoard(Board board) {
        for (Cell[] cellRow : board.getCells()) {
            for (Cell cell : cellRow) {
                // get the row and column of the button
                int row = cell.getY();
                int col = cell.getX();
                // update cell button
                cells[row][col].updateCellButton(cell);
            }
        }
    }

    /**
     * Sets all buttons to disabled
     */
    public void disableAllButtons() {
        for (int i = 0; i < rowsN; i++) {
            for (int j = 0; j < colsN; j++) {
                // set the button to disabled
                cells[i][j].disableButton();
            }
        }
    }

    /**
     * Resets all the buttons
     */
    public void resetAllButtons() {
        for (int i = 0; i < rowsN; i++) {
            for (int j = 0; j < colsN; j++) {
                // reset the button
                cells[i][j].resetButton();
            }
        }
    }

    /**
     * Shos all end buttons for a given start button
     * @param startButton the start button
     */
    public void showEndButtons(CellButton startButton) {
        for (Cell endCell: startButton.getEndCells()) {// for each end cell
            if(endCell != null) {
                cells[endCell.getY()][endCell.getX()].showEnd();// show the button as an end button
            }
        }
    }

    /**
     * Shows all start buttons
     */
    public void showAllStartButtons() {
        for (int i = 0; i < rowsN; i++) {
            for (int j = 0; j < colsN; j++) {
                cells[i][j].showIfStart();// shows the button as a start if it is a start
            }
        }
    }

    /**
     * Sets all start and end buttons given the valid moves
     * @param moves the valid moves
     */
    public void setStartButtons(Multimap<Cell, Cell> moves) {
        for (int i = 0; i < rowsN; i++) {
            for (int j = 0; j < colsN; j++) {
                // get end cells for the current cell button
                Collection<Cell> endCells = moves.get(cells[i][j].getCell());

                if(!endCells.isEmpty()) {// if there are end cells
                    // store the end cells in the cell button
                    cells[i][j].setEndCells(endCells);
                    cells[i][j].setStart();// mark the cell button as a start button

                    for (Cell endCell: endCells) {
                        if(endCell != null) {
                            // for each of the end cells, mark their button as an end button
                            cells[endCell.getY()][endCell.getX()].setAsEnd();
                        }
                    }
                }
            }
        }
    }
}
