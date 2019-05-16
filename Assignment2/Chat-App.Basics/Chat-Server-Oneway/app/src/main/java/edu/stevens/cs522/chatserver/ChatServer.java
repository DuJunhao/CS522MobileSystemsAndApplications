/*********************************************************************

    Chat server: accept chat messages from clients.
    
    Sender name and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2017 Stevens Institute of Technology

**********************************************************************/
package edu.stevens.cs522.chatserver;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class ChatServer extends Activity implements OnClickListener {

	final static public String TAG = ChatServer.class.getCanonicalName();
		
	/*
	 * Socket used both for sending and receiving
	 */
	private DatagramSocket serverSocket; 

	/*
	 * True as long as we don't get socket errors
	 */
	private boolean socketOK = true; 

	/*
	 * TODODeclare a listview for messages, and an adapter for displaying messages.
	 */
	private ListView messages_listView;
	private ArrayList<String> messages_list=new ArrayList<String>();
	private ArrayAdapter<String> messageAdapter;
	Button next;

	/*
	 * Called when the activity is first created. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		/**
		 * Let's be clear, this is a HACK to allow you to do network communication on the main thread.
		 * This WILL cause an ANR, and is only provided to simplify the pedagogy.  We will see how to do
		 * this right in a future assignment (using a Service managing background threads).
		 */
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); 
		StrictMode.setThreadPolicy(policy);

		try {
			/*
			 * Get port information from the resources.
			 */
			int port = Integer.parseInt(this.getString(R.string.app_port));
			serverSocket = new DatagramSocket(port);
		} catch (Exception e) {
			Log.e(TAG, "Cannot open socket" + e.getMessage());
			return;
		}

		/*
		 * TODO: Initialize the UI.
		 */
		messages_listView = (ListView)findViewById(R.id.msgList);
		messageAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,messages_list);
		messages_listView.setAdapter(messageAdapter);
	}

	public void onClick(View v) {
		
		byte[] receiveData = new byte[1024];

		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

		try {
			
			serverSocket.receive(receivePacket);
			Log.i(TAG, "Received a packet: "+receivePacket.toString());

			InetAddress sourceIPAddress = receivePacket.getAddress();
			Log.i(TAG, "Source IP Address: " + sourceIPAddress);
			
			/*
			 * TODO: Extract sender and receiver from message and display.
			 */
			receiveData=receivePacket.getData();
			String message=new String(receiveData,"UTF-8");
			int index=message.indexOf("6");
			String name=message.substring(0,index);
			message=message.substring(index+1);
			messages_list.add(message);
			messageAdapter.notifyDataSetChanged();
		} catch (Exception e) {
			
			Log.e(TAG, "Problems receiving packet: " + e.getMessage());
			socketOK = false;
		} 

	}

	/*
	 * Close the socket before exiting application
	 */
	public void closeSocket() {
		serverSocket.close();
	}

	/*
	 * If the socket is OK, then it's running
	 */
	boolean socketIsOK() {
		return socketOK;
	}
	
}