import java.util.*;


public class BlockTest extends Blockchain{
	public static void main (String args[]){
		
//		 input:
//		 "tx|test0000|1";
//		 "tx|test0000|2";
//		 "tx|test0000|3";
//		 "tx|test0000|4";
//		 "tx|test0000|5";
//		 "tx|test0000|6";

		Blockchain list = new Blockchain();
		Scanner input = new Scanner(System.in);
		while(true){
			System.out.println("Enter the transaction: ");
			String txString = input.next();
			//output is the return int from the method
			int output = list.addTransaction(txString);
			System.out.println(list.toString());
		}
	}
}
