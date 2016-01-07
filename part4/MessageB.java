package part4;

public class MessageB<T> {
	public T value;
	public long tid;
	public int priority;
	
	public MessageB() {
		value = null;
		tid = -1;
		priority = 0;
	}
	
	public MessageB(T val, long t) {
		value = val;
		tid = t;
		priority = -1;
	}
	
	public MessageB(T val, long t, int prio) {
		value = val;
		tid = t;
		priority = prio;
	}
}
