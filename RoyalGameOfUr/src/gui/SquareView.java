package gui;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;

/**
 * This class describes a square JLabel that can have a rossete and has a
 * changable border color.
 */
public class SquareView extends JLabel {

    private static final long serialVersionUID = 1L;

    // Gets the rossete image from the assets folder and scales it to 100x100
    private static final Icon rosetteIcon = new ImageIcon(
            new ImageIcon("src/assets/RosseteSquare.png").getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH));
    private Color backgroundColor = Color.LIGHT_GRAY;
    private Color borderColor = Color.BLACK;
    private boolean isHighlighted = false;
    private boolean isPainted;
    private int id;

    /**
     * Creates a SquareGUI label from the parameters given. The id is used to link
     * this object with its {@code Squares} counterpart. Sets the icon to a rossete
     * according to isRosette. If isPainted is false it leaves this square blank but
     * still clickable.
     * 
     * @param isRosette If this square should have a rossete or not
     * @param id        The unique id of this square
     * @param isPainted If this square should be blank or not
     */
    public SquareView(boolean isRosette, int id, boolean isPainted) {
        super();
        this.isPainted = isPainted;
        this.id = id;
        setOpaque(true);
        // If not painted the square will be a blank label with specified size
        if (isPainted) {
            if (isRosette) {
                setIcon(rosetteIcon);
            }
            setBackground(backgroundColor);
            setBorderColor(borderColor);
        }
        // Slightly larger than 100x100 as it had cliping artifacts when setting the
        // size to exactly 100x100
        setPreferredSize(new Dimension(104, 104));

        validate();
    }

    /**
     * Sets the border color to the given color. Used localy in
     * {@code setHighlighted}
     * 
     * @param borderColor The color to set the border to.
     */
    private void setBorderColor(Color borderColor) {
        if (isPainted) {
            setBorder(BorderFactory.createLineBorder(borderColor));
        }
    }

    /**
     * Sets the border color to a specifed highlighted color if isHighlighted is
     * true.
     * 
     * @param isHighlighted Whether to set the border to highlighted color or
     *                      regular.
     */
    public void setHighlighted(boolean isHighlighted) {
        this.isHighlighted = isHighlighted;
        if (isHighlighted) {
            setBorderColor(Color.RED);
        } else {
            setBorderColor(borderColor);
        }

    }

    /**
     * Returns if this square is currently highlighted.
     * 
     * @return True if this square is currently highlighted.
     */
    public boolean isHighlighted() {
        return isHighlighted;
    }

    /**
     * Returns this square's id
     * 
     * @return The id of this square.
     */
    public int getID() {
        return id;
    }
}
