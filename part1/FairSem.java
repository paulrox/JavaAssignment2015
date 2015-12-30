package part1;

public class FairSem {
	CyclicQueue tid_queue;				// thread queue
	CyclicQueue extracted_tid;			// extracted TID queue
	private int value;					// semaphore value
	
	public FairSem(int n, int t_num) {
		tid_queue = new CyclicQueue(t_num);
		extracted_tid = new CyclicQueue(t_num);
		value = n;
	}
	
	public synchronized void fairWait() {
		System.out.println(Thread.currentThread().getName() + " started P\n");
		if (value == 0) {	// red semaphore, add the current thread to the queue
				tid_queue.insert(Thread.currentThread().getId());
				while (extracted_tid.firstElem() != Thread.currentThread().getId()) {
					try { wait();
					} catch (InterruptedException e) {}
				}
				extracted_tid.extract();
		} else value--;
		System.out.println(Thread.currentThread().getName() + " terminated P\n");
	}
	
	public synchronized void fairSignal() {
		if (!tid_queue.empty()) {
			/* extract the first thread in the thread queue
			 * and insert it in the extracted queue */
			extracted_tid.insert(tid_queue.extract());
			notifyAll(); // wake up all the waiting threads
		} else value++;
		System.out.println(Thread.currentThread().getName() + " executed V, value = " + value + "\n");
	}
}
