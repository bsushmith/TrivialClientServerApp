package com.cb.client;

import com.cb.server.ChatServer;

import java.io.*;
import java.util.*;
import java.net.Socket;


public class ClientHandler implements Runnable {
    private String name;
    private Socket socket;
    final private DataInputStream in;
    final private DataOutputStream out;

    public ClientHandler(String name, Socket socket, DataInputStream in, DataOutputStream out)  {
        this.name = name;
        this.socket = socket;
        this.in = in;
        this.out = out;
    }

    public void run(){
        String rcvd;
        while(true) {
            try {
                rcvd = in.readUTF();
                System.out.println(rcvd);

                if (rcvd.equalsIgnoreCase("bye")) {
                    exitClient(this.name);
                    System.out.println("Exiting Client 1");
                    break;
                }
                sendMsg(rcvd);
            }
            catch(IOException ioe){
                System.out.println("Exiting Client 11");
                exitClient(this.name);
                break;
            }
        }
    }

    private void sendMsg(String rcvd) {

        StringTokenizer s = new StringTokenizer(rcvd, ":");
        String recipientClient = s.nextToken();
        String msg = s.nextToken();
        int returnCode = 0;

        if (ChatServer.clients.containsKey(recipientClient)) {
            if (ChatServer.availableClients.contains(recipientClient)) {
                returnCode = writeMsg(recipientClient, finalClientMsg(msg));
            } else {
                removeClient(recipientClient);
                bufferMsg(recipientClient, finalClientMsg(msg));
            }
        }

        if (returnCode == -1) {
            removeClient(recipientClient);
            bufferMsg(recipientClient, finalClientMsg(msg));
        }
    }

    private int writeMsg(String clientName, String msg){
        try {
            ChatServer.clients.get(clientName).out.writeUTF(msg);
        }
        catch (IOException ioe) {
            System.out.println("RClient is offline");
            return -1;
        }
        return 0;
    }

    private String finalClientMsg(String msg){
        return this.name + ":" + msg;
    }

    private void bufferMsg(String clientName, String msg){
        ChatServer.bufferedClientMsgs.get(clientName).add(msg);
    }

    private void removeClient(String clientName){
        ChatServer.availableClients.remove(clientName);
    }

    private void exitClient(String clientName){
        try (
                DataInputStream dis = this.in;
                DataOutputStream dos = this.out;
                Socket s = this.socket
                ) {
            System.out.println("Exiting client " + clientName);
        }
        catch (IOException ioe) {
            System.out.println("CAN U SEE ME ?");
        }
    }
}