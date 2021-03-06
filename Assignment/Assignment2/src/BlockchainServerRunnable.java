import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;


public class BlockchainServerRunnable implements Runnable{

    private Socket clientSocket;
    private Blockchain blockchain;

    public BlockchainServerRunnable(Socket clientSocket, Blockchain blockchain) {
    	this.blockchain = blockchain;
    	this.clientSocket = clientSocket;
    }

    public void run() {
        InputStream input = null;
		OutputStream output = null;
		try{
			input = clientSocket.getInputStream();
			output = clientSocket.getOutputStream();
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(input));
			PrintWriter outWriter = new PrintWriter(output, true);
			String request;
			while((request=inputReader.readLine())!= null){
				
				if(request.startsWith("tx") && !request.equals("pb") && !request.equals("cc")){
					boolean out = blockchain.addTransaction(request);
					if(out == false){
						outWriter.print("Rejected\n\n");
						outWriter.flush();
					}
					else if(out == true || out == true){
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
					inputReader.close();
					clientSocket.close();
				}
				else {
					outWriter.print("Error\n\n");
					outWriter.flush();
				}
			}

		}
		catch(IOException e) {
			System.err.println("Socket Connection reset");
		}
    }
    
    // implement any helper method here if you need any
}
