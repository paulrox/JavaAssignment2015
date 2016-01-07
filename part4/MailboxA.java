package part4;

import part2.SynchPort;
import part2.Message;

public class MailboxA extends Thread{
	public SynchPort<TidMsg<Integer>> request;
	public SynchPort<TidMsg<Integer>> insert_p;
	SynchPort<TidMsg<Integer>> cons_port;
	int waiting_prod;
	boolean waiting_cons;
	Queue buffer;
	TidQueue tid_queue;
	
	public MailboxA() {
		super("Mailbox");
		request = new SynchPort<TidMsg<Integer>>();
		insert_p = new SynchPort<TidMsg<Integer>>();
		waiting_prod = 0;
		waiting_cons = false;
		buffer = new Queue();
		tid_queue = new TidQueue();
		
		setDaemon(true);
	}
	
	public void run() {
		System.out.println("Mailbox started");
		Message<TidMsg<Integer>> msg_in, msg_out;
		msg_in = new Message<TidMsg<Integer>>();
		int val;
		long prod_tid;
		while(true) {
			if (!buffer.empty() || !buffer.full()) {
				msg_in = request.receive();
				switch(msg_in.info.value) {
				case 0:	/* insert request */
					if (buffer.full()) {
						waiting_prod++;
					} else {
						msg_in = insert_p.receive();
						buffer.insert(msg_in.info.value);
						tid_queue.insert(msg_in.info.tid);;
						if (waiting_cons) {
							val = buffer.remove();
							prod_tid = tid_queue.remove();
							msg_out = new Message<TidMsg<Integer>>();
							msg_out.info = new TidMsg<Integer>(val, prod_tid);
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
						msg_out = new Message<TidMsg<Integer>>();
						msg_out.info = new TidMsg<Integer>(val, prod_tid);
						msg_in.ret.send(msg_out);
						if (waiting_prod > 0) {
							waiting_prod--;
							msg_in = insert_p.receive();
							buffer.insert(msg_in.info.value);
							tid_queue.insert(msg_in.info.tid);
						}
					}
				}
			}
		}
	}
}
