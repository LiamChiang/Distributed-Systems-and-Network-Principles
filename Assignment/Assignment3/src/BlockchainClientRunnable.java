import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;

public class BlockchainClientRunnable implements Runnable{

    private String host;
    private int port;
    private String message;

    public BlockchainClientRunnable(ServerInfo ip, String message) {
        this.host = ip.getHost();
        this.port = ip.getPort();
        this.message = message;
    }

    @Override
    public void run() {
        try {
            // create socket with a timeout of 2 seconds
            Socket server = new Socket();
            server.connect(new InetSocketAddress(host, port), 2000);
            PrintWriter printWriter = new PrintWriter(server.getOutputStream(), true);
            
            // send the message forward
            printWriter.println(message);
            printWriter.flush();

            // close printWriter and socket
            printWriter.close();
            server.close();

        } catch (IOException e) {

        }
    }
}