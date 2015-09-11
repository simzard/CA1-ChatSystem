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

    private Scanner input;
    private PrintWriter output;

    boolean running = true;

    private Notifier notifier;
    private Thread notifierThread;

    public void setRunning(boolean r) {
        running = r;
    }

    public Client() {
        notifier = new Notifier();
        notifierThread = new Thread(notifier);
    }

    public Client(String ip, int port, String userName, Observer observer) {
        if (observer != null) {
            this.addObserver(observer);
        }

    }

    public void startNotifier() {
        notifierThread.start();
    }

    public void connect(String address, int port) throws UnknownHostException, IOException {
        InetAddress serverAddress = InetAddress.getByName(address);
        Socket socket = new Socket(serverAddress, port);
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

        Client client = new Client();

        try {
            client.connect(ip, port);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

        client.addObserver(client);
        client.startNotifier();
        client.send("USER#" + userName);

        Scanner userInput = new Scanner(System.in);
        String sendString = null;
        do {
            sendString = userInput.nextLine();
            client.send(sendString);
        } while (!sendString.equals("STOP#"));

        client.setRunning(false);

    }

    @Override
    public void update(Observable o, Object arg) {
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

    private class Notifier implements Runnable {

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
