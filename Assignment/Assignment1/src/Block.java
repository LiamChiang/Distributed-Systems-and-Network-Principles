import java.util.ArrayList;
import java.util.Base64;
import java.security.MessageDigest;
import java.io.IOException;
import java.io.DataOutputStream;
import java.security.NoSuchAlgorithmException;
import java.io.ByteArrayOutputStream;

public class Block extends Transaction{
	private Block previousBlock;
	private byte[] previousHash;
	private ArrayList<Transaction> transactions;
	
	public Block() {
		transactions = new ArrayList<>();
	}
	
	public Block getPreviousBlock(){
		return previousBlock;
	}
	
	public byte[] getPreviousHash(){
		return previousHash;
	}
	
	public ArrayList<Transaction> getTransactions(){
		return transactions;
	}
	
	public void setPreviousBlock(Block previousBlock){
		this.previousBlock = previousBlock;
	}
	
	public void setPreviousHash(byte[] previousHash){
		this.previousHash = previousHash;
	}
	
	public void setTransactions(ArrayList<Transaction> transaction){
		this.transactions = transaction;
	}
	
	public String toString(){
		String cutOffRule = new String(new char[81]).replace("\0", "-") + "\n";
		String previousHashString =String.format("|PreviousHash:|%65s|\n", 
				Base64.getEncoder().encodeToString(previousHash));
		String hashString = String.format("|CurrentHash:|%66s|\n", 
				Base64.getEncoder().encodeToString(calculateHash()));
		String transactionsString = "";
		for(Transaction tx: transactions){
			transactionsString += tx.toString(); 
		}
		
		return "Block:\n"
				+cutOffRule
				+hashString
				+cutOffRule
				+transactionsString
				+cutOffRule
				+previousHashString
				+cutOffRule;
	}
	
	public byte[] calculateHash() {
		byte[] hash = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
		try{
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			DataOutputStream dos = new DataOutputStream(baos);

			if(previousBlock == null){
				previousHash = hash;
			}
			dos.write(getPreviousHash());
			for(int i = 0; i < transactions.size(); i++){
				dos.writeUTF("tx|"+transactions.get(i).getSender()+"|"+transactions.get(i).getContent());
			}
			
			byte[] bytes = baos.toByteArray();
			hash = digest.digest(bytes);
			
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		return hash;
	}
}
