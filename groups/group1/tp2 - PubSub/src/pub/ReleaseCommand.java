package pub;

import java.util.Set;
import java.util.SortedSet;

import core.Message;
import core.MessageImpl;
import core.PubSubCommand;
import core.client.Client;

/**
 * Implementa mensagem de Release para uma variável X
 */
public class ReleaseCommand implements PubSubCommand {
    @Override
	public Message execute(Message m, SortedSet<Message> log, Set<String> subscribers) {
		
		Message response = new MessageImpl();
		int logId = m.getLogId();
		logId++;
		
		response.setLogId(logId);
		m.setLogId(logId);
		
		log.add(m);

		String[] content = m.getContent().split("_");
		String resource = content[1] + "_" + content[2];
		
		for( Message message : log ) {
			// firstAcquire
			if(message.getType().equals("acquire") && message.getContent().equals("acquire_" + resource)){
				message.setType("acquire_finished");
				break;
			}
		}

		Message msg = new MessageImpl();
		msg.setContent(m.getContent());
		msg.setLogId(logId);
		msg.setType("wakeup");
				
		for(String aux:subscribers){
			String[] ipAndPort = aux.split(":");
			Client client = new Client(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
			msg.setBrokerId(m.getBrokerId());
			client.sendReceive(msg);
		}
		
		//String resource = m.getContent().split("_")[2];

		System.out.println("[BROKER] Released: " + resource);
		response.setContent("Released: " + resource);
		response.setType("release_ack");
		

		return response;

	}
}
