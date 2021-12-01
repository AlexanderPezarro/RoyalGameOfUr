package rgou.view.panel;

import rgou.model.Savable;
import rgou.model.rules.Ruleset;
import rgou.view.gui.GUIStandards;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

public class RulesEditorPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private JButton exitButton;
    private JPanel editorPanel;
    private JSpinner dice1Spinner;
    private JSpinner dice2Spinner;
    private JSpinner piece1Spinner;
    private JSpinner piece2Spinner;
    private JSpinner teleportAmountSpinner;
    private JCheckBox exactFinishBox;
    private JCheckBox shareSpaceBox;
    private JCheckBox jumpOccupiedBox;
    private JCheckBox teleportSpecialHops;
    private JTextField nameBox;
    private JButton saveButton;

    private Dimension editorPanelSize;

    private static final int buttonWidth = 300;
    private static final int buttonHeight = 100;


    public RulesEditorPanel() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLayout((LayoutManager) null);
        this.exitButton = new JButton("Back to menu");
        this.exitButton.setBounds((Math.round((float) screenSize.getWidth() / 2) - 90), 20, 180, 40);
        exitButton.setForeground(GUIStandards.buttonFontColour);
        this.setBackground(GUIStandards.backgroundColour);
        this.add(exitButton);

        editorPanel = new JPanel();
        editorPanelSize = new Dimension((int) screenSize.getWidth() / 2, (int) screenSize.getHeight() / 2);
        editorPanel.setBounds((int) (screenSize.getWidth() - editorPanelSize.getWidth()) / 2, (int) (screenSize.getHeight() - editorPanelSize.getHeight()) / 2, (int) editorPanelSize.getWidth(), (int) editorPanelSize.getHeight());
        editorPanel.setBackground(GUIStandards.buttonColour);

        //Add necessary buttons
        FlowLayout f = new FlowLayout();
        f.setHgap(40);
        f.setVgap(20);
        f.setAlignment(FlowLayout.CENTER);
        editorPanel.setLayout(f);

        //Default ruleset to set default values
        setupEditor();

        this.add(editorPanel);
        displayRuleset(new Ruleset());
    }

    public void addChangePanelListener(ActionListener al) {
        exitButton.addActionListener(al);
        exitButton.setActionCommand("settings");
    }

    public void setupEditor() {
//Dice spinners
        Ruleset r = new Ruleset();
        dice1Spinner = new JSpinner();
        dice2Spinner = new JSpinner();
        for (JSpinner s : new JSpinner[]{dice1Spinner, dice2Spinner}) {
            SpinnerNumberModel nm = new SpinnerNumberModel();
            nm.setMinimum(Ruleset.MIN_DICES);
            nm.setMaximum(Ruleset.MAX_DICES);
            s.setModel(nm);
            JLabel l = new JLabel("Player" + (s == dice1Spinner ? 1 : 2) + "'s number of dice:");
            l.setFont(GUIStandards.buttonFont);
            s.setFont(GUIStandards.buttonFont);
            editorPanel.add(l);
            editorPanel.add(s);
        }

        //Piece spinners
        piece1Spinner = new JSpinner();
        piece2Spinner = new JSpinner();
        for (JSpinner s : new JSpinner[]{piece1Spinner, piece2Spinner}) {
            SpinnerNumberModel nm = new SpinnerNumberModel();
            nm.setMinimum(Ruleset.MIN_PIECES);
            nm.setMaximum(Ruleset.MAX_PIECES);
            s.setModel(nm);
            JLabel l = new JLabel("Player" + (s == piece1Spinner ? 1 : 2) + "'s number of pieces:");
            l.setFont(GUIStandards.buttonFont);
            s.setFont(GUIStandards.buttonFont);
            editorPanel.add(l);
            editorPanel.add(s);
        }

        teleportAmountSpinner = new JSpinner();
        teleportAmountSpinner.setFont(GUIStandards.buttonFont);
        SpinnerNumberModel nm = new SpinnerNumberModel();
        nm.setMinimum(Ruleset.MIN_TELEPORT);
        nm.setMaximum(Ruleset.MAX_TELEPORT);
        teleportAmountSpinner.setModel(nm);
        JLabel l = new JLabel("Teleport amount:");
        l.setFont(GUIStandards.buttonFont);
        editorPanel.add(l);
        editorPanel.add(teleportAmountSpinner);

        //Checkboxes
        exactFinishBox = new JCheckBox("Exact finish?");
        shareSpaceBox = new JCheckBox("Share space?");
        jumpOccupiedBox = new JCheckBox("Jump ahead if landing tile is occupied?");
        teleportSpecialHops = new JCheckBox("Teleport to special?");

        for (JCheckBox b : new JCheckBox[]{exactFinishBox, shareSpaceBox, jumpOccupiedBox, teleportSpecialHops}) {
            b.setFont(GUIStandards.buttonFont);
            b.setBackground(editorPanel.getBackground());
            editorPanel.add(b);
        }

        //Name
        nameBox = new JTextField();
        nameBox.setFont(GUIStandards.buttonFont);
        nameBox.setPreferredSize(new Dimension((int) 180, 40));
        editorPanel.add(nameBox);

        //Save
        saveButton = new JButton();
        saveButton.setFont(GUIStandards.buttonFont);
        saveButton.setBackground(GUIStandards.sendMessageColour);
        saveButton.setText("Save to file");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveCurrentRuleset();
            }
        });

        editorPanel.add(saveButton);
        //displayRuleset(r);
    }

    public void displayRuleset(Ruleset r){
        dice1Spinner.setValue(r.getDices(true));
        dice2Spinner.setValue(r.getDices(false));
        piece1Spinner.setValue(r.getPieces(true));
        piece2Spinner.setValue(r.getPieces(false));
        exactFinishBox.setSelected(r.isExactFinish());
        shareSpaceBox.setSelected(r.isShareSpace());
        jumpOccupiedBox.setSelected(r.isJumpOccupied());
        teleportSpecialHops.setSelected(r.isTeleportSpecialHops());
        teleportAmountSpinner.setValue(r.getTeleportAmount());
        nameBox.setText(r.getName());
    }

    public void saveCurrentRuleset() {
        Ruleset r = new Ruleset();
        r.setDices(true, (int) dice1Spinner.getValue());
        r.setDices(false, (int) dice2Spinner.getValue());
        r.setPieces(true, (int) piece1Spinner.getValue());
        r.setPieces(false, (int) piece2Spinner.getValue());
        r.setExactFinish(exactFinishBox.isSelected());
        r.setJumpOccupied(jumpOccupiedBox.isSelected());
        r.setShareSpace(shareSpaceBox.isSelected());
        r.setTeleportSpecialHops(teleportSpecialHops.isSelected());
        r.setTeleportAmount((int) teleportAmountSpinner.getValue());
        r.setName(nameBox.getText());
        try {
            r.saveToFile(r.toJsonString());
        } catch (Exception ee) {
            System.out.println(ee.getMessage());
        }

    }
}
