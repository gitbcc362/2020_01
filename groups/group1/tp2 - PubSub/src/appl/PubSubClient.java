package appl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import core.Address;
import core.Message;
import core.MessageImpl;
import core.Server;
import core.client.Client;

public class PubSubClient {
	
	private Server observer;
	private ThreadWrapper clientThread;

	private Address clientAddress;

	private List<Address> brokerAddresses;
	
	public PubSubClient(){
		//this constructor must be called only when the method
		//startConsole is used
		//otherwise the other constructor must be called
		this.brokerAddresses = new ArrayList<Address>();
	}
	
	public PubSubClient(String clientAddress, int clientPort){

		this.clientAddress = new Address(clientAddress, clientPort);
		this.brokerAddresses = new ArrayList<Address>();

		observer = new Server(clientPort);
		clientThread = new ThreadWrapper(observer);
		clientThread.start();
	}
	
	public void subscribe(String brokerAddress, int brokerPort){
					
		Message msgBroker = new MessageImpl();
		msgBroker.setBrokerId(brokerPort);
		msgBroker.setType("sub");
		msgBroker.setContent(this.clientAddress.toString());
		Client subscriber = new Client(brokerAddress, brokerPort);
		subscriber.sendReceive(msgBroker);
	}
	
	public void publish(String message, String brokerAddress, int brokerPort){
		Message msgPub = new MessageImpl();
		msgPub.setBrokerId(brokerPort);
		msgPub.setType("pub");
		msgPub.setContent(message);
		
		Client publisher = new Client(brokerAddress, brokerPort);

		//  aq ele recebe o feedback do broker
		publisher.sendReceive(msgPub);
		
	}

	public void useResource(String credentials) throws InterruptedException {
		System.out.println("[" + this.clientAddress.toString() + "] Estou acessando o recurso : " + credentials);
		Thread.sleep(4000); // wait -> ??
		System.out.println("[" + this.clientAddress.toString() + "] Terminei de usar o recurso : " + credentials);
	}

	private Message firstAcquire(String resource){
		// quando eu der um release, eu mudar o tipo da mensagem para acquire_finished
		// criar um log auxiliar que controla os acquires
		for( Message message : this.observer.getLogMessages() ) {
			String[] protocolMessage = message.getContent().split("_"); // messageType_ClientAddr_resource => notify, wakeup
			String resourceName = resource.split("_")[0]; // resource_credentials
			// notify: acquire_Marcos_varX ==> acquire_finished: acquire_Marcos_varX
			if(!(message.getType().equals("acquire_finished")) && protocolMessage[0].equals("acquire") && protocolMessage[2].equals(resourceName))
				return message;
		}

		throw new IllegalStateException("No acquire resources found.");
	}


	/**
	 * tentativa de acessar um recurso
	 * @param id
	 * @param credentials
	 * @throws InterruptedException
	 */
	private String accessResource(int id, String resource) throws InterruptedException {

		Set<Message> logs = this.observer.getLogMessages();

		synchronized (logs) {
			while (firstAcquire(resource).getLogId() != id) {
				System.out.println("Estou esperando pelo recurso " + resource);
				logs.wait();
			}
		}
		
		// na hora que chegar uma mensagem no log, eu dar um notify -> independente de qual release for.
		// jeito nao elegante, esperar um tempo e olhar de novo
		return resource; 
	}

	public String acquire(String resource, String brokerAddress, int brokerPort) throws InterruptedException{
		Message msgPub = new MessageImpl();
		msgPub.setBrokerId(brokerPort);
		msgPub.setType("acquire");
		msgPub.setContent("acquire_"+this.clientAddress.toString()+"_"+resource);
		
		Client publisher = new Client(brokerAddress, brokerPort);

		//  aq ele recebe o feedback do broker - acquire_ack
		Message response = publisher.sendReceive(msgPub); // recupero o __offset__
		System.out.println(response.getLogId());
		
		if (response.getType().equals("acquire_ack")) {
			// verificar se é a minha vez, se for acesso o recurso que está em response.content
			// se não for eu durmo e espero o server me acordar em caso de releases. aí eu verifico dnv
			return this.accessResource(response.getLogId(), response.getContent());
		}
		else 
			throw new IllegalStateException("No acquire_ack  found.");	
	}

	public void release(String resource, String brokerAddress, int brokerPort) {
		Message msgPub = new MessageImpl();
		msgPub.setBrokerId(brokerPort);
		msgPub.setType("release");
		msgPub.setContent("release_"+this.clientAddress.toString()+"_"+resource);
		
		Client publisher = new Client(brokerAddress, brokerPort);

		//  aq ele recebe o feedback do broker
		publisher.sendReceive(msgPub);
	}

