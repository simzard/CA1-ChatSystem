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
    String msg;
    Client client;

    
    
    public ClientTest() {
    }

    @BeforeClass
    public static void setUpClass() {
        new Thread (new Runnable() {
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

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of send method, of class Client.
     */
    @Test
    public void testSend() {
        
        // need to wait some time before the server thread is ready to accept a client connection
        // otherwise we get : connection refused 
        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
            Logger.getLogger(ClientTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
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
        assertEquals(msg, "USERLIST#simon");
        msg = null; // remember to reset test to null 
        client.send("MSG#*#hej til alle");
        while (msg == null) ;
        assertEquals(msg, "MSG#simon#hej til alle");
        msg = null;
    }

        // TODO review the generated test code and remove the default call to fail.
}
