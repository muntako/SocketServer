package com.androidsrc.server.thread;

/**
 * Created by ADMIN on 31-Aug-17.
 */


import com.androidsrc.server.model.RequestClient;
import com.androidsrc.server.model.ResponseToClient;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static com.androidsrc.server.model.Constant.FORWARDED_MESSAGE;
import static com.androidsrc.server.model.Constant.MESSAGE_DELIVERED;
import static com.androidsrc.server.model.Constant.MESSAGE_FROM_OTHER;
import static com.androidsrc.server.model.Constant.MESSAGE_RECEIVED_BY_SERVER;
import static com.androidsrc.server.model.Constant.REQUEST_CONNECT_CLIENT;
import static com.androidsrc.server.model.Constant.SEND_MESSAGE_CLIENT;

/**

 */
public class ManageUser implements Runnable {

    private Socket clientSocket = null;
    private String serverText = null;

    private RequestClient requestClient = null;
    private String ipAddress;


    DataInputStream dataInputStream = null;
    DataOutputStream dataOutputStream = null;

    interface forwardMessage {
        void onMessageReceived(ManageUser user, String idRequest);
    }

    private forwardMessage forwardMessage;

    interface onSocketClosed {
        void deleteMap(String key);
    }

    private onSocketClosed onSocketClosed;


    ManageUser(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText = serverText;
    }

    public void run() {
        while (clientSocket.isConnected()&&!clientSocket.isClosed()) {
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
                    if (dataInputStream == null)
                        return;
                    messageFromClient = dataInputStream.readUTF();
                } catch (EOFException e) {
                    messageToClient = "EOFException" + e;
                    dataOutputStream.writeUTF(messageToClient);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Gson gson = new Gson();
                requestClient = gson.fromJson(messageFromClient, RequestClient.class);
                if (requestClient != null)
                    if (requestClient.getRequestKey().equalsIgnoreCase(REQUEST_CONNECT_CLIENT)) {
                        System.out.println("Client Request Connect" + messageFromClient);
                        messageToClient = "Connection Accepted\t" + getTime();
                        ACKMessage(true, messageToClient);
                    } else if (requestClient.getRequestKey().equalsIgnoreCase(SEND_MESSAGE_CLIENT)) {
//                    System.out.println("Client send message" + messageFromClient);
                        forwardMessage.onMessageReceived(this, requestClient.getIdRequest());
                    }
            } catch (Exception e) {
                //report exception somewhere.
                e.printStackTrace();
                System.out.println("IOException " + e);
                try {
                    if (clientSocket != null)
                        clientSocket.close();
                    ACKMessage(false, "socket closed");
                    onSocketClosed.deleteMap(getClientSocket().getInetAddress().getHostAddress());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        System.out.println("Connection closed");
    }

    private String getTime() {
        long time = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault());
        Date resultdate = new Date(time);
        return sdf.format(resultdate);
    }

    private void ACKMessage(boolean success, String message) throws IOException {
        ResponseToClient response = new ResponseToClient(success, "server", message, MESSAGE_RECEIVED_BY_SERVER);
        Gson gson = new Gson();
        dataOutputStream.writeUTF(gson.toJson(response));
        System.out.println(message);
    }

    void sendMessageNotification(boolean success, String id) throws IOException {
        ResponseToClient response = new ResponseToClient(success, "server", "message delivered", MESSAGE_DELIVERED, id);
        Gson gson = new Gson();
        dataOutputStream.writeUTF(gson.toJson(response));
//        System.out.println(id);
    }

    boolean sendMessage(RequestClient req) {
        ResponseToClient response = new ResponseToClient(true, req.getNickname(), req.getMessage(), MESSAGE_FROM_OTHER);
        Gson gson = new Gson();
        if (clientSocket.isClosed()) {
            System.out.println("client closed");
            return false;
        } else
            try {
                dataOutputStream.writeUTF(gson.toJson(response));
                System.out.println(req.getNickname() + "(" + req.getIpAddress() + ") -> " + clientSocket.getInetAddress().getHostAddress() + " :" + req.getMessage());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                //System.out.print(e.getCause() + "");
            }
        return false;
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

    public ManageUser.onSocketClosed getOnSocketClosed() {
        return onSocketClosed;
    }

    public void setOnSocketClosed(ManageUser.onSocketClosed onSocketClosed) {
        this.onSocketClosed = onSocketClosed;
    }
}