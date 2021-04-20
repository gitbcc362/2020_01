package appl;

import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Random;
import core.Message;

public class OneAppl {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new OneAppl(true);
	}
	
	public OneAppl(){
		PubSubClient client = new PubSubClient();
		client.startConsole();
	}

    public static String playMusic(){
        String[] songNames = {
            "Xuxa - Abecedário da Xuxa",
            "Xuxa - Doce Mel",
            "Xuxa - Lua de Cristal",
            "Xuxa - Ilariê" };
        return songNames [new Random().nextInt(songNames.length)];
    }
	
	public OneAppl(boolean flag){
		String brokersIp = "34.66.72.37";
		Integer brokersPort = 8081;
		String[] clientIp = {"34.67.100.60", "104.154.105.80", "35.222.64.135"};
		String[] clientNames = {"Flavia", "Douglas", "Dani"};
		PubSubClient listener = new PubSubClient(clientIp[2], 8082);

		listener.subscribe(brokersIp, brokersPort);
		Integer n = ThreadLocalRandom.current().nextInt(3, 50);

		
		for(int i = 0; i<n; i++) {
		    listener.publish("Toca-ai " + clientNames[2], brokersIp, 8081);
			
			Integer position = 0;
			Integer releasesCount = 0;
			Boolean logMeOut = false;
			
			while(logMeOut == false) {
				List<Message> log = listener.getLogMessages();
				
				Iterator<Message> it = log.iterator();
				System.out.println("Log " + clientNames[2] + " itens: ");
				Integer index = 0;
				if(!it.hasNext()) {
					//ESTA NA MINHA VEZ
					listener.publish(clientNames[2] + " Tocando: " + playMusic() , brokersIp, 8081);
					System.out.println("Tocando");
					sleep(3000);
					listener.publish("Tocou " + clientNames[2], brokersIp, 8081);
					logMeOut = true;
				}
				while(it.hasNext()){
					Message aux = it.next();
					System.out.println(aux.getContent() + aux.getLogId() + " | ");
					String[] words = aux.getContent().split(" ");
					String logType;
					String logName;
	
					if(position == 0 && words.length > 1) {
						logType = words[0];
						logName = words[1];
						if (logName.equals(clientNames[2])) {
							position = index + 1;
							releasesCount = position - 1;
						}
					}
					System.out.println(clientNames[2]  + " position: " + position + " relases count: " + releasesCount);
					if (position > 0 && releasesCount == 0) {
						// ESTA NA MINHA VEZ
						// TOCANDO MINHA MUSICA
						
						listener.publish(clientNames[2] + " Tocando: " + playMusic() , brokersIp, 8081);
						sleep(3000);
						listener.publish("Tocou " + clientNames[2], brokersIp, 8081);
						logMeOut = true;

						
					} else if (position > 0 && words.length > 1){
						logType = words[0];
						logName = words[1];
						// VERIFICAR SE É DO TIPO RELASE
						if (logType.equals("Tocou")) {
							// ACHEI UM RELEASE
							releasesCount -= 1;
						}
					}
					if (words.length > 1) {
						logType = words[0];
						logName = words[1];
						if (logType.equals("Tocou") || logType.equals("Toca-ai")) {
							index += 1;
						}
					}
				}
				sleep(3000);
			}
			sleep(3000);
		}
		listener.unsubscribe(brokersIp, 8081);
		listener.stopPubSubClient();
	}
	
	public void sleep(int time) {
		try {
			System.out.print("Aguardando...");
		    Thread.sleep(time);
		} 
		catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
	}
}
