package part4;

import java.util.concurrent.ThreadLocalRandom;
import part2.SynchPort;
import part2.Message;

class ProducerA extends Thread {
	MailboxA mb;
	SynchPort<TidMsg<Integer>> in;
	
	public ProducerA(MailboxA m, String name) {
		super(name);
		mb = m;
		in = new SynchPort<TidMsg<Integer>>();
	}
	public void run() {
		Message<TidMsg<Integer>> msg = new Message<TidMsg<Integer>>();
		for (int i = 0; i < 5; i++) {
			msg.info = new TidMsg<Integer>(0); /* insert request */
			msg.ret = in;
			mb.request.send(msg);
			msg = new Message<TidMsg<Integer>>();
			msg.info = new TidMsg<Integer>(
					ThreadLocalRandom.current().nextInt(0, 100),
					Thread.currentThread().getId());
			msg.ret = in;
			mb.insert_p.send(msg);
		}
	}
}

class ConsumerA extends Thread {
	MailboxA mb;
	public SynchPort<TidMsg<Integer>> in;
	
	public ConsumerA(MailboxA m, String name) {
		super(name);
		mb = m;
		in = new SynchPort<TidMsg<Integer>>();
	}
	public void run() {
		Message<TidMsg<Integer>> msg;
		for (int i = 0; i < 50; i++) {
			msg = new Message<TidMsg<Integer>>();
			msg.info = new TidMsg<Integer>(1);  /* remove request */
			msg.ret = in;
			mb.request.send(msg);
			msg = in.receive();
			System.out.println("Consumer received " + msg.info.value +
					" from thread: " + msg.info.tid);
		}
	}
}

public class TestA {
	
	public static void main(String[] args) {
		MailboxA mail = new MailboxA();
		ConsumerA cons = new ConsumerA(mail, "Consumer");
		ProducerA[] prods = new ProducerA[10];
		for (int i = 0; i < 10; i++) {
			prods[i] = new ProducerA(mail, "Producer-" + i);
		}
		mail.start();
		cons.start();
		for (int i = 0; i < 10; i++) {
			prods[i].start();
		}
	}
}