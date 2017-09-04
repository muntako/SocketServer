package com.androidsrc.server;

import java.io.*;
import java.net.*;
import java.util.Enumeration;
public class echo3 {
    public static void main(String args[]) {

        MultiThreadedServer server = new MultiThreadedServer(9999);
        new Thread(server).start();
    }


}
