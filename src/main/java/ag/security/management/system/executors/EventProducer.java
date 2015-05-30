package ag.security.management.system.executors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ag.security.management.system.SharedQueue;
import ag.security.management.system.providers.DataProvider;

public class EventProducer<T> implements Runnable {

	private static Logger logger = LoggerFactory.getLogger(EventProducer.class);

	private int maxQueueSize = 5;
	private DataProvider provider;
	private SharedQueue<T> sharedQueue;

	public EventProducer(DataProvider provider, SharedQueue<T> sharedQueue) {
		this.provider = provider;
		this.sharedQueue = sharedQueue;
	}

	public void run() {
		try {
			produce();
		} catch (InterruptedException interruptedException) {
			logger.error("Exception occurred while execute EventProducer\n"
					+ interruptedException.getMessage());
		}
	}

	private void produce() throws InterruptedException {
		String line;
		InputStream iStream = provider.getDataStream();
		BufferedReader bufferReader = new BufferedReader(new InputStreamReader(iStream));

		try {
			while ((line = bufferReader.readLine()) != null) {
				addEventToQueue(line);
			}
		} catch (IOException e) {
			logger.error("Exception occured when parse event stream.");
		}
	}

	private void addEventToQueue(String line) throws InterruptedException {
		synchronized (sharedQueue) {
			while (sharedQueue.lastInQueue() == maxQueueSize) {
				logger.debug("Shared queue is full. Wait for consumer.\n");
				sharedQueue.wait();
			}
		}

		synchronized (sharedQueue) {
			sharedQueue.enqueue(castObjTmp(line));
			sharedQueue.notifyAll();
		}
	}

	@SuppressWarnings("unchecked")
	private T castObjTmp(String line) {
		return (T) line;
	}
}
