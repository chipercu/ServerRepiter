package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {

//            ServerSocket clientSocket = new ServerSocket(2106);
//            ServerSocket serverSocket = new ServerSocket(2107);
        while (true) {
//                final Socket accept = clientSocket.accept();
//                final InputStream inputStream = accept.getInputStream();

            try { // получение строки клиентом
                ServerSocket clientSocket = new ServerSocket(2106);
                Socket socket = clientSocket.accept();
                /* здесь "ИМЯ_СЕРВЕРА" компьютер, на котором стоит сервер-сокет"*/
                BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                ServerSocket serverSocket = new ServerSocket(2107);
                serverSocket.accept();
                String msg = br.readLine();
                System.out.println(msg);
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("ошибка: " + e);
            }
//                serverSocket.close();
        }
    }

    public void send() throws IOException {
        Socket s = null;
        try { // отправка строки клиенту
//создание объекта и назначение номера порта
            ServerSocket server = new ServerSocket(2107);
            s = server.accept();//ожидание соединения
            PrintStream ps = new PrintStream(s.getOutputStream());
// помещение строки "привет!" в буфер
            ps.println("привет!");
// отправка содержимого буфера клиенту и его очищение
            ps.flush();
            ps.close();
        } catch (IOException e) {
            System.err.println("Ошибка: " + e);
        } finally {
            if (s != null)
                s.close(); // разрыв соединения
        }

    }


}
