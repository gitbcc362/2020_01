package appl;

import java.util.Iterator;
import java.util.List;

import core.Message;
import core.client.BrokerStatusListener;

public class OneAppl implements BrokerStatusListener {

	PubSubClient joubert;
	int brokerPort = 8080;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new OneAppl(true);
	}
	
	public OneAppl(){
		PubSubClient client = new PubSubClient();
		client.startConsole();
	}
	
	public OneAppl(boolean flag){
		joubert = new PubSubClient("localhost", 8082);
		PubSubClient debora = new PubSubClient("localhost", 8083);
		PubSubClient jonata = new PubSubClient("localhost", 8084);
		
		joubert.subscribe("localhost", 8080);
		Thread accessOne = new ThreadWrapper(joubert, "access Joubert- var X", "localhost", brokerPort, this);
		
		debora.subscribe("localhost", 8080);
		jonata.subscribe("localhost", 8081);
						
		//accessOne = new ThreadWrapper(joubert, "access Joubert- var X", "localhost", 8080);
		Thread accessTwo = new ThreadWrapper(debora, "access Debora- var X", "localhost", 8080, this);
		Thread accessThree = new ThreadWrapper(jonata, "access Jonata- var X", "localhost", 8081, this);
		accessOne.start();
		accessTwo.start();
		accessThree.start();
		
		try {
			accessTwo.join();
			accessOne.join();
			accessThree.join();
		} catch (Exception e){
			e.printStackTrace();
		}
		
				
		List<Message> logJoubert = joubert.getLogMessages();
		List<Message> logDebora = debora.getLogMessages();
		List<Message> logJonata = jonata.getLogMessages();
		
		Iterator<Message> it = logJoubert.iterator();
		System.out.print("Log Joubert itens: ");
		while(it.hasNext()){
			Message aux = it.next();
			System.out.print(aux.getContent() + aux.getLogId() + " | ");
		}
		System.out.println();
		
		it = logJonata.iterator();
		System.out.print("Log Jonata itens: ");
		while(it.hasNext()){
			Message aux = it.next();
			System.out.print(aux.getContent() + aux.getLogId() + " | ");
		}
		System.out.println();
		
		it = logDebora.iterator();
		System.out.print("Log Debora itens: ");
		while(it.hasNext()){
			Message aux = it.next();
			System.out.print(aux.getContent() + aux.getLogId() + " | ");
		}
		System.out.println();
		
		joubert.unsubscribe("localhost", 8080);
		debora.unsubscribe("localhost", 8080);
		jonata.unsubscribe("localhost", 8080);
		
		joubert.stopPubSubClient();
		debora.stopPubSubClient();
		jonata.stopPubSubClient();
	}

	@Override
	public void onBrokerDown() {
		// broker backup need to be primary
		this.brokerPort = 8081;
		Thread accessOne = new ThreadWrapper(joubert, "access Joubert- var X", "localhost", brokerPort, this);
		accessOne.start();
		try {
			accessOne.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	class ThreadWrapper extends Thread{
		PubSubClient c;
		String msg;
		String host;
		int port;
		BrokerStatusListener brokerStatusListener;

		public ThreadWrapper(PubSubClient c, String msg, String host, int port, BrokerStatusListener brokerStatusListener){
			this.c = c;
			this.msg = msg;
			this.host = host;
			this.port = port;
			this.brokerStatusListener = brokerStatusListener;
		}

		public void run(){
			c.publish(msg, host, port, brokerStatusListener);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
