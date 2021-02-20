package rndgenerator;

import java.io.*;
import java.net.*;


public class RndServer {

    final static int port = 2005;
    final static int NEXT_RND = 1;

    public static void main(String[] args)
    {
        new RndServer().createConnection();
    }

    private boolean createConnection()
    {
        Socket socket;	
		    
        try {
            // first try to connect to other party, if it is already listening
                socket = new Socket("localhost", port);                    
                System.out.println("Server is already running");
                return false;
        } catch (Exception e) {}
            
        // otherwise create a listening socket and wait for the other party to connect
        System.out.println("Server started. Listening for clients.");
        try {
            ServerSocket srv = new ServerSocket(port);
            while (true)
            {
                socket = srv.accept();
                System.out.println("New client arriving.");	
                new ClientManager(socket);
            }
        } catch (UnknownHostException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }
        return true;
    }
}
