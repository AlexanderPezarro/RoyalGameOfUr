package rgou.view.panel;

import rgou.view.gui.GUIStandards;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.DimensionUIResource;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class HomePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private JPanel buttonsPanel;

    private JLabel titleLabel;
    private JButton playButton;
    private JButton settingsButton;
    private JButton networkButton;
    private JButton profileButton;
    private ExitButton exitButton;

    private static final int buttonWidth = 300;
    private static final int buttonHeight = 100;

    public HomePanel() { ;

        this.setLayout(null);

        buttonsPanel = new JPanel();

        buttonsPanel.setBorder(BorderFactory.createLineBorder(GUIStandards.buttonFontColour, 2));

        titleLabel = new JLabel("The Royal Game of Ur");
        playButton = new JButton("Play");
        settingsButton = new JButton("Settings");
        networkButton = new JButton("Network Multiplayer");
        profileButton = new JButton("Profile");
        exitButton = new ExitButton();
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        Component[] componentList = new Component[]{titleLabel, playButton, settingsButton, networkButton, profileButton, exitButton};

        buttonsPanel.setLayout(new GridLayout(componentList.length,1));
        for(Component b: componentList){
            if(JButton.class.isInstance(b)){
                ((JButton) b).setAlignmentY(SwingConstants.CENTER);
                b.setBackground(GUIStandards.buttonColour);
            }
            b.setFont(GUIStandards.buttonFont);
            b.setForeground(GUIStandards.buttonFontColour);
            buttonsPanel.add(b);
        }

        titleLabel.setFont(new Font(GUIStandards.buttonFont.getFontName(), Font.BOLD, GUIStandards.buttonFont.getSize()));
        buttonsPanel.setBackground(GUIStandards.backgroundColour);
        this.setBackground(GUIStandards.backgroundColour);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        buttonsPanel.setBounds((Math.round((float) screenSize.getWidth()) - buttonWidth) / 2,(Math.round((float) screenSize.getHeight()) - buttonHeight * componentList.length) / 2,buttonWidth,buttonHeight * componentList.length);
        this.add(buttonsPanel);
    }

    public void addChangePanelListener(ActionListener al) {
        playButton.addActionListener(al);
        playButton.setActionCommand("game");

        settingsButton.addActionListener(al);
        settingsButton.setActionCommand("settings");

        networkButton.addActionListener(al);
        networkButton.setActionCommand("network");

        profileButton.addActionListener(al);
        profileButton.setActionCommand("profile");

        exitButton.addActionListener(al);
        exitButton.setActionCommand("exit");
    }
    
}
