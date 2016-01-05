package part1;

public class FairSem {
	TidList waiting;			// thread queue
	TidList wakeup;			// extracted TID queue
	private int value;			// semaphore value
	
	public FairSem(int n) {
		waiting = new TidList();
		wakeup = new TidList();
		value = n;
	}
	
	public synchronized void fairWait() {
		//System.out.println(Thread.currentThread().getName() + " started P\n");
		if (value == 0) {	// red semaphore, add the current thread to the queue
				waiting.insert(Thread.currentThread().getId());
				while (wakeup.firstElem() != Thread.currentThread().getId()) {
					try { wait();
					} catch (InterruptedException e) {}
				}
				wakeup.extract();
				if (!(wakeup.empty())) {	/* wake up the other waiting threads */
					notifyAll();
				}
		} else value--;
		//System.out.println(Thread.currentThread().getName() + " terminated P\n");
	}
	
	public synchronized void fairSignal() {
		if (!waiting.empty()) {
			/* extract the first thread in the thread queue
			 * and insert it in the extracted queue */
			wakeup.insert(waiting.extract());
			notifyAll(); // wake up all the waiting threads
		} else value++;
		//System.out.println(Thread.currentThread().getName() + " executed V, value = " + value + "\n");
	}
}
