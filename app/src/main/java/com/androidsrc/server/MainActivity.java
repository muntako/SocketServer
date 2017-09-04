package com.androidsrc.server;

import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

	MultiThreadedServer server;
//	Server server;
	TextView infoip, msg;
	Handler updateConversationHandler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		infoip = (TextView) findViewById(R.id.infoip);
		msg = (TextView) findViewById(R.id.msg);
//		server = new Server(this);
		server = new MultiThreadedServer(8080);
		new Thread(server).start();
		infoip.setText(server.getIpAddress()+":"+server.getServerPort()+"\n");

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		server.onDestroy();
	}
	
}