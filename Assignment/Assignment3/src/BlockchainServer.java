import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class BlockchainServer {

    public static void main(String[] args) {

        if (args.length != 3) {
            return;
        }

        int localPort = 0;
        int remotePort = 0;
        String remoteHost = null;

        try {
            localPort = Integer.parseInt(args[0]);
            remoteHost = args[1];
            remotePort = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            return;
        }

        Blockchain blockchain = new Blockchain();

        HashMap<ServerInfo, Date> serverStatus = new HashMap<ServerInfo, Date>();
        serverStatus.put(new ServerInfo(remoteHost, remotePort), new Date());

        PeriodicCommitRunnable pcr = new PeriodicCommitRunnable(blockchain);
        Thread pct = new Thread(pcr);
        pct.start();

        // start the heartbeat sending
        new Thread(new PeriodicHeartBeatRunnable(serverStatus, localPort)).start();

        // start the perioticprint runnable
        new Thread(new PeriodicPrinterRunnable(serverStatus)).start();

        new Thread(new PeriodicCatchUpRunnable(serverStatus)).start();

        new Thread(new PeriodicLatestHashSenderRunnable(serverStatus, blockchain, localPort)).start();
        
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(localPort);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(new BlockchainServerRunnable(clientSocket, blockchain, serverStatus)).start();
            }

        } catch (IllegalArgumentException e) {
        } catch (IOException e) {
        } finally {
            try {
                pcr.setRunning(false);
                pct.join();
                if (serverSocket != null)
                    serverSocket.close();
            } catch (IOException e) {
            } catch (InterruptedException e) {
            }
        }
    }
}
