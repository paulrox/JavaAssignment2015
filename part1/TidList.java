package part1;

import java.util.ArrayList;
import java.util.List;

/*
 * Class for handling a list of thread TIDs
 * 
 * Note: We don't know how many threads will use this
 * queue, and we don't even know how long the queue will be.
 * For those reasons we avoid any deadlock possibility by
 * using notifyAll() instead of notify().
 */

public class TidList {
	private List<Long> queue;
	
	public TidList() {
		queue = new ArrayList<Long>();
	}
	
	public synchronized void insert(long var) {
		while (queue.size() == Integer.MAX_VALUE) {
			try { wait();
			} catch (InterruptedException e) {}
		}
		queue.add(var);
		notifyAll();
	}
	
	public synchronized long extract() {
		long tmp;
		while (queue.isEmpty()) {
			try{ wait();
			} catch (InterruptedException e) {}
		}
		tmp = queue.remove(0);
		notifyAll();
		return tmp;
	}
	
	public synchronized long firstElem() {
		if (!queue.isEmpty()) {
			return queue.get(0);
		} else {
			return -1; //this number could never be a thread TID
		}
	}
	
	public synchronized boolean empty() {
		return (queue.isEmpty());
	}
}
