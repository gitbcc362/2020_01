package sync;

import core.Message;
import core.MessageImpl;
import core.PubSubCommand;

import java.util.Set;
import java.util.SortedSet;

public class GiveMeSecCommand implements PubSubCommand {
    @Override
    public Message execute(Message m, SortedSet<Message> log, Set<String> subscribers, boolean isPrimary, String sencondaryServerAddress, int secondaryServerPort) {
        Message response = new MessageImpl();

        response.setLogId(m.getLogId());

        log.add(m);

        response.setContent(sencondaryServerAddress);
        response.setBrokerId(secondaryServerPort);

        return response;
    }
}
