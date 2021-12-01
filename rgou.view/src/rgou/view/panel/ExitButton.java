package rgou.view.panel;

import javax.swing.*;
import java.awt.event.ActionListener;

public class ExitButton extends JButton {

    private final static String DEFAULT_TEXT = "Exit";

    public ExitButton() {
        this.addActionListener(makeExitButtonActionListener());
        this.setText(DEFAULT_TEXT);
    }

    private ActionListener makeExitButtonActionListener() {
        return e -> {
            System.exit(0);
        };
    }
}
