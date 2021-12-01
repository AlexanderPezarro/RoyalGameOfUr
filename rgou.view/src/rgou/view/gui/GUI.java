package rgou.view.gui;

import rgou.model.game.Game;
import rgou.view.panel.GamePanel;
import rgou.view.panel.HomePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class GUI extends JFrame {

    private static final long serialVersionUID = 1L;

    private final JPanel container;

    private final HomePanel home;
    private final GamePanel game;
    private final JPanel settings;
    private final JPanel network;

    private CardLayout cardLayout;

    public GUI() {

        setExtendedState(MAXIMIZED_BOTH);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        container = new JPanel();

        cardLayout = new CardLayout();
        container.setLayout(cardLayout);

        home = new HomePanel();
        game = new GamePanel();
        
        settings = new JPanel();
        network = new JPanel();

        setUpActionListeners();

        container.add(home, "home");
        container.add(game, "game");
        container.add(settings, "settings");
        container.add(network, "network");

        add(container);
    }

    private void setUpActionListeners() {

        ActionListener cpl = makeChangePanelListener();

        home.addChangePanelListener(cpl);
        game.addChangePanelListener(cpl);
    }

    private ActionListener makeChangePanelListener() {

        return e -> {
            String panelName = e.getActionCommand();

            cardLayout.show(container, panelName);
        };
    }


}
