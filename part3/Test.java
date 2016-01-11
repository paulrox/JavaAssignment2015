package part3;

import java.util.concurrent.ThreadLocalRandom;
import part2.Message;

class Producer extends Thread {
	int port_index;
	
	public Producer(String name, int num) {
		super(name);
		port_index = num;
	}
	public void run() {
		Message<Integer> msg = new Message<Integer>();
		/* simulate message production */
		try{ sleep(ThreadLocalRandom.current().nextInt(100, 600)); 
		} catch (InterruptedException e) {}
		msg.info = ThreadLocalRandom.current().nextInt(0, 100);
		Consumer.pa.send(msg, port_index);
	}
}

class Consumer extends Thread {
	public static PortArray<Integer> pa;
	int[] listening1, listening2;
	int ports_num;
	int pa_size;
	
	public Consumer(String name, int[] vett1, int[] vett2, int n, int size) {
		super(name);
		pa = new PortArray<Integer>(size, true);
		listening1 = vett1;
		listening2 = vett2;
		ports_num = n;
		pa_size = size;
	}
	public void run() {
		Message<Integer> msg = new Message<Integer>();
		for (int i = 0; i < 5; i++) {
			msg = pa.receive(listening1, ports_num);
			/* simulate message use */
		}
		for (int i = 0; i < 5; i++) {
			msg = pa.receive(listening2, ports_num);
			/* simulate message use */
		}
	}
}

public class Test {
	
	public static void main(String[] args) {
		int ports_vett1[] = {1, 3, 4, 6, 9};
		int ports_vett2[] = {2, 7, 5, 0, 8};
		int[] indexes = {4, 3, 1, 9, 3, 7, 5, 0, 2, 8};
		int ports_num = 5;
		int prod_num = 10;
		int ports_size = 10;
		Consumer cons = new Consumer("Consumer", ports_vett1, ports_vett2,
				ports_num, ports_size);
		Producer[] prods = new Producer[prod_num];
		for (int i = 0; i < prod_num; i++) {
			prods[i] = new Producer("Producer-" + i, indexes[i]);
			prods[i].start();
		}
		cons.start();
	}
}