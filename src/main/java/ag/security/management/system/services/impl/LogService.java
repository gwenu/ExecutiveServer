package ag.security.management.system.services.impl;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ag.security.management.system.executors.LogConsumer;
import ag.security.management.system.executors.LogProducer;
import ag.security.management.system.providers.DataProvider;
import ag.security.management.system.services.Executable;

public class LogService implements Executable {

	private static Logger logger = LoggerFactory.getLogger(LogService.class);

	private ExecutorService producer = Executors.newFixedThreadPool(2);
	private ExecutorService consumer = Executors.newSingleThreadExecutor();
	private BlockingQueue<String> sharedQueue = new LinkedBlockingQueue<String>();

	private List<DataProvider> logProviders;

	public LogService(List<DataProvider> logProviders) {
		this.logProviders = logProviders;
	}

	public void proceed() {
		logger.info("LogService starts working...");
		
		for (DataProvider provider : logProviders) {
			Runnable logProducer = new LogProducer(sharedQueue, provider);
			producer.submit(logProducer);
		}
		
		consumer.submit(new LogConsumer(sharedQueue));
	}

	public void shutdownAfterCompletion() {
		try {
			producer.shutdown();
			producer.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			consumer.shutdown();
			consumer.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			logger.info("InterruptedException while waiting for LogService termination: "
					+ e.getMessage());
		}
	}
}
