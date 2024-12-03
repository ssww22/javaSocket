package myClient;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;

public class MyClient {
	public static void main(String[] args) {
		BufferedReader in = null;
		BufferedWriter out = null;
		Scanner scanner = new Scanner(System.in);
		String ip = "";
		int port = 0;
		try {
			Socket socket = new Socket(ip,port);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			//이름을 등록하는 루프
			String inputMessage;
			while(true) {
				inputMessage = in.readLine();
				if(inputMessage == "완료") {
					break;
				}
				System.out.println("서버 : "+ inputMessage);
				String name = scanner.nextLine();
				out.write(name);
				out.flush();
				//스레드 시작
				ClientThread clientThread = new ClientThread(socket);
				clientThread.start();
			}
			
			
		}catch(IOException e) {
			System.out.print(e.getMessage());
		}finally {
			
		}
	}
}

class ClientThread extends Thread{
	private Socket socket;
	
	public ClientThread(Socket socket) {
		this.socket = socket;	
	}
	public void run(IOException e) {
		try {
			//채팅을 위한 준비 
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			Scanner scanner = new Scanner(System.in);
			String inputMessage, sendMessage;
			//메시지를 읽음
			inputMessage = in.readLine();
			while(inputMessage != null) {
				sendMessage = scanner.nextLine();
				out.write(sendMessage);
				out.flush();
			}
			
		}catch(IOException e1){System.out.print(e1.getMessage());}finally {
			
		}
		
	}
}
