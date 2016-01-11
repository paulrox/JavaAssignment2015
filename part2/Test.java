package part2;

import java.util.concurrent.ThreadLocalRandom;
import part1.TestList;

class Producer extends Thread {
	
	public Producer(String name) {
		super(name);
	}
	public void run() {
		Message<Integer> msg = new Message<Integer>();
		msg.info = ThreadLocalRandom.current().nextInt(0, 100);
		Consumer.in.send(msg);
	}
}

class Consumer extends Thread {
	public static SynchPort<Integer> in;
	int pnum;
	
	public Consumer(String name, SynchPort<Integer> inport, int n) {
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

	static boolean testOk = true;
	public static void main(String[] args) {
		Consumer cons = new Consumer("Consumer", cport, pnum);
		Producer[] prods = new Producer[pnum];
		for (int i = 0; i < pnum; i++) {
			prods[i] = new Producer("Producer-" + i);
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

