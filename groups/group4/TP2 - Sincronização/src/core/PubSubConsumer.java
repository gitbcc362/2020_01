package core;


import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;


//the useful socket consumer
public class PubSubConsumer<S extends Socket> extends GenericConsumer<S>{
	
	private int uniqueLogId;
	private SortedSet<Message> log;
	private Set<String> subscribers;
		
	public PubSubConsumer(GenericResource<S> re) {		
		super(re);
		uniqueLogId = 1;
		log = new TreeSet<Message>(new MessageComparator());
		subscribers = new TreeSet<String>();
		
	}
	
	
	@Override
	protected void doSomething(S str) {
		try{
			// TODO Auto-generated method stub
			ObjectInputStream in = new ObjectInputStream(str.getInputStream());
			
			Message msg = (Message) in.readObject();
			//msg.setBrokerId(brokerId);
			
			if(!msg.getType().equals("notify"))
				msg.setLogId(uniqueLogId);
			
			Message response = commands.get(msg.getType()).execute(msg, log, subscribers);
			
			if(!msg.getType().equals("notify"))
				uniqueLogId = msg.getLogId();
				
			
			ObjectOutputStream out = new ObjectOutputStream(str.getOutputStream());
			out.writeObject(response);
			out.flush();
			out.close();
			in.close();
						
			str.close();
				
		}catch (Exception e){
			e.printStackTrace();
			
		}
				
	}	
	
	public Set<Message> getMessages(){
		return log;
	}

}
