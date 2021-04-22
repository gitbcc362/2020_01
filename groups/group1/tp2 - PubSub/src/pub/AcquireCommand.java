package pub;

import java.util.Set;
import java.util.SortedSet;

import core.Message;
import core.MessageImpl;
import core.PubSubCommand;
import core.client.Client;

/**
 * Implementa mensagem de acesso à uma variável X.
 */
public class AcquireCommand implements PubSubCommand{

	@Override
	public Message execute(Message m, SortedSet<Message> log, Set<String> subscribers) {
		
		Message response = new MessageImpl();
		int logId = m.getLogId();
		logId++;
		
		response.setLogId(logId);
		m.setLogId(logId);
		
		log.add(m);
		for(Message m1:log){
			System.out.print(m1.getLogId() + " "+ m1.getContent()+" (" + m1.getType() + ") | ");
		}
		System.out.println();
		
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
		
		String resource = m.getContent().split("_")[2] + "_credentials";
		response.setContent(resource);
		response.setType("acquire_ack");

		return response;

	}
}