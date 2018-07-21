import java.util.Date;

public class Producer implements Runnable{
	private Channel<Date> queue;

	public Producer(Channel<Date> queue) {
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
			message = new Date();
			System.out.println("Producer produced" + message);
			queue.send(message);
		}
	}
}