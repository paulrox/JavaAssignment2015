package part4;

public class Queue {
	int[] buffer;
	int rear, front, count;
	
	public Queue() {
		buffer = new int[4];
		rear = front = count = 0;
	}
	
	public void insert(int val) {
		buffer[rear] = val;
		rear = (rear + 1) % 4;
		count++;
	}
	
	public int remove() {
		int ret;
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
