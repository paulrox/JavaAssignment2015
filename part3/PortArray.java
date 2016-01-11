package part3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import part1.FairSem;
import part2.Message;
import part2.SynchPort;

public class PortArray<T> {
	int port_num;				/* total number of ports */
	int[] waiting;				/* messages waiting to be received */
	int msg_count;				/* number of pending in the array */
	List<SynchPort<T>> ports;	/* list containing the ports used */
	FairSem mutex, available;
	boolean testEnable;			/* enables testing */
	
	public PortArray(int n, boolean t) {
		SynchPort<T> p;
		port_num = n;
		msg_count = 0;
		ports = new ArrayList<SynchPort<T>>();
		mutex = new FairSem(1);		/* mutual exclusion semaphore */
		available = new FairSem(0);	/* message availability semaphore */
		waiting = new int[n];
		for (int i = 0; i < n; i++) {
			waiting[i] = 0;
			p = new SynchPort<T>();
			ports.add(p);
		}
		testEnable = t;
	}
	
	public void send(Message<T> m, int n) {
		mutex.fairWait();
		waiting[n]++;
		msg_count++;
		mutex.fairSignal();
		available.fairSignal();
		if (testEnable) System.out.println(Thread.currentThread().getName() +
				" sent message " + m.info + " through port " + n);
		ports.get(n).send(m);
	}
	
	public Message<T> receive(int[] v, int n) {
		Message<T> m = new Message<T>();
		int rand_i, j;
		boolean found = false;
		j = 0;
		mutex.fairWait();
		if (msg_count == 0) {
			mutex.fairSignal();
			if (testEnable) System.out.println(Thread.currentThread().getName() +
					" waiting for a message");
			available.fairWait();
			mutex.fairWait();
		}
		while (!found) {
			rand_i = ThreadLocalRandom.current().nextInt(0, port_num);
			j = rand_i;
			do {
				for (int i = 0; i < n && !found; i++) {
					if (j == v[i] && waiting[j] > 0) found = true;
				}
				if (!found) j = (j + 1) % port_num;
			} while (j != rand_i && !found);
			if (!found && msg_count == 0) {
				mutex.fairSignal();
				if (testEnable) System.out.println(Thread.currentThread().getName() +
						" waiting for a message");
				available.fairWait();
				mutex.fairWait();
			}
		}
		waiting[j]--;
		msg_count--;
		mutex.fairSignal();
		m = ports.get(j).receive();
		m.index = j;
		if (testEnable) System.out.println(Thread.currentThread().getName() +
				" received message " + m.info + " from port " + m.index);
		return m;	
	}
}
