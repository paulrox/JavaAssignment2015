package part4;

import part2.SynchPort;
import part2.Message;

public class MailboxA extends Thread{
	/* port for receiving service requests */
	public static SynchPort<Integer> request = new SynchPort<Integer>();
	/* port for receiving producers values */
	public static SynchPort<Integer> insert_p = new SynchPort<Integer>();
	int waiting_prod;		/* numbers of waiting producers */
	boolean waiting_cons;	/* true if the consumer is waiting */
	Queue buffer;			/* Mailbox buffer */
	TidQueue tid_queue;		/* producers TID queue */
	
	public MailboxA() {
		super("Mailbox");
		waiting_prod = 0;
		waiting_cons = false;
		buffer = new Queue();
		tid_queue = new TidQueue();
		
		setDaemon(true);
	}
	
	public void run() {
		System.out.println("Mailbox started");
		Message<Integer> msg_in, msg_out;
		msg_in = new Message<Integer>();
		int val;
		long prod_tid;
		while(true) {
			msg_in = request.receive();
			switch(msg_in.info) {
			case 0:	/* insert request */
				if (buffer.full()) {
					waiting_prod++;
				} else {
					msg_in = insert_p.receive();
					buffer.insert(msg_in.info);
					tid_queue.insert(msg_in.tid);;
					if (waiting_cons) {
						val = buffer.remove();
						prod_tid = tid_queue.remove();
						msg_out = new Message<Integer>();
						msg_out.info = val;
						msg_out.tid = prod_tid;
						waiting_cons = false;
						ConsumerA.in.send(msg_out);
					}
				}
				break;
			case 1: /* remove request */
				if (buffer.empty()) {
					waiting_cons = true;
				} else {
					val = buffer.remove();
					prod_tid = tid_queue.remove();
					msg_out = new Message<Integer>();
					msg_out.info = val;
					msg_out.tid = prod_tid;
					ConsumerA.in.send(msg_out);
					if (waiting_prod > 0) {
						waiting_prod--;
						msg_in = insert_p.receive();
						buffer.insert(msg_in.info);
						tid_queue.insert(msg_in.tid);
					}
				}
				break;
			default:
				break;
			}
		}
	}
}
