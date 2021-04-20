package appl;

import core.Server;
import java.util.Scanner;

public class Broker {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Broker();
	}
	
	public Broker() {	
		
		int primaryPort = 8080;
		int secondaryPort = 8081;
		String primaryAddress = "34.70.208.126";
		String secondaryAddress = "34.66.72.37";

		Scanner reader = new Scanner(System.in);
		
		System.out.print("Is the broker primary?: Y/N");
		String respYN = reader.next();
		
		boolean isPrimary;
		if(respYN.equalsIgnoreCase("Y")) isPrimary = true;
		else isPrimary = false;

		int currentPort;
		String currentAddress;
		int otherPort;
		String otherAddress;

		if (isPrimary) {
			currentPort = primaryPort;
			currentAddress = primaryAddress;
			otherPort = secondaryPort;
			otherAddress = secondaryAddress;
		} else {
			currentPort = secondaryPort;
			currentAddress = secondaryAddress;
			otherPort = primaryPort;
			otherAddress = primaryAddress;
		}
		Server s = new Server(currentPort, isPrimary, currentAddress, otherAddress, otherPort);
		
		ThreadWrapper brokerThread = new ThreadWrapper(s);
		brokerThread.start();
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
