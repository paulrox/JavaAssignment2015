package part1;

public class FairSem {
	private final int max_threads;		// max number of waiting threads
	private long[] tid_queue;			// thread queue
	private long extracted_tid;			// last extracted thread TID
	private int value;					// semaphore value
	private int front, rear, count;		// support for cyclic queue
	
	public FairSem(int n, int t_num) {
		max_threads = t_num;
		tid_queue = new long[max_threads];
		value = n;
		front = rear = count = 0;
		extracted_tid = 0;
	}
	
	public synchronized void fairWait() {
		System.out.println(Thread.currentThread().getName() + " started P\n");
		if (value == 0) {	// red semaphore, add the current thread to the queue
				count++;
				tid_queue[rear] = Thread.currentThread().getId();
				rear = (rear + 1) % max_threads;
				while (extracted_tid != Thread.currentThread().getId()) {
					try { wait();
					} catch (InterruptedException e) {}
				}
				extracted_tid = 0;	// once woke up, reset the extracted tid
		} else value--;
		System.out.println(Thread.currentThread().getName() + " terminated P\n");
	}
	
	public synchronized void fairSignal() {
		if (count > 0) {
			count--;
			extracted_tid = tid_queue[front];
			front = (front + 1) % max_threads;
			notifyAll();
		} else value++;
		System.out.println(Thread.currentThread().getName() + " executed V\n");
	}
}
