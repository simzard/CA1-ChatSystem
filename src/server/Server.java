/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import interfaces.ServerInterface;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Simon, Afrooz and Ib
 */
public class Server implements ServerInterface {

    List<ClientHandler> clients = new ArrayList();
    Socket socket;

    public Server(Socket socket) {
        this.socket = socket;
    }

    private void removeClient(Socket s) {
        for (ClientHandler ch : clients) {
            if (ch.getSocket() == s) {
                // we have found the correct client associated with this socket
                clients.remove(ch);
                break;
            }
        }
    }

    private void addClient(ClientHandler ch) {
        clients.add(ch);
    }

    @Override
    public void stopServer() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

     private boolean syntaxIsOK(String message) {
        boolean boo = false;

        String[] msg = message.split("#");
        int msgLength = msg.length;

        if (msgLength == 0) {
            boo = false;
        } else if (msgLength == 1 && "STOP".equals(msg[0])) {
            boo = true;
        } else if (msgLength == 2 && "USER".equals(msg[0]) && msg[1].length() >= 1) {
            boo = true;
        } else if (msgLength == 3 && "MSG".equals(msg[0]) && msg[1].length() >= 1 && msg[2].length() >= 1) {
            boo = !(msg[1].contains(",") && msg[1].startsWith(",") || msg[1].endsWith(","));
        }
        return boo;
    }

     private String processInput(String message) { //ikke færdig endnu! 
        String s = null;

        String[] parts = message.split("#");

        switch (parts[0]) {
            case "USER":
                
                break;
            case "MSG":
                
                break;
            case "USERLIST":
                s = parts[0] + "#" + parts[1];
                break;
            case "STOP":
                return null;

        }
        return s;
    }

    public static void main(String[] args) throws IOException {

        String ip = "localhost";
        int port = 4321;

        if (args.length == 2) {

            ip = args[0];
            port = Integer.parseInt(args[1]);

        }

        ServerSocket serverSocket = new ServerSocket();

        serverSocket.bind(new InetSocketAddress(ip, port));

        while (true) {

            ServerThread serverObject = new ServerThread(serverSocket.accept());

            Thread serverThread = new Thread(serverObject);
            serverThread.start();
        }

    }
}
