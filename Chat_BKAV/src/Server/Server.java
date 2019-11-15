package Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Server {
	private int port;
	public static ArrayList<Socket> listSK;
	public Server(int port) {
		this.port=port;
	}
	private void  execute() throws IOException {
		ServerSocket server =new ServerSocket(port);
		WriteServer write=new WriteServer();
		write.start();
		System.out.println("Server is listening...");
		while (true) {
			Socket socket=server.accept();
			System.out.println("Connected to "+socket);
			Server.listSK.add(socket);
			ReadServer read=new ReadServer(socket);
			read.start();
		}
	}
	public static void main(String[] args) throws IOException {
		Server.listSK=new ArrayList<Socket>();
		Server server=new Server(1234);
		server.execute();
	}
}
class ReadServer extends Thread{
	private Socket socket;
	public ReadServer(Socket socket) {
		this.socket=socket;
	}
	
	public void run() {
		DataInputStream dis=null;
		DataOutputStream dos=null;
		try {
			dis=new DataInputStream(socket.getInputStream());
			while (true) {
				String sms=dis.readUTF();
				if (sms.contains("exit")) {
					Server.listSK.remove(socket);
					System.out.println("Disconected to "+socket);
					dis.close();
					socket.close();
					continue;
				}
				for (Socket item:Server.listSK) {
					if (item.getPort()!=socket.getPort()) {
						dos=new DataOutputStream(item.getOutputStream());
						dos.writeUTF(sms);
					}
				}
				System.out.println(sms);
			}
		}catch(Exception e) {
			try {
				dis.close();
				socket.close();
			}catch(IOException ex) {
				System.out.println("Disconnect");
			}
		}
	}
}
class WriteServer extends Thread{
	
	public void run() {
		DataOutputStream dos=null;
		Scanner sc=new Scanner(System.in);
		while (true) {
			try {
				String sms=sc.nextLine();
				for (Socket item:Server.listSK) {
					dos=new DataOutputStream(item.getOutputStream());
					dos.writeUTF("Server: "+sms);
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	}
}