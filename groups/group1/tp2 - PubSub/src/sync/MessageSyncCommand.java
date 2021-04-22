package sync;

import java.io.IOException;
import java.util.List;
import java.util.SortedSet;

import core.Address;
import core.Message;
import core.MessageImpl;
import core.PubSubCommand;
import core.client.Client;

public class MessageSyncCommand implements PubSubCommand{

	@Override
	public Message execute(Message m, SortedSet<Message> log, List<String> subscribers, Address backup) {
		
		String[] content = m.getContent().split("=>");
		
		boolean help = false;
		int start = 0; 

		if (content.length == 3){
			start = Integer.parseInt(content[2]);
			help = true;
		}

		m.setType(content[0]);
		m.setContent(content[1]);

		log.add(m);

		if (m.getType().equals("sub"))
			subscribers.add(m.getContent());
		
		if (m.getType().equals("unsub")){
			subscribers.remove(m.getContent());
			System.out.println("********************************************************************");
			for(Message m1:log){
				System.out.println("  ["+  m1.getLogId() + "] -- " + m1.getType()  + " -- " + m1.getContent());
			}
			System.out.println("********************************************************************");

			System.out.println();
		}

		if (m.getType().equals("release")) {
			String[] releaseContent = m.getContent().split("_");
			String resource = releaseContent[1] + "_" + releaseContent[2];

			for( Message message : log ) {
				// firstAcquire
				if(message.getType().equals("acquire") && message.getContent().equals("acquire_" + resource)){
					message.setType("acquire_finished");
					break;
				}
			}

		}

		if (help) {
			Message msg = new MessageImpl();
			msg.setContent(m.getContent());
			msg.setLogId(m.getLogId());
			msg.setType("notify");
			
			for(int i = start; i < subscribers.size(); i++) {
				try {
					String aux = subscribers.get(i);
					System.out.println("[BACKUP] Notificando subscribe " + aux);
					String[] ipAndPort = aux.split(":");
					Client client = new Client(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
					msg.setBrokerId(m.getBrokerId());
					client.sendReceive(msg);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}


        Message response = new MessageImpl();

		response.setContent("Message synchronized: " + m.getContent());
		response.setType("sync_ack");
		
		System.out.println("[SYNC] Message synchronized: (" + m.getType() + ") " +  m.getContent());

		return response;

	}

}
