package pub;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;

import core.Address;
import core.Message;
import core.MessageImpl;
import core.PubSubCommand;
import core.client.Client;

public class PubCommand implements PubSubCommand{

	@Override
	public Message execute(Message m, SortedSet<Message> log, List<String> subscribers, Address backup) {
		
		Message response = new MessageImpl();
		int logId = m.getLogId();
		logId++;
		
		response.setLogId(logId);
		m.setLogId(logId);
		
		log.add(m);

		if (! backup.empty() ){
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
		
		
		Message msg = new MessageImpl();
		msg.setContent(m.getContent());
		msg.setLogId(logId);
		msg.setType("notify");

		for(String aux:subscribers){
			try {
				String[] ipAndPort = aux.split(":");
				Client client = new Client(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
				msg.setBrokerId(m.getBrokerId());
				client.sendReceive(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		response.setContent("Message published: " + m.getContent());
		response.setType("pub_ack");
		
		return response;

	}

}
