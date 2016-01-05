package part3;

import part2.Message;
import part2.SynchPort;

public class MessageInc<T> {
	public Message<T> msg;
	public int p_index;		/* port index */
	
	public MessageInc() {
		msg = new Message<T>();
		p_index = -1;
	}
	
	public MessageInc(T i, SynchPort<T> p) {
		msg = new Message<T>(i, p);
		p_index = -1;
	}
	
}