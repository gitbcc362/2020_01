package sub;

import java.util.List;
import java.util.SortedSet;

import core.Address;
import core.Message;
import core.MessageImpl;
import core.PubSubCommand;

public class SubCommandOne implements PubSubCommand{

	@Override
	public Message execute(Message m, SortedSet<Message> log, List<String> subscribers, Address backup) {
		
		Message response = new MessageImpl();
				
		if(subscribers.contains(m.getContent())){
			response.setContent("subscriber exists: " + m.getContent());
			response.setLogId(m.getLogId());
			System.out.println("[BROKER] -> Subscriber exists: " + m.getContent());
		}else{
			int logId = m.getLogId();
			logId++;
			
			response.setLogId(logId);
			m.setLogId(logId);
			
			subscribers.add(m.getContent());
			log.add(m);
			
			response.setContent("Subscriber added: " + m.getContent());
			System.out.println("[BROKER] -> Subscriber added: " + m.getContent());

		}
		
		response.setType("sub_ack");
		
		return response;

	}

}
