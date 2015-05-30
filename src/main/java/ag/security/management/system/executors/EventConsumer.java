package ag.security.management.system.executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ag.security.management.system.SharedQueue;

public class EventConsumer<T> implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(EventConsumer.class);

	private SharedQueue<T> sharedQueue;
	private static final int MAX_WAIT_TIMEOUT = 5000; 

	public EventConsumer(SharedQueue<T> sharedQueue) {
		this.sharedQueue = sharedQueue;
	}

	public void run() {
		while(true) {
			try {
				consume();
			} catch (InterruptedException e) {
				logger.error(e.getMessage());
			}
		}
	}

	private void consume() throws InterruptedException {
		synchronized (sharedQueue) {
			if (sharedQueue.size() > 0) {
				processEvent(sharedQueue.dequeue());
				sharedQueue.notifyAll();
			} else {
				sharedQueue.wait(MAX_WAIT_TIMEOUT);
			}
		}
	}

	private void processEvent(T event) {
		if (event != null) {
			System.out.println("EventConsumer " + event);
		}
	}
}
