package rgou.view.panel;

import rgou.view.gui.GUIStandards;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class SettingsPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JPanel buttonsPanel;
    private JLabel titleLabel;
    private JButton settingsEditorButton;
    private JButton ruleSet;
    private JButton boardEditor;
    private JButton pathEditor;
    private JButton exitButton;
    private static final int buttonWidth = 300;
    private static final int buttonHeight = 100;

    public SettingsPanel() {
        this.setLayout((LayoutManager)null);
        this.buttonsPanel = new JPanel();
        this.buttonsPanel.setBorder(BorderFactory.createLineBorder(GUIStandards.buttonFontColour, 2));
        this.exitButton = new JButton("Back to menu");
        this.settingsEditorButton = new JButton("Edit Settings");
        this.ruleSet = new JButton("Game Rules");
        this.boardEditor = new JButton("Board Editor");
        this.pathEditor = new JButton("Path Editor");
        this.titleLabel = new JLabel("Settings");
        this.titleLabel.setHorizontalAlignment(0);
        Component[] componentList = new Component[]{this.titleLabel, this.settingsEditorButton, this.ruleSet, this.boardEditor, this.pathEditor, this.exitButton};
        this.buttonsPanel.setLayout(new GridLayout(componentList.length, 1));
        Component[] var2 = componentList;
        int var3 = componentList.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            Component b = var2[var4];
            if (JButton.class.isInstance(b)) {
                ((JButton) b).setAlignmentY(0.0F);
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
        this.buttonsPanel.setBounds((Math.round((float) screenSize.getWidth()) - 300) / 2, (Math.round((float) screenSize.getHeight()) - 100 * componentList.length) / 2, 300, 100 * componentList.length);
        this.add(this.buttonsPanel);

    }

    public void addChangePanelListener(ActionListener al) {
        settingsEditorButton.addActionListener(al);
        settingsEditorButton.setActionCommand("settingsEditor");
        boardEditor.addActionListener(al);
        boardEditor.setActionCommand("boardEditor");
        ruleSet.addActionListener(al);
        ruleSet.setActionCommand("rulesEditor");
        pathEditor.addActionListener(al);
        pathEditor.setActionCommand("pathEditor");
        exitButton.addActionListener(al);
        exitButton.setActionCommand("home");
    }
}
