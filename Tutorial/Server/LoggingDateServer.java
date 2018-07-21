import java.net.*;
import java.io.*;

public class LoggingDateServer{
	public static void main (String[] args){
		try{
			//TODO
			int id = 0;
			int portnumber = Integer.parseInt(args[0]);
			ServerSocket serverSocket = new ServerSocket(portnumber);
			while(true){
				//TODO
				Socket client = serverSocket.accept();
				PrintWriter pout = new PrintWriter(client.getOutputStream(), true);
				BufferedReader bin = new BufferedReader(new InputStreamReader(client.getInputStream()));
				File f;
				f = new File()
				BufferedWriter out = new BufferedWriter(new FileWriter(f));
				String date;
				pout.println(new java.util.Date().toString());
				out.write(new java.util.Date().toString() + " from " + client.getInetAddress().getHostName());
				out.flush();

				//TODO
				client.close();
			}

		}catch(IOException ioe) {
			System.err.println(ioe);
		}
	}
}