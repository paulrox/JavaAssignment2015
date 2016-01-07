package part2;

public class Message<T> {
	public T info;
	public SynchPort<T> ret;
	
	public Message() {
		info = null;
		ret = null;
	}
	
	public Message(T i) {
		info = i;
		ret = null;
	}
	
	public Message(T i, SynchPort<T> p) {
		info = i;
		ret = p;
	}
}
