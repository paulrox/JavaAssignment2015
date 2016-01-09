package part1;

public class FairSem {
	TidList waiting;			// waiting TIDs queue
	TidList wakeup;				// extracted TIDs queue
	int value;					// semaphore value
	boolean testEnabled;		// enables testing
	TestList<TidList> t_list;	// lists used for testing
	
	public FairSem(int n) {
		waiting = new TidList();
		wakeup = new TidList();
		value = n;
		testEnabled = false;
		t_list = null;
	}
	
	public FairSem(int n, boolean t, TestList<TidList> tl) {
		waiting = new TidList();
		wakeup = new TidList();
		value = n;
		testEnabled = t;
		t_list = tl;
	}
	
	public synchronized void fairWait() {
		if (testEnabled) {
			System.out.println(Thread.currentThread().getName() + " started P\n");
			t_list.in_P.insert(Thread.currentThread().getId());
		}
		if (value == 0) {	/* red semaphore, add the current thread to the queue */
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
		if (testEnabled){
			System.out.println(Thread.currentThread().getName() + " terminated P\n");
			t_list.out_P.insert(Thread.currentThread().getId());
		}
	}
	
	public synchronized void fairSignal() {
		if (!waiting.empty()) {
			/* extract the first thread in the thread queue
			 * and insert it in the extracted queue */
			wakeup.insert(waiting.extract());
			notifyAll(); // wake up all the waiting threads
		} else value++;
		if (testEnabled) System.out.println(Thread.currentThread().getName() + " executed V, value = " + value + "\n");
	}
}
