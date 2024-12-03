package myServer;

import java.io.*;
import java.net.*;
import java.util.*;

public class Myserver {
	public static void main(String[] args) {
		ServerSocket svSocket = null;
		BufferedReader in = null;
		PrintWriter out = null;
		
		try {
			svSocket = new ServerSocket(10007);
			Socket socket = svSocket.accept();
			//신규유저면 이름 등록 
			//아니면 채팅시작
			out = new PrintWriter(socket.getOutputStream(),true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			//이름이 있으면 중복이라고 알려주기
			//없으면 스레드 생성 후 채팅 시작
			while(true) {
				out.write("이름을 입력하세요\n");
				out.flush();
				//이름
				String name = in.readLine();
				if(NameHash.containsValue(name) == false) {
					break;
				}else {
					continue;
				}
			}
			// 인사 보내기 
			
			out.println("완료");
			out.println(NameHash.getName(socket)+"님 환영합니다\n");
			while(true) {
				SvThread svThread = new SvThread(socket);
				svThread.start();
			}
			
		}catch(IOException e){
			
		}finally {
			try {
				svSocket.close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}

		}	
	}
}
class  NameHash{
	private String name;
	private Socket socket;
	private String getName;
	private static Map<Socket, String> nameHash = new HashMap<>();

	public NameHash() {
	}
	public void register(Socket socket, String name) {//해시맵에 이름등록
		this.socket =socket;
		this.name = name;
		nameHash.put(socket, name);
	}
	
	public static String getName(Socket socket) {
		 return nameHash.get(socket);
	}
	public static boolean containsValue(String name) {//가입자인지 확인하기
		if(nameHash.containsValue(name)) {
			System.out.println("이름이 이미 존재합니다.");
			//클라이언트에게 이름이 중복이라고 보내는 기능 만들기
			return true;
		}else {System.out.println("사용 가능한 이름입니다.");
		return false;
	}
		}
	public boolean containsKey(Socket socket) {//키 값이 있는지 확인
		return nameHash.containsKey(socket);
	}
}

class SvThread extends Thread{
	private Socket socket;
	public SvThread(Socket socket) {
		this.socket = socket;
	}
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter (socket.getOutputStream());
			//채팅을 위한 문자열 변수 선언
			String clientName = NameHash.getName(socket);
			while(true) {
				String inputMessage = in.readLine();
				if(inputMessage == null) {
					out.println(clientName+"님이 접속을 종료했습니다.");
					break;
				}
				out.println(clientName+" : "+inputMessage);
			}
		}catch(IOException e) {
			System.out.println(e.getMessage()); 
		}finally {
			try {
				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			

		}
	}
	
}