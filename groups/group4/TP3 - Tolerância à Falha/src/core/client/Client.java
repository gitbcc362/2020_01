package core.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import core.Message;

public class Client {
	
	private Socket s;

	public Client(String ip, int port){
		try{
			s = new Socket(ip, port);
		}catch (Exception e){
			System.out.println("O broker " + ip + " na porta: " + port + " caiu, não temos um backup disponivel ainda");		
		}
	}
	
	public Client(String ip, int port, String secondaryIp, int secondaryPort){
		try {
			s = new Socket(ip, port);
		} catch (Exception e){
			System.out.println("O broker " + ip + " na porta: " + port + " caiu, tentando se conectar com outro broker...");
			try {
				s = new Socket(secondaryIp, secondaryPort);
				System.out.println("CONECTADO AO BROKER " + secondaryIp + " na porta " + secondaryPort);
			} catch (Exception e2){
				System.out.println("NENHUM BROKER DISPONÍVEL NO MOMENTO");
			}
			
		}
	}
	
	public Message sendReceive(Message msg){
		try{
			ObjectOutputStream out = new ObjectOutputStream(new BufferedOutputStream(s.getOutputStream()));
			out.writeObject(msg);
			out.flush();
			
			ObjectInputStream in = new ObjectInputStream(new BufferedInputStream(s.getInputStream()));
			Message response = (Message) in.readObject();
			
			in.close();
			out.close();			
			s.close();
			return response;
		}catch(Exception e){
			return null;
		}
	}

}
