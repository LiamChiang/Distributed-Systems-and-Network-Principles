import java.net.*;
import java.io.*;

public class DateServer{
	public static void main (String[] args){
		try{
			//TODO
			int portnumber = Integer.parseInt(args[0]);
			ServerSocket serverSocket = new ServerSocket(portnumber);
			while(true){
				//TODO
				Socket client = serverSocket.accept();
				PrintWriter pout = new PrintWriter(client.getOutputStream(), true);
				pout.println(new java.util.Date().toString());
				//TODO
				client.close();
			}

		}catch(IOException ioe) {
			System.err.println(ioe);
		}
	}
}