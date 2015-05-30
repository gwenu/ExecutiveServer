package ag.security.management.system.services.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ag.security.management.system.SharedQueue;
import ag.security.management.system.executors.EventConsumer;
import ag.security.management.system.executors.EventProducer;
import ag.security.management.system.providers.DataProvider;
import ag.security.management.system.services.Executable;

public class EventService<T> implements Executable {

	private static Logger logger = LoggerFactory.getLogger(EventProducer.class);

	private List<DataProvider> providers;
	private SharedQueue<T> sharedQueue;

	public EventService(List<DataProvider> providers, SharedQueue<T> sharedQueue) {
		this.providers = providers;
		this.sharedQueue = sharedQueue;
	}

	public void proceed() {
		logger.info("EventService starts working...");

		for (DataProvider provider : providers) {
			Thread providerThread = new Thread(new EventProducer<T>(provider, sharedQueue));
			providerThread.start();
		}

		Thread consumerThread = new Thread(new EventConsumer<T>(sharedQueue));
		consumerThread.start();
	}

	public void shutdownAfterCompletion() {

	}

}
