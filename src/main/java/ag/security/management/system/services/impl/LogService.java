package ag.security.management.system.services.impl;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ag.security.management.system.executors.LogProducer;
import ag.security.management.system.providers.LogProvider;
import ag.security.management.system.services.Executable;

public class LogService implements Executable {

	private Logger logger = LoggerFactory.getLogger(LogService.class);

	private ExecutorService producer = Executors.newFixedThreadPool(2);
	private ExecutorService consumer = Executors.newSingleThreadExecutor();
	private BlockingQueue<String> sharedQueue = new LinkedBlockingQueue<String>();

	private List<LogProvider> logProviders;

	public LogService(List<LogProvider> logProviders) {
		this.logProviders = logProviders;
	}

	public void proceed() {
		for (LogProvider provider : logProviders) {
			Runnable logProducer = new LogProducer(consumer, sharedQueue, provider);
			producer.submit(logProducer);
		}
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
