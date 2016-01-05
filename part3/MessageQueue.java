package part3;

public class MessageQueue<T> {
	int max_msg;
	MsgQueueElem<T> first;
	
	public MessageQueue(int n) {
		MsgQueueElem<T> tmp;
		max_msg = n;
		tmp = new MsgQueueElem<T>();
		first = tmp;
		for (int i = 0; i < n - 1; i++) {
			tmp.next = new MsgQueueElem<T>();
			tmp = tmp.next;
		}
		tmp.next = null;
	}
	
	public void insert(Message<T> msg, int pos) {
		MsgQueueElem<T> tmp = first;
		for (int i = 0; i < pos; i++) {
			tmp = tmp.next;
		}
		tmp.msg = msg;
	}
	
	public Message<T> extract(int pos) {
		MsgQueueElem<T> tmp = first;
		for (int i = 0; i < pos; i++) {
			tmp = tmp.next;
		}
		return tmp.msg;
	}
}
