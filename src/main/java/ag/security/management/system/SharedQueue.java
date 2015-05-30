package ag.security.management.system;

import java.util.LinkedList;
import java.util.List;

public class SharedQueue<T> {

	private volatile int lastInQueue;
	private List<T> sharedQueue = new LinkedList<T>();

	public void enqueue(T event) {
		synchronized (sharedQueue) {
			sharedQueue.add(event);
			lastInQueue = sharedQueue.size() - 1;
		}
	}

	public T dequeue() throws InterruptedException {
		T last = null;

		synchronized (sharedQueue) {
			last = sharedQueue.get(lastInQueue);
			sharedQueue.remove(lastInQueue);
			lastInQueue = sharedQueue.size() - 1;
		}

		return last;
	}

	public int size() {
		synchronized (sharedQueue) {
			return sharedQueue.size();
		}
	}

	public int lastInQueue() {
		synchronized (sharedQueue) {
			return lastInQueue;
		}
	}
}
