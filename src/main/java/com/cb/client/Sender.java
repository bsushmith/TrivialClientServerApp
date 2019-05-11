package com.cb.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Sender implements Runnable{
    private final Socket socket;

    public Sender(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {

            try(
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    BufferedReader inCli = new BufferedReader(new InputStreamReader(System.in));
                    Socket socket = this.socket
                    ) {
                while (true) {
                    String msg = inCli.readLine();
                    out.writeUTF(msg);
                }
            }
            catch (IOException ioe){
                System.exit(1);
            }

    }
}
