package core;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;


//the useful socket consumer
public class PubSubConsumer<S extends Socket> extends GenericConsumer<S>{
	
	private int uniqueLogId;
	private SortedSet<Message> log;
	private Set<String> subscribers;
	private boolean isPrimary;
	private String secondaryServer;
	private int secondaryPort;

	// add primary server info
	private String primaryServer;
	private int primaryPort;

	public PubSubConsumer(GenericResource<S> re, boolean isPrimary, String primaryServer, int primaryPort, String secondaryServer, int secondaryPort) {		
		super(re);
		uniqueLogId = 1;
		log = new TreeSet<Message>(new MessageComparator());
		subscribers = new TreeSet<String>();
		
		this.isPrimary = isPrimary;
		this.secondaryServer = secondaryServer;
		this.secondaryPort = secondaryPort;
		// add primary server info
		this.primaryPort = primaryPort;
		this.primaryServer = primaryServer;
	}
	
	//qdo chega uma msg do servidor esse m�todo � executado
	@Override
	protected void doSomething(S str) {
		try{
			// TODO Auto-generated method stub
			ObjectInputStream in = new ObjectInputStream(str.getInputStream());
			
			Message msg = (Message) in.readObject();
			
			Message response = null;

			if(msg.getType().startsWith("syncAddrs")){
				response = commands.get(msg.getType()).execute(msg, log, subscribers, isPrimary, primaryServer, primaryPort, secondaryServer, secondaryPort);

			}
			//verifica se � primario e se a msg n come�a com sync
			else if(!isPrimary && !msg.getType().startsWith("sync")){
				
				//Client client = new Client(secondaryServer, secondaryPort);
				//response = client.sendReceive(msg);
				//converso com o prim�rio
				response = new MessageImpl();
				response.setType("backup");
				response.setContent(secondaryServer+":"+secondaryPort);
				
			} else {
				//agora se eu s� o prim�rio fa�o o q eu j� fazia: vejo se � um pub...
				if(!msg.getType().equals("notify") && !msg.getType().startsWith("sync"))
					msg.setLogId(uniqueLogId);
				
				response = commands.get(msg.getType()).execute(msg, log, subscribers, isPrimary, primaryServer, primaryPort, secondaryServer, secondaryPort);
				
				if(!msg.getType().equals("notify"))
					uniqueLogId = msg.getLogId();
			}				
			
			//converso com o cliente
			ObjectOutputStream out = new ObjectOutputStream(str.getOutputStream());
			out.writeObject(response);
			out.flush();
			out.close();
			in.close();
						
			str.close();
				
		}catch (Exception e){
			try {
				str.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
				
	}	
	
	public List<Message> getMessages(){
		CopyOnWriteArrayList<Message> logCopy = new CopyOnWriteArrayList<Message>();
		logCopy.addAll(log);
		
		return logCopy;
	}

}
