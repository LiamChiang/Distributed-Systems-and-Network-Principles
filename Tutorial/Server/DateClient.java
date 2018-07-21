import java.net.*;
import java.io.*;

public class DateClient{
	public static void main (String[] args){
		try{
			//TODO
			String domain_name = args[0];	
			int portnumber = Integer.parseInt(args[1]);

			
			
				//TODO
				Socket server = new Socket(domain_name, portnumber);
				BufferedReader bin = new BufferedReader(new InputStreamReader(server.getInputStream()));
				PrintWriter pout = new PrintWriter(server.getOutputStream(), true);
				String line;
				while((line = bin.readLine())!=null){
					System.out.println(line);
				}
				//TODO
				bin.close();
				pout.close();
				server.close();
			

		}catch(IOException ioe) {
			System.err.println(ioe);
		}
	}
}