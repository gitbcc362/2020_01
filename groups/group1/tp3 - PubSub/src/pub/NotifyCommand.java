package pub;

import java.util.List;
import java.util.SortedSet;

import core.Address;
import core.Message;
import core.MessageImpl;
import core.PubSubCommand;

public class NotifyCommand implements PubSubCommand{

	@Override
	public Message execute(Message m, SortedSet<Message> log, List<String> subscribers, Address backup) {
		
		Message response = new MessageImpl();
		
		System.out.println("[NOTIFY] " + m.getContent());
		
		response.setContent("Message notified: " + m.getContent());
		
		response.setType("notify_ack");

		synchronized (log){
			log.add(m);
		}
				
		return response;

	}

}

