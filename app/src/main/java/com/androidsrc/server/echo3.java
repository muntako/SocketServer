package com.androidsrc.server;

import java.io.*;
import java.net.*;
import java.util.Enumeration;
public class echo3 {
    public static void main(String args[]) {

        MultiThreadedServer server = new MultiThreadedServer(9000);
        new Thread(server).start();

//        try {
//            Thread.sleep(20 * 1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println("Stopping Server");
//        server.stop();
    }

//    static class SocketServerThread extends Thread{
//        @Override
//        public void run() {
//            // declaration section:
//// declare a server socket and a client socket for the server
//// declare an input and an output stream
//            ServerSocket echoServer = null;
//            String line;
//            DataInputStream is;
//            PrintStream os;
//            Socket clientSocket = null;
//            echo3 echo = new echo3();
//// Try to open a server socket on port 9999
//// Note that we can't choose a port less than 1023 if we are not
//// privileged users (root)
//
//
//            try {
//                echoServer = new ServerSocket(9999);
//
//                System.out.println(new echo3().getIpAddress()+" port "+echoServer.getLocalPort());
//            }
//            catch (IOException e) {
//                System.out.println(e.toString());
//            }
//// Create a socket object from the ServerSocket to listen and accept
//// connections.
//// Open input and output streams
//            try {
//                clientSocket = echoServer.accept();
//                is = new DataInputStream(clientSocket.getInputStream());
//                os = new PrintStream(clientSocket.getOutputStream());
//// As long as we receive data, echo that data back to the client.
//                while (true) {
//                    line = is.readLine();
//                    os.println(line);
//                }
//            }
//            catch (IOException e) {
//                System.out.println(e.toString());
//            }
//        }
//    }
//
//
//
//    public String getIpAddress() {
//        String ip = "";
//        try {
//            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
//                    .getNetworkInterfaces();
//            while (enumNetworkInterfaces.hasMoreElements()) {
//                NetworkInterface networkInterface = enumNetworkInterfaces
//                        .nextElement();
//                Enumeration<InetAddress> enumInetAddress = networkInterface
//                        .getInetAddresses();
//                while (enumInetAddress.hasMoreElements()) {
//                    InetAddress inetAddress = enumInetAddress
//                            .nextElement();
//
//                    if (inetAddress.isSiteLocalAddress()) {
//                        ip += "Server running at : "
//                                + inetAddress.getHostAddress();
//                    }
//                }
//            }
//
//        } catch (SocketException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//            ip += "Something Wrong! " + e.toString() + "\n";
//        }
//        return ip;
//    }


}
