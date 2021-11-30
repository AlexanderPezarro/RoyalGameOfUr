package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Font;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainMenu extends JFrame {

    public static void main(String[] args) {
        new MainMenu();
    }

    public MainMenu() {
        setTitle("Menu");
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel backgroundPanel = new JPanel(new GridBagLayout());
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel titleLabel = new JLabel("Royal Game of Ur");
        titleLabel.setFont(new Font(null, Font.PLAIN, 70));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 60, 0);
        backgroundPanel.add(titleLabel, gbc);

        JButton singlePlayerButton = new JButton("Single Player");
        singlePlayerButton.setFont(new Font(null, Font.PLAIN, 40));
        singlePlayerButton.setFocusPainted(false);
        singlePlayerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new BoardView();
                dispose();
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(5, 0, 5, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(singlePlayerButton, gbc);

        JButton multiplayerButton = new JButton("Multiplayer");
        multiplayerButton.setFont(new Font(null, Font.PLAIN, 40));
        multiplayerButton.setFocusPainted(false);
        multiplayerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        buttonPanel.add(multiplayerButton, gbc);

        JButton helpButton = new JButton("How to play");
        helpButton.setFont(new Font(null, Font.PLAIN, 40));
        helpButton.setFocusPainted(false);
        helpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // TODO
            }
        });
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        buttonPanel.add(helpButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.fill = GridBagConstraints.NONE;
        backgroundPanel.add(buttonPanel, gbc);

        add(backgroundPanel);
        setVisible(true);
    }
}
