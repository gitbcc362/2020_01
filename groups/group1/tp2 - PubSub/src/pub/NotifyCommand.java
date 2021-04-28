package pub;

import java.util.Set;
import java.util.SortedSet;

import core.Message;
import core.MessageImpl;
import core.PubSubCommand;

public class NotifyCommand implements PubSubCommand{

	@Override
	public Message execute(Message m, SortedSet<Message> log, Set<String> subscribers) {
		
		Message response = new MessageImpl();
		
		System.out.println("Message notified: " + m.getContent());
		
		response.setContent("Message notified: " + m.getContent());
		
		response.setType("notify_ack");

		synchronized (log){
			log.add(m);
		}
				
		return response;

	}

}

