/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Simon, Afrooz and Ib
 */
public class ServerThread implements Runnable {

    Socket socket;
    BufferedReader dataInFromSocket;
    //PrintWriter dataOutFromSocket;

    public ServerThread(Socket socket) {

        this.socket = socket;

        try {
            dataInFromSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //dataOutFromSocket = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private boolean addClient(ClientHandler ch) {

        for (ClientHandler c : ClientHandler.getClients()) {
            if (c.getUserName().equals(ch.getUserName())) {
                return false;
            }
        }
        ClientHandler.getClients().add(ch);
        return true;
    }

    private void removeClient(Socket s) {
        for (ClientHandler ch : ClientHandler.getClients()) {
            if (ch.getSocket() == s) {
                // we have found the correct client associated with this socket
                ClientHandler.getClients().remove(ch);
                break;
            }
        }
    }

    private void close() throws IOException {
        socket.close();
        removeClient(socket);
        String serverResponse = "USERLIST#";
        for (ClientHandler cs : ClientHandler.getClients()) {
            serverResponse += cs.getUserName() + ",";
        }
        serverResponse = serverResponse.substring(0, serverResponse.length() - 1);
        sendToAll(serverResponse);
    }

    private void handleClient() {

    }

    private void sendToAll(String message) {

        for (ClientHandler ch : ClientHandler.getClients()) {
            ch.send(message);
        }
    }

    @Override
    public void run() {
        String clientInput = null;
        String serverResponse = null;
        try {
            clientInput = dataInFromSocket.readLine();

        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }

        String msg[] = clientInput.split("#");

        if (msg.length == 2 && msg[0].equals("USER") && msg[1].length() >= 1) {
            String userName = msg[1];
            ClientHandler ch = new ClientHandler(socket, userName);
            if (addClient(ch)) {
                serverResponse = "USERLIST#";
                for (ClientHandler cs : ClientHandler.getClients()) {
                    serverResponse += cs.getUserName() + ",";
                }
                serverResponse = serverResponse.substring(0, serverResponse.length() - 1);
                sendToAll(serverResponse);

                try {
                    while (Server.isRunning()
                            && (clientInput = dataInFromSocket.readLine()) != null
                            && !clientInput.equals("STOP#")) {

                        if (syntaxIsOK(clientInput)) {
                            processInput(clientInput);
                        }

                    }
                    close();
                } catch (IOException ex) {
                    Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

        } else {
            try {
                close();

            } catch (IOException ex) {
                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    private boolean syntaxIsOK(String message) {
        boolean boo = false;

        String[] msg = message.split("#");
        int msgLength = msg.length;

        if (msgLength == 0) {
            boo = false;
        } else if (msgLength == 1 && "STOP".equals(msg[0])) {
            boo = true;

        } else if (msgLength == 3 && "MSG".equals(msg[0]) && msg[1].length() >= 1 && msg[2].length() >= 1) {
            boo = !(msg[1].contains(",") && msg[1].startsWith(",") || msg[1].endsWith(","));
        }
        return boo;
    }

    private void processInput(String message) { //ikke færdig endnu! 
        String s = null;

        String[] parts = message.split("#");

        switch (parts[0]) {
            case "MSG":
                String users[] = parts[1].split(",");

                s = "MSG#";

                String sender = null;
                for (ClientHandler ch : ClientHandler.getClients()) {
                    if (socket == ch.getSocket()) {
                        sender = ch.getUserName();
                        break;
                    }
                }

                if (parts[1].equals("*")) {
                    sendToAll("MSG#" + sender + "#" + parts[2]);
                } else {

                    for (String user : users) {
                        for (ClientHandler ch : ClientHandler.getClients()) {

                            if (ch.getUserName().equals(user)) {

                                ch.send("MSG#" + sender + "#" + parts[2]);
                            }
                        }
                    }
                }

                break;

        }
    }

}
