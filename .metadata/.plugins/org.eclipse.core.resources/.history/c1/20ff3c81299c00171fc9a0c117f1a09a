import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class BlockchainServer {

	private Blockchain blockchain;

	public BlockchainServer() {
		blockchain = new Blockchain();
	}

	// getters and setters
	public void setBlockchain(Blockchain blockchain) {
		this.blockchain = blockchain;
	}

	public Blockchain getBlockchain() {
		return blockchain;
	}

	public static void main(String[] args) {
		if (args.length != 1) {
			return;
		}
		int portNumber = Integer.parseInt(args[0]);
		BlockchainServer bcs = new BlockchainServer();
		InputStream input = null;
		OutputStream output = null;
		try {
			ServerSocket serverSocket = new ServerSocket(portNumber);
			while(true){
				Socket client = serverSocket.accept();
				
				input = client.getInputStream();
				output = client.getOutputStream();
				bcs.serverHandler(input, output);
				
				
				client.close();
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void serverHandler(InputStream clientInputStream, OutputStream clientOutputStream) {

		BufferedReader inputReader = new BufferedReader(new InputStreamReader(clientInputStream));
		PrintWriter outWriter = new PrintWriter(clientOutputStream, true);
		try {
			String request;
			Blockchain blockchain = new Blockchain();
			while((request=inputReader.readLine())!= null){
				
				if(request.startsWith("tx") && !request.equals("pb") && !request.equals("cc")){				
					int out = blockchain.addTransaction(request);
					if(out == 0){
						outWriter.print("Rejected\n\n");
						outWriter.flush();
					}
					else if(out == 1 || out == 2){
						outWriter.print("Accepted\n\n");	
						outWriter.flush();
					}
				}
				else if(request.equals("pb")){
					outWriter.print(blockchain.toString()+"\n");
					outWriter.flush();
				}
				else if(request.equals("cc")){
					outWriter.close();
				}
				else {
					outWriter.print("Error\n\n");
					outWriter.flush();
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// implement helper functions here if you need any.
}
