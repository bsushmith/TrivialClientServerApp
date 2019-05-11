package com.cb.server;

import java.io.*;
import java.util.*;
import java.net.Socket;


public class ClientHandler implements Runnable {
    private final String name;
    private final Client client;

    public ClientHandler(Client client)  {
        this.client = client;
        this.name = client.getName();
    }

    private void broadcastOnlineStatus() {
        try {
            for (Client c : ChatServer.clients.values()) {
                if (ChatServer.availableClients.contains(c.getName()) && !c.getName().equals(name))
                    c.getWriter().writeUTF(name + " is Online");
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void getOnlineClientsStatus(){
        try {
            if (ChatServer.availableClients.size() > 1) {
                client.getWriter().writeUTF("Online Clients: ");
                for (String s : ChatServer.availableClients) {
                    if (!name.equals(s))
                        client.getWriter().writeUTF(s);
                }
            }
            else client.getWriter().writeUTF("You are the lone warrior on this Server");
        }
        catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private void broadcastExitStatus(){
        try {
            for (Client c : ChatServer.clients.values()) {
                if ( ChatServer.availableClients.contains(c.getName()) && !c.getName().equals(name))
                    c.getWriter().writeUTF(name + " went Offline");
            }
        }
        catch (IOException ioe ) {
            ioe.printStackTrace();
        }
    }

    private void sendMsg(String rcvd) {

        StringTokenizer s = new StringTokenizer(rcvd, ":");
        if (s.countTokens() != 2){
            return;
        }
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
        broadcastExitStatus();
    }

    private void exitClient(String clientName){
        try (
                DataInputStream dis = client.getReader();
                DataOutputStream dos = client.getWriter();
                Socket s = client.getSocket()
                ) {
            System.out.println("Exiting client " + clientName);
        }
        catch (IOException ioe) {
            System.out.println("Exiting client");
        }
    }

    public void run(){
        broadcastOnlineStatus();
        getOnlineClientsStatus();
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

}