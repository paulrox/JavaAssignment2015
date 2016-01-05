package part3;

import part1.FairSem;

public class PortArray<T> {
	int port_num;
	int last_index;
	SynchPortElem<T> first;
	FairSem mutex, available;
	
	public PortArray(int n) {
		SynchPortElem<T> tmp;
		port_num = n;
		last_index = 0;
		tmp = new SynchPortElem<T>();
		first = tmp;
		for (int i = 0; i < n - 1; i++) {
			tmp.next = new SynchPortElem<T>();
			tmp = tmp.next;
		}
		tmp.next = null;
		mutex = new FairSem(1);
		available = new FairSem(0);
	}
	
	public void send(MessageInc<T> m, int n) {
		SynchPortElem<T> tmp;
		mutex.fairWait();
		tmp = first;
		for (int i = 0; i < n; i++) {
			tmp = tmp.next;
		}
		tmp.msgAvailable = true;
		available.fairSignal();
		mutex.fairSignal();
		tmp.port.send(m.msg);
	}
	
	public MessageInc<T> receive(int[] v, int n) {
		int index;
		SynchPortElem<T> tmp;
		MessageInc<T> m;
		available.fairWait();
		mutex.fairWait();
		tmp = first;
		index = last_index;
		for (int i = 0; i < last_index; i++) {
			tmp = tmp.next;
			index = (index + 1) % port_num; 
		}
		while (tmp.msgAvailable == false) {
			if (tmp.next != null) {
				tmp = tmp.next;
				index = (index + 1) % port_num; 
			} else {
				tmp = first;
				index = 0;
			}
		}
		last_index = index;
		m = new MessageInc
		
	}
}
