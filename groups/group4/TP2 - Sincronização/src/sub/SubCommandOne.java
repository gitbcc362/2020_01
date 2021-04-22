package sub;

import java.util.Set;
import java.util.SortedSet;

import core.Message;
import core.MessageImpl;
import core.PubSubCommand;

public class SubCommandOne implements PubSubCommand{

	@Override
	public Message execute(Message m, SortedSet<Message> log, Set<String> subscribers) {
		
		Message response = new MessageImpl();
				
		if(subscribers.contains(m.getContent())){
			response.setContent("subscriber exists: " + m.getContent());
			response.setLogId(m.getLogId());
		}else{
			int logId = m.getLogId();
			logId++;
			
			response.setLogId(logId);
			m.setLogId(logId);
			
			subscribers.add(m.getContent());
			log.add(m);
			
			response.setContent("Subscriber added: " + m.getContent());
			/*String[] ipAndPort = m.getContent().split(":");
			Client client = new Client(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
			Message msg = new MessageImpl();
			msg.setType("notify");
			msg.setContent("Subscriber added: " + m.getContent());
			msg.setLogId(logId);
			client.sendReceive(msg);*/
		}
		
		response.setType("sub_ack");
		
		return response;

	}

}
