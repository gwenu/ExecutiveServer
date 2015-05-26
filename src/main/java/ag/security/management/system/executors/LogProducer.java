package ag.security.management.system.executors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ag.security.management.system.providers.LogProvider;

public class LogProducer implements Runnable {
	private Logger logger = LoggerFactory.getLogger(LogProducer.class);
	
	private ExecutorService consumer;
	private LogProvider logProvider;
	private BlockingQueue<String> sharedQueue;
	
	public LogProducer(ExecutorService consumer, BlockingQueue<String> sharedQueue, LogProvider logProvider) {
		this.consumer = consumer;
		this.sharedQueue = sharedQueue;
		this.logProvider = logProvider;
	}

	public void run() {
		String line;
		InputStream iStream = logProvider.getLogs();
		BufferedReader bufferReader = new BufferedReader(new InputStreamReader(iStream));
		Runnable consume = new LogConsumer(sharedQueue);
		
		try {
			while((line = bufferReader.readLine()) != null) {
				sharedQueue.put(line);
				consumer.submit(consume);
			}
		} catch (IOException e) {
			logger.error("Cannot read line from log file: " + e.getStackTrace());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
