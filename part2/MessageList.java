/*
 * Class for handling a list Messages
 * 
 * Note: We don't know how many threads will use this
 * queue, and we don't even know how long the queue will be.
 * For those reasons we avoid any deadlock possibility by
 * using notifyAll() instead of notify().
 */

package part2;

import java.util.ArrayList;
import java.util.List;

public class MessageList<T> {
	List<Message<T>> queue;
	
	public MessageList() {
		queue = new ArrayList<Message<T>>();
	}
	
	public synchronized void insert(Message<T> var) {
		while (queue.size() == Integer.MAX_VALUE) {
			try { wait();
			} catch (InterruptedException e) {
				System.err.println("Interrupted Exception in thread TID: " +
						Thread.currentThread().getId());
			}
		}
		queue.add(var);
		notifyAll();
	}
	
	public synchronized Message<T> extract() {
		Message<T> tmp;
		while (queue.isEmpty()) {
			try{ wait();
			} catch (InterruptedException e) {
				System.err.println("Interrupted Exception in thread TID: " +
						Thread.currentThread().getId());
			}
		}
		tmp = queue.remove(0);
		notifyAll();
		return tmp;
	}
	
	public synchronized Message<T> firstElem() {
		if (!queue.isEmpty()) {
			return queue.get(0);
		} else {
			return null;
		}
	}
	
	public synchronized boolean empty() {
		return (queue.isEmpty());
	}
}
