package com.androidsrc.server.thread;

import com.androidsrc.server.model.RequestClient;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


/**
 * Created by ADMIN on 05-Sep-17.
 *
 */

public class messageThread implements Runnable {

    private Socket clientSocket;

    RequestClient requestClient;

    messageThread(Socket clientSocket, RequestClient req) {
        this.clientSocket = clientSocket;
        this.requestClient = req;
    }

    @Override
    public void run() {
        DataOutputStream dataOutputStream = null;
        while (true) {
            try {
                OutputStream output = clientSocket.getOutputStream();
                dataOutputStream = new DataOutputStream(output);

                String messageToClient = null;

                System.out.println("Data from client " + requestClient.getMessage());

                messageToClient = "Connection Accepted\nDelivered to"+requestClient.getDestination()+" \nmessage\t :" + requestClient.getMessage();
                dataOutputStream.writeUTF(messageToClient);
                System.out.println("client message:" + messageToClient);
                output.close();
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
    }
}
