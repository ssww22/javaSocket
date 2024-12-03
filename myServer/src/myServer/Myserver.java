package myServer;

import java.io.*;
import java.net.*;
import java.util.*;

public class Myserver {
	public static void main(String[] args) {
		ServerSocket svSocket = null;
		try {
			System.out.println("클라이언트 연결 대기 중");
			svSocket = new ServerSocket(10007);
			//1. 클라이언트가 연결됨 동시에 새로운 스레드 생성
			//2. 이름 등록 및 메시지 처리
			//3.채팅시작
			while(true) {//클라이언트가 접속하면 새로운 스레드 생성 
				Socket socket = svSocket.accept();
				SvThread svThread = new SvThread(socket);
				svThread.start();
			}
		}catch(IOException e){
			System.out.println(e.getMessage());
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
	private static Map<Socket, String> nameHash = new HashMap<>();

	
	public static void register(Socket socket, String name) {//해시맵에 이름등록
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
	public static boolean containsKey(Socket socket) {//키 값이 있는지 확인
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
			//스레드가 시작되면 해야 할 일
			//1. 이름 등록
			//2. 채팅
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter (socket.getOutputStream(),true);
			//이름을 등록함
			while(true) {
				out.println("이름을 입력하세요");
				String name = in.readLine();
				
				if(NameHash.containsValue(name) == false) {//이름 등록이 완료되면
					NameHash.register(socket, name);
					out.println("완료");
					break;
				}else {//실패하면
					continue;
				}
			}
			//채팅을 위한 문자열 변수 선언
			String clientName = NameHash.getName(socket);
			String inputMessage;
			//채팅 시작
			while(true) {
				inputMessage = in.readLine();
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
				//이름 삭제
				 NameHash.register(socket, null);
				socket.close();
				 System.out.println("클라이언트 연결 종료");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}