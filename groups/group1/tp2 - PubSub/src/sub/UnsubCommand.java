package sub;

import java.util.Set;
import java.util.SortedSet;

import core.Message;
import core.MessageImpl;
import core.PubSubCommand;

public class UnsubCommand implements PubSubCommand{

	@Override
	public Message execute(Message m, SortedSet<Message> log, Set<String> subscribers) {
		
		Message response = new MessageImpl();
				
		if(!subscribers.contains(m.getContent()))
			response.setContent("Subscriber not exists: " + m.getContent());
		else{
			
			subscribers.remove(m.getContent());
			
			response.setContent("Subscriber removed: " + m.getContent());
		}
		
		response.setType("unsub_ack");
		
		return response;

	}

}

