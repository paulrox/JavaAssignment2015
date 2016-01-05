package part2;

import java.util.concurrent.ThreadLocalRandom;

class Producer extends Thread {
	Consumer cons;
	SynchPort<Integer> in;
	
	public Producer(SynchPort<Integer> inport, Consumer c, String name) {
		super(name);
		cons = c;
		in = inport;
	}
	public void run() {
		Message<Integer> msg = new Message<Integer>
		(ThreadLocalRandom.current().nextInt(0, 100), in);
		try{ sleep(ThreadLocalRandom.current().nextInt(100, 600)); 
		} catch (InterruptedException e) {}
		cons.in.send(msg);
	}
}

class Consumer extends Thread {
	public SynchPort<Integer> in;
	int pnum;
	
	public Consumer(SynchPort<Integer> inport, String name, int n) {
		super(name);
		in = inport;
		pnum = n;
	}
	public void run() {
		Message<Integer> msg;
		for (int i = 0; i < pnum; i++) {
			try{ sleep(ThreadLocalRandom.current().nextInt(100, 600)); 
			} catch (InterruptedException e) {}
			msg = in.receive();
		}
	}
}

public class Test {
	static int pnum = 5;
	static SynchPort<Integer> cport = new SynchPort<Integer>();
	static SynchPort<Integer> pport0 = new SynchPort<Integer>();
	static SynchPort<Integer> pport1 = new SynchPort<Integer>();
	static SynchPort<Integer> pport2 = new SynchPort<Integer>();
	static SynchPort<Integer> pport3 = new SynchPort<Integer>();
	static SynchPort<Integer> pport4 = new SynchPort<Integer>();
	
	public static void main(String[] args) {
		Consumer cons = new Consumer(cport, "Consumer", pnum);
		Producer[] prods = new Producer[pnum];
		prods[0] = new Producer(pport0, cons, "Producer-0");
		prods[1] = new Producer(pport1, cons, "Producer-1");
		prods[2] = new Producer(pport2, cons, "Producer-2");
		prods[3] = new Producer(pport3, cons, "Producer-3");
		prods[4] = new Producer(pport4, cons, "Producer-4");
		for (int i = 0; i < pnum; i++) {
			prods[i].start();
		}
		cons.start();
	}

}

