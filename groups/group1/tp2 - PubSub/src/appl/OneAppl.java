package appl;

import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.SortedSet;
import java.util.TreeSet;

import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import core.Message;
import core.MessageComparator;

public class OneAppl {
	private static String serverIp;
	private static int serverPort;
	private static String clientIp;
	private static int clientPort;

	public static void setServerIP(String ip){
		serverIp = ip;
	}

	public String getServerIP(){
		return serverIp;
	}

	public static void setServerPort(String port){
		serverPort = Integer.parseInt(port);
	}

	public int getServerPort(){
		return serverPort;
	}

	public static void setClientIP(String ip){
		clientIp = ip;
	}

	public String getClientIP(){
		return clientIp;
	}


	public static void setClientPort(String port){
		clientPort = Integer.parseInt(port);
	}

	public int getClientPort(){
		return clientPort;
	}

	public static void main(String[] args) throws UnknownHostException {
		setServerIP(args[0]);
		setServerPort(args[1]);
		setClientIP(args[2]);
		setClientPort(args[3]);
		new OneAppl(true);
	}
	
	public OneAppl(){
		PubSubClient client = new PubSubClient();
		client.startConsole();
	}
	

	public OneAppl(boolean flag){

		PubSubClient cliente = new PubSubClient(getClientIP(), getClientPort());

		String brokerAddress = getServerIP();
		Integer brokerPort = getServerPort();

		cliente.subscribe(brokerAddress, brokerPort);
		
		String[] resources = {"var X", "var Y", "var Z"};
		
		try {
			for(int i = 0; i < 10; i++){

				Random rand = new Random();
				int r = rand.nextInt(resources.length);
				String msg = resources[r];

				String resource = cliente.acquire(msg, brokerAddress, brokerPort);
				cliente.useResource(resource);
				cliente.release(msg, brokerAddress, brokerPort);
			
			}
			// String resource = cliente.acquire(resources[0], brokerAddress, brokerPort); 
			// cliente.useResource(resource);
			// cliente.release(resources[0], brokerAddress, brokerPort);
	
			SortedSet<Message> log =  new TreeSet<Message>(new MessageComparator());
			log.addAll(cliente.getLogMessages());

		
			Iterator<Message> it = log.iterator();
			System.out.print("Log itens: ");
			while(it.hasNext()){
				Message aux = it.next();
				System.out.print(aux.getContent() + ":" + aux.getLogId() + " | ");
			}
			System.out.println();

			System.out.println();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally {
			cliente.unsubscribe(brokerAddress, brokerPort);
			cliente.stopPubSubClient();
		}

	}
}