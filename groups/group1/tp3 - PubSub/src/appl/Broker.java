package appl;

import core.Address;
import core.Message;
import core.MessageImpl;
import core.Server;
import core.client.Client;

import java.util.Scanner;

public class Broker {

	public static void main(String[] args) {
		new Broker();
	}
	
	public Broker(){		

		Scanner reader = new Scanner(System.in);  // Reading from System.in
		System.out.print("[SYSTEM] Enter the Broker port number: ");
		int port = reader.nextInt(); // Scans the next token of the input as an int.
		Address address = new Address("localhost", port);
		

		System.out.println("[SYSTEM] Is the broker primary? (Y | N)");
		boolean isPrimary = reader.next().toLowerCase().equals("y");

		Server s;
		ThreadWrapper brokerThread;

		boolean successBackup = false;

		if (! isPrimary) {
			System.out.print("[SYSTEM] Enter the Primary Broker ip: ");
			String primaryHost = reader.next();

			System.out.print("[SYSTEM] Enter the Primary Broker port number: ");
			int primaryPort = reader.nextInt(); // Scans the next token of the input as an int.
		
			s = new Server(port, isPrimary, new Address(primaryHost, primaryPort));
			brokerThread = new ThreadWrapper(s);
			brokerThread.start();

			Message msgBroker = new MessageImpl();
			msgBroker.setBrokerId(port);
			msgBroker.setType("backupSub");
			msgBroker.setContent(address.toString()); 

			Message status = null;

			try {
				Client subscriber = new Client(primaryHost, primaryPort);

				status = subscriber.sendReceive(msgBroker);
			
			} catch (Exception e) {
				successBackup = false;
			}

			if (status != null && status.getType().equals("backupSub_ack"))
				successBackup = true;
			
		} else {
			s = new Server(port, isPrimary);
			brokerThread = new ThreadWrapper(s);
			brokerThread.start();
		}

		if (successBackup || isPrimary) {
			System.out.print("[SYSTEM] Shutdown the broker (Y|N)?: ");
			String resp = reader.next(); 
			if (resp.equals("Y") || resp.equals("y")){
				System.out.println("[SYSTEM] Broker stopped...");
				s.stop();
				brokerThread.interrupt();
				
			}
		}
		else {
			System.out.println("[SYSTEM] O backup não foi aceito pelo broker ou o broker não existe.");
			s.stop();
			brokerThread.interrupt();
		}

		
		//once finished
		reader.close();
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
