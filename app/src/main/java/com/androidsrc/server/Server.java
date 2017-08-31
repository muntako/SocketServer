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
//
//	private class SocketServerThread extends Thread {
//
//		int count = 0;
//
//		@Override
//		public void run() {
//			try {
//				serverSocket = new ServerSocket(socketServerPORT);
//
//				while (true) {
//					Socket socket = serverSocket.accept();
//					count++;
//					message += "#" + count + " from "
//							+ socket.getInetAddress() + ":"
//							+ socket.getPort() + "\n";
//
//					activity.runOnUiThread(new Runnable() {
//
//						@Override
//						public void run() {
//							activity.msg.setText(message);
//						}
//					});
//
////					SocketServerFirstReplyThread socketServerFirstReplyThread = new SocketServerFirstReplyThread(
////							socket, count);
////					socketServerFirstReplyThread.run();
//					SocketServerReplyThread socketReplyThread = new SocketServerReplyThread(socket,message);
//					socketReplyThread.run();		}
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//
//	}

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

                while (true) {
                    socket = serverSocket.accept();
                    dataInputStream = new DataInputStream(
                            socket.getInputStream());
                    dataOutputStream = new DataOutputStream(
                            socket.getOutputStream());

                    final String messageFromClient, messageToClient, request;

                    //If no message sent from client, this code will block the program
                    messageFromClient = dataInputStream.readUTF();

                    final JSONObject jsondata;
                    jsondata = new JSONObject(messageFromClient);

                    try {
                        request = jsondata.getString("request");
                        activity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                activity.msg.setText("request dari client :" + request);
                            }
                        });
                        if (request.equals(REQUEST_CONNECT_CLIENT)) {
                            messageToClient = "Connection Accepted";
                            dataOutputStream.writeUTF(messageToClient);
                        }else if(request.equals(SEND_MESSAGE_CLIENT)){
                            // There might be other queries, but as of now nothing.
                            String message = jsondata.getString("message");
                            messageToClient = "Connection Accepted, Server accept message "+ message;
                            dataOutputStream.writeUTF(messageToClient);
                        } else {
                            // There might be other queries, but as of now nothing.
                            messageToClient = "Connection Accepted, Server accept request "+ request;
                            dataOutputStream.writeUTF(messageToClient);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "Unable to get request");
                        dataOutputStream.flush();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
//            finally {
//                if (socket != null) {
//                    try {
//                        socket.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                if (dataInputStream != null) {
//                    try {
//                        dataInputStream.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//
//                if (dataOutputStream != null) {
//                    try {
//                        dataOutputStream.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }

        }

    }

    private class SocketServerFirstReplyThread extends Thread {

        private Socket hostThreadSocket;
        int cnt;

        SocketServerFirstReplyThread(Socket socket, int c) {
            hostThreadSocket = socket;
            cnt = c;
        }

        @Override
        public void run() {
            OutputStream outputStream;
            String msgReply = "Hello from Server, you are #" + cnt;

            try {
                outputStream = hostThreadSocket.getOutputStream();
                PrintStream printStream = new PrintStream(outputStream);
                printStream.print(msgReply);
                printStream.close();

                message += "replayed: " + msgReply + "\n";

                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        activity.msg.setText(message);
                    }
                });

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                message += "Something wrong! " + e.toString() + "\n";
            }

            activity.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    activity.msg.setText(message);
                }
            });
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
