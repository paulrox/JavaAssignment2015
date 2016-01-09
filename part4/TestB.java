package part4;

import java.util.concurrent.ThreadLocalRandom;
import part2.SynchPort;
import part2.Message;

class ProducerB extends Thread {
	int priority;
	MailboxB mb;
	SynchPort<MessageB<Integer>> in;
	
	public ProducerB(MailboxB m, String name, int prio) {
		super(name);
		priority = prio;
		mb = m;
		in = new SynchPort<MessageB<Integer>>();
	}
	public void run() {
		Message<MessageB<Integer>> msg = new Message<MessageB<Integer>>();
		try{ sleep(ThreadLocalRandom.current().nextInt(100, 600)); 
		} catch (InterruptedException e) {}
		for (int i = 0; i < 5; i++) {
			msg.info = new MessageB<Integer>(0, -1, priority); /* insert request */
			msg.ret = in;
			mb.request.send(msg);
			msg = in.receive();
			msg = new Message<MessageB<Integer>>();
			msg.info = new MessageB<Integer>(
					ThreadLocalRandom.current().nextInt(0, 100),
					Thread.currentThread().getId());
			msg.ret = in;
			mb.insert_p.send(msg);
		}
	}
}

class ConsumerB extends Thread {
	MailboxB mb;
	public SynchPort<MessageB<Integer>> in;
	
	public ConsumerB(MailboxB m, String name) {
		super(name);
		mb = m;
		in = new SynchPort<MessageB<Integer>>();
	}
	public void run() {
		Message<MessageB<Integer>> msg;
		for (int i = 0; i < 50; i++) {
			msg = new Message<MessageB<Integer>>();
			msg.info = new MessageB<Integer>(1);  /* remove request */
			msg.ret = in;
			mb.request.send(msg);
			msg = in.receive();
			System.out.println("Consumer received " + msg.info.value +
					" from thread: " + msg.info.tid);
		}
	}
}

public class TestB {
	
	public static void main(String[] args) {
		MailboxB mail = new MailboxB();
		ConsumerB cons = new ConsumerB(mail, "Consumer");
		ProducerB[] prods = new ProducerB[10];
		for (int i = 0; i < 10; i++) {
			prods[i] = new ProducerB(mail, "Producer-" + i, i + 1);
		}
		mail.start();
		cons.start();
		for (int i = 0; i < 10; i++) {
			prods[i].start();
		}
	}
}