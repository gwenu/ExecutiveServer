package ag.security.management.system.executors;

import java.util.concurrent.BlockingQueue;

public class LogConsumer implements Runnable {
	
	private BlockingQueue<String> shareQueue;
	
	public LogConsumer(BlockingQueue<String> sharedQueue) {
		this.shareQueue = sharedQueue;
	}

	public void run() {
		while(shareQueue.size() != 0) {
			try {
				System.out.println("LogConsumer " + shareQueue.take());
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
