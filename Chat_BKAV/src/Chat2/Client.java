package Chat2;



import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	private InetAddress host;
	private int port;
	
	public Client(InetAddress host, int port) {
		this.host=host;
		this.port=port;
	}
	private void execute() throws IOException {
		Socket client=new Socket(host, port);
		DataInputStream dis=new DataInputStream(client.getInputStream());
		DataOutputStream dos=new DataOutputStream(client.getOutputStream());
		Scanner sc=new Scanner(System.in);
		boolean check=false;
		String name="";
		String pass="";
		do {
			System.out.println("Your name: ");
		    name=sc.nextLine();
			System.out.println("Password: ");
			pass=sc.nextLine();
			
			dos.writeUTF(name);
			dos.writeUTF(pass);
			check=dis.readBoolean();
			if (!check) {
				System.out.println("Incorrect!");
			}
		} while (!check);
		
		System.out.println("Logged in successfully!");
		System.out.println("Guide: ");
		System.out.println("- Enter 'online' to show list of people are online. ");
		System.out.println("- chat + <UsernameReceive> + '<content>' : chat to person asked.");
		System.out.println("       + 'all' + '<content>' : chat to all everyone.");
		System.out.println("- file + <UsernameReceive> + '<fileName>': send file");
		System.out.println("- Enter 'exit' to be out.");
		System.out.println("Start!");
		ReadClient read=new ReadClient(client, name);
		read.start();
		WriteClient write=new WriteClient(client, name);
		write.start();
		
		
	}
	public static void main(String[] args) throws IOException {
		Client client=new Client(InetAddress.getLocalHost(), 1234);
		client.execute();
	}
}
class ReadClient extends Thread{
	private Socket client;
	String userName;
	public ReadClient(Socket client, String userName) {
		this.client=client;
		this.userName=userName;
	}
	
	public void run() {
		DataInputStream dis=null;
		try {
			dis=new DataInputStream(client.getInputStream());
			while (true) {
				String sms=dis.readUTF();
				if (sms.contains("receive file")) {
					String fileName=dis.readUTF();
					int sizeF=dis.readInt();
					byte[] buffer=new byte[sizeF];
					FileOutputStream fos=new FileOutputStream(userName+"\\"+fileName);
					InputStream is=client.getInputStream();
					is.read(buffer, 0, buffer.length);
					fos.write(buffer, 0, buffer.length);
					fos.close();
					System.out.println("Received file: '"+fileName+"'.");
				}
				
			}
		}catch(Exception e) {
			try {
				dis.close();
				client.close();
			}catch(IOException ex) {
				System.out.println("Disconnected");
			}
		}
	}
}
class WriteClient extends Thread{
	private Socket client;
	private String name;
	private String pass;
	public WriteClient(Socket client, String name) {
		this.client=client;
		this.name=name;
	}
	
	public void run() {
		DataOutputStream dos=null;
		Scanner sc=null;
		String regexFile1="File \\w+ '.*'", regexFile2="file \\w+ '.*'";
		try {
			dos=new DataOutputStream(client.getOutputStream());
			sc=new Scanner(System.in);
			while(true) {
				String sms=sc.nextLine();
				dos.writeUTF(sms);
				// gui file
				if (sms.matches(regexFile1)||sms.matches(regexFile2)) {
					//gui file len server
					String file = sms.substring(sms.indexOf("'") + 1, sms.length() - 1);
					File f=new File(file);
					dos.writeUTF(f.getName());
					//gui kich thuoc file
					int sizeF=(int) f.length();
					dos.writeInt(sizeF);
					//gui noi dung file
					if (f.exists()) {
						FileInputStream fis=new FileInputStream(file);
						byte[] buffer=new byte[sizeF];
						fis.read(buffer, 0, buffer.length);
						fis.close();
						OutputStream os=client.getOutputStream();
						os.write(buffer, 0, buffer.length);
					}
					System.out.println("Sent file sucessfully.");
				}
			}
		} catch (Exception e) {
			System.out.println("Disconnected");
		}
	}
}