package rgou.view.networking;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class LANsender extends Thread {

    // Set Variables
    private Socket socket;

    public String messageHeader;
    public String messageContent;

    public LANsender(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        sendMessage(messageHeader,messageContent);
    }

    public void sendMessage(String type, String message) {
        // Get input and output streams
        try {
            PrintStream out = new PrintStream (socket.getOutputStream());
            String send = type+"-:-"+message;

            // Send Message
            out.println(send);
            //System.out.println("sent: "+send);

            // Close output stream
            if (type.equals("close")) {
                socket.close();
                out.close();
            }

            //System.out.println("Sent Message: "+send);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
