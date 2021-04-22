package appl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import core.Message;
import core.MessageImpl;
import core.Server;
import core.client.Client;

public class PubSubClient {
	
	private Server observer;
	private ThreadWrapper clientThread;
	
	String primaryAddress;
	int primaryPort;
	String secondaryAddress;
	int secondaryPort;
	
	private String clientAddress;
	private int clientPort;
	
	public PubSubClient(){
		//this constructor must be called only when the method
		//startConsole is used
		//otherwise the other constructor must be called
		this.primaryAddress = "";
		this.primaryPort = 0;
		this.secondaryAddress = "";
		this.secondaryPort = 0;
	}
	
	public PubSubClient(String clientAddress, int clientPort){
		this.clientAddress = clientAddress;
		this.clientPort = clientPort;
		this.primaryAddress = "";
		this.primaryPort = 0;
		this.secondaryAddress = "";
		this.secondaryPort = 0;
		observer = new Server(clientPort);
		clientThread = new ThreadWrapper(observer);
		clientThread.start();
	}
	
	public void subscribe(String brokerAddress, int brokerPort){	
		if(this.primaryPort == 0) {
			Message msgAddrs = new MessageImpl();
			msgAddrs.setBrokerId(brokerPort);
			msgAddrs.setType("syncAddrs");
			msgAddrs.setContent("Give me brokers addresses please :)");
			Client subscriber = new Client(brokerAddress, brokerPort, secondaryAddress, secondaryPort);
			Message responseAddrs = subscriber.sendReceive(msgAddrs);
			this.primaryAddress = getAddrs(responseAddrs.getContent(), 0);
			this.primaryPort = getPort(responseAddrs.getContent(), 0);
			this.secondaryAddress = getAddrs(responseAddrs.getContent(), 1);
			this.secondaryPort = getPort(responseAddrs.getContent(), 1);
		} else { 
			Message msgBroker = new MessageImpl();
			msgBroker.setBrokerId(brokerPort);
			msgBroker.setType("sub");
			msgBroker.setContent(clientAddress+":"+clientPort);
			String[] brokersAddrs = getBrokersAddrs(brokerAddress);
			Integer[] brokersPort = getBrokersPort(brokerAddress);
			Client subscriber = new Client(brokersAddrs[0], brokersPort[0], brokersAddrs[1], brokersPort[1]);
			Message response = subscriber.sendReceive(msgBroker);
			if(response.getType().equals("backup")){			
				brokerAddress = response.getContent().split(":")[0];
				brokerPort = Integer.parseInt(response.getContent().split(":")[1]);
				subscriber = new Client(brokersAddrs[0], brokersPort[0], brokersAddrs[1], brokersPort[1]);
			}
		}
	}
	
	public void unsubscribe(String brokerAddress, int brokerPort){
		
		Message msgBroker = new MessageImpl();
		msgBroker.setBrokerId(brokerPort);
		msgBroker.setType("unsub");
		msgBroker.setContent(clientAddress+":"+clientPort);
		Client subscriber = new Client(brokerAddress, brokerPort, secondaryAddress, secondaryPort);
		Message response = subscriber.sendReceive(msgBroker);
		
		if(response.getType().equals("backup")){			
			brokerAddress = response.getContent().split(":")[0];
			brokerPort = Integer.parseInt(response.getContent().split(":")[1]);
			subscriber = new Client(brokerAddress, brokerPort, secondaryAddress, secondaryPort);
			subscriber.sendReceive(msgBroker);
		}
	}
	
	public void publish(String message, String brokerAddress, int brokerPort){
		Message msgPub = new MessageImpl();
		msgPub.setBrokerId(brokerPort);
		msgPub.setType("pub");
		msgPub.setContent(message);
		
		Client publisher = new Client(brokerAddress, brokerPort, secondaryAddress, secondaryPort);
		Message response = publisher.sendReceive(msgPub);
		
		if(response.getType().equals("backup")){			
			brokerAddress = response.getContent().split(":")[0];
			brokerPort = Integer.parseInt(response.getContent().split(":")[1]);
			publisher = new Client(brokerAddress, brokerPort, secondaryAddress, secondaryPort);
			publisher.sendReceive(msgPub);
		}
		
		
	}
	
	public List<Message> getLogMessages(){
		return observer.getLogMessages();
	}

	public void stopPubSubClient(){
		System.out.println("Client stopped...");
		observer.stop();
		clientThread.interrupt();
	}
		
	public void startConsole(){
		Scanner reader = new Scanner(System.in);  // Reading from System.in
		
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
		
		subscribe(brokerAddress, brokerPort);
		
		System.out.println("Do you want to subscribe for more brokers? (Y|N)");
		String resp = reader.next();
		
		if(resp.equals("Y")||resp.equals("y")){
			String message = "";
			
			while(!message.equals("exit")){
				System.out.println("You must inform the broker credentials...");
				System.out.print("Enter the broker address (ex. localhost): ");
				brokerAddress = reader.next();
				System.out.print("Enter the broker port (ex.8080): ");
				brokerPort = reader.nextInt();
				subscribe(brokerAddress, brokerPort);
				System.out.println(" Write exit to finish...");
				message = reader.next();
			}
		}
		
		System.out.println("Do you want to publish messages? (Y|N)");
		resp = reader.next();
		if(resp.equals("Y")||resp.equals("y")){
			String message = "";			
			
			while(!message.equals("exit")){
				System.out.println("Enter a message (exit to finish submissions): ");
				message = reader.next();
								
				System.out.println("You must inform the broker credentials...");
				System.out.print("Enter the broker address (ex. localhost): ");
				brokerAddress = reader.next();
				System.out.print("Enter the broker port (ex.8080): ");
				brokerPort = reader.nextInt();
				
				publish(message, brokerAddress, brokerPort);
				
				List<Message> log = observer.getLogMessages();
				
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

	public String getAddrs(String content, int i) {
		return content.split("-")[i].split(":")[0];
	}

	public int getPort(String content, int i) {
		return Integer.parseInt(content.split("-")[i].split(":")[1]);
	}
	
	public String[] getBrokersAddrs(String brokerAddress) {
		String[] brokers = new String[2];
		if(brokerAddress == this.primaryAddress) {
			brokers[0] = primaryAddress;
			brokers[1] = secondaryAddress;
		} else {
			brokers[1] = primaryAddress;
			brokers[0] = secondaryAddress;
		}
		
		return brokers;
	}

	public Integer[] getBrokersPort(String brokerAddress) {
		Integer[] brokers = new Integer[2];
		if(brokerAddress == this.primaryAddress) {
			brokers[0] = primaryPort;
			brokers[1] = secondaryPort;
		} else {
			brokers[1] = primaryPort;
			brokers[0] = secondaryPort;
		}
		
		return brokers;
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
