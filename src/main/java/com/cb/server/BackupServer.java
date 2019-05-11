package com.cb.server;

import java.io.*;
import java.util.List;
import java.util.Map;

public class BackupServer implements Runnable {

    private final String name;
    private static final String clientsBackupPath = "/tmp/chatserverbackup_clients.ser";
    private static final String bufferedMsgsbackupPath = "/tmp/chatserverbackup_bufferedMsgs.ser";

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

    private static void createBackUp() {

        backUpClients();
        backUpBufferedMsgs();
    }

    public static void restoreBackUp() {
        restoreClients();
        restoreBufferedMsgs();
    }

    private static void backUpBufferedMsgs() {
        try (
                FileOutputStream fileOut = new FileOutputStream(bufferedMsgsbackupPath);
                ObjectOutputStream objOut = new ObjectOutputStream(fileOut)
        ) {
            objOut.writeObject(ChatServer.bufferedClientMsgs);

        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static void backUpClients() {
        try (
                FileOutputStream fileOut = new FileOutputStream(clientsBackupPath);
                ObjectOutputStream objOut = new ObjectOutputStream(fileOut)
        ) {
            objOut.writeObject(ChatServer.clients);
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    private static void restoreClients() {
        try (
                FileInputStream fileIn = new FileInputStream(clientsBackupPath);
                ObjectInputStream objIn = new ObjectInputStream(fileIn)
        ) {
            //noinspection unchecked
            ChatServer.clients = (Map<String, Client>) objIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static void restoreBufferedMsgs() {

        try (
                FileInputStream fileIn = new FileInputStream(bufferedMsgsbackupPath);
                ObjectInputStream objIn = new ObjectInputStream(fileIn)
        ) {
            //noinspection unchecked
            ChatServer.bufferedClientMsgs = (Map<String, List<String>>) objIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
