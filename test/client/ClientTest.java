/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.IOException;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import server.Server;

/**
 *
 * @author simon
 */
public class ClientTest {

    Observer obs;
    Observer obs2;
    Observer obs3;
    String msg;
    String msg2;
    String msg3;
    Client client;
    Client client2;
    Client client3;

    public ClientTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Server.main(null);
                } catch (IOException ex) {
                    Logger.getLogger(ClientTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
    }

    @AfterClass
    public static void tearDownClass() {
        Server.stopServer();
    }

    /**
     * need to wait some time before the server thread is ready to accept a
     * client connection otherwise we get : connection refused
     */
    @Before
    public void setUp() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(ClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @After
    public void tearDown() {
    }

    /**
     * Testing protocol if USERLIST is returned after sending the USER# command
     */
    @Test
    public void testUserListReturned() {

        try {
            client = new Client();

            obs = new Observer() {
                @Override
                public void update(Observable o, Object arg) {

                    msg = (String) arg;
                }
            };

            client.addObserver(obs);
            client.connect("localhost", 9090);
        } catch (IOException ex) {
            Logger.getLogger(ClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        client.startNotifier();
        client.send("USER#simon");
        while (msg == null) ;
        assertTrue(msg.startsWith("USERLIST#") || msg.contains("simon"));
        msg = null;

    }

    /**
     * Testing protocol if command MSG#*# is working (MSG to all)
     */
    @Test
    public void testMSGtoAll() {

        try {
            client = new Client();

            obs = new Observer() {
                @Override
                public void update(Observable o, Object arg) {

                    msg = (String) arg;
                }
            };

            client.addObserver(obs);
            client.connect("localhost", 9090);
        } catch (IOException ex) {
            Logger.getLogger(ClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        client.startNotifier();
        client.send("USER#afrooz");
        while (msg == null) ;
        msg = null;
        client.send("MSG#*#hello world");
        while (msg == null) ;
        assertEquals(msg, "MSG#afrooz#hello world");
        msg = null;
    }
    
    /**
     * Testing protocol if command STOP# works, by connecting with user#Ib
     * disconnecting and then reconnecting with same username
     */
    @Test
    public void testSTOPCommand() {
        
        
         try {
            client = new Client();

            obs = new Observer() {
                @Override
                public void update(Observable o, Object arg) {

                    msg = (String) arg;
                }
            };

            client.addObserver(obs);
            client.connect("localhost", 9090);
        } catch (IOException ex) {
            Logger.getLogger(ClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        client.startNotifier();
        client.send("USER#ib");
        while (msg == null) ;
        msg = null;
        client.send("STOP#");

        
        
         try {
            client = new Client();

            obs = new Observer() {
                @Override
                public void update(Observable o, Object arg) {

                    msg = (String) arg;
                }
            };

            client.addObserver(obs);
            client.connect("localhost", 9090);
        } catch (IOException ex) {
            Logger.getLogger(ClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        client.startNotifier();
        client.send("USER#ib");
    
    msg = null;

    }
    
    
    

}
