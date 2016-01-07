package part2;

import part1.FairSem;

public class SynchPort <T> {
	Message<T> slot;		/* port buffer, just one slot */
	FairSem empty, full, synch;
	
	public SynchPort() {
		empty = new FairSem(1);	/* semaphore for sending thread		*/
		full = new FairSem(0);	/* semaphore for receiving thread 	*/
		synch = new FairSem(0);	/* semaphore for waiting the end of
								 * receive operation 				*/
	}
	
	public void send(Message<T> m) {
		empty.fairWait();
		slot = m;
		//System.out.println(Thread.currentThread().getName() +
		//		" sent message " + m.info);
		full.fairSignal();
		synch.fairWait();
	}
	
	public Message<T> receive() {
		Message<T> m = new Message<T>();
		
		full.fairWait();
		m = slot;
		//System.out.println(Thread.currentThread().getName() +
		//		" received message " + m.info);
		empty.fairSignal();
		synch.fairSignal();
		
		return m;
	}
}
