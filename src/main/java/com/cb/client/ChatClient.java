package com.cb.client;

import java.io.*;
import java.net.Socket;


public class ChatClient {

    private static final int PortNumber = 59090;
    private static final String Hostname = "localhost";

    public static void main(String[] args) throws IOException {

        final Socket socket = new Socket(Hostname, PortNumber);
        final BufferedReader inCli = new BufferedReader(new InputStreamReader(System.in));
        final DataInputStream in = new DataInputStream(socket.getInputStream());
        final DataOutputStream out = new DataOutputStream(socket.getOutputStream());

        Thread sendMsg  = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try{
                        String msg = inCli.readLine();
                        out.writeUTF(msg);
                    }
                    catch (IOException ioe){
//                        ioe.printStackTrace();
                        close(socket);
                        System.exit(1);
                    }

                }
            }
        });

        Thread readMsg = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try{
                        String msg = in.readUTF();
                        if (msg.equalsIgnoreCase("PING"))
                            out.writeUTF("OK");
                        else {
                            System.out.println(msg);
                        }
                    }
                    catch (IOException ioe) {
//                        ioe.printStackTrace();
                        close(socket);
                        System.exit(1);
                    }
                }
            }
        });

        sendMsg.start();
        readMsg.start();

    }

    public static void close(Socket socket) {
        try (
                Socket s = socket
        ) {
            System.out.println();
        } catch (IOException ioe) {
            System.out.println();
        }
    }
}
