package part3;

public class MessageInc<T> {
	public T msg;
	public int p_index;		/* port index */
	
	public MessageInc() {
		msg = null;
		p_index = -1;
	}
	
	public MessageInc(T i) {
		msg = i;
		p_index = -1;
	}
	
}