package rgou.view.networking;

import rgou.view.gui.GUI;
import rgou.view.gui.GUIStandards;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class NetworkPanel extends JPanel {
    // Set Variables
    private static final long serialVersionUID = 1L;
    private JPanel buttonsPanel;
    private JLabel titleLabel;
    private JButton hostButton;
    private JButton joinButton;
    private JButton exitButton;
    private static final int buttonWidth = 300;
    private static final int buttonHeight = 100;

    public NetworkPanel() {

        // Set buttons
        this.setLayout((LayoutManager)null);
        this.buttonsPanel = new JPanel();
        this.buttonsPanel.setBorder(BorderFactory.createLineBorder(GUIStandards.buttonFontColour, 2));
        this.titleLabel = new JLabel("LAN Play");
        this.hostButton = new JButton("Host");
        this.joinButton = new JButton("Join Host");
        this.exitButton = new JButton("Back to menu");
        this.titleLabel.setHorizontalAlignment(0);
        Component[] componentList = new Component[]{this.titleLabel, this.hostButton, this.joinButton, this.exitButton};
        this.buttonsPanel.setLayout(new GridLayout(componentList.length, 1));
        Component[] var2 = componentList;
        int var3 = componentList.length;

        // Add buttons to screen

        for(int var4 = 0; var4 < var3; ++var4) {
            Component b = var2[var4];
            if (JButton.class.isInstance(b)) {
                ((JButton)b).setAlignmentY(0.0F);
                b.setBackground(GUIStandards.buttonColour);
            }

            b.setFont(GUIStandards.buttonFont);
            b.setForeground(GUIStandards.buttonFontColour);
            this.buttonsPanel.add(b);
        }

        this.titleLabel.setFont(new Font(GUIStandards.buttonFont.getFontName(), 1, GUIStandards.buttonFont.getSize()));
        this.buttonsPanel.setBackground(GUIStandards.backgroundColour);
        this.setBackground(GUIStandards.backgroundColour);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.buttonsPanel.setBounds((Math.round((float)screenSize.getWidth()) - 300) / 2, (Math.round((float)screenSize.getHeight()) - 100 * componentList.length) / 2, 300, 100 * componentList.length);
        this.add(this.buttonsPanel);

        hostButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        LANHost hostPanel = GUI.getHostPanel();
                        hostPanel.startHost();
                    }
                }
        );

        joinButton.addActionListener(
                new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        LANHost hostPanel = GUI.getHostPanel();
                        hostPanel.startHost();
                    }
                }
        );

    }

    public void addChangePanelListener(ActionListener al) {
        exitButton.addActionListener(al);
        exitButton.setActionCommand("home");
        hostButton.addActionListener(al);
        hostButton.setActionCommand("host");
        joinButton.addActionListener(al);
        joinButton.setActionCommand("peer");
    }
}
