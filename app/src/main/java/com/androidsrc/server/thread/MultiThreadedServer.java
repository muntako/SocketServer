package com.androidsrc.server.thread;

/**
 * Created by ADMIN on 31-Aug-17.
 */

import com.androidsrc.server.model.RequestClient;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiThreadedServer implements Runnable, ManageUser.forwardMessage,ManageUser.onSocketClosed {

    private int serverPort = 9000;
    private ServerSocket serverSocket = null;
    private boolean isStopped = false;
    private Thread runningThread = null;

    private Map<String, ManageUser> clients = new HashMap<>();


    public MultiThreadedServer(int port) {
        this.serverPort = port;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void run() {
        synchronized (this) {
            this.runningThread = Thread.currentThread();
        }
        openServerSocket();
        while (!isStopped()) {
            Socket clientSocket = null;
            try {
                clientSocket = this.serverSocket.accept();
            } catch (IOException e) {
                if (isStopped()) {
                    System.out.println("Server Stopped.");
                    return;
                }
                throw new RuntimeException(
                        "Error accepting client connection", e);
            }
            ManageUser client = new ManageUser(clientSocket, "Multithreaded Server");
            client.setForwardMessage(this);
            client.setOnSocketClosed(this);
            new Thread(client).start();
            String id = client.getClientSocket().getInetAddress().getHostAddress();
            if (id != null)
                clients.put(id, client);

            System.out.println("clients connected" + clients.toString());
        }
        System.out.println("Server Stopped.");
    }


    private synchronized boolean isStopped() {
        return this.isStopped;
    }

    public synchronized void stop() {
        this.isStopped = true;
        try {
            this.serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException("Error closing server", e);
        }
    }

    private void openServerSocket() {
        try {
            this.serverSocket = new ServerSocket(this.serverPort);
            System.out.print(getIpAddress() + " port " + serverPort + "\n");
        } catch (IOException e) {
            throw new RuntimeException("Cannot open port " + serverPort + "\n", e);
        }
    }

    public String getIpAddress() {
        String ip = "";
        try {
            Enumeration<NetworkInterface> enumNetworkInterfaces = NetworkInterface
                    .getNetworkInterfaces();
            while (enumNetworkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = enumNetworkInterfaces
                        .nextElement();
                Enumeration<InetAddress> enumInetAddress = networkInterface
                        .getInetAddresses();
                while (enumInetAddress.hasMoreElements()) {
                    InetAddress inetAddress = enumInetAddress
                            .nextElement();

                    if (inetAddress.isSiteLocalAddress()) {
                        ip += "Server running at : "
                                + inetAddress.getHostAddress();
                    }
                }
            }

        } catch (SocketException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            ip += "Something Wrong! " + e.toString() + "\n";
        }
        return ip;
    }

    public void onDestroy() {
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onMessageReceived(ManageUser user, String id) {
        RequestClient requestClient = user.getRequestClient();
        String destination = requestClient.getDestination();
        ManageUser otherUser = clients.get(destination);
        if (otherUser != null) {
            boolean sent = otherUser.sendMessage(requestClient);
            try {
                user.sendMessageNotification(sent, id);
            } catch (IOException e) {
                e.printStackTrace();

            }
        } else
            try {
                user.sendMessageNotification(false, "");
                System.out.print("No client with ip "+destination);
            } catch (IOException e) {
                e.printStackTrace();
            }

    }

    @Override
    public void deleteMap(String key) {
        clients.remove(key);
    }
}