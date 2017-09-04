package com.androidsrc.server;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;

public class Server {
    private MainActivity activity;
    String TAG = "SERVER";
    private static final String REQUEST_CONNECT_CLIENT = "request-connect-client";
    public static final String SEND_MESSAGE_CLIENT = "send-message-client";
    ServerSocket serverSocket;
    String message = "";
    static final int socketServerPORT = 8080;

    public Server(MainActivity activity) {
        this.activity = activity;
        Thread socketServerThread = new Thread(new SocketServerThread());
        socketServerThread.start();
    }

    public int getPort() {
        return socketServerPORT;
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

    private class SocketServerThread extends Thread {

        @Override
        public void run() {

            Socket socket = null;
            DataInputStream dataInputStream = null;
            DataOutputStream dataOutputStream = null;

            try {
                Log.i(TAG, "Creating server socket");
                if (serverSocket==null)
                    serverSocket = new ServerSocket(socketServerPORT);

                socket = serverSocket.accept();
                while (!socket.isClosed()) {
                    Log.i(TAG,"Still running");
                    dataInputStream = new DataInputStream(
                            socket.getInputStream());
                    dataOutputStream = new DataOutputStream(
                            socket.getOutputStream());

                    final String messageFromClient, messageToClient, request;

                    //If no message sent from client, this code will block the program
                    messageFromClient = dataInputStream.readUTF();

                    final JSONObject jsondata;
                    jsondata = new JSONObject(messageFromClient);
                    long time = System.currentTimeMillis();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd MM yyyy HH:mm");
                    Date resultdate = new Date(time);
                    String dateTime = sdf.format(resultdate);

                    try {
                        request = jsondata.getString("request");
                        activity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                activity.msg.setText("request dari client :" + messageFromClient);
                            }
                        });
                        if (messageFromClient.contains(REQUEST_CONNECT_CLIENT)) {
                            messageToClient = "Connection Accepted\n" + dateTime;
                        } else {
                            messageToClient = "request Accepted\n" +messageFromClient+"\n"+ dateTime;
                        }
                        dataOutputStream.writeUTF(messageToClient);
                        Log.i(TAG,"--Server response :" + messageToClient);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Unable to get request");
                        dataOutputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e(TAG,e.toString());
                    }
                }

            }catch (Exception e){
                Log.e(TAG,e.toString());
            }
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
}
