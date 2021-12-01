package rgou.view.networking;

import java.net.Socket;
import java.util.concurrent.TimeUnit;

public class HeartBeat extends Thread {
    private Boolean forceStop = false;
    private Boolean alive = true;
    private LANreciever owner;
    private Socket socket;

    public HeartBeat(LANreciever owner, Socket socket) {
        this.owner = owner;
        this.socket = socket;
    }

    public void forceStop() {
        forceStop = true;
    }

    public void resetLife() {
        alive = true;
    }

    @Override
    public void run() {
        while (!forceStop && alive) {
            try {
                //System.out.println("Heart: " + alive);
                // Send heart beat and attempt death
                LANsender sender = new LANsender(socket);
                sender.messageHeader = "heartbeat";
                sender.messageContent = "null";
                sender.start();

                alive = false;

                // Wait 10 seconds
                try {
                    TimeUnit.SECONDS.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            catch (NullPointerException e) {
                // Lost Connection via socket
                break;
            }
        }

        // Tell quit on death
        owner.owner.otherQuit();
    }
}
