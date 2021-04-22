package sub;

import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;

import core.Message;
import core.MessageImpl;
import core.PubSubCommand;
import core.client.Client;

public class SubCommandTwo implements PubSubCommand{

	@Override
	public Message execute(Message m, SortedSet<Message> log, Set<String> subscribers) {
		
		Message response = new MessageImpl();
				
		if(subscribers.contains(m.getContent()))
			response.setContent("subscriber exists: " + m.getContent());
		else{
			int logId = m.getLogId();
			logId++;
			
			response.setLogId(logId);
			m.setLogId(logId);
			
			subscribers.add(m.getContent());
			log.add(m);
			
			response.setContent("Subscriber added: " + m.getContent());
			
			//start a client to send all existing log messages
			//for the subscribed user
			if(!log.isEmpty()){
				String[] ipAndPort = m.getContent().split(":");
				for(Message msg : log){

					if (! (msg.getType().equals("acquire_finished") ||  msg.getContent().startsWith("release")) ){
						Client client = new Client(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
						Message aux = new MessageImpl();					
						aux.setType("notify");
						aux.setContent(msg.getContent());
						aux.setLogId(msg.getLogId());
						aux.setBrokerId(m.getBrokerId());
						client.sendReceive(aux);
					}
					
				}
			}
		
		}
		
		response.setType("sub_ack");
		
		return response;

	}

}

