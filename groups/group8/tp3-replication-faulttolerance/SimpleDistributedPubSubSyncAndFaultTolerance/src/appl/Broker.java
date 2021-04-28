package appl;

import core.Server;
import java.util.Scanner;

public class Broker {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new Broker();
	}
	
	public Broker(){		

		// Scanner reader = new Scanner(System.in);  // Reading from System.in
		// System.out.print("Enter the Broker port number: ");
		// int port = reader.nextInt(); // Scans the next token of the input as an int.
		
		// System.out.print("Is the broker primary?: Y/N");
		// String respYN = reader.next();
		
		// System.out.print("Enter the secondary Broker address: ");
		// String secondAddress = reader.next();
		
		// System.out.print("Enter the secondary Broker port number: ");
		// int secondPort = reader.nextInt();
		
		// boolean respBol;
		// if(respYN.equalsIgnoreCase("Y")) respBol = true;
		// else respBol = false;
		
		// Server s = new Server(port, respBol, secondAddress, secondPort);


		Scanner reader = new Scanner(System.in);
		System.out.print("Is the broker primary?: Y/N");
		String respYN = reader.next();
	
		Server s;
		if(respYN.equalsIgnoreCase("Y")){
			s = new Server(8080, true, "localhost", 8081);
		}else{
			s = new Server(8081, false, "localhost", 8080);
		}

		ThreadWrapper brokerThread = new ThreadWrapper(s);
		brokerThread.start();
		
		System.out.print("Shutdown the broker (Y|N)?: ");
		String resp = reader.next(); 
		if (resp.equals("Y") || resp.equals("y")){
			System.out.println("Broker stopped...");
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
