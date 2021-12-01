package rgou.view.panel;

import javax.swing.*;
import java.awt.event.*;

public class HomePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private JButton playButton;
    private JButton settingsButton;
    private JButton networkButton;

    public HomePanel() {
        this.add(new JLabel("This is the Home Page"));

        playButton = new JButton("Play");
        settingsButton = new JButton("Settings");
        networkButton = new JButton("Network");

        this.add(playButton);
        this.add(settingsButton);
        this.add(networkButton);
    }

    public void addChangePanelListener(ActionListener al) {
        playButton.addActionListener(al);
        playButton.setActionCommand("game");

        settingsButton.addActionListener(al);
        settingsButton.setActionCommand("settings");

        networkButton.addActionListener(al);
        networkButton.setActionCommand("network");
    }
    
}
