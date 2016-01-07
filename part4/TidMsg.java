package part4;

public class TidMsg<T> {
	public T value;
	public long tid;

	public TidMsg(T i) {
		value = i;
		tid = -1;
	}
	
	public TidMsg(T i, long t) {
		value = i;
		tid = t;
	}
}
