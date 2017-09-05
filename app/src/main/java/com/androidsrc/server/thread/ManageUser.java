package com.androidsrc.server.thread;

/**
 * Created by ADMIN on 31-Aug-17.
 */


import com.alibaba.fastjson.JSONPObject;
import com.androidsrc.server.model.RequestClient;
import com.androidsrc.server.model.ResponseToClient;
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


    DataInputStream dataInputStream = null;
    DataOutputStream dataOutputStream = null;

    interface forwardMessage{
        void onMessageReceived(ManageUser user);
    }

    private forwardMessage forwardMessage;


    ManageUser(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText = serverText;
    }

    public void run() {
        while (!clientSocket.isClosed()) {
            try {
                InputStream input = clientSocket.getInputStream();
                OutputStream output = clientSocket.getOutputStream();

                dataInputStream = new DataInputStream(
                        input);
                dataOutputStream = new DataOutputStream(
                        output);

                String messageFromClient = null, messageToClient = null;

                //If no message sent from client, this code will block the program
                try {
                    if (dataInputStream==null)
                        return;
                    messageFromClient = dataInputStream.readUTF();
                }catch (Exception e){
                    e.printStackTrace();
                }

                Gson gson = new Gson();
                requestClient = gson.fromJson(messageFromClient,RequestClient.class);
                System.out.println("Data from client " + messageFromClient);
                if (requestClient.getRequestKey().equalsIgnoreCase(REQUEST_CONNECT_CLIENT)) {
                    messageToClient = "Connection Accepted\t" + getTime();
                    sendMessage(messageToClient);
                } else if (requestClient.getRequestKey().equalsIgnoreCase(FORWARDED_MESSAGE)){
                    messageToClient = "Connection Accepted\n" + requestClient.getMessage();
                    sendMessage(messageToClient);
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

    private void sendMessage(String message) throws IOException {
        ResponseToClient response = new ResponseToClient(true,"server","connection success");
        Gson gson = new Gson();
        dataOutputStream.writeUTF(gson.toJson(response));
        System.out.println(message);
    }

    void sendMessage(RequestClient req){
        ResponseToClient response = new ResponseToClient(true,req.getNickname(),req.getMessage());
        Gson gson = new Gson();
        try {
            dataOutputStream.writeUTF(gson.toJson(response));
            System.out.println(req.getNickname()+" to "+clientSocket.getInetAddress().getHostAddress()+"\nmessage\t:"+req.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
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