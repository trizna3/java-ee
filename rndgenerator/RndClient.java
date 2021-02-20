package rndgenerator;

import java.net.*;
import java.io.*;

public class RndClient
{
    public static void main(String args[]) throws Exception
    {
        Socket socket = new Socket("localhost", RndServer.port);
        OutputStream wr = socket.getOutputStream();
        InputStream rd = socket.getInputStream();

        BufferedReader vstup = new BufferedReader(new InputStreamReader(System.in));

        while (true)
        {
            if ("quit".equals(vstup.readLine())) break;

            wr.write(RndServer.NEXT_RND);
            int rnd = (rd.read() << 24) +
                      (rd.read() << 16) +
                      (rd.read() << 8) +
                      (rd.read());
            System.out.println("next random: " + rnd);
        }

        wr.close();
        rd.close();
        socket.close();
        System.out.println("Bye.");
    }
}
