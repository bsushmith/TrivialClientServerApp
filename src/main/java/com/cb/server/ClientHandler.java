package com.cb.server;

import java.io.*;
import java.util.*;
import java.net.Socket;


public class ClientHandler implements Runnable {
    private String name;
    private final Client client;

    public ClientHandler(Client client)  {
        this.client = client;
        this.name = client.getName();
    }

    public void run(){
        String rcvd;
        while(true) {
            try {
                rcvd = client.getReader().readUTF();
                System.out.println(rcvd);

                if (rcvd.equalsIgnoreCase("bye")) {
                    exitClient(this.name);
                    removeClient(this.name);
                    break;
                }
                sendMsg(rcvd);
            }
            catch(IOException ioe){
                exitClient(this.name);
                removeClient(this.name);
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
        else {
            try {
                this.client.getWriter().writeUTF("Client doesn't exist");
            }
            catch(IOException ioe) {
                ioe.printStackTrace();
            }
        }

        if (returnCode == -1) {
            removeClient(recipientClient);
            bufferMsg(recipientClient, finalClientMsg(msg));
        }
    }

    private int writeMsg(String clientName, String msg){
        try {
            ChatServer.clients.get(clientName).getWriter().writeUTF(msg);
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
                DataInputStream dis = client.getReader();
                DataOutputStream dos = client.getWriter();
                Socket s = client.getSocket();
                ) {
            System.out.println("Exiting client " + clientName);
        }
        catch (IOException ioe) {
            System.out.println("Exiting client");
        }
    }
}