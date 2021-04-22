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
		/**
		 * Método executado pelo consumidor único do servidor do PubSubClient ou do Broker
		 * Ao conectar, o servidor produz o socket que será consumido neste método.
		 * Basicamente lê a mensagem, executa o comando requerido e envia a resposta obtida pelo comando.
		 * Atualmente, as mensagens podem ser:
		 * 	-> publicar alguma coisa para todos subscribers
		 *  -> receber uma notificação de algum nó que me inscrevi
		 *  -> subscrever em algum server
		 */
		try{
			ObjectInputStream in = new ObjectInputStream(str.getInputStream());
			
			Message msg = (Message) in.readObject();
			//msg.setBrokerId(brokerId);
			
			if(!(msg.getType().equals("notify")) && !(msg.getType().equals("wakeup")) )
				msg.setLogId(uniqueLogId);
			
			
			Message response = commands.get(msg.getType()).execute(msg, log, subscribers);
			
			if(!(msg.getType().equals("notify"))  && !(msg.getType().equals("wakeup")))
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
