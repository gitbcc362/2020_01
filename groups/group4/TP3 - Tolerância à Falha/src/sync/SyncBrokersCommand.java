package sync;

import java.util.Set;
import java.util.SortedSet;

import core.Message;
import core.MessageImpl;
import core.PubSubCommand;

public class SyncBrokersCommand implements PubSubCommand{

	@Override
	public Message execute(Message m, SortedSet<Message> log, Set<String> subscribers, boolean isPrimary, String primaryServerAddress, int primaryServerPort, String sencondaryServerAddress, int secondaryServerPort) {

		Message response = new MessageImpl();
		response.setLogId(m.getLogId());
		
		log.add(m);

		String msg = primaryServerAddress+":"+primaryServerPort+"-"+sencondaryServerAddress+":"+secondaryServerPort;
		response.setContent(msg);
		response.setType("getAddrs");
		
		return response;
	}

}
