package com.cb.server;

public class BackupServer implements Runnable{
    private String name;

    public BackupServer(String name){
        this.name = name;
    }

    @Override
    public void run() {
        while(true) {
            ChatServer.createBackUp();
            try {
                Thread.sleep(3000);
            }
            catch(InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }
}
