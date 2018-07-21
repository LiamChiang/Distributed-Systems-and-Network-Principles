import java.io.*;
import java.net.Socket;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;

public class PeriodicLatestHashSenderRunnable implements Runnable {
	private HashMap<ServerInfo, Date> serverStatus;
	private Blockchain blockchain;
	private int localPort;

	public PeriodicLatestHashSenderRunnable(HashMap<ServerInfo, Date> serverStatus, Blockchain blockchain, int localPort) {
		// System.out.println("Init");
		this.serverStatus = serverStatus;
		this.blockchain = blockchain;
		this.localPort = localPort;
	}

	private List<ServerInfo> randomlyPickOnePeer() {
		List<ServerInfo> randomServerInfos = new ArrayList<>();
		Set<ServerInfo> serverInfos = this.serverStatus.keySet();

		if (serverInfos.size() <= 5) {
			for (ServerInfo serverInfo: serverInfos) {
				randomServerInfos.add(serverInfo);
			}
		} else {
			List<ServerInfo> allServerInfos = new ArrayList<>();
			for (ServerInfo serverInfo: serverInfos) {
				allServerInfos.add(serverInfo);
			}

			Random rand = new Random();
			int count = 0;
			Set<Integer> set = new HashSet<Integer>();
			while (count < 5) {
				int randomIndex = (int)(Math.random() * allServerInfos.size());
				if (!set.contains(randomIndex)) {
					set.add(randomIndex);
					randomServerInfos.add(allServerInfos.get(randomIndex));
					++count;
				}
			}
		}
		return randomServerInfos;
	}


	@Override
	public void run() {
		while (true) {

			List<ServerInfo> randomServerInfos = this.randomlyPickOnePeer();
			
			ArrayList<Thread> threadArrayList = new ArrayList<>();
            for (ServerInfo ip: randomServerInfos) {
            	if (this.blockchain.getHead()!= null && this.blockchain.getHead().calculateHash() != null) {
            		String hash = Base64.getEncoder().encodeToString(this.blockchain.getHead().calculateHash());
            		String lbmessage = "lb|" + this.localPort + "|" + this.blockchain.getLength() + "|" + hash;
                	Thread thread = new Thread(new BlockchainClientRunnable(ip, lbmessage));
                	threadArrayList.add(thread);
                	thread.start();
            	}
            }

            for (Thread thread : threadArrayList) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                }
            }

            // sleep for two seconds
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
		}
	}
}