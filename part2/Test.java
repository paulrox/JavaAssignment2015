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
	public static SynchPort<Integer> in = new SynchPort<Integer>(true, Test.tl);
	int pnum;
	
	public Consumer(String name, int n) {
		super(name);
		pnum = n;
	}
	public void run() {
		Message<Integer> msg;
		for (int i = 0; i < pnum; i++) {
			msg = in.receive();
			try{ sleep(ThreadLocalRandom.current().nextInt(100, 600)); 
			} catch (InterruptedException e) {
				System.err.println("Interrupted Exception during sleep of"
						+ "thread " + Thread.currentThread().getId());
			}
		}
	}
}

public class Test {
	static int pnum = 5;
	/* queue of received messages */
	static MessageList<Integer> in = new MessageList<Integer>();
	/* queue of sent messages */
	static MessageList<Integer> out = new MessageList<Integer>();
	static TestList<MessageList<Integer>> tl = new 
			TestList<MessageList<Integer>>(in, out);
	/* test result */
	static boolean testOk = true;
	
	public static void main(String[] args) {
		Consumer cons = new Consumer("Consumer", pnum);
		Producer[] prods = new Producer[pnum];
		for (int i = 0; i < pnum; i++) {
			prods[i] = new Producer("Producer-" + i);
			prods[i].start();
		}
		cons.start();
		
		for (int i = 0; i < 5; i++) {
			try{
				prods[i].join();
			} catch (InterruptedException e) {
				System.err.println("Error joining on producers");
			}	
		}
		try {
			cons.join();
		} catch (InterruptedException e) {
			System.err.println("Error joining on consumer");
		}
		
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

