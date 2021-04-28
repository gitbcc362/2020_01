package pub;

import java.util.Set;
import java.util.SortedSet;

import core.Message;
import core.MessageImpl;
import core.PubSubCommand;
import core.client.Client;

public class PubCommand implements PubSubCommand{

	@Override
	public Message execute(Message m, SortedSet<Message> log, Set<String> subscribers) {
		
		Message response = new MessageImpl();
		int logId = m.getLogId();
		logId++;
		
		response.setLogId(logId);
		m.setLogId(logId);
		
		log.add(m);
		
		Message msg = new MessageImpl();
		msg.setContent(m.getContent());
		msg.setLogId(logId);
		msg.setType("notify");
				
		for(String aux:subscribers){
			String[] ipAndPort = aux.split(":");
			Client client = new Client(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
			msg.setBrokerId(m.getBrokerId());
			client.sendReceive(msg);
		}
		
		response.setContent("Message published: " + m.getContent());
		response.setType("pub_ack");
		
		return response;

	}

}
