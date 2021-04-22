package appl;

import java.util.SortedSet;
import java.util.TreeSet;

import java.util.Iterator;
import java.util.Random;

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

	public static void main(String[] args) throws Exception {
		setServerIP(args[0]);
		setServerPort(args[1]);
		setClientIP(args[2]);
		setClientPort(args[3]);
		new OneAppl();
	}

	public OneAppl() throws Exception{

		PubSubClient cliente = new PubSubClient(getClientIP(), getClientPort());

		String brokerAddress = getServerIP();
		Integer brokerPort = getServerPort();

		cliente.subscribe("broker_abacaxi", brokerAddress, brokerPort);
		
		String[] resources = {"var X", "var Y", "var Z"};
		
		try {
			for(int i = 0; i < 10; i++){

				Random rand = new Random();
				int r = rand.nextInt(resources.length);
				String msg = resources[r];

				String resource = cliente.acquire(msg, "broker_abacaxi");
				cliente.useResource(resource);
				cliente.release(msg, "broker_abacaxi");
			
			}
			
			/* String resource = cliente.acquire(resources[0], "broker_abacaxi"); 
			cliente.useResource(resource);
			cliente.release(resources[0], "broker_abacaxi"); */
	
			SortedSet<Message> log =  new TreeSet<Message>(new MessageComparator());
			log.addAll(cliente.getLogMessages());

		
			Iterator<Message> it = log.iterator();

			System.out.println("********************************************************************");
			while(it.hasNext()){
				Message aux = it.next();
				System.out.println("  ["+  aux.getLogId() + "] -- " + aux.getContent());
			}
			System.out.println("********************************************************************");

			System.out.println();

		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally {
			cliente.unsubscribe("broker_abacaxi");
			cliente.stopPubSubClient();
		}

	}
}