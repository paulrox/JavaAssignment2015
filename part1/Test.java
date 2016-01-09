package part1;

import java.util.concurrent.ThreadLocalRandom;

class Worker extends Thread {
	FairSem s;
	
	public Worker(FairSem my_s) {
		s = my_s;
	}
	public void run() {
		s.fairWait();
		/* simulating a random computation */
		try{ sleep(ThreadLocalRandom.current().nextInt(100, 600));
		} catch (InterruptedException e) {}
		s.fairSignal();
	}
}

public class Test {
	
	public static void main(String[] args) {
		boolean testOk = true;
		TidList in = new TidList();
		TidList out = new TidList();
		TestList<TidList> tl = new TestList<TidList>(in, out);
		FairSem s = new FairSem(3, true, tl);
		Worker[] threads = new Worker[10];
		
		for (int i = 0; i < 10; i++) {
			threads[i] = new Worker(s);
			threads[i].start();
		}
		
		for (int i = 0; i < 10; i++) {
			try{
				threads[i].join();
			} catch (InterruptedException e) {}	
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
