package com.androidsrc.server;

import com.androidsrc.server.thread.MultiThreadedServer;

public class echo3 {
    public static void main(String args[]) {

        MultiThreadedServer server = new MultiThreadedServer(9000);
        new Thread(server).start();
    }
}
