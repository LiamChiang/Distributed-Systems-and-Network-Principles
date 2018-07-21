import java.util.Date;
public class Consumer implements Runnable{
	private Channel<Date> queue;

	public Consumer(Channel<Date> queue) {
		this.queue = queue;
	}
	public void run(){
		Date message;

		while(true){
			try{
				Thread.sleep(500);
			}
			catch (Exception E){
				System.err.println("Already interrupted");
			}
			message = queue.receive();
			System.out.println("Successfully recieved " + message);
		}
	}
}