package core;


import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import utils.Tuple;


//the useful socket consumer
public class PubSubConsumer<S extends Socket> extends GenericConsumer<S>{
	
	public PubSubConsumer(GenericResource<S> re,  GenericResource<Tuple<Socket, Message>> primary,  GenericResource<Tuple<Socket, Message>> sync) {
        super(re);
		this.log = new TreeSet<Message>(new MessageComparator());

        this.syncResource = sync;
        this.primaryResource = primary;

		this.subscribers = new ArrayList<String>();
    }

	private SortedSet<Message> log;
	private List<String> subscribers;

	private GenericResource<Tuple<Socket, Message>> syncResource;
	private GenericResource<Tuple<Socket, Message>> primaryResource;

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

			if (! msg.getType().toLowerCase().contains("sync")) 
                primaryResource.putRegister(new Tuple<Socket, Message>(str, msg)); 
			else 
				syncResource.putRegister(new Tuple<Socket, Message>(str, msg));
				// se for nulo poderia emitir uma mensagem de erro dizendo que o primário não executa sync -> Primário n deve chegar nesta linha
			
		}catch (Exception e){
			e.printStackTrace();  
        }
    }


    @Override
    public SortedSet<Message> getMessages() {
        return log;
    }

    public void setSyncResource(GenericResource<Tuple<Socket, Message>> syncResource){
		this.syncResource = syncResource;
	}

    public void setPrimaryResource(GenericResource<Tuple<Socket, Message>> primaryResource){
		this.primaryResource = primaryResource;
	}

    public List<String> getSubscribers() {
		return subscribers;
	}

}