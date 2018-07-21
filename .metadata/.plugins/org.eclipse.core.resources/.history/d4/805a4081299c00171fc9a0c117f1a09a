import java.util.concurrent.TimeUnit;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class BlockchainClient {

	public static void main(String[] args) {

		if (args.length != 2) {
			return;
		}

		String serverName = args[0];
		int portNumber = Integer.parseInt(args[1]);
		BlockchainClient bcc = new BlockchainClient();
		InputStream input = null;
		OutputStream output = null;

		try {
			Socket sock = new Socket(serverName, portNumber);
			while(true){
				input = sock.getInputStream();
				output = sock.getOutputStream();
				bcc.clientHandler(input, output);
				sock.close();
			}
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
			
		} catch (IOException e) {

			e.printStackTrace();

		}
	}

	public void clientHandler(InputStream serverInputStream, OutputStream serverOutputStream) {
		BufferedReader inputReader = new BufferedReader(new InputStreamReader(serverInputStream));
		PrintWriter outWriter = new PrintWriter(serverOutputStream, true);
		String lineSeparator = System.getProperty("line.separator");
		Scanner sc = new Scanner(System.in);
		try{
			while (sc.hasNextLine()) {
			
				String request = sc.nextLine();
				String res;
				outWriter.write(request+"\n");
				outWriter.flush();
				if(request.equals("cc")){
					inputReader.close();
					outWriter.close();
					sc.close();
				}
				while((res=inputReader.readLine())!=null&&inputReader.ready()==true){
					System.out.print(res+"\n");
				}
				System.out.println();
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		catch (Exception E){
				System.err.println("Already interrupted");
		}
	}
	// implement helper functions here if you need any.
}
