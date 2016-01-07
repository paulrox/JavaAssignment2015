package part4;

import java.util.List;
import java.util.ArrayList;
import part2.SynchPort;
import part2.Message;

public class MailboxB extends Thread{
	public SynchPort<MessageB<Integer>> request;
	public SynchPort<MessageB<Integer>> insert_p;
	List <SynchPort<MessageB<Integer>>> prod_reply;
	SynchPort<MessageB<Integer>> cons_port;
	boolean[] blocked = new boolean[10];
	int waiting_prod;
	boolean waiting_cons;
	Queue buffer;
	TidQueue tid_queue;
	
	public MailboxB() {
		super("Mailbox");
		SynchPort<MessageB<Integer>> sp = new SynchPort<MessageB<Integer>>();
		request = new SynchPort<MessageB<Integer>>();
		insert_p = new SynchPort<MessageB<Integer>>();
		prod_reply = new ArrayList<SynchPort<MessageB<Integer>>>();
		waiting_prod = 0;
		waiting_cons = false;
		for (int i = 0; i < 10; i++) {
			blocked[i] = false;
			prod_reply.add(sp);
		}
		buffer = new Queue();
		tid_queue = new TidQueue();
		
		setDaemon(true);
	}
	
	public void run() {
		System.out.println("Mailbox started");
		Message<MessageB<Integer>> msg_in, msg_out;
		msg_in = new Message<MessageB<Integer>>();
		int val, i;
		long prod_tid;
		while(true) {
			if (!buffer.empty() || !buffer.full()) {
				msg_in = request.receive();
				switch(msg_in.info.value) {
				case 0:	/* insert request */
					if (buffer.full()) {
						waiting_prod++;
						blocked[msg_in.info.priority - 1] = true;
						prod_reply.add(msg_in.info.priority - 1, msg_in.ret);
					} else {
						msg_in = insert_p.receive();
						buffer.insert(msg_in.info.value);
						tid_queue.insert(msg_in.info.tid);;
						if (waiting_cons) {
							val = buffer.remove();
							prod_tid = tid_queue.remove();
							msg_out = new Message<MessageB<Integer>>();
							msg_out.info = new MessageB<Integer>(val, prod_tid);
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
						msg_out = new Message<MessageB<Integer>>();
						msg_out.info = new MessageB<Integer>(val, prod_tid);
						msg_in.ret.send(msg_out);
						if (waiting_prod > 0) {
							i = 0;
							while(!blocked[i]) i++;
							waiting_prod--;
							blocked[i] = false;
							msg_out = new Message<MessageB<Integer>>();
							msg_out.info = new MessageB<Integer>(0, -1);
							prod_reply.get(i).send(msg_out);
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
