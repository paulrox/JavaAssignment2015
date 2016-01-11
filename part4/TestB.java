package part4;

import java.util.concurrent.ThreadLocalRandom;
import part2.SynchPort;
import part2.Message;

/* Producer thread */

class ProducerB extends Thread {
	int priority;
	public SynchPort<Integer> in;
	
	public ProducerB(String name, int prio) {
		super(name);
		priority = prio;
		in = new SynchPort<Integer>();
	}
	public void run() {
		Message<Integer> msg = new Message<Integer>();
		try{ sleep(ThreadLocalRandom.current().nextInt(100, 600)); 
		} catch (InterruptedException e) {}
		for (int i = 0; i < 5; i++) {
			msg.info = 0; /* insert request */
			msg.tid = Thread.currentThread().getId();
			msg.priority = priority;
			msg.ret = in;
			MailboxB.request.send(msg);	/* send the request */
			msg = in.receive();		/* receive a reply from the Mailbox */
			msg = new Message<Integer>();
			msg.info = ThreadLocalRandom.current().nextInt(0, 100);
			msg.tid = Thread.currentThread().getId();
			msg.ret = in;
			MailboxB.insert_p.send(msg);	/* send the value */
		}
	}
}

/* Consumer thread */

class ConsumerB extends Thread {
	public static SynchPort<Integer> in = new SynchPort<Integer>();
	
	public ConsumerB(String name) {
		super(name);
	}
	public void run() {
		Message<Integer> msg;
		for (int i = 0; i < 50; i++) {
			msg = new Message<Integer>();
			msg.info = 1;  /* remove request */
			msg.ret = in;
			MailboxB.request.send(msg);	/* send the request */
			msg = in.receive();	/* receive the value */
			System.out.println("Consumer received " + msg.info +
					" from thread: " + msg.tid);
			/* every 10 receives, wait some time*/
			if (i % 10 == 0) {
				try{ sleep(ThreadLocalRandom.current().nextInt(100, 600)); 
				} catch (InterruptedException e) {
					System.err.println("Interrupted Exception during sleep of"
							+ "thread " + Thread.currentThread().getId());
				}
			}
		}
	}
}

public class TestB {
	
	public static void main(String[] args) {
		long[] tid_list = new long[10];
		ProducerB[] prods = new ProducerB[10];
		ConsumerB cons = new ConsumerB("Consumer");
		for (int i = 0; i < 10; i++) {
			prods[i] = new ProducerB("Producer-" + i, i + 1);
			tid_list[i] = prods[i].getId();
		}
		MailboxB mail = new MailboxB(true, tid_list);
		mail.start();
		cons.start();
		for (int i = 0; i < 10; i++) {
			prods[i].start();
		}
	}
}