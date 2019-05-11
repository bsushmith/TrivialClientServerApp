package com.cb.server;

import java.io.*;
import java.util.List;
import java.util.Map;

public class BackupServer implements Runnable {
    private String name;

    public BackupServer(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        while (true) {
            createBackUp();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }

    public static void createBackUp() {

//        backUpClients();
        backUpBufferedMsgs();
    }

    public static void restoreBackUp() {
//        restoreClients();
        restoreBufferedMsgs();
    }

    public static void backUpBufferedMsgs() {
        try (
                FileOutputStream fileOut = new FileOutputStream("/tmp/chatserverbackup_bufferedMsgs.ser");
                ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
        ) {
            objOut.writeObject(ChatServer.bufferedClientMsgs);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
//
//    public static void backUpClients() {
//        try (
//                FileOutputStream fileOut = new FileOutputStream("/tmp/chatserverbackup_clients.ser");
//                ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
//        ) {
//            objOut.writeObject(ChatServer.clients);
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//        }
//    }
//
//    public static void restoreClients() {
//        try (
//                FileInputStream fileIn = new FileInputStream("/tmp/chatserverbackup_clients.ser");
//                ObjectInputStream objIn = new ObjectInputStream(fileIn);
//        ) {
//            ChatServer.clients = (Map<String, ClientHandler>) objIn.readObject();
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//        } catch (ClassNotFoundException c) {
//            c.printStackTrace();
//        }
//    }

    public static void restoreBufferedMsgs() {

        try (
                FileInputStream fileIn = new FileInputStream("/tmp/chatserverbackup_bufferedMsgs.ser");
                ObjectInputStream objIn = new ObjectInputStream(fileIn);
        ) {
            ChatServer.bufferedClientMsgs = (Map<String, List<String>>) objIn.readObject();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } catch (ClassNotFoundException c) {
            c.printStackTrace();
        }
    }

}
