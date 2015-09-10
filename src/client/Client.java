/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.NoSuchElementException;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.text.BadLocationException;

/**
 *
 * @author simon
 */
public class Client extends Observable implements Observer {

    Socket socket;
    private int port;
    String userName = null;
    private InetAddress serverAddress;
    private Scanner input;
    private PrintWriter output;
    private boolean GUI = false;

    boolean running = true;

    public Client(String ip, int port, String userName, Observer observer) {
        if (observer != null) {
            this.addObserver(observer);
            GUI = true;
        }
        this.userName = userName;
        try {
            connect(ip, port);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        //System.out.println("Sending 'USER#" + userName + "'");
        send("USER#" + userName);

        Notifier notifier = new Notifier();
        notifier.addObserver(this);
        Thread notifierThread = new Thread(notifier);

        notifierThread.start();
        if (!GUI) {
            Scanner userInput = new Scanner(System.in);
            String sendString = null;
            do {
                sendString = userInput.nextLine();
                output.println(sendString);
            } while (!sendString.equals("STOP#"));

            running = false;
        }
    }

    public void connect(String address, int port) throws UnknownHostException, IOException {
        this.port = port;
        serverAddress = InetAddress.getByName(address);
        socket = new Socket(serverAddress, port);
        input = new Scanner(socket.getInputStream());
        output = new PrintWriter(socket.getOutputStream(), true);  //Set to true, to get auto flush behaviour
    }

    public void send(String msg) {
        output.println(msg);
    }

    public void stop() throws IOException {
        output.println("STOP#");
    }

    public String receive() {
        String msg = null;

        try {
            msg = input.nextLine();
        } catch (NoSuchElementException e) {
            return null;
        }

        return msg;
    }

    public static void main(String[] args) {
        String userName = "simon";
        int port = 9090;
        String ip = "localhost";
        switch (args.length) {
            case 3:
                userName = args[2];
            case 2:
                port = Integer.parseInt(args[0]);
                ip = args[1];

                break;
        }

        Client commandLineClient = new Client(ip, port, userName, null);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (GUI) {

            setChanged();
            notifyObservers(arg);
        } else {
            String responseText = (String) arg;
            String tokens[] = responseText.split("#");

            String display = "";

            if (tokens[0].equals("USERLIST")) {
                System.out.println(responseText);

            }

            if (tokens[0].equals("MSG")) {
                System.out.println(responseText);

            }
        }
    }

    private class Notifier extends Observable implements Runnable {

        @Override
        public void run() {
            while (running) {

                String msg = receive();
                if (msg != null) {
                    // oops we have a message notify the observers
                    setChanged();
                    notifyObservers(msg);
                }

            }
        }

    }
}
