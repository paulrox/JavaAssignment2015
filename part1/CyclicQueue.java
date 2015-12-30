package part1;

public class CyclicQueue {
	private long[] queue;
	private int lenght;
	private int count;
	private int front;
	private int rear;
	
	public CyclicQueue(int n) {
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
		return queue[front];
	}
	
	public synchronized boolean empty() {
		return (count == 0);
	}
}
