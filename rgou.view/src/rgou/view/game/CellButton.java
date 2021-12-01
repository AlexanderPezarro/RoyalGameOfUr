package rgou.view.game;

import rgou.model.element.Cell;
import rgou.model.element.Cell.Type;
import rgou.view.gui.GUIStandards;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static rgou.view.game.GamePanel.getImageFromFile;

/**
 * This class is a wrapper for the JButton class and each instance represents a cell of the board
 */
public class CellButton extends JButton {

    // sizes for the button and its image
    public static int buttonSize = 115;
    public static int imageSize = 115;

    // Colors for the buttons
    private final static Color normalColor = Color.WHITE;
    private final static Color startColor = new Color(0, 128, 128);
    private final static Color endColor = new Color(218,165,32);
    private final static Color startSelected =  new Color(62, 142, 222);
    private final static Color endSelected =  new Color(255,215,0);

    // Borders for the buttons
    private final static Border normalBorder = new LineBorder(null);
    private final static Border startBorder = new LineBorder(startColor, 3);
    private final static Border endBorder = new LineBorder(endColor, 3);

    // Flags for the button
    private boolean disabled;// true if it is a normal cell
    private boolean start;// true if it is the start cell for a valid move
    private boolean end;// true if it is the end cell of a valid move

    // The cell the this button represents
    private final Cell cell;
    private ArrayList<Cell> endCells;// the possible end cells of a move that starts with this button

    // its position in the grid of buttons
    private final int row;
    private final int col;

    // Images for the player pieces
    public static Image player1Image;
    public static Image player2Image;

    // Images for the cell abilities
    private static Image safeImage;
    private static Image teleportImage;
    private static Image resetImage;
    private static Image moveImage;
    private static Image rerollImage;
    private static Image diceImage;
    private static Image pieceImage;
    private static Image opponentPieceImage;
    private static BufferedImage numbers;// image for a tile set of numbers for the move by N ability

    /**
     * This method sets the images for the cell abilities if it has not been set already
     */
    private static void setImages() {

        if(numbers == null) {
            // use the method from GamePanel to load images with a specified height
            player1Image = getImageFromFile("player1", 2*imageSize/6);
            player2Image = getImageFromFile("player2", 2*imageSize/6);

            safeImage = getImageFromFile("safe", imageSize);
            teleportImage = getImageFromFile("teleport", imageSize);
            resetImage = getImageFromFile("reset", imageSize);
            moveImage = getImageFromFile("move", imageSize);
            rerollImage = getImageFromFile("reroll", imageSize);
            diceImage = getImageFromFile("dice", imageSize);
            pieceImage = getImageFromFile("piece", imageSize);
            opponentPieceImage = getImageFromFile("opponent_piece", imageSize);;

            try {
                numbers = ImageIO.read(new File("images/nums.png"));
            } catch (IOException e) {}
        }
    }

    /**
     * Class constructor
     * @param cell the cell this CellButton will represent
     */
    public CellButton(Cell cell) {
        super();
        this.cell = cell;
        this.row = cell.getY();
        this.col = cell.getX();
        this.disabled = true;

        // set images for the buttons if not already done
        setImages();

//        if not a normal cell, then hide it
        if(!cell.getType().equals(Type.Normal)) {
            setOpaque(false);
            setContentAreaFilled(true);
            setBorderPainted(false);
        }

        // update the button with the details of the cell
        updateCellButton(cell);
    }

    /**
     * This method sets the end cells for the button
     * @param endCells the collection of end cells
     */
    public void setEndCells(Collection<Cell> endCells) {
        this.endCells = new ArrayList<>(endCells);
    }

    /**
     * This method returns the list of end cells of the button
     * @return the list of end cells of the button
     */
    public ArrayList<Cell> getEndCells() {
        return endCells;
    }

    public Cell getCell() {
        return cell;
    }

    /**
     * This method updates the button using information from the cell it represents
     * @param cell the cell
     */
    public void updateCellButton(Cell cell) {

        // draw the cell button
        setLayout(null);
        setBackground(normalColor);
        setBorder(normalBorder);

        ImageIcon icon = createMergedIcon(cell);

        setIcon(icon);
    }

    /**
     * This method creates a merged image icon for the button based on the cell abilities
     * @param cell the cell
     * @return the merged icon
     */
    private ImageIcon createMergedIcon(Cell cell) {

        // create a buffered image to store the combined image
        BufferedImage combinedImage = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB);

        Graphics graphics = combinedImage.getGraphics();// get its graphics

        // FOr each ability, if true, add the image to the graphics of the combined image

        if(cell.isSafe()) {
            drawImage(graphics, safeImage);
        }

        else if(cell.isTeleport()) {
            drawImage(graphics, teleportImage);
        }

        else if(cell.isReset()) {
            drawImage(graphics, resetImage);
        }

        else if(cell.isRandMove()) {
            drawImage(graphics, moveImage);
        }

        else if (cell.getMoveN() != 0) {
            // determine the coordinates of the number based on N
            int n = Math.abs(cell.getMoveN());
            int y = 0;
            if(cell.getMoveN() < 0) {
                y = 157;
            }

            // get sub image from tile set
            Image number = numbers.getSubimage(157*n, y, 157, 157);
            drawImage(graphics, number);
        }

        if(cell.isReroll()) {
            drawImage(graphics, rerollImage);
        }

        else if (cell.isExtraDice()) {
            drawImage(graphics, diceImage);
        }

        else if(cell.isExtraPiece()) {
            drawImage(graphics, pieceImage);
        }

        else if (cell.isExtraPieceOpp()) {
            drawImage(graphics, opponentPieceImage);
        }

        // now add piece images if any

        if(cell.getPieces(true) > 0) {// add if has player 1 piece
            drawPieceImage(graphics, player1Image, cell.getPieces(true));
        }

        else if(cell.getPieces(false) > 0) {// else add if has player 2 piece
            drawPieceImage(graphics, player2Image, cell.getPieces(false));
        }

        graphics.dispose();

        return new ImageIcon(combinedImage);
    }

    /**
     * Draws the images for the pieces on the cell
     * @param graphics the graphics for the combined image
     * @param image the image of the piece
     * @param pieces the number of pieces to be drawn
     */
    private void drawPieceImage(Graphics graphics, Image image, int pieces) {
        int center = imageSize/2;// center of the cell square

        for (int i = 0; i < pieces; i++) {// for each piece, draw the piece s few pixels above the piece under it
            graphics.drawImage(image, center - image.getWidth(null) / 2, center - (i*(imageSize/20)) - image.getHeight(null) / 2, null);
        }
    }

    /**
     * This method draws an image onto the graphics of a buffered image
     * @param graphics the graphics
     * @param image the image
     */
    private void drawImage(Graphics graphics, Image image) {
        int center = imageSize/2;
        graphics.drawImage(image, center - image.getWidth(null)/2, center - image.getHeight(null)/2, null);
    }

    public int getRow() {
        return row;
    }

    public int getCol() {
        return col;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public boolean isStart() {
        return start;
    }

    public boolean isEnd() {
        return end;
    }

    public void setAsEnd() {
        this.end = true;
    }

    /**
     * Sets the button to a normal button with no special properties like start or end
     */
    public void resetButton() {
        disabled = true;
        start = false;
        end = false;

        // For the cells on the gap their buttons should be hidden
        if(cell.getType().equals(Type.Normal)) {
            setBackground(normalColor);
            setBorder(normalBorder);
        }
        else {
            // hide the button
            setContentAreaFilled(false);
            setBorderPainted(false);
        }
    }

    /**
     * Mark the button as a start button
     */
    public void setStart() {
        start = true;
        disabled = false;
        setBorderPainted(true);
        //set its color to blue
        setBackground(startSelected);
        setBorder(startBorder);
    }

    /**
     * Disable the button
     */
    public void disableButton() {
        disabled = true;
        // set its border and content to normal
        if(cell.getType().equals(Type.Normal)) {
            setBorder(normalBorder);
            setBackground(normalColor);
        }
        else {
            // for a cell in the gap, hide it
            setContentAreaFilled(false);
            setBorderPainted(false);
        }
    }

    /**
     * Show the button if it is a start button
     */
    public void showIfStart() {
        disabled = false;
        if(start) {
            showStartColors();
        }
    }

    /**
     * Display the button as a start button
     */
    public void showStartColors() {
        setBorderPainted(true);
        // paint it blue
        setBackground(startSelected);
        setBorder(startBorder);
    }

    /**
     * Display the button as an end button
     */
    public void showEnd() {
        disabled = false;
        // paint it gold
        setBorderPainted(true);
        setBackground(endSelected);
        setBorder(endBorder);
    }
}
