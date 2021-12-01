package rgou.view.panel;

import rgou.view.gui.GUIStandards;

import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;
import java.awt.event.ActionListener;

public class InputPanel extends JPanel {
    private JTextField inputBox;
    private JButton inputButton;

    public InputPanel(){

        inputBox = new JTextField();
        inputBox.setMaximumSize(new DimensionUIResource(400, 32));
        inputBox.setBackground(GUIStandards.buttonColour);

        inputButton = new JButton();
        inputButton.setMaximumSize(new DimensionUIResource(80,32));
        inputButton.setBackground(GUIStandards.sendMessageColour);

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        this.add(inputBox);
        this.add(inputButton);
    }


    public JTextField getInputBox(){
        return this.inputBox;
    }

    public JButton getInputButton(){
        return this.inputButton;
    }

}
