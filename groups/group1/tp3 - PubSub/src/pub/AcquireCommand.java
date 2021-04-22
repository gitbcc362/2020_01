package pub;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;

import core.Address;
import core.Message;
import core.MessageImpl;
import core.PubSubCommand;
import core.client.Client;

/**
 * Implementa mensagem de acesso à uma variável X.
 */
public class AcquireCommand implements PubSubCommand{

	@Override
	public Message execute(Message m, SortedSet<Message> log, List<String> subscribers, Address backup) {
		
		Message response = new MessageImpl();
		int logId = m.getLogId();
		logId++;
		
		response.setLogId(logId);
		m.setLogId(logId);
		
		log.add(m);

		int notifyLimit = subscribers.size();
				
		if (! backup.empty() ){
			try {
				notifyLimit =  (subscribers.size() / 2) + 1;
				Client client = new Client(backup.getIp(), backup.getPort());
				Message aux = new MessageImpl();					
				aux.setType("msgSync");
				aux.setContent(m.getType() + "=>" + m.getContent() + "=>" + notifyLimit);
				aux.setLogId(m.getLogId());
				aux.setBrokerId(m.getBrokerId());
				client.sendReceive(aux);
			} catch (IOException e) {
				System.out.println("[BROKER] Deletando backup...");
				notifyLimit = subscribers.size();
				backup.setIp(null);
			}
		}

		Message msg = new MessageImpl();
		msg.setContent(m.getContent());
		msg.setLogId(logId);
		msg.setType("notify");
		
		for(int i = 0; i < notifyLimit; i++){
			try {
				String aux = subscribers.get(i);
				String[] ipAndPort = aux.split(":");
				Client client = new Client(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
				msg.setBrokerId(m.getBrokerId());
				client.sendReceive(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		String resource = m.getContent().split("_")[2] + "_credentials";
		response.setContent(resource);
		response.setType("acquire_ack");

		return response;

	}
}