package part3;

import java.util.concurrent.ThreadLocalRandom;
import part2.SynchPort;

class Producer extends Thread {
	SynchPort<Integer> in;
	PortArray<Integer> out;
	int port_index;
	
	public Producer(SynchPort<Integer> inport, PortArray<Integer> outport, 
			String name, int num) {
		super(name);
		in = inport;
		out = outport;
		port_index = num;
	}
	public void run() {
		MessageInc<Integer> msg = new MessageInc<Integer>
		(ThreadLocalRandom.current().nextInt(0, 100), in);
		try{ sleep(ThreadLocalRandom.current().nextInt(100, 600)); 
		} catch (InterruptedException e) {}
		out.send(msg, port_index);
	}
}

class Consumer extends Thread {
	public PortArray<Integer> in;
	int[] listening;
	int ports_num;
	
	public Consumer(PortArray<Integer> inport, String name, int[] vett, int n) {
		super(name);
		in = inport;
		listening = vett;
		ports_num = n;
	}
	public void run() {
		MessageInc<Integer> msg;
		for (int i = 0; i < 5; i++) {
			try{ sleep(ThreadLocalRandom.current().nextInt(100, 600)); 
			} catch (InterruptedException e) {}
			msg = in.receive(listening, ports_num);
		}
	}
}

public class Test {
	static int ports_vett[] = {1, 3, 4, 6, 9};
	static int ports_num = 5;
	static int pnum = 5;
	static int ports_size = 10;
	static PortArray<Integer> cports = new PortArray<Integer>(ports_size);
	static SynchPort<Integer> pport0 = new SynchPort<Integer>();
	static SynchPort<Integer> pport1 = new SynchPort<Integer>();
	static SynchPort<Integer> pport2 = new SynchPort<Integer>();
	static SynchPort<Integer> pport3 = new SynchPort<Integer>();
	static SynchPort<Integer> pport4 = new SynchPort<Integer>();
	
	public static void main(String[] args) {
		
		Consumer cons = new Consumer(cports, "Consumer", ports_vett, ports_num);
		Producer[] prods = new Producer[pnum];
		prods[0] = new Producer(pport0, cports, "Producer-0", 4);
		prods[1] = new Producer(pport1, cports, "Producer-1", 4);
		prods[2] = new Producer(pport2, cports, "Producer-2", 1);
		prods[3] = new Producer(pport3, cports, "Producer-3", 6);
		prods[4] = new Producer(pport4, cports, "Producer-4", 3);
		for (int i = 0; i < pnum; i++) {
			prods[i].start();
		}
		cons.start();
	}

}