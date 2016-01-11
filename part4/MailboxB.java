package part4;

import java.util.List;
import java.util.ArrayList;
import part2.SynchPort;
import part2.Message;

public class MailboxB extends Thread{
	/* port for receiving service requests */
	public static SynchPort<Integer> request = new SynchPort<Integer>();
	/* port for receiving producers values */
	public static SynchPort<Integer> insert_p = new SynchPort<Integer>();
	List<SynchPort<Integer>> prod_reply;	/* list of producers reply ports */
	boolean[] blocked = new boolean[10];	/* producers state */
	int waiting_prod;		/* number of waiting producers */
	boolean waiting_cons;	/* true if the consumer is waiting */
	Queue buffer;			/* Mailbox buffer */
	TidQueue tid_queue;		/* producers TID queue */
	boolean testEnable;		/* enables testing */
	long[] test_list;		/* TID list for testing */
	
	public MailboxB() {
		super("Mailbox");
		SynchPort<Integer> sp = new SynchPort<Integer>();
		prod_reply = new ArrayList<SynchPort<Integer>>();
		waiting_prod = 0;
		waiting_cons = false;
		for (int i = 0; i < 10; i++) {
			blocked[i] = false;
			/* we add empty ports to the list */
			prod_reply.add(sp);
		}
		buffer = new Queue();
		tid_queue = new TidQueue();
		testEnable = false;
		test_list = null;
		
		setDaemon(true);
	}
	
	public MailboxB(boolean t, long[] tl) {
		super("Mailbox");
		SynchPort<Integer> sp = new SynchPort<Integer>();
		prod_reply = new ArrayList<SynchPort<Integer>>();
		waiting_prod = 0;
		waiting_cons = false;
		for (int i = 0; i < 10; i++) {
			blocked[i] = false;
			prod_reply.add(sp);
		}
		buffer = new Queue();
		tid_queue = new TidQueue();
		testEnable = t;
		test_list = tl;
		
		setDaemon(true);
	}
	
	public void run() {
		Message<Integer> msg_in, msg_out;
		msg_in = new Message<Integer>();
		int val, i;
		long prod_tid;
		String output;
		System.out.println("Mailbox started");
		while(true) {
			msg_in = request.receive();
			switch(msg_in.info) {
			case 0:	/* insert request */
				if (buffer.full()) {	/* buffer full */
					prod_reply.set(msg_in.priority - 1, msg_in.ret);
					waiting_prod++;
					blocked[msg_in.priority - 1] = true;
				} else {				/* buffer not full */
					msg_out = new Message<Integer>();
					msg_in.ret.send(msg_out);
					msg_in = insert_p.receive();
					buffer.insert(msg_in.info);
					tid_queue.insert(msg_in.tid);
					if (waiting_cons) {	/* consumer is waiting */
						val = buffer.remove();
						prod_tid = tid_queue.remove();
						msg_out = new Message<Integer>();
						msg_out.info = val;
						msg_out.tid = prod_tid;
						waiting_cons = false;
						ConsumerB.in.send(msg_out);
					}
				}
				break;
			case 1: /* remove request */
				if (buffer.empty()) {	/* buffer empty */
					waiting_cons = true;
					System.out.println("Consumer is waiting");
				} else {
					val = buffer.remove();
					prod_tid = tid_queue.remove();
					msg_out = new Message<Integer>();
					msg_out.info = val;
					msg_out.tid = prod_tid;
					msg_in.ret.send(msg_out);
					if (waiting_prod > 0) {	/* producers are waiting */
						i = 0;
						if (testEnable) {
							output = "Producers waiting: ";
							for (int j = 0; j < 10; j++) {
								if (blocked[j]) {
									output += test_list[j] + " ";
								}
							}
							System.out.println(output);
						}
						/* search the high priority producer */
						while(!blocked[i]) i++;
						waiting_prod--;
						blocked[i] = false;
						msg_out = new Message<Integer>();
						prod_reply.get(i).send(msg_out);
						msg_in = insert_p.receive();
						buffer.insert(msg_in.info);
						tid_queue.insert(msg_in.tid);
						if (testEnable) System.out.println("Producer " + 
								msg_in.tid + " inserted value: " + msg_in.info);
					}
				}
				break;
			default:
				break;
			}
		}
	}
}
