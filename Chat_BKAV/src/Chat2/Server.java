package Chat2;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class Server {
	private int port;
	
	public static ManagerAccount ma=new ManagerAccount();
	public static ManagerChat mc=new ManagerChat(DBConnection.getConnection());
	
	public static ArrayList<Socket> listSK;
	public static List<String> users=new ArrayList<String>();
	public static Map<String, Socket> map=new HashMap<>();
	
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
			DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
            String username="";
            String password="";
            boolean check=false;
            do {
            	username = dis.readUTF();
                password = dis.readUTF();
                check = ma.checkLogin(username, password);
                dos.writeBoolean(check);
            }while (!check);
        
        	Server.listSK.add(socket);
            System.out.println(username + " connected");
            map.put(username, socket);
            ReadServer read = new ReadServer(socket, username, mc);
            read.start();
		}
	}
	public static void main(String[] args) throws IOException {
		ma.accs.add(new Account("tuan", "1234"));
		ma.accs.add(new Account("anh", "1234"));
		ma.accs.add(new Account("mai", "1234"));
		
		ma.writeFile("account.txt");
		ma.readFile("account.txt");
		for (Account user : ma.accs) {
            users.add(user.getUsername());
        }
		
		Server.listSK=new ArrayList<Socket>();
		Server server=new Server(1234);
		server.execute();
	}
}
class ReadServer extends Thread{
	private Socket socket;
	String userName;
	ManagerChat mc;
	public ReadServer(Socket socket, String userName, ManagerChat mc) {
		this.socket=socket;
		this.userName=userName;
		this.mc=mc;
	}
	
	public void run() {
		DataInputStream dis=null;
		DataOutputStream dos=null;
		try {
			dis=new DataInputStream(socket.getInputStream());
			dos=new DataOutputStream(socket.getOutputStream());
			while (true) {
				String sms=dis.readUTF();
				
				String regexText1 = "chat \\w+ '.*'", regexText2 = "Chat \\w+ '.*'";
				String regexFile1="File \\w+ '.*'", regexFile2="file \\w+ '.*'";
				
				if (sms.equals("exit")) {
					Server.listSK.remove(socket);
					System.out.println("Disconected to "+socket);
					dis.close();
					socket.close();
					continue;
				}else if (sms.equals("online")) {
					dos.writeUTF("List of people are online: ");
					for (String it : Server.map.keySet()) {
						dos.writeUTF(it);
						
					}
				}else {
					if(sms.matches(regexText1)||sms.matches(regexText2)) {
						String[] smsSplit=sms.split(" ");
						String userReceive=smsSplit[1];
						String content=smsSplit[2].substring(1, smsSplit[2].length()-1);
						if (Server.map.keySet().contains(userReceive)) {
							if (!userName.equals(userReceive)) {
								HistoryChat his=new HistoryChat(userName, userReceive, content, LocalDateTime.now().toString());
								mc.insert(his);
								
								Socket s = Server.map.get(userReceive);
		                        dos = new DataOutputStream(s.getOutputStream());
		                        dos.writeUTF(userName + ": " + content);
		                        System.out.println(userName +" "+ sms);
							}else {
								dos.writeUTF("You can't send to yourself.");
							}
							
						}else if (Server.users.contains(userReceive)) {
							HistoryChat his=new HistoryChat(userName, userReceive, content, LocalDateTime.now().toString());
							mc.insert(his);
							System.out.println(userName +" "+ sms);
						}
						else if(userReceive.equals("all")){
							for (Socket item:Server.listSK) {
								if (item.getPort()!=socket.getPort()) {
									dos=new DataOutputStream(item.getOutputStream());
									dos.writeUTF(userName+": "+ content);
								}
							}
						}
						else {
							dos.writeUTF("Username doesn't exist.");
						}
					
					}
					else if (sms.matches(regexFile1)||sms.matches(regexFile2)) {
						String[] smsSplit=sms.split(" ");
						String userReceive=smsSplit[1];
						String fileName = dis.readUTF();
						int sizeF=dis.readInt();
						byte[] buffer=new byte[sizeF];
						FileOutputStream fos=new FileOutputStream("server\\"+userName+"\\"+fileName);
						
						InputStream is=socket.getInputStream();
						is.read(buffer, 0, buffer.length);
						fos.write(buffer, 0, buffer.length);
						fos.close();
						System.out.println("Server received '"+fileName+"' from "+userName);
						if (Server.map.keySet().contains(userReceive)) {
							Socket s=Server.map.get(userReceive);
							dos=new DataOutputStream(s.getOutputStream());
							dos.writeUTF("receive file");
							dos.writeUTF(fileName);
							dos.writeInt(sizeF);
							FileInputStream fis=new FileInputStream("server\\"+userName+"\\"+fileName);
							OutputStream os=s.getOutputStream();
							os.write(buffer, 0, buffer.length);
							fis.close();
							System.out.println("Server sent '"+fileName+"' to "+userReceive);
							HistoryChat h = new HistoryChat(userName, userReceive, "send a new file: "+fileName, LocalDateTime.now().toString());
                            mc.insert(h);
						}
					}
					else {
						dos.writeUTF("Synxtax error!");
					}
				} 
			}
		}catch(Exception e) {
			e.printStackTrace();
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