package org.example;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by a.kiperku
 * Date: 28.08.2023
 */

public class ProxyMultiThread {
    public static void main(String[] args) {
        try {
//            if (args.length != 3)
//                throw new IllegalArgumentException("insuficient arguments");
            // and the local port that we listen for connections on
//            String host = args[0];
//            int remoteport = Integer.parseInt(args[1]);
//            int localport = Integer.parseInt(args[2]);
            String host = "127.0.0.1";
            int remoteport = 2107;
            int localport = 2106;
            // Print a start-up message
            System.out.println("Starting proxy for " + host + ":" + remoteport
                    + " on port " + localport);
            ServerSocket server = new ServerSocket(localport);

            boolean b = firstConnect(host, remoteport);
            while (!b){
                Thread.sleep(500);
                b = firstConnect(host, remoteport);
            }


            while (true) {
                new ThreadProxy(server.accept(), host, remoteport);
            }
        } catch (Exception e) {
            System.err.println(e);
            System.err.println("Usage: java ProxyMultiThread "
                    + "<host> <remoteport> <localport>");
        }
    }

    public static boolean firstConnect(String host, int port){
        final Socket socket;
        try {
            socket = new Socket(host, port);
            // a new thread to manage streams from server to client (DOWNLOAD)
            final InputStream inFromServer = socket.getInputStream();
            final OutputStream outToServer = socket.getOutputStream();
            // a new thread for uploading to the server
            new Thread() {
                public void run() {
                    int bytes_read = 64;
                    try {
                        byte key = 68;
                        //TODO [FUZZY] шифровка пакета
                        final byte[] cryptRequest = new byte[64];
                        cryptRequest[0] = key;

                        outToServer.write(cryptRequest, 0, bytes_read);
                        outToServer.flush();
                        //TODO CREATE YOUR LOGIC HERE

                    } catch (IOException ignored) {
                    }
                }
            }.start();
            int bytes_read;
            final byte[] cryptRequest = new byte[1024];
            try {
                while ((bytes_read = inFromServer.read(cryptRequest)) != -1) {
                    if (bytes_read == 204){
                        System.out.println("Подключение установлено");
                        return true;
                    }
                    //TODO CREATE YOUR LOGIC HERE
                }
                outToServer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
//                PrintWriter out = new PrintWriter(new OutputStreamWriter(outToClient));
//                out.flush();
            throw new RuntimeException(e);
        }
        return false;
    }

}



/**
 * Handles a socket connection to the proxy server from the client and uses 2
 * threads to proxy between server and client
 *
 * @author jcgonzalez.com
 */
class ThreadProxy extends Thread {
    private Socket sClient;
    private final String SERVER_URL;
    private final int SERVER_PORT;

    ThreadProxy(Socket sClient, String ServerUrl, int ServerPort) {
        this.SERVER_URL = ServerUrl;
        this.SERVER_PORT = ServerPort;
        this.sClient = sClient;
        this.start();
    }

    @Override
    public void run() {
        try {
            final byte[] request = new byte[1024];
            byte[] reply = new byte[4096];
            final InputStream inFromClient = sClient.getInputStream();
            final OutputStream outToClient = sClient.getOutputStream();
            Socket client = null, server = null;
            // connects a socket to the server
            try {
                server = new Socket(SERVER_URL, SERVER_PORT);
            } catch (IOException e) {
                PrintWriter out = new PrintWriter(new OutputStreamWriter(outToClient));
                out.flush();
                throw new RuntimeException(e);
            }
            // a new thread to manage streams from server to client (DOWNLOAD)
            final InputStream inFromServer = server.getInputStream();
            final OutputStream outToServer = server.getOutputStream();
            // a new thread for uploading to the server
            new Thread() {
                public void run() {
                    int bytes_read;
                    try {
                        while ((bytes_read = inFromClient.read(request)) != -1) {
                            byte key = 1;

                            //TODO [FUZZY] шифровка пакета
                            final byte[] cryptRequest = new byte[1024];
                            for (int i = 0; i < request.length; i++) {
                                final byte b = request[i];
                                cryptRequest[i] = (byte) (b + key);
                            }
                            //TODO [FUZZY]

                            outToServer.write(cryptRequest, 0, bytes_read);
                            outToServer.flush();
                            //TODO CREATE YOUR LOGIC HERE
                        }
                    } catch (IOException ignored) {
                    }
                    try {
                        outToServer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
            // current thread manages streams from server to client (DOWNLOAD)
            int bytes_read;
            try {
                while ((bytes_read = inFromServer.read(reply)) != -1) {
                    outToClient.write(reply, 0, bytes_read);
                    outToClient.flush();
                    //TODO CREATE YOUR LOGIC HERE
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (server != null)
                        server.close();
                    if (client != null)
                        client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            outToClient.close();
            sClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
