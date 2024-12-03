package myClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class MyClient {
	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		String ip = "localhost";
		int port = 10007;
		try {
			Socket socket = new Socket(ip,port);
			System.out.println("연결이 완료되었습니다.");
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
			//이름을 등록하는 루프
			String inputMessage;
			while(true) {
				inputMessage = in.readLine();
				if(inputMessage.equals("완료")) {
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
			ClientThread clientThread = new ClientThread(socket);
			clientThread.start();//수신 스레드 시작
			while(true) {//메시지 송신을 위함
				String userMessage = scanner.nextLine();
				if("exit".equalsIgnoreCase(userMessage)) {
					System.out.println("채팅을 종료합니다.");
					break;
				}
				out.println(userMessage);
				
			}
			
		}catch(IOException e) {
			System.out.print(e.getMessage());
		}finally {
			scanner.close();
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

			String inputMessage;
			//메시지를 읽음
			while((inputMessage = in.readLine()) != null) {
				System.out.println(inputMessage);
			}
			
			
		}catch(IOException e1){System.out.print(e1.getMessage()+"서버와의 연결이 끊겼습니다.");}finally {
			
		}
		
	}
}
