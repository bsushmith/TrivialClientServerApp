package com.cb.server;

import java.io.*;
import java.util.List;
import java.util.Map;

public class BackupServer implements Runnable {
    private String name;
    private static String clientsBackupPath = "/tmp/chatserverbackup_clients.ser";
    private static String bufferedMsgsbackupPath = "/tmp/chatserverbackup_bufferedMsgs.ser";

    public BackupServer(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        while (true) {
            createBackUp();
            try {
                Thread.sleep(1500);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }

    public static void createBackUp() {

        backUpClients();
        backUpBufferedMsgs();
    }

    public static void restoreBackUp() {
        restoreClients();
        restoreBufferedMsgs();
    }

    public static void backUpBufferedMsgs() {
        try (
                FileOutputStream fileOut = new FileOutputStream(bufferedMsgsbackupPath);
                ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
        ) {
            objOut.writeObject(ChatServer.bufferedClientMsgs);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void backUpClients() {
        try (
                FileOutputStream fileOut = new FileOutputStream(clientsBackupPath);
                ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
        ) {
            objOut.writeObject(ChatServer.clients);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    public static void restoreClients() {
        try (
                FileInputStream fileIn = new FileInputStream(clientsBackupPath);
                ObjectInputStream objIn = new ObjectInputStream(fileIn);
        ) {
            ChatServer.clients = (Map<String, Client>) objIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void restoreBufferedMsgs() {

        try (
                FileInputStream fileIn = new FileInputStream(bufferedMsgsbackupPath);
                ObjectInputStream objIn = new ObjectInputStream(fileIn);
        ) {
            ChatServer.bufferedClientMsgs = (Map<String, List<String>>) objIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
