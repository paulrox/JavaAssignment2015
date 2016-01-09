package part2;

public class Message<T> {
	public T info;
	public int index;
	public long tid;
	public int priority;
	public SynchPort<T> ret;
	
	public Message() {
		info = null;
		index = -1;
		tid = -1;
		priority = -1;
		ret = null;
	}
}
