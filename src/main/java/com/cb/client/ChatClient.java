package com.cb.client;

import java.io.*;
import java.net.Socket;


public class ChatClient {

    private static final int PortNumber = 59090;
    private static final String Hostname = "localhost";

    public static void main(String[] args) throws IOException {

        final Socket socket = new Socket(Hostname, PortNumber);

        Sender messageSender = new Sender(socket);
        Reader messageReader = new Reader(socket);

        Thread sendMsg = new Thread(messageSender);
        Thread readMsg = new Thread(messageReader);

        sendMsg.start();
        readMsg.start();
    }
}