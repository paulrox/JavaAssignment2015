package part4;

import part2.SynchPort;
import part2.Message;

public class MailboxA extends Thread{
	public SynchPort<Integer> request;
	public SynchPort<Integer> insert_p;
	SynchPort<Integer> cons_port;
	int waiting_prod;
	boolean waiting_cons;
	Queue buffer;
	TidQueue tid_queue;
	
	public MailboxA() {
		super("Mailbox");
		request = new SynchPort<Integer>();
		insert_p = new SynchPort<Integer>();
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
			if (!buffer.empty() || !buffer.full()) {
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
							cons_port.send(msg_out);
						}
					}
					break;
				case 1: /* remove request */
					if (buffer.empty()) {
						cons_port = msg_in.ret;
						waiting_cons = true;
					} else {
						val = buffer.remove();
						prod_tid = tid_queue.remove();
						msg_out = new Message<Integer>();
						msg_out.info = val;
						msg_out.tid = prod_tid;
						msg_in.ret.send(msg_out);
						if (waiting_prod > 0) {
							waiting_prod--;
							msg_in = insert_p.receive();
							buffer.insert(msg_in.info);
							tid_queue.insert(msg_in.tid);
						}
					}
				}
			}
		}
	}
}
