package rgou.view.networking;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ChatHub {
    /*
        LACK OF COMMENTS HERE DUE TO THIS BEING A DECREMENTED FEATURE FOR TESTING NETWORKING!
     */


    private static final Color buttonColour = new Color(220, 219, 203);
    private static final Font buttonFont = new Font("Sylfaen", Font.PLAIN, 20);
    private static final Color buttonFontColour = new Color(28, 24, 1);
    private static final Color backgroundColour = new Color(243, 241, 219);

    private static final int buttonWidth = 300;
    private static final int buttonHeight = 100;

    private JFrame boxFrame = new JFrame();

    private NetworkPlayer owner;

    private JTextArea chats = new JTextArea();

    public ChatHub(NetworkPlayer owner) {
        this.owner = owner;
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setBorder(BorderFactory.createLineBorder(buttonFontColour, 2));

        boxFrame.setSize(500,500);

        JTextField textBox = new JTextField(20);
        textBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textBox.getText();
            }
        });

        chats.setBackground(Color.lightGray);
        chats.setSize(20,100);
        chats.setEditable(false);

        JButton send = new JButton("Send");
        send.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textBox.getText();
                textBox.setText("");
                owner.sendMessage("chat",text);
                addMessage("You",text);
            }
        });

        Component[] componentList = new Component[]{chats, textBox, send};
        buttonsPanel.setLayout(new GridLayout(componentList.length, 1));
        Component[] var2 = componentList;
        int var3 = componentList.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            Component b = var2[var4];
            if (JButton.class.isInstance(b)) {
                ((JButton)b).setAlignmentY(0.0F);
                b.setBackground(buttonColour);
            }

            b.setFont(buttonFont);
            b.setForeground(buttonFontColour);
            buttonsPanel.add(b);
        }

        // Show Chat
        boxFrame.add(buttonsPanel);
        boxFrame.pack();
        boxFrame.setVisible(true);
    }

    public void addMessage(String sender, String message) {
        String msg = sender + ": " + message;
        chats.append(msg+"\n");
    }

}
