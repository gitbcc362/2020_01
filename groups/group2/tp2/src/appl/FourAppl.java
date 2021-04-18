package appl;

import core.Message;

import javax.swing.Timer;
import java.awt.event.ActionListener;
import java.util.*;

public class FourAppl {

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
        //PubSubClient joubert = new PubSubClient("joubert", "localhost", 8081);
        //PubSubClient debora = new PubSubClient("debora", "localhost", 8082);
        PubSubClient jonata = new PubSubClient("zezinho", "0.0.0.0", 8084);

        //joubert.subscribe("localhost", 8080);
        //debora.subscribe("localhost", 8080);
        jonata.subscribe("0.0.0.0", 8080);

//		joubert.subscribe("localhost", 8085);
//		debora.subscribe("localhost", 8085);
//		jonata.subscribe("localhost", 8085);

        //Thread accessOne = new ThreadWrapper(debora, "debora_acquire_x1", "localhost", 8080);
        Thread accessFive = new ThreadWrapper(jonata, "zezinho_acquire_x1", "0.0.0.0", 8080);
        //Thread accessTwo = new ThreadWrapper(debora, "debora_acquire_x1", "localhost", 8080);
        //Thread accessThree = new ThreadWrapper(jonata, "jonata_acquire_x1", "localhost", 8080);
        //Thread accessFour = new ThreadWrapper(debora, "debora_acquire_x1", "localhost", 8080);
        //accessOne.start();
        accessFive.start();

        //accessTwo.start();
        //accessThree.start();
        //accessFour.start();

        try{
            //	accessTwo.join();
            accessFive.join();
            //accessOne.join();
            //	accessThree.join();
            //accessFour.join();
        } catch (Exception e){
            e.printStackTrace();
        }

        Set<Message> toRemove = new HashSet<>();
        ArrayList<Message> acquires = new ArrayList<>();
        ArrayList<Message> releases = new ArrayList<>();
        StringBuilder lastLog = new StringBuilder();
        ArrayList<Message> logAR;
        HashMap<String, ArrayList<Message>> topicQueues = new HashMap<>();
        //boolean isFirstTime = true;
        boolean pause = false;

        try {
            //synchronized (logJoubert) {
            while (!pause) {
                try {
                    List<Message> logJoubert = jonata.getLogMessages();
//						if (!isFirstTime)
//							logJoubert.wait();
//						else
//							isFirstTime = false;

                    //logAR.clear();
                    if (logJoubert != null) {
                        logAR = new ArrayList<>();
                        logAR.addAll(logJoubert);

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
//                            if (logAR.size() == 0)
//                                pause = true;

                            createTopicQueues(topicQueues, logAR);
                            for (String key : topicQueues.keySet()) {
                                ArrayList<Message> topicQueue = topicQueues.get(key);
                                Message aux = topicQueue.get(0);
                                String[] content = aux.getContent().split("_");
                                if (!currentWorking.contains(aux)) {
                                    if (aux.getContent().contains("zezinho_acquire")) {
                                        release(jonata, content[2], aux);
                                        //} else if (aux.getContent().contains("debora_acquire")){
                                        //    release(debora, content[2], aux);
                                        //} else if (aux.getContent().contains("jonata_acquire")){
                                        //    release(igor, content[2], aux);
                                    }
                                }
                            }

                            //printLogs(logAR);
                        }
                    }
                } catch (Exception e) {
                    //e.printStackTrace();
                }
                //}
            }
        } catch (Exception e) {
            e.printStackTrace();
            //joubert.stopPubSubClient();
            jonata.stopPubSubClient();
//			igor.stopPubSubClient();
        }

//		joubert.stopPubSubClient();
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
        System.out.println("Started ".concat(client.getClientName()).concat(" ").concat(var));
        currentWorking.add(currentMessage);
        Random r = new Random();
        int time = r.nextInt(5);
        time = time*1000;
//        switch (client.getClientName()) {
//            case "joubert" -> time = 5000;
//            case "debora" -> time = 3000;
//            case "jonata" -> time = 1000;
//        }

        javax.swing.Timer timer = new Timer(time, null);
        ActionListener ac = event -> {
            System.out.println("Finished ".concat(client.getClientName()).concat(" ").concat(var));

            Thread a = new ThreadWrapper(client, client.getClientName().concat("_release_").concat(var), "0.0.0.0", 8080);
            a.start();
            try{
                a.join();
            } catch (Exception e){
                e.printStackTrace();
            }
            timer.stop();

            Thread accessOne = new ThreadWrapper(client, "zezinho_acquire_x1", "0.0.0.0", 8080);
            accessOne.start();
            try {
                accessOne.join();
            } catch (Exception e) {

            }
            currentWorking.remove(currentMessage);

        };

        timer.addActionListener(ac);
        timer.setRepeats(false);
        timer.start();
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
            c.publish(msg, host, port);
        }
    }

}