package rgou.view.profile;

import rgou.model.profile.Profile;
import rgou.view.gui.GUIStandards;
import rgou.view.panel.InputPanel;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.plaf.DimensionUIResource;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

public class ProfilePanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private JPanel profileGrid;
    private JButton[] profileButtons;
    private InputPanel inputPanel;
    private JButton exitButton;

    private JLabel[] infoLabels = new JLabel[4];

    /**
     * Constructor
     */
    public ProfilePanel() {
        this.setLayout(null);
        this.setBackground(GUIStandards.backgroundColour);
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // All profiles to be displayed
        List<Profile> profiles = Profile.loadAllProfiles();

        /*
        Mainpanel - contains following two panels
        profilePanel - where selected profile's info is displayed
        profileGrid - where user can select profile
         */
        mainPanel.setBounds(screenSize.width / 5, 0, 3 * screenSize.width / 5, screenSize.height);
        mainPanel.setBorder(new LineBorder(GUIStandards.gamePanelsBackgroundColour, 4));
        JPanel profilePanel = new JPanel();
        profilePanel.setLayout(new GridLayout(infoLabels.length + 1, 1));
        profilePanel.setBackground(GUIStandards.backgroundColour);

        int perRow = (int) Math.ceil(Math.sqrt(Profile.MAX_PROFILES + 1));
        profileButtons = new JButton[Profile.MAX_PROFILES];

        profileGrid = new JPanel();
        profileGrid.setLayout(new GridLayout(perRow, perRow));


        Profile currentProfile = Profile.getCurrentUser();
        String currentUsername = "";
        if (currentProfile != null) {
            currentUsername = currentProfile.getName();
        }

        // Display all stored profiles
        for (int i = 0; i < perRow * perRow; i++) {
            if (i == perRow * perRow - 1) {
                inputPanel = new InputPanel();
                inputPanel.getInputButton().addActionListener(newProfileActionListener());
                profileGrid.add(inputPanel);
                continue;
            }
            JButton b = new JButton();
            Color borderColor = profileGrid.getBackground();
            if (i < profiles.size()) {
                Profile p = profiles.get(i);
                // Give current user a red border
                if (currentUsername.equals(p.getName())) {
                    borderColor = GUIStandards.gameBackgroundColour;
                }
                b.setText(p.getName());
                b.addActionListener(miniPanelActionListener());
            }
            b.setBackground(GUIStandards.gamePanelsBackgroundColour);
            b.setFont(new Font(b.getFont().getName(), b.getFont().getStyle(), 24));
            ;
            b.setBorder(new LineBorder(borderColor, 4));
            profileGrid.add(b);
            profileButtons[i] = b;

        }

        mainPanel.add(profilePanel);
        mainPanel.add(profileGrid);
        this.exitButton = new JButton("Back to menu");
        this.exitButton.setBackground(GUIStandards.buttonColour);
        profilePanel.add(exitButton);
        // Display selected profile's info e.g. name, time created, games won etc.
        for (int i = 0; i < infoLabels.length; i++) {
            infoLabels[i] = new JLabel("", SwingConstants.CENTER);
            infoLabels[i].setFont(GUIStandards.buttonFont);
            profilePanel.add(infoLabels[i]);
        }
        setInfoLabels(Profile.getCurrentUser());
        this.add(mainPanel);


    }

    /**
     * Updates the info labels with info from profile p
     * @param p
     */
    private void setInfoLabels(Profile p) {
        if (p == null) return;
        infoLabels[0].setText("Name: " + p.getName());
        infoLabels[1].setText("Created at: " + p.getTimeCreated());
        int wins = p.getCompletedGames(true).size();
        int losses = p.getCompletedGames(false).size();
        double wl;
        if (wins != 0 && losses != 0) {
            wl = wins / losses;
        }
        else if(wins > losses){
            wl = 1;
        }
        else {
            wl = 0;
        }
        infoLabels[2].setText("Number of games played: " + (wins + losses));
        infoLabels[3].setText("Win/Loss: " + wins + "/ " + losses + " (" + Math.round(wl * 100) + "%)");
    }

    /**
     * What happens when a user selects a different profile
     * @return
     */
    private ActionListener miniPanelActionListener() {
        return e -> {
            JButton b = (JButton) e.getSource();
            try {
                // Set new current user
                Profile.fromFile(b.getText()).setCurrentUser();
                // Draw new border around newly selected user
                updateProfileButtonBorders();
                // Display info of newly selected user
                setInfoLabels(Profile.getCurrentUser());
            } catch (Exception ee) {
                System.out.println("ERROR SETTING CURRENT USER AS " + b.getText());
            }
        };
    }

    /**
     *
     * @return ActionListener for creating new profile
     */
    private ActionListener newProfileActionListener() {
        return e -> {
            try {
                String name = inputPanel.getInputBox().getText();
                new Profile(name, new ArrayList<>()).toFile();
                inputPanel.getInputBox().setText("");
                addToProfileButtons(name);
            } catch (Exception ee) {
                System.out.println("ERROR SETTING CURRENT USER AS " + inputPanel.getInputBox().getText());
            }
        };
    }

    /**
     * Draws border around selected profile
     */
    private void updateProfileButtonBorders() {
        String currentUserName = Profile.getCurrentUser().getName();
        for (JButton b : profileButtons) {
            Color borderColor = profileGrid.getBackground();
            if (b.getText().equals(currentUserName)) {
                borderColor = GUIStandards.gameBackgroundColour;
            }
            b.setBackground(GUIStandards.gamePanelsBackgroundColour);
            b.setBorder(new LineBorder(borderColor, 4));
        }
    }

    /**
     * Adds profile to buttons
     * @param name
     */
    private void addToProfileButtons(String name) {
        for (JButton b : profileButtons) {
            if (b.getText().equals("")) {
                b.setText(name);
                b.addActionListener(miniPanelActionListener());
                return;
            }
        }
    }

    public void addChangePanelListener(ActionListener al) {
        exitButton.addActionListener(al);
        exitButton.setActionCommand("home");
    }

}
