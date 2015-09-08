/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.net.Socket;

//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.PrintWriter;
//import java.text.SimpleDateFormat;
//import java.util.Calendar;
//import java.util.Map;
//import java.util.logging.Level;
//import java.util.logging.Logger;

/**
 *
 * @author Simon, Afrooz and Ib
 */
public class ServerThread implements Runnable {

    Socket socket;

    public ServerThread(Socket socket) {

        this.socket = socket;
    }

    private void handleClient() {

    }

    @Override
    public void run() {

    }

}

//Paste this code into runmethod to test the server
//BufferedReader dataInFromSocket;
//    PrintWriter dataOutFromSocket;
//    String stringFromClient;
//    String[] stringArray;
//    String beforeHashtag;
//    String afterHashtag;
//    String word;
//
//try {
//            dataInFromSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//            dataOutFromSocket = new PrintWriter(socket.getOutputStream(), true);
//            String timeStamp = new SimpleDateFormat("yyyy:MM:dd_HH:mm:ss").format(Calendar.getInstance().getTime());
//            dataOutFromSocket.println("Server timestamp:" + timeStamp);
//
//        } catch (IOException ex) {
//            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        while (true) {
//
//            try {
//
//                stringFromClient = dataInFromSocket.readLine();
//
//                String[] parts = stringFromClient.split("#");
//      
//                
//                beforeHashtag = parts[0];
//                afterHashtag = parts[1];
//
//                if (beforeHashtag == null || beforeHashtag.trim().equals("")){
//                
//                    socket.close();
//                    return;
//                
//                }
//                
//                
//                switch (beforeHashtag) {
//
//                    case "upper":
//
//                        dataOutFromSocket.println(afterHashtag.toUpperCase());
//
//                        break;
//
//                    case "lower":
//
//                        dataOutFromSocket.println(afterHashtag.toUpperCase());
//
//                        break;
//
//                    case "reverse":
//
//                        dataOutFromSocket.println(new StringBuilder(afterHashtag).reverse().toString());
//
//                        break;
//
//
//                }
//
//            } catch (IOException ex) {
//                Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
//            }
//
//        }
//    }
