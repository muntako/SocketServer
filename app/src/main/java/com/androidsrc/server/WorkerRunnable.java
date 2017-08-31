package com.androidsrc.server;

/**
 * Created by ADMIN on 31-Aug-17.
 *
 */

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

/**

 */
public class WorkerRunnable implements Runnable{

    private static final String REQUEST_CONNECT_CLIENT = "request-connect-client";
    public static final String SEND_MESSAGE_CLIENT = "send-message-client";

    private Socket clientSocket = null;
    private String serverText   = null;

    public WorkerRunnable(Socket clientSocket, String serverText) {
        this.clientSocket = clientSocket;
        this.serverText   = serverText;
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

                String messageFromClient, messageToClient = null, request;

                //If no message sent from client, this code will block the program
                messageFromClient = dataInputStream.readUTF();

                final JSONObject jsondata;

                long time = System.currentTimeMillis();
                SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy HH:mm");
                Date resultdate = new Date(time);
                String dateTime = sdf.format(resultdate);
                System.out.println("Request processed: " + dateTime);
                System.out.println("Data from client " + messageFromClient);
                if (messageFromClient.contains(REQUEST_CONNECT_CLIENT)) {
                    messageToClient = "Connection Accepted\n" + dateTime;
                } else {
                    messageToClient = "request Accepted\n" +messageFromClient+"\n"+ resultdate;
                }

                dataOutputStream.writeUTF(messageToClient);
                System.out.println("--Server response :" + messageToClient);
//            output.close();
//            input.close();
            } catch (IOException e) {
                //report exception somewhere.
                e.printStackTrace();
                System.out.println("IOException " + e);
                try {
                    if (clientSocket!=null)
                        clientSocket.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }
}