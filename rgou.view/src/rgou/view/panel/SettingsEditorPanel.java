package rgou.view.panel;

import rgou.model.Savable;
import rgou.model.element.Board;
import rgou.model.element.PathBoard;
import rgou.model.rules.Ruleset;
import rgou.view.gui.GUIStandards;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsEditorPanel extends JPanel {

    private JButton exitButton;
    private JPanel mainPanel;
    private Dimension screenSize;
    private Dimension mainPanelSize;
    private JButton[][] selectorButtons = new JButton[3][];

    private JScrollPane ruleScroll = new JScrollPane();
    private JScrollPane boardScroll = new JScrollPane();
    private JScrollPane pathScroll = new JScrollPane();


    public SettingsEditorPanel() {
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setLayout((LayoutManager) null);
        this.exitButton = new JButton("Back to menu");
        this.exitButton.setBounds((Math.round((float) screenSize.getWidth() / 2) - 90), 20, 180, 40);
        exitButton.setForeground(GUIStandards.buttonFontColour);
        this.setBackground(GUIStandards.backgroundColour);

        mainPanel = new JPanel();
        mainPanelSize = new Dimension((int) screenSize.getWidth() / 2, (int) screenSize.getHeight() / 2);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBounds((int) (screenSize.getWidth() - mainPanelSize.getWidth()) / 2, (int) (screenSize.getHeight() - mainPanelSize.getHeight()) / 2, (int) mainPanelSize.getWidth(), (int) mainPanelSize.getHeight());

        setRuleScroll(getScroll(new Ruleset()));
       setBoardScroll(getScroll(new Board()));
        setPathScroll(getScroll(new PathBoard()));

        mainPanel.add(ruleScroll);
        mainPanel.add(boardScroll);
        mainPanel.add(pathScroll);

        this.add(exitButton);
        this.add(mainPanel);
    }

    public JScrollPane getScroll(Savable sampleObject) {
        JPanel listPanel = new JPanel();
        String currentFilename = sampleObject.getCurrentPath();
        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        java.util.List<Savable> itemList = sampleObject.getAllFromDir();
        selectorButtons[0] = new JButton[itemList.size()];
        for (int i = 0; i < itemList.size(); i++) {
            JButton b = new JButton(itemList.get(i).getName());
            int final_i = i;
            JButton final_b = b;
            final_b.setPreferredSize(new Dimension(scroll.getWidth() / 5, (int) Math.round(scroll.getHeight() * 0.7)));
            final_b.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        itemList.get(final_i).setCurrent();
                        for (JButton bb : selectorButtons[0]) {
                            bb.setBorder(new LineBorder(bb.getBackground(), 4));
                        }
                        final_b.setBorder(new LineBorder(GUIStandards.gamePanelsBackgroundColour, 4));
                    } catch (Exception ee) {
                    }
                }
            });
            if (itemList.get(i).getFilePath().equals(currentFilename)) {
                final_b.setBorder(new LineBorder(GUIStandards.gamePanelsBackgroundColour, 4));
            }
            selectorButtons[0][i] = final_b;
            final_b.setPreferredSize(new Dimension(100,100));
            listPanel.add(final_b);
        }
        return scroll;
    }


    public void addChangePanelListener(ActionListener al) {
        exitButton.addActionListener(al);
        exitButton.setActionCommand("settings");
    }

    public void setRuleScroll(JScrollPane ruleScroll) {
        this.ruleScroll = ruleScroll;
    }

    public void setBoardScroll(JScrollPane boardScroll){
        this.boardScroll = boardScroll;
    }

    public void setPathScroll(JScrollPane pathScroll){
        this.pathScroll = pathScroll;
    }

    public void update(){
        setRuleScroll(getScroll(new Ruleset()));
        setBoardScroll(getScroll(new Board()));
        setPathScroll(getScroll(new PathBoard()));
        ruleScroll.revalidate();
        ruleScroll.repaint();
    }
}
