package com.cb.server;

import com.cb.client.ClientHandler;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;



public class ChatServer {

    public static Map<String, ClientHandler> clients = Collections.synchronizedMap(new HashMap<String, ClientHandler>());
    public static Set<String> availableClients = Collections.synchronizedSet(new HashSet<String>());
    public static  Map<String, List<String>> bufferedClientMsgs= Collections.synchronizedMap(new HashMap<String, List<String>>());

    private static final int port = 59090;

    public static void main(String... args) {

        try (
                ServerSocket serverSocket = new ServerSocket(port)
        ) {
            while (true) {
                Socket socket = serverSocket.accept();
                ClientHandler clientHandler = addClient(socket);

                Thread t = new Thread(clientHandler);
                t.start();
            }
        } catch (IOException ioe) {
            System.err.println("Process terminated with error " + ioe.getStackTrace());
            System.exit(1);
        }
    }


    private static String getName(DataInputStream din, DataOutputStream dout){
        try {
            dout.writeUTF("Please enter the client ID: ");
            while (true) {
                String name = din.readUTF();
                if (!isOnline(name)) {
                    return name;
                } else {
                    dout.writeUTF("This ID is already taken. Please enter another ID: ");
                }
            }
        }
        catch (IOException e) {
            System.err.println("Caught Exception: ioe " + e.getStackTrace());
            System.exit(1);
        }
        return "-1";
    }

    private static boolean isOnline(String name){
        return (availableClients.contains(name));
    }

    private static ClientHandler addClient(Socket socket) {
        ClientHandler clientHandler = null;
        try {
            DataInputStream din = new DataInputStream(socket.getInputStream());
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
            String name = getName(din, dout);
            clientHandler = new ClientHandler(name, socket, din, dout);
            availableClients.add(name);
            clients.put(name,clientHandler);
            if (!bufferedClientMsgs.containsKey(name)) {
                bufferedClientMsgs.put(name, new ArrayList<String>());
            }
            else {
                unreceivedMsgs(name, dout);
            }

            dout.writeUTF("You can chat now");

        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            return clientHandler;
        }

    }



    private static void unreceivedMsgs(String name, DataOutputStream dout){
        boolean unreadMsgs = false;

        if (!ChatServer.bufferedClientMsgs.get(name).isEmpty()){
            unreadMsgs = true;
        }
        
        try{
            if (unreadMsgs) {
                dout.writeUTF("You have unread messages... ");
                for (String s :ChatServer.bufferedClientMsgs.get(name)){
                    dout.writeUTF(s);
                }
                ChatServer.bufferedClientMsgs.put(name, new ArrayList<String>());
            }
        }
        catch (IOException ioe){
            System.out.println("Unable to read unread messages... ");
        }
    }
    
}
