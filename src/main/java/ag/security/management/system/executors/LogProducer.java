package ag.security.management.system.executors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ag.security.management.system.providers.DataProvider;

public class LogProducer implements Runnable {
	private static Logger logger = LoggerFactory.getLogger(LogProducer.class);
	
	private DataProvider logProvider;
	private BlockingQueue<String> sharedQueue;
	
	public LogProducer(BlockingQueue<String> sharedQueue, DataProvider logProvider) {
		this.sharedQueue = sharedQueue;
		this.logProvider = logProvider;
	}

	public void run() {
		String line;
		InputStream iStream = logProvider.getDataStream();
		BufferedReader bufferReader = new BufferedReader(new InputStreamReader(iStream));
		
		try {
			while((line = bufferReader.readLine()) != null) {
				sharedQueue.put(line);
			}
		} catch (IOException e) {
			logger.error("Cannot read line from log file: " + e.getStackTrace());
		} catch (InterruptedException e) {
			logger.info("" + e.getMessage());
		}
	}
}
