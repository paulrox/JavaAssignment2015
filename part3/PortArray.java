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
	List<SynchPort<T>> ports;	/* list containing the ports used */
	FairSem mutex, available;
	boolean testEnable;			/* enables testing */
	
	public PortArray(int n, boolean t) {
		SynchPort<T> p;
		port_num = n;
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
		if (n >= port_num || n < 0) {
			System.err.println("Wrong index in send() function! index = " + n);
			System.exit(1);
		}
		mutex.fairWait();
		waiting[n]++;
		available.fairSignal();
		mutex.fairSignal();
		if (testEnable) System.out.println(Thread.currentThread().getName() +
				" sent message " + m.info + " through port " + n);
		ports.get(n).send(m);
	}
	
	public Message<T> receive(int[] v, int n) {
		Message<T> m = new Message<T>();
		int rand_i, j;
		boolean found = false;
		j = 0;
		if (n > port_num || n <= 0) {
			System.err.println("Error in receive() funtion! array dim: " + n);
			System.exit(1);
		}
		mutex.fairWait();
		while (!found) {
			/* generate a random initial index */
			rand_i = ThreadLocalRandom.current().nextInt(0, port_num);
			j = rand_i;
			do {
				for (int i = 0; i < n && !found; i++) {
					if (j == v[i] && waiting[j] > 0) found = true;
				}
				if (!found) j = (j + 1) % port_num;
			} while (j != rand_i && !found);
			if (!found) {
				/* if not found, wait the next message */
				mutex.fairSignal();
				if (testEnable) System.out.println(Thread.currentThread().getName() +
						" waiting for a message");
				available.fairWait();
				mutex.fairWait();
			}
		}
		/* port found */
		waiting[j]--;
		mutex.fairSignal();
		m = ports.get(j).receive();
		m.index = j;
		if (testEnable) System.out.println(Thread.currentThread().getName() +
				" received message " + m.info + " from port " + m.index);
		return m;	
	}
}
