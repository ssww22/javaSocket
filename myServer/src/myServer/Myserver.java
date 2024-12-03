package myServer;

import java.io.*;
import java.net.*;
import java.util.*;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class Myserver {
	public static void main(String[] args) {
        System.out.println("서버 시작 중...");
        try (ServerSocket serverSocket = new ServerSocket(10007)) {
            System.out.println("서버가 시작되었습니다. 클라이언트 연결 대기 중...");

            while (true) {
                Socket socket = serverSocket.accept(); // 클라이언트 연결 대기
                System.out.println("새 클라이언트 연결됨: " + socket.getInetAddress());
                new SvThread(socket).start(); // 새로운 클라이언트를 스레드로 처리
            }
        } catch (IOException e) {
            System.err.println("서버 오류: " + e.getMessage());
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

	public static void remove(Socket socket) {
		nameHash.remove(socket);
	}
}

class SvThread extends Thread{
	private Socket socket;
	private String clientName;
	private PrintWriter out;
	
	public SvThread(Socket socket) {
		this.socket = socket;
	}
	 public void run() {
	        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
	            out = new PrintWriter(socket.getOutputStream(), true);
	            synchronized (ChatServer.clientWriters) {
	                ChatServer.clientWriters.add(out); // 출력 스트림 추가
	            }

	            // 클라이언트 이름 등록
	            while (true) {
	                out.println("이름을 입력하세요:");
	                String name = in.readLine();

	                if (name == null || name.isBlank()) {
	                    out.println("유효한 이름을 입력하세요.");
	                    continue;
	                }

	                if (NameHash.containsValue(name)) { // 이름 중복 확인
	                    out.println("이미 존재하는 이름입니다. 다른 이름을 입력하세요.");
	                } else {
	                    NameHash.register(socket, name); // 이름 등록
	                    clientName = name;
	                    ChatServer.broadcast(clientName + "님이 채팅방에 입장하셨습니다.");
	                    out.println("완료"); // 이름 등록 완료 메시지 전송
	                    break; // 이름 등록 루프 종료
	                }
	            }

	            // 메시지 처리
	            String inputMessage;
	            while ((inputMessage = in.readLine()) != null) {
	                ChatServer.broadcast(clientName + ": " + inputMessage); // 브로드캐스트 메시지 전송
	            }
	        } catch (IOException e) {
	            System.err.println(clientName + " 연결 오류: " + e.getMessage());
	        } finally {
	            try {
	                synchronized (ChatServer.clientWriters) {
	                    ChatServer.clientWriters.remove(out); // 출력 스트림 제거
	                }
	                ChatServer.broadcast(clientName + "님이 채팅방에서 나가셨습니다.");
	                socket.close();
	                System.out.println(clientName + " 연결 종료");
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }
	
}
class ChatServer {
    public static final List<PrintWriter> clientWriters = Collections.synchronizedList(new ArrayList<>());

    // 메시지를 모든 클라이언트에게 전송
    public static void broadcast(String message) {
        synchronized (clientWriters) {
            for (PrintWriter writer : clientWriters) {
                writer.println(message);
                writer.flush();
            }
        }
    }
}