	public void unsubscribe(String brokerAddress, int brokerPort){
		Message msgPub = new MessageImpl();
		msgPub.setBrokerId(brokerPort);
		msgPub.setType("unsub");
		msgPub.setContent(this.clientAddress.toString());
		
		Client publisher = new Client(brokerAddress, brokerPort);

		//  aq ele recebe o feedback do broker
		publisher.sendReceive(msgPub);
		
	}

	public Set<Message> getLogMessages(){
		return observer.getLogMessages();
	}

	public void stopPubSubClient(){
		System.out.println("Client stopped...");
		observer.stop();
		clientThread.interrupt();
	}
		
	public void startConsole(){
		Scanner reader = new Scanner(System.in);  // Reading from System.in


		System.out.print("Enter the client address (ex. localhost): ");
		String clientAddress = reader.next();
		System.out.print("Enter the client port (ex.8080): ");
		int clientPort = reader.nextInt();
		

		System.out.println("Now you need to inform the broker credentials...");
		System.out.print("Enter the broker address (ex. localhost): ");
		String brokerAddress = reader.next();
		System.out.print("Enter the broker port (ex.8080): ");
		int brokerPort = reader.nextInt();
		
		observer = new Server(clientPort);
		clientThread = new ThreadWrapper(observer);
		clientThread.start();
		
		Message msgBroker = new MessageImpl();
		msgBroker.setType("sub");
		msgBroker.setBrokerId(brokerPort);
		msgBroker.setContent(clientAddress+":"+clientPort);
		Client subscriber = new Client(brokerAddress, brokerPort);
		subscriber.sendReceive(msgBroker);
		this.brokerAddresses.add(new Address(brokerAddress, brokerPort));

		System.out.println("Do you want to subscribe for more brokers? (Y|N)");
		String resp = reader.next();
		
		if(resp.equals("Y")||resp.equals("y")){
			String message = "";
			Message msgSub = new MessageImpl();
			msgSub.setType("sub");
			msgSub.setContent(clientAddress+":"+clientPort);
			while(!message.equals("exit")){
				System.out.println("You must inform the broker credentials...");
				System.out.print("Enter the broker address (ex. localhost): ");
				brokerAddress = reader.next();
				System.out.print("Enter the broker port (ex.8080): ");
				brokerPort = reader.nextInt();
				subscriber = new Client(brokerAddress, brokerPort);
				msgSub.setBrokerId(brokerPort);
				subscriber.sendReceive(msgSub);
				System.out.println(" Write exit to finish...");
				message = reader.next();
				this.brokerAddresses.add(new Address(brokerAddress, brokerPort));
			}
		}
		
		System.out.println("Do you want to publish messages? (Y|N)");
		resp = reader.next();
		if(resp.equals("Y")||resp.equals("y")){
			String message = "";			
			Message msgPub = new MessageImpl();
			msgPub.setType("pub");
			while(!message.equals("exit")){
				System.out.println("Enter a message (exit to finish submissions): ");
				message = reader.next();
				msgPub.setContent(message);
								
				Address broker = selectBroker(reader);
				
				msgPub.setBrokerId(broker.getPort());
				Client publisher = new Client(broker.getIp(), broker.getPort());
				publisher.sendReceive(msgPub);
				
				Set<Message> log = observer.getLogMessages();
				
				Iterator<Message> it = log.iterator();
				System.out.print("Log itens: ");
				while(it.hasNext()){
					Message aux = it.next();
					System.out.print(aux.getContent() + aux.getLogId() + " | ");
				}
				System.out.println();

			}
		}
		
		System.out.print("Shutdown the client (Y|N)?: ");
		resp = reader.next(); 
		if (resp.equals("Y") || resp.equals("y")){
			System.out.println("Client stopped...");
			observer.stop();
			clientThread.interrupt();
			
		}
		
		//once finished
		reader.close();
	}

	private Address selectBroker(Scanner reader){
		System.out.println("Select broker credentials: ");

		for(int i = 0 ; i < this.brokerAddresses.size(); i++){
			System.out.println("\t ["+ i+1 + "] : " + this.brokerAddresses.get(i).toString());
		}

		while (reader.hasNext()) {
			int answer = reader.nextInt();
			if(answer > 0 && answer <= this.brokerAddresses.size()) {
				System.out.println("Broker " + answer + " selected.");
				return this.brokerAddresses.get(answer-1);
			}
	
			System.out.println("[ERROR] Select a valid broker. Try again:");
		}		

		throw new IllegalStateException("Broker not selected.");
	}
	
	class ThreadWrapper extends Thread{
		Server s;
		public ThreadWrapper(Server s){
			this.s = s;
		}
		public void run(){
			s.begin();
		}
	}	

}
