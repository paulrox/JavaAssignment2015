package part2;

import part1.FairSem;
import part1.TestList;

public class SynchPort <T> {
	Message<T> slot;					/* port buffer, just one slot */
	FairSem empty, full, synch;
	boolean testEnabled;				/* enables testing */
	TestList<MessageList<T>> t_list;	/* testing list */
	
	public SynchPort() {
		empty = new FairSem(1);	/* semaphore for sending thread		*/
		full = new FairSem(0);	/* semaphore for receiving thread 	*/
		synch = new FairSem(0);	/* semaphore for waiting the end of
								 * receive operation 				*/
		testEnabled = false;
		t_list = null;
	}
	
	public SynchPort(boolean t, TestList<MessageList<T>> tl) {
		empty = new FairSem(1);	/* semaphore for sending thread		*/
		full = new FairSem(0);	/* semaphore for receiving thread 	*/
		synch = new FairSem(0);	/* semaphore for waiting the end of
								 * receive operation 				*/
		testEnabled = true;
		t_list = tl;
	}
	
	public void send(Message<T> m) {
		empty.fairWait();
		slot = m;
		if (testEnabled) {
			System.out.println(Thread.currentThread().getName() +
					" sent message " + m.info);
			t_list.in_P.insert(m);
		}
				
		full.fairSignal();
		synch.fairWait();
	}
	
	public Message<T> receive() {
		Message<T> m = new Message<T>();	
		full.fairWait();
		m = slot;
		if (testEnabled) {
			System.out.println(Thread.currentThread().getName() + 
					" received message " + m.info);
			t_list.out_P.insert(m);
		}			
		empty.fairSignal();
		synch.fairSignal();	
		return m;
	}
}
