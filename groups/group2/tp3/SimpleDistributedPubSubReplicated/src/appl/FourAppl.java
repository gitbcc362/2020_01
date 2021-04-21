package appl;

import java.awt.event.ActionListener;
import java.util.*;

import core.Message;
import core.client.BrokerStatusListener;

public class FourAppl implements BrokerStatusListener {

	PubSubClient gabriel;
	int brokerPort = 8080;

	Set<Message> currentWorking = new HashSet<>(); //Only cause this class is simulating multiple machines

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new FourAppl(true);
	}
	
	public FourAppl(){
		PubSubClient client = new PubSubClient();
		client.startConsole();
	}
	
	public FourAppl(boolean flag){
		gabriel = new PubSubClient("localhost", 8084);
//		PubSubClient debora = new PubSubClient("localhost", 8083);
//		PubSubClient jonata = new PubSubClient("localhost", 8084);
		
		gabriel.subscribe("localhost", 8080);
		Thread accessOne = new ThreadWrapper(gabriel, "gabriel_acquire_x1", "localhost", brokerPort);
		
//		debora.subscribe("localhost", 8080);
//		jonata.subscribe("localhost", 8080);
						
		//accessOne = new ThreadWrapper(gabriel, "access gabriel- var X", "localhost", 8080);
//		Thread accessTwo = new ThreadWrapper(debora, "access Debora- var X", "localhost", 8080);
//		Thread accessThree = new ThreadWrapper(jonata, "access Jonata- var X", "localhost", 8080);

		accessOne.start();
//		accessTwo.start();
//		accessThree.start();
		
		try {
			//accessTwo.join();
			accessOne.join();
			//accessThree.join();
		} catch (Exception e){
			e.printStackTrace();
		}
		
				
		//List<Message> loggabriel = gabriel.getLogMessages();
		//List<Message> logDebora = debora.getLogMessages();
		//List<Message> logJonata = jonata.getLogMessages();
		Set<Message> toRemove = new HashSet<>();
		ArrayList<Message> acquires = new ArrayList<>();
		ArrayList<Message> releases = new ArrayList<>();
		StringBuilder lastLog = new StringBuilder();
		ArrayList<Message> logAR;
		HashMap<String, ArrayList<Message>> topicQueues = new HashMap<>();
		//boolean isFirstTime = true;
		boolean pause = false;

		try {
			//synchronized (loggabriel) {
			while (!pause) {
				try {
					List<Message> loggabriel = gabriel.getLogMessages();
//						if (!isFirstTime)
//							loggabriel.wait();
//						else
//							isFirstTime = false;

					//logAR.clear();
					if (loggabriel != null) {
						logAR = new ArrayList<>();
						logAR.addAll(loggabriel);

						ArrayList<Message> auxList = new ArrayList<>();
						for (Message aux : logAR) {
							if (!aux.getContent().contains("acquire") && !aux.getContent().contains("release")) {
								auxList.add(aux);
							}
						}
						logAR.removeAll(auxList);

						StringBuilder currentLog = new StringBuilder();
						for (Message aux : logAR) {
							currentLog.append(aux.getContent()).append(" | ");
						}

						if (!currentLog.toString().equals(lastLog.toString())) {
							lastLog = currentLog;

							toRemove.clear();
							acquires.clear();
							releases.clear();
							for (Message log : logAR) {
								if (log.getContent().contains("acquire")) {
									acquires.add(log);
								} else {
									releases.add(log);
								}
							}

							if (releases.size() == 20) {
								System.out.print("releases -> ");
								printLogs(releases);
								System.out.print("acquires -> ");
								printLogs(acquires);
								break;
							}

							for (Message release : releases) {
								boolean isFirst = true;
								for (Message acquire : acquires) {
									if (release.getContent().replace("release", "acquire").equals(acquire.getContent()) && isFirst) {
										if (!toRemove.contains(acquire)) {
											toRemove.add(acquire);
											isFirst = false;
										}
									}
								}
							}

							toRemove.addAll(releases);
							logAR.removeAll(toRemove);
//							if (logAR.size() == 0)
//								pause = true;

							createTopicQueues(topicQueues, logAR);
							for (String key : topicQueues.keySet()) {
								ArrayList<Message> topicQueue = topicQueues.get(key);
								Message aux = topicQueue.get(0);
								String[] content = aux.getContent().split("_");
								if (!currentWorking.contains(aux)) {
									if (aux.getContent().contains("gabriel_acquire")) {
										release(gabriel, content[2], aux);
									} else if (aux.getContent().contains("debora_acquire")) {
										//release(debora, content[2], aux);
									} else if (aux.getContent().contains("jonata_acquire")) {
										//release(jonata, content[2], aux);
									}
								}
							}

							printLogs(logAR);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				//}
			}
		} catch (Exception e) {
			e.printStackTrace();
			gabriel.stopPubSubClient();
//			debora.stopPubSubClient();
//			jonata.stopPubSubClient();
		}
		
//		Iterator<Message> it = loggabriel.iterator();
//		System.out.print("Log gabriel itens: ");
//		while(it.hasNext()){
//			Message aux = it.next();
//			System.out.print(aux.getContent() + aux.getLogId() + " | ");
//		}
//		System.out.println();
//
//		it = logJonata.iterator();
//		System.out.print("Log Jonata itens: ");
//		while(it.hasNext()){
//			Message aux = it.next();
//			System.out.print(aux.getContent() + aux.getLogId() + " | ");
//		}
//		System.out.println();
//
//		it = logDebora.iterator();
//		System.out.print("Log Debora itens: ");
//		while(it.hasNext()){
//			Message aux = it.next();
//			System.out.print(aux.getContent() + aux.getLogId() + " | ");
//		}
//		System.out.println();
//
//		gabriel.unsubscribe("localhost", 8080);
//		debora.unsubscribe("localhost", 8080);
//		jonata.unsubscribe("localhost", 8080);
//
//		gabriel.stopPubSubClient();
//		debora.stopPubSubClient();
//		jonata.stopPubSubClient();
	}

	private void createTopicQueues(HashMap<String, ArrayList<Message>> topicQueues, ArrayList<Message> logs) {
		topicQueues.clear();
		for (Message l : logs) {
			String topic = l.getContent().split("_")[2];
			topicQueues.computeIfAbsent(topic, k -> new ArrayList<>());
			topicQueues.get(topic).add(l);
		}
	}

	private void printLogs2(ArrayList<Message> logs) {
		StringBuilder currentPrint = new StringBuilder();
		ArrayList<Message> logsAux = new ArrayList<>(logs);
		for (Message aux : logsAux) {
			currentPrint.append(aux.getContent()).append(" | ");
		}
		System.out.println(currentPrint);
		System.out.println();
	}

	private void printLogs(ArrayList<Message> logs) {
		StringBuilder currentPrint = new StringBuilder();
		ArrayList<Message> logsAux = new ArrayList<>(logs);
		for (Message aux : logsAux) {
			currentPrint.append(aux.getContent()).append(" | ");
		}
		System.out.println(currentPrint);
		System.out.println();
	}

	private void release(PubSubClient client, String var, Message currentMessage) {
		System.out.println("Started ".concat("gabriel").concat(" ").concat(var));
		currentWorking.add(currentMessage);
		Random r = new Random();
		int time = r.nextInt(5);
		time = time*1000;
//		int time = 0;
//		switch ("gabriel") {
//			case "gabriel" -> time = 5000;
//			case "debora" -> time = 3000;
//			case "jonata" -> time = 1000;
//		}

		javax.swing.Timer timer = new javax.swing.Timer(time, null);
		ActionListener ac = event -> {
			System.out.println("Finished ".concat("gabriel").concat(" ").concat(var));

			Thread a = new ThreadWrapper(client, "gabriel".concat("_release_").concat(var), "localhost", 8080);
			a.start();
			try{
				a.join();
			} catch (Exception e){
				e.printStackTrace();
			}
			timer.stop();

			Thread accessOne = new ThreadWrapper(client, "gabriel_acquire_x1", "localhost", 8080);
			accessOne.start();
			try {
				accessOne.join();
			} catch (Exception e) {
				e.printStackTrace();
			}
			currentWorking.remove(currentMessage);

		};

		timer.addActionListener(ac);
		timer.setRepeats(false);
		timer.start();
	}

	@Override
	public void onBrokerDown() {
		// broker backup need to be primary
		this.brokerPort = 8080;
		Thread accessOne = new ThreadWrapper(gabriel, "access gabriel- var X", "localhost", brokerPort);
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

		public ThreadWrapper(PubSubClient c, String msg, String host, int port){
			this.c = c;
			this.msg = msg;
			this.host = host;
			this.port = port;
		}

		public void run(){
			c.publish(msg, null, host, port);
		}
	}

}
