package part2;

public class Message<T> {
	public T info;
	public int index;		/* index field, used in part 3 */
	public long tid;		/* TID field, used in part 4A */
	public int priority;	/* priority field used in part 4B */
	public SynchPort<T> ret;
	
	public Message() {
		info = null;
		index = -1;
		tid = -1;
		priority = -1;
		ret = null;
	}
}
