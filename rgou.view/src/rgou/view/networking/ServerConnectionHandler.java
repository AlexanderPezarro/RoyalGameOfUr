package rgou.view.networking;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerConnectionHandler extends Thread {
    // Set Variables
    private final ServerSocket serverSocket;
    private HostNetworkInterface owner;

    public ServerConnectionHandler(ServerSocket socket, HostNetworkInterface owner) {
        this.serverSocket = socket;
        this.owner = owner;
    }

    @Override
    public void run()
    {
        try
        {
            // Get socket from server socket
            if (serverSocket!=null) {
                // Detect Peer Connection
                Socket socket = serverSocket.accept();
                owner.clientConnect(socket);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
