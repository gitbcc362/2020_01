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
 * Implementa mensagem de Release para uma variável X
 */
public class ReleaseCommand implements PubSubCommand {
    @Override
	public Message execute(Message m, SortedSet<Message> log, List<String> subscribers, Address backup) {
		
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
		
		//String resource = m.getContent().split("_")[2];

		System.out.println("[BROKER] Released: " + resource);
		response.setContent("Released: " + resource);
		response.setType("release_ack");
		

		return response;

	}
}
