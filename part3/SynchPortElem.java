package part3;

import part2.SynchPort;

public class SynchPortElem<T> {
	SynchPort<T> port;
	boolean msgAvailable;
	SynchPortElem<T> next;
	
	public SynchPortElem() {
		port = new SynchPort<T>();
		msgAvailable = false;
		next = null;
	}
}
