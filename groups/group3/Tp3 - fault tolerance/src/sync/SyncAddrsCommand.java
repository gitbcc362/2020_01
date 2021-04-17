package sync;

import java.util.Set;
import java.util.SortedSet;

import core.Message;
import core.MessageImpl;
import core.PubSubCommand;

public class SyncAddrsCommand implements PubSubCommand {

	@Override
	public Message execute(Message m, SortedSet<Message> log, Set<String> subscribers, boolean isPrimary,
			String sencondaryServerAddress, int secondaryServerPort) {
		System.out.println(
				"sencondaryServerAddress " + sencondaryServerAddress + "secondaryServerPort" + secondaryServerPort);
		Message response = new MessageImpl();

		response.setLogId(m.getLogId());

		response.setContent(sencondaryServerAddress);
		response.setBrokerId(secondaryServerPort);

		return response;
	}
}
