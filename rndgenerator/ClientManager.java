/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rndgenerator;

import java.net.*;
import java.io.*;
import java.util.*;

public class ClientManager implements Runnable
{
	InputStream rd;
    OutputStream wr;
    Random rnd;

    public ClientManager(Socket socket)
    {
        rnd = new Random();
        try {
            wr = socket.getOutputStream();
            rd = socket.getInputStream();
        } catch (Exception e)
        {
            System.out.println("Error obtaining input/output streams from socket.");
        }
        new Thread(this).start();
    }

    @Override
    public void run()
    {
        while (true)
        {
            try {
                int request = rd.read();
                switch (request)
                {
                    case RndServer.NEXT_RND:
                        int r = rnd.nextInt();
                        wr.write(r >> 24);
                        wr.write((r >> 16) & 255);
                        wr.write((r >> 8) & 255);
                        wr.write(r & 255);
                        break;
                    case -1:
                        System.out.println("Client left.");
                        return;
                }
            } catch (Exception e)
            {
                System.out.println("Error communicating with the client");
            }
        }
    }
}
