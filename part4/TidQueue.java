package part4;

public class TidQueue {
	long[] buffer;
	int rear, front, count;
	
	public TidQueue() {
		buffer = new long[4];
		rear = front = count = 0;
	}
	
	public void insert(long val) {
		buffer[rear] = val;
		rear = (rear + 1) % 4;
		count++;
	}
	
	public long remove() {
		long ret;
		ret = buffer[front];
		front = (front + 1) % 4;
		count--;
		return ret;
	}
	
	public boolean empty() {
		return (count == 0);
	}
	
	public boolean full() {
		return (count == 4);
	}
}
