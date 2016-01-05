package part1;

import java.util.concurrent.ThreadLocalRandom;

class Worker extends Thread {
	FairSem s;
	
	public Worker(FairSem my_s) {
		s = my_s;
	}
	public void run() {
		try{ sleep(ThreadLocalRandom.current().nextInt(100, 600)); 
		} catch (InterruptedException e) {}
		s.fairWait();
		try{ sleep(ThreadLocalRandom.current().nextInt(100, 600));
		} catch (InterruptedException e) {}
		s.fairSignal();
	}
}

public class Test {
	
	public static void main(String[] args) {
		FairSem s = new FairSem(1);
		Worker[] threads = new Worker[5];
		
		for (int i = 0; i < 5; i++) {
			threads[i] = new Worker(s);
			threads[i].start();
		}
	}

}
