package com.cb.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;

public class Client implements Serializable {

    private transient final Socket socket;
    private transient final DataInputStream din;
    private transient final DataOutputStream dout;
    private final String name;
    private final InetAddress address;


    public Client(Socket socket, DataInputStream din, DataOutputStream dout, String name){
        this.socket = socket;
        this.din = din;
        this.dout = dout;
        this.name = name;
        this.address = socket.getInetAddress();
    }

    public DataInputStream getReader() {
        return din;
    }

    public DataOutputStream getWriter(){
        return dout;
    }

    public String getName() {
        return name;
    }

    public Socket getSocket() {
        return socket;
    }

    public InetAddress getAddress() {
        return address;
    }
}
