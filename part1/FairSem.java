package part1;

import java.util.concurrent.ThreadLocalRandom;

class Worker extends Thread {
	FairSem s;
	
	public Worker(FairSem my_s) {
		s = my_s;
	}
	public void run() {
		try{ sleep(ThreadLocalRandom.current().nextInt(100, 600)); 
		} catch (InterruptedException e) {}
		s.fairWait();
		try{ sleep(ThreadLocalRandom.current().nextInt(100, 600));
		} catch (InterruptedException e) {}
		s.fairSignal();
	}
}

public class FairSem {
	private final int max_threads = 10;
	private long[] tid_queue;
	private long owner_tid;
	private int value;
	private int front, rear, count;
	
	public FairSem(int n) {
		tid_queue = new long[max_threads];
		value = n;
		front = rear = count = 0;
		owner_tid = 0;
	}
	
	public synchronized void fairWait() {
		System.out.println(Thread.currentThread().getName() + " started P\n");
		if (value == 0) {
				count++;
				tid_queue[rear] = Thread.currentThread().getId();
				rear = (rear + 1) % max_threads;
				while (owner_tid != Thread.currentThread().getId()) {
					try { wait();
					} catch (InterruptedException e) {}
				}
		} else value--;
		System.out.println(Thread.currentThread().getName() + " terminated P\n");
	}
	
	public synchronized void fairSignal() {
		System.out.println(Thread.currentThread().getName() + " started V\n");
		if (count > 0) {
			count--;
			owner_tid = tid_queue[front];
			front = (front + 1) % max_threads;
			notifyAll();
		} else value++;
		System.out.println(Thread.currentThread().getName() + " terminated V\n");
	}
	
	public static void main(String[] args) {
		FairSem s = new FairSem(1);
		Worker[] threads = new Worker[5];
		
		for (int i = 0; i < 5; i++) {
			threads[i] = new Worker(s);
			threads[i].start();
		}
	}
}
