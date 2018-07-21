class MyThread implements Runnable {
	public void run() {
		System.out.println("Hello World");
	}
	public static void main (String args[]){
		Thread mt = new Thread(new MyThread());
		mt.start();
	}
}