package part2;

import java.util.concurrent.ThreadLocalRandom;
import part1.TestList;

class Producer extends Thread {
	Consumer cons;
	SynchPort<Integer> in;
	
	public Producer(SynchPort<Integer> inport, Consumer c, String name) {
		super(name);
		cons = c;
		in = inport;
	}
	public void run() {
		Message<Integer> msg = new Message<Integer>();
		msg.info = ThreadLocalRandom.current().nextInt(0, 100);
		msg.ret = in;
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
			msg = in.receive();
			try{ sleep(ThreadLocalRandom.current().nextInt(100, 600)); 
			} catch (InterruptedException e) {}
		}
	}
}

public class Test {
	static int pnum = 5;
	static MessageList<Integer> in = new MessageList<Integer>();
	static MessageList<Integer> out = new MessageList<Integer>();
	static TestList<MessageList<Integer>> tl = new 
			TestList<MessageList<Integer>>(in, out);
	static SynchPort<Integer> cport = new SynchPort<Integer>(true, tl);
	static SynchPort<Integer> pport0 = new SynchPort<Integer>();
	static SynchPort<Integer> pport1 = new SynchPort<Integer>();
	static SynchPort<Integer> pport2 = new SynchPort<Integer>();
	static SynchPort<Integer> pport3 = new SynchPort<Integer>();
	static SynchPort<Integer> pport4 = new SynchPort<Integer>();
	static boolean testOk = true;
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
		
		for (int i = 0; i < 5; i++) {
			try{
				prods[i].join();
			} catch (InterruptedException e) {}	
		}
		try {
			cons.join();
		} catch (InterruptedException e) {}
		
		if (tl.in_P.queue.size() == tl.out_P.queue.size()) {
			for (int i = 0; i < tl.in_P.queue.size(); i++) {
				if (tl.in_P.extract() != tl.out_P.extract()) testOk = false;
			}
		} else {
			testOk = false;
		}
		if (testOk) System.out.println("Test Passed!");
		else System.out.println("Test Failed!");
	}

}

