package com.cb.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Reader implements Runnable {
    private final Socket socket;

    public Reader(Socket socket){
        this.socket = socket;
    }

    @Override
    public void run() {
            try (
                    DataInputStream in = new DataInputStream(socket.getInputStream());
                    DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                    Socket socket = this.socket
                    ){
                while(true) {
                    String msg = in.readUTF();
                    if (msg.equalsIgnoreCase("PING"))
                        out.writeUTF("OK");
                    else {
                        System.out.println(msg);
                    }
                }
            }
            catch (IOException ioe) {
                System.exit(1);
            }
    }
}
