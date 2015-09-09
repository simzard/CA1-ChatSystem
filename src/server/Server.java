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
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 *
 * @author Simon, Afrooz and Ib
 */
public class Server implements ServerInterface {

    private static final Logger logger = Logger.getLogger(Server.class.getName());
    
    private static boolean running = true;
    
    Socket socket;

    public static Logger getLogger() {
        return logger;
    }
    
    public static boolean isRunning() {
        return running;
    }
    
    public Server(Socket socket) {
        this.socket = socket;
    }

    

    @Override
    public void stopServer() {
        logger.info("Stopping server...");
        running = false;
    }

     

    public static void main(String[] args) throws IOException {

        Handler fh = new FileHandler("chatlog.txt", false);  // overwrite
        fh.setFormatter(new SimpleFormatter());
        logger.addHandler(fh);
        
        
        String ip = "localhost";
        int port = 9090;

        if (args.length == 2) {

            ip = args[0];
            port = Integer.parseInt(args[1]);

        }

        ServerSocket serverSocket = new ServerSocket();

        serverSocket.bind(new InetSocketAddress(ip, port));

        logger.info("Server started at ip:" + ip + " listening at port " + port);
        
        while (Server.isRunning()) {

            ServerThread serverObject = new ServerThread(serverSocket.accept());

            logger.info("New client connected");
            
            Thread serverThread = new Thread(serverObject);
            
            serverThread.start();
        }

    }
}
