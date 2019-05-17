/*********************************************************************

    Chat server: accept chat messages from clients.
    
    Sender chatName and GPS coordinates are encoded
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
import java.util.UUID;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.async.QueryBuilder;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.MessageManager;
import edu.stevens.cs522.chat.managers.PeerManager;
import edu.stevens.cs522.chat.managers.TypedCursor;
import edu.stevens.cs522.chat.rest.ChatHelper;
import edu.stevens.cs522.chat.rest.ServiceManager;
import edu.stevens.cs522.chat.settings.Settings;
import edu.stevens.cs522.chat.util.InetAddressUtils;
import edu.stevens.cs522.chat.util.ResultReceiverWrapper;

import static edu.stevens.cs522.chat.settings.Settings.SETTINGS;

public class ChatActivity extends Activity implements OnClickListener, QueryBuilder.IQueryListener<ChatMessage>, ResultReceiverWrapper.IReceive {

	final static public String TAG = ChatActivity.class.getCanonicalName();
		
    /*
     * UI for displaying received messages
     */
	private SimpleCursorAdapter messages;
	
	private ListView messageList;

    private SimpleCursorAdapter messagesAdapter;

    private MessageManager messageManager;

    private PeerManager peerManager;

    private ServiceManager serviceManager;

    /*
     * Widgets for dest address, message text, send button.
     */
    private EditText chatRoomName;

    private EditText messageText;

    private Button sendButton;

    private static int Count;

    public static final String ViewPeers="viewPeers";
    private static String chat_name = "Chat_name_text";

    private Cursor AllMessagesCursor;
    public static final int MESSAGES_LOADER_ID = 1;
    public static final int PEERS_LOADER_ID=2;

    private SharedPreferences settings;
    private Settings setting;
    /*
     * Helper for Web service
     */
    private ChatHelper helper;

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

        setContentView(R.layout.messages);

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

        // TODOinstantiate helper for service
        helper=new ChatHelper(this);
        // TODOinitialize sendResultReceiver
        sendResultReceiver=new ResultReceiverWrapper(new Handler());

        /**
         * Initialize settings to default values.
         */
        if (!Settings.isRegistered(this)) {
            // TODOlaunch registration activity
            Settings.getClientId(this);
            startActivity(new Intent(this, RegisterActivity.class));
        }
        SharedPreferences prefs = this.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("client-id",Settings.getClientId(this).toString());
        editor.commit();

        // TOD(SYNC) initialize serviceManager
        serviceManager=new ServiceManager(this);

        /**
         * Initialize settings to default values.
         */
        if (!Settings.isRegistered(this)) {
            // TODOlaunch registration activity
            Settings.getClientId(this);
            startActivity(new Intent(this, RegisterActivity.class));
        }

    }

	public void onResume() {
        super.onResume();
        sendResultReceiver.setReceiver(this);
        if (Settings.SYNC) {
            serviceManager.scheduleBackgroundOperations();
        }
    }

    public void onPause() {
        super.onPause();
        sendResultReceiver.setReceiver(null);
        if (Settings.SYNC) {
            serviceManager.cancelBackgroundOperations();
        }
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

            // TODOPEERS provide the UI for registering
            case R.id.register:
                Intent register = new Intent(this, RegisterActivity.class);
                startActivity(register);
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
        if (helper != null) {

            String chatRoom;

            String message = null;

            // TODOget chatRoom and message from UI, and use helper to post a message
            messageText=(EditText) findViewById(R.id.message_text);
            chatRoomName=(EditText) findViewById(R.id.chat_room);
            message=messageText.getText().toString().trim();
            chatRoom=chatRoomName.getText().toString().trim();
            helper.postMessage(chatRoom,message,sendResultReceiver);
            // TODOadd the message to the database
            /*final ChatMessage chatMessage = new ChatMessage();

            chatMessage.chatRoom=chatRoomName.getText().toString().trim();
            chatMessage.messageText=messageText.getText().toString().trim();
            chatMessage.sender=this.setting.getChatName(this);
            chatMessage.senderId=this.setting.getSenderId(this);
            Count++;
            chatMessage.id=Count;
            // End tod

            Log.i(TAG, "Sent message: " + message);

            messageText.setText("");*/
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


}