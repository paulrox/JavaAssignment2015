package part3;

public class MsgQueueElem<T> {
	public Message<T> msg;
	public MsgQueueElem<T> next;
	
	public MsgQueueElem() {
		msg = new Message<T>();
		next = null;
	}
	public MsgQueueElem(T i) {
		msg = new Message<T>(i);
		next = null;
	}
}
