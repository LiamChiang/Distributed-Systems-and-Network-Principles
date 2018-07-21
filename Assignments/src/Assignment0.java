import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.ArrayList;

public class Assignment0 {

	public static void main(String []args) throws IOException {

		ArrayList<String> messages = new ArrayList<>();
		messages.add("12345");
		messages.add("12345");
		System.out.println(hashMessages(messages));
		/* you should see the hash value calculated is 
		 7ccQDEgOM+OWvega620WYCXJHs1w53wSumP329xFirw= */
	};
	
	public static String hashMessages(ArrayList<String> messages) throws IOException { 
		String hashString = "";
		try
		{
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
			DataOutputStream dos = new DataOutputStream(baos);

			for(int i = 0; i < messages.size(); i++){
				dos.writeUTF(messages.get(i));
			}

			byte[] bytes = baos.toByteArray();
			byte[] hash = digest.digest(bytes);
			hashString = Base64.getEncoder().encodeToString(hash);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return hashString; 
	};
};