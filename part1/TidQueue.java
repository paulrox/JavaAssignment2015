package part1;

/*
 * Class for handling a cyclic queue composed by
 * threads TIDs.
 * 
 * Note: We don't know how many threads will use this
 * queue, and we don't even know how long the queue will be.
 * For those reasons we avoid any deadlock possibility by
 * using notifyAll() instead of notify().
 */

public class TidQueue {
	private long[] queue;
	private int lenght;
	private int count;
	private int front;
	private int rear;
	
	public TidQueue(int n) {
		queue = new long[n];
		lenght = n;
		count = rear = front = 0;
	}
	
	public synchronized void insert(long var) {
		while (count == lenght) {
			try { wait();
			} catch (InterruptedException e) {}
		}
		queue[rear] = var;
		rear = (rear + 1) % lenght; 
		count++;
		notifyAll();
	}
	
	public synchronized long extract() {
		long tmp;
		while (count == 0) {
			try{ wait();
			} catch (InterruptedException e) {}
		}
		tmp = queue[front];
		front = (front + 1) % lenght;
		count--;
		notifyAll();
		return tmp;
	}
	
	public synchronized long firstElem() {
		if (count != 0) {
			return queue[front];
		} else {
			return -1; //this number could never be a thread TID
		}
	}
	
	public synchronized boolean empty() {
		return (count == 0);
	}
}
