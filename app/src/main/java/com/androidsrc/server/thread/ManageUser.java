package com.androidsrc.server.thread;

/**
 * Created by ADMIN on 31-Aug-17.
 */


import com.alibaba.fastjson.JSONPObject;
import com.androidsrc.server.model.RequestClient;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**

 */
public class ManageUser implements Runnable {

    private static final String REQUEST_CONNECT_CLIENT = "request-connect-client";
    public static final String SEND_MESSAGE_CLIENT = "send-message-client";
    public static final String FORWARDED_MESSAGE = "forwarded-message";

    private Socket clientSocket = null;
    private String serverText = null;

    private RequestClient requestClient = null;
    private String ipAddress;

    interface forwardMessage{
        void onMessageReceived(ManageUser user);
    }

    private forwardMessage forwardMessage;


    ManageUser(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText = serverText;
    }

    public void run() {
        DataInputStream dataInputStream = null;
        DataOutputStream dataOutputStream = null;
        while (!clientSocket.isClosed()) {
            try {
                InputStream input = clientSocket.getInputStream();
                OutputStream output = clientSocket.getOutputStream();

                dataInputStream = new DataInputStream(
                        input);
                dataOutputStream = new DataOutputStream(
                        output);

                String messageFromClient, messageToClient = null;

                //If no message sent from client, this code will block the program
                messageFromClient = dataInputStream.readUTF();

                Gson gson = new Gson();
                requestClient = gson.fromJson(messageFromClient,RequestClient.class);
                ipAddress = requestClient.getIpAddress();
                System.out.println("RequestClient processed: " + getTime());
                System.out.println("Data from client " + messageFromClient);
                if (requestClient.getRequestKey().equalsIgnoreCase(REQUEST_CONNECT_CLIENT)) {
                    messageToClient = "Connection Accepted\n" + getTime();
                    dataOutputStream.writeUTF(messageToClient);
                    System.out.println("--Server response :" + messageToClient);
                } else if (requestClient.getRequestKey().equalsIgnoreCase(FORWARDED_MESSAGE)){
                    messageToClient = "Connection Accepted\n" + requestClient.getMessage();
                    dataOutputStream.writeUTF(messageToClient);
                    System.out.println("client message:" + messageToClient);
                }else if (requestClient.getRequestKey().equalsIgnoreCase(SEND_MESSAGE_CLIENT)){
                    requestClient.setRequestKey(FORWARDED_MESSAGE);
                    forwardMessage.onMessageReceived(this);
                }
            } catch (IOException e) {
                //report exception somewhere.
                e.printStackTrace();
                System.out.println("IOException " + e);
                try {
                    if (clientSocket != null)
                        clientSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        System.out.println("Connection closed");
    }

    private String getTime(){
        long time = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault());
        Date resultdate = new Date(time);
        return sdf.format(resultdate);
    }

    RequestClient getRequestClient() {
        return requestClient;
    }

    public void setRequestClient(RequestClient requestClient) {
        this.requestClient = requestClient;
    }


    public ManageUser.forwardMessage getForwardMessage() {
        return forwardMessage;
    }

    void setForwardMessage(ManageUser.forwardMessage forwardMessage) {
        this.forwardMessage = forwardMessage;
    }

    String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    public void setClientSocket(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }
}