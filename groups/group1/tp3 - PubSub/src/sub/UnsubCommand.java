package sub;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;

import core.Address;
import core.Message;
import core.MessageImpl;
import core.PubSubCommand;
import core.client.Client;

public class UnsubCommand implements PubSubCommand{

	@Override
	public Message execute(Message m, SortedSet<Message> log, List<String> subscribers, Address backup) {
		Message response = new MessageImpl();
				
		if(!subscribers.contains(m.getContent()))
			response.setContent("Subscriber not exists: " + m.getContent());
		else{
			int logId = m.getLogId();
			logId++;
			
			response.setLogId(logId);
			m.setLogId(logId);
			
			subscribers.remove(m.getContent());

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

			log.add(m);
			
			response.setContent("Subscriber removed: " + m.getContent());
			
			System.out.println("********************************************************************");
			for(Message m1:log){
				System.out.println("  ["+  m1.getLogId() + "] -- " + m1.getType()  + " -- " + m1.getContent());
			}
			System.out.println("********************************************************************");

			System.out.println();
		}
		
		response.setType("unsub_ack");
		
		return response;

	}

}

