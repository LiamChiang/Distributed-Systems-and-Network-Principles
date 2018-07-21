import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Date;
import java.util.Set;

public class PeriodicCatchUpRunnable implements Runnable{
    private HashMap<ServerInfo, Date> serverStatus;

    public PeriodicCatchUpRunnable(HashMap<ServerInfo, Date> ips) {
        this.serverStatus = ips;
    }

    @Override
    public void run() {
        try {
            // create socket with a timeout of 2 seconds
            for (ServerInfo ip: serverStatus.keySet()){
                Socket server = new Socket();
                server.connect(new InetSocketAddress(ip.getHost(), ip.getPort()), 2000);

                PrintWriter printWriter = new PrintWriter(server.getOutputStream(), true);
                
                // send the message forward
                printWriter.println("cu");
                printWriter.flush();

                // close printWriter and socket
                printWriter.close();
                server.close();
            }
            

        } catch (IOException e) {

        }
    }
}