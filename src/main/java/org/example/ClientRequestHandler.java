package org.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by a.kiperku
 * Date: 28.08.2023
 */

public class ClientRequestHandler implements Runnable{


    private final Socket clientSocket;
    private final String targetServerAddress;

    public ClientRequestHandler(Socket clientSocket, String targetServerAddress) {
        this.clientSocket = clientSocket;
        this.targetServerAddress = targetServerAddress;
    }

    @Override
    public void run() {

        try {
            InputStream clientInputStream = clientSocket.getInputStream();
            OutputStream clientOutputStream = clientSocket.getOutputStream();



        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
