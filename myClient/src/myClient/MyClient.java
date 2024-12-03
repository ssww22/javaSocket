package myClient;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class MyClient {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String ip = "localhost";
        int port = 10007;

        try (Socket socket = new Socket(ip, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            if (socket.isConnected()) {
                System.out.println("서버와 연결되었습니다.");
            }

            // 이름 등록
            String serverMessage;
            while (true) {
                serverMessage = in.readLine(); // 서버 메시지 수신
                System.out.println("서버: " + serverMessage);

                if ("완료".equals(serverMessage)) {
                    System.out.println("이름 등록 완료!");
                    break;
                }

                System.out.print("이름 입력: ");
                String name = scanner.nextLine();
                out.println(name); // 이름 전송
            }

            // 수신 스레드 시작
            ClientThread clientThread = new ClientThread(socket);
            clientThread.start();

            // 메시지 송신
            while (true) {
                System.out.print("메시지 입력: ");
                String userMessage = scanner.nextLine();

                if ("exit".equalsIgnoreCase(userMessage)) {
                    System.out.println("채팅을 종료합니다.");
                    break;
                }

                out.println(userMessage); // 메시지 서버로 전송
            }

        } catch (IOException e) {
            System.err.println("클라이언트 오류: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}

// 서버 메시지 수신 스레드
class ClientThread extends Thread {
    private final Socket socket;

    public ClientThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String inputMessage;
            while ((inputMessage = in.readLine()) != null) { // 서버 메시지 수신
                System.out.println(inputMessage);
            }
        } catch (IOException e) {
            System.err.println("서버와의 연결이 종료되었습니다: " + e.getMessage());
        }
    }
}
