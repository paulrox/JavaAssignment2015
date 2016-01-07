package part3;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import part1.FairSem;
import part2.SynchPort;

public class PortArray<T> {
	int port_num;
	List<SynchPort<T>> ports;
	int[] waiting;
	FairSem mutex, available;
	
	public PortArray(int n) {
		SynchPort<T> p;
		port_num = n;
		ports = new ArrayList<SynchPort<T>>();
		mutex = new FairSem(1);
		available = new FairSem(0);
		waiting = new int[n];
		for (int i = 0; i < n; i++) {
			waiting[i] = 0;
			p = new SynchPort<T>();
			ports.add(p);
		}
	}
	
	public void send(MessageInc<T> m, int n) {
		mutex.fairWait();
		waiting[n]++;
		mutex.fairSignal();
		available.fairSignal();
		System.out.println(Thread.currentThread().getName() +
				" sent message " + m.msg.info + " through port " + n);
		ports.get(n).send(m.msg);
	}
	
	public MessageInc<T> receive(int[] v, int n) {
		MessageInc<T> m = new MessageInc<T>();
		int rand_i, j;
		boolean found = false;
		j = 0;
		available.fairWait();
		mutex.fairWait();
		while (!found) {
			rand_i = ThreadLocalRandom.current().nextInt(0, port_num);
			j = rand_i;
			System.out.println("rand_i: " + rand_i);
			do {
				for (int i = 0; i < n && !found; i++) {
					if (j == v[i] && waiting[j] > 0) found = true;
				}
				if (!found) j = (j + 1) % port_num;
			} while (j != rand_i && !found);
			if (!found) {
				mutex.fairSignal();
				available.fairWait();
				mutex.fairWait();
			}
		}
		waiting[j]--;
		mutex.fairSignal();
		m.msg = ports.get(j).receive();
		m.p_index = j;
		System.out.println(Thread.currentThread().getName() +
				" received message " + m.msg.info + " from port " + m.p_index);
		return m;	
	}
}
