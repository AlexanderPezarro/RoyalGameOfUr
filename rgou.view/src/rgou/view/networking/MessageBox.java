package rgou.view.networking;

import javax.swing.*;
import javax.swing.plaf.DimensionUIResource;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import rgou.view.gui.*;
import rgou.view.panel.InputPanel;

public class MessageBox extends JPanel {
    public JTextArea messageBox;
    private JScrollPane scrollPane;
    private InputPanel inputPanel;
    public NetworkPlayer player;

    public MessageBox(){
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        messageBox = new JTextArea();
        messageBox.setAlignmentX(CENTER_ALIGNMENT);
        messageBox.setEditable(false);
        messageBox.setBackground(GUIStandards.backgroundColour);
        messageBox.setForeground(GUIStandards.buttonFontColour);

        inputPanel = new InputPanel();

        inputPanel.getInputButton().addActionListener(inputButtonActionListener());



        scrollPane = new JScrollPane(messageBox);
        this.add(scrollPane);
        this.add(inputPanel);
    }

    public void pushMessage(String msg){
        messageBox.append("(" + DateTimeFormatter.ofPattern("HH:mm:ss").format(LocalDateTime.now()) + ")\t" + msg + "\n");
        JScrollBar bar = scrollPane.getVerticalScrollBar();
        bar.setValue(bar.getMaximum());
    }

    private ActionListener inputButtonActionListener() {
        return e -> {
            String msg = inputPanel.getInputBox().getText();
            pushMessage("You: " + msg);
            inputPanel.getInputBox().setText("");

            // Try to send msg to opponent
            try {
                player.sendMessage("msg", msg);
            } catch (NullPointerException nullError) {;};
        };
    }
}
