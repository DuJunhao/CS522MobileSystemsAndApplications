/*********************************************************************

    Chat server: accept chat messages from clients.
    
    Sender name and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2017 Stevens Institute of Technology

**********************************************************************/
package edu.stevens.cs522.chat.activities;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;
import android.os.ResultReceiver;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.async.QueryBuilder;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.MessageManager;
import edu.stevens.cs522.chat.managers.PeerManager;
import edu.stevens.cs522.chat.managers.TypedCursor;
import edu.stevens.cs522.chat.services.ChatService;
import edu.stevens.cs522.chat.services.IChatService;
import edu.stevens.cs522.chat.util.InetAddressUtils;
import edu.stevens.cs522.chat.util.ResultReceiverWrapper;

public class ChatActivity extends Activity implements OnClickListener, QueryBuilder.IQueryListener<ChatMessage>, ServiceConnection, ResultReceiverWrapper.IReceive {

	final static public String TAG = ChatActivity.class.getCanonicalName();
		
    /*
     * UI for displaying received messages
     */
	private SimpleCursorAdapter messages;
	
	private ListView messageList;

    private SimpleCursorAdapter messagesAdapter;

    private MessageManager messageManager;

    private PeerManager peerManager;

    /*
     * Widgets for dest address, message text, send button.
     */
    private EditText destinationHost;

    private EditText destinationPort;

    private EditText messageText;

    private Button sendButton;

    private static int Count;

    public static final String ViewPeers="viewPeers";

    private Cursor AllMessagesCursor;
    public static final int MESSAGES_LOADER_ID = 1;
    public static final int PEERS_LOADER_ID=2;
    /*
     * Use to configure the app (user name and port)
     */
    private SharedPreferences settings;

    /*
     * Reference to the service, for sending a message
     */
    private IChatService chatService;

    /*
     * For receiving ack when message is sent.
     */
    private ResultReceiverWrapper sendResultReceiver;
	
	/*
	 * Called when the activity is first created. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        /**
         * Initialize settings to default values.
         */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

		if (savedInstanceState == null) {
			PreferenceManager.setDefaultValues(this, R.xml.settings, false);
		}

        settings = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.messages);

        // TODOuse SimpleCursorAdapter to display the messages received.
        String[] from = new String[] { MessageContract.SENDER, MessageContract.MESSAGE_TEXT };
        int[] to = new int[] { android.R.id.text1, android.R.id.text2 };
        messagesAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2,
                null, from, to,
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        // TODOcreate the message and peer managers, and initiate a query for all messages
        messageManager = new MessageManager(this);
        messageManager.getAllMessagesAsync(this);

        peerManager=new PeerManager(this);
        peerManager.getAllPeersAsync(new QueryBuilder.IQueryListener<Peer>() {
            @Override
            public TypedCursor<Peer> handleResults(TypedCursor<Peer> results) {
                return  results;
            }
            @Override
            public void closeResults() {
            }
        });
        ListView list = (ListView)findViewById(R.id.message_list);
        list.setAdapter(messagesAdapter);
        // TODOinitiate binding to the service
        Intent bindIntent = new Intent(this, ChatService.class);
        bindService(bindIntent, this, Context.BIND_AUTO_CREATE);
        // TODOinitialize sendResultReceiver
        sendResultReceiver=new ResultReceiverWrapper(new Handler());

    }

	public void onResume() {
        super.onResume();
        sendResultReceiver.setReceiver(this);
    }

    public void onPause() {
        super.onPause();
        sendResultReceiver.setReceiver(null);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // TODOinflate a menu with PEERS and SETTINGS options
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chatserver_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {

            // TODOPEERS provide the UI for viewing list of peers
            case R.id.peers:
                Intent viewPeers_Intent=new Intent(this, ViewPeersActivity.class);
                startActivity(viewPeers_Intent);
                break;

            // TODOSETTINGS provide the UI for settings
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;

            default:
        }
        return false;
    }



    /*
     * Callback for the SEND button.
     */
    public void onClick(View v) {
        if (chatService != null) {
            /*
			 * On the emulator, which does not support WIFI stack, we'll send to
			 * (an AVD alias for) the host loopback interface, with the server
			 * port on the host redirected to the server port on the server AVD.
			 */

            InetAddress destAddr;

            int destPort;

            String username;

            String message = null;

            // TODOget destination and message from UI, and username from preferences.
            destinationHost=(EditText) findViewById(R.id.destination_host);
            destinationPort=(EditText) findViewById(R.id.destination_port);
            messageText=(EditText) findViewById(R.id.message_text);
            try {
                destAddr=InetAddress.getByName(destinationHost.getText().toString());
                destPort=Integer.parseInt(destinationPort.getText().toString());
                message=messageText.getText().toString();
                username=settings.getString(SettingsActivity.USERNAME_KEY,SettingsActivity.DEFAULT_USERNAME);

                // TODOuse the service to send a message to the specified destination.
                chatService.send(destAddr,destPort,username,message,sendResultReceiver,Count);
                // End tod

                Log.i(TAG, "Sent message: " + message);

                messageText.setText("");
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle data) {
        switch (resultCode) {
            case RESULT_OK:
                // TODOshow a success toast message
                Toast toast=Toast.makeText(ChatActivity.this,"Successfully received the message ",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                break;
            default:
                // TODOshow a failure toast message
                Toast toastFailure=Toast.makeText(ChatActivity.this,"Failed to receive the message",Toast.LENGTH_LONG);
                toastFailure.setGravity(Gravity.CENTER, 0, 0);
                toastFailure.show();
                break;
        }
    }

    @Override
    public TypedCursor<ChatMessage> handleResults(TypedCursor<ChatMessage> results) {
        // TOD
        AllMessagesCursor=results.getCursor();
        messagesAdapter.changeCursor(AllMessagesCursor);
        if(AllMessagesCursor!=null)
            Count=AllMessagesCursor.getCount();
        messagesAdapter.notifyDataSetChanged();
        return results;
    }

    @Override
    public void closeResults() {
        // TOD
        messagesAdapter.notifyDataSetChanged();
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        // TODOinitialize chatService
        chatService = ((ChatService.ChatBinder)service).getService();
        //Messenger messenger=new Messenger(service);
    }



    @Override
    public void onServiceDisconnected(ComponentName name) {
        chatService = null;
    }
}