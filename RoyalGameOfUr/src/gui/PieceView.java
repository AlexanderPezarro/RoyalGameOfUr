package gui;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import java.awt.Dimension;

/**
 * This class creates a JLabel with one of two icons that looks like a playing
 * piece.
 */
public class PieceView extends JLabel {

    private static final long serialVersionUID = 1L;
    private static final Icon whitePlayingPieceIcon = new ImageIcon("src/assets/WhitePlayingPiece.png");
    private static final Icon blackPlayingPieceIcon = new ImageIcon("src/assets/BlackPlayingPiece.png");
    private int id;
    private boolean isBlack;

    /**
     * Creates a new playing piece with the specified id and the icon depending on
     * isBlack.
     * 
     * @param id      The id of this playing piece
     * @param isBlack If true uses the black piece icon otherwise the white piece
     *                icon
     */
    public PieceView(int id, boolean isBlack) {
        super();

        if (isBlack) {
            setIcon(blackPlayingPieceIcon);
        } else {
            setIcon(whitePlayingPieceIcon);
        }
        setPreferredSize(new Dimension(50, 50));
        this.id = id;
        this.isBlack = isBlack;
    }

    /**
     * Returns this playing piece's id
     * 
     * @return The id of this playing piece
     */
    public int getID() {
        return id;
    }

    /**
     * Returns if this playing piece is a black or white piece
     * 
     * @return True if this playing piece is a black and false otherwise
     */
    public boolean isBlackPiece() {
        return isBlack;
    }
}
