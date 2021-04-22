package sub;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;

import core.Address;
import core.Message;
import core.MessageImpl;
import core.PubSubCommand;
import core.client.Client;

public class SubCommandTwo implements PubSubCommand{

	@Override
	public Message execute(Message m, SortedSet<Message> log, List<String> subscribers, Address backup) {
		
		Message response = new MessageImpl();
				
		if(subscribers.contains(m.getContent())){
			response.setContent("subscriber exists: " + m.getContent());
			response.setType("sub_error");
		}
		else{
			int logId = m.getLogId();
			logId++;
			
			response.setLogId(logId);
			m.setLogId(logId);
			
			subscribers.add(m.getContent());
			log.add(m);


			if (! backup.empty()){
				try {
					Client client = new Client(backup.getIp(), backup.getPort());
					Message aux = new MessageImpl();					
					aux.setType("msgSync");
					aux.setContent(m.getType() + "=>" + m.getContent());
					aux.setLogId(m.getLogId());
					aux.setBrokerId(m.getBrokerId());
					client.sendReceive(aux);
				} catch (IOException e) {
					System.out.println("[BROKER] Deletando backup...");
					backup.setIp(null);
				}
			}
			
			
			response.setContent(!backup.empty() ? backup.toString() : "");

			//start a client to send all existing log messages
			//for the subscribed user
			if(!log.isEmpty()){
				String[] ipAndPort = m.getContent().split(":");
				for(Message msg : log){

					if (! (msg.getType().equals("acquire_finished") ||  msg.getContent().startsWith("release")) ){
						try {
							Client client = new Client(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
							Message aux = new MessageImpl();					
							aux.setType("notify");
							aux.setContent(msg.getContent());
							aux.setLogId(msg.getLogId());
							aux.setBrokerId(m.getBrokerId());
							client.sendReceive(aux);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
				}
			}
			response.setType("sub_ack");
		}

		
		return response;

	}

}

