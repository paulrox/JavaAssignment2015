package part1;

import java.util.concurrent.ThreadLocalRandom;

class Worker extends Thread {
	
	public void run() {
		Test.s.fairWait();
		/* simulating a random computation */
		try{ sleep(ThreadLocalRandom.current().nextInt(100, 600));
		} catch (InterruptedException e) {
			System.err.println("Interrupted Exception during sleep of"
					+ "thread " + Thread.currentThread().getId());
		}
		Test.s.fairSignal();
	}
}

public class Test {
	static TidList in = new TidList();
	static TidList out = new TidList();
	static TestList<TidList> tl = new TestList<TidList>(in, out);
	public static FairSem s = new FairSem(3, true, tl);
	
	public static void main(String[] args) {
		boolean testOk = true;
		Worker[] threads = new Worker[10];
		
		for (int i = 0; i < 10; i++) {
			threads[i] = new Worker();
			threads[i].start();
		}
		
		for (int i = 0; i < 10; i++) {
			try{
				threads[i].join();
			} catch (InterruptedException e) {
				System.err.println("Error in thread join");
				System.exit(1);
			}	
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
