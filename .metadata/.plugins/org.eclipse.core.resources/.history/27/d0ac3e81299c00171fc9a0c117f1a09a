import java.util.ArrayList;

public class Blockchain extends Block{
	private Block head;
	private ArrayList<Transaction> pool;
	private int length;
	
	private final int poolLimit = 3;
	
	public Blockchain(){
		pool = new ArrayList<>();
		length = 0;
	}
	
	
	//getters and setters
	public Block getHead() {
		return head;
		
	}
	public ArrayList<Transaction> getPool() {
		return pool;
		
	}
	public int getLength() {
		return length;
		
	}
	public void setHead(Block head){
		this.head = head;
		
	}
	public void setPool(ArrayList<Transaction> pool){
		this.pool = pool;
		
	}
	public void setLength(int length) {
		this.length = length;
		
	}
	
	//add Transaction
	public int addTransaction(String txString){
		//verify the format of transaction
		String[] data = txString.split("\\|"); 
		if(data.length != 3){
			// System.out.println("Invalid transaction length\n");
			return 0;
		}
		if(data[0].matches("tx") == false || data[1].matches("[a-z]{4}[0-9]{4}") == false || data[2].replaceAll(" ","").length() > 70){
			// System.out.println(txString);
			// System.out.println("Invalid transacition format\n");
			return 0;
		}
		//convert the transaction from String into Transaction
		//and add the transactions into pool
		Transaction testing = new Transaction();
		testing.setSender(data[1]);
		testing.setContent(data[2]);
		pool.add(testing);
		//create new block if pool's size has reached the limit
		if(pool.size() == poolLimit){
			
			Block newblock = new Block();
			
			newblock.setTransactions(pool);
			if(head == null){
				byte[] hash = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
				System.out.println("1");
				head = new Block();
				newblock.setPreviousHash(hash);
				setHead(newblock);
			}
			else{
				System.out.println("2");
				newblock.setPreviousBlock(head);
				newblock.setPreviousHash(head.calculateHash());
				setHead(newblock);
			}
			
			ArrayList<Transaction> newlist = new ArrayList<Transaction>(pool);
			setPool(newlist);
			length = length + 1;
			setLength(length);
			pool.clear();
			
			return 2;
		}
		return 1;
	}
	public String toString(){
		String cutOffRule = new String(new char[81]).replace("\0", "-") + "\n";
		String poolString = "";
		for(Transaction tx: pool){
			poolString += tx.toString();
		}
		String blockString = "";
		Block bl = head;
		while(bl != null){
			blockString += bl.toString();
			bl = bl.getPreviousBlock();
		}
		
		return "Pool:\n"
				+cutOffRule
				+poolString
				+cutOffRule
				+blockString;
	}
	
}
