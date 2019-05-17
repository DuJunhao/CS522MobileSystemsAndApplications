/*********************************************************************

    Chat server: accept chat messages from clients.
    
    Sender chatName and GPS coordinates are encoded
    in the messages, and stripped off upon receipt.

    Copyright (c) 2017 Stevens Institute of Technology

**********************************************************************/
package edu.stevens.cs522.chat.activities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.managers.TypedCursor;
import edu.stevens.cs522.chat.rest.ChatHelper;
import edu.stevens.cs522.chat.settings.Settings;
import edu.stevens.cs522.chat.util.ResultReceiverWrapper;

import static edu.stevens.cs522.chat.settings.Settings.SETTINGS;

public class RegisterActivity extends Activity implements OnClickListener, ResultReceiverWrapper.IReceive {

	final static public String TAG = RegisterActivity.class.getCanonicalName();
		
    /*
     * Widgets for dest address, message text, send button.
     */
    private TextView clientIdText;

    private EditText userNameText;

    private static String chat_name = "Chat_name_text";

    private Button registerButton;

    /*
     * Helper for Web service
     */
    private ChatHelper helper;

    /*
     * For receiving ack when registered.
     */
    private ResultReceiverWrapper registerResultReceiver;
	
	/*
	 * Called when the activity is first created. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        /**
         * Initialize settings to default values.
         */
		if (Settings.isRegistered(this)) {
			finish();
            return;
		}

        setContentView(R.layout.register);

        // TODOinstantiate helper for service
        helper=new ChatHelper(this);
        // TODOinitialize registerResultReceiver
        registerResultReceiver=new ResultReceiverWrapper(new Handler());
        // TODOget references to views
        userNameText=(EditText)findViewById(R.id.chat_name_text);
        SharedPreferences prefs = this.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
        String senderName = prefs.getString(chat_name, null);
        if(senderName==null)
        {
            userNameText.setText(R.string.user_name_pref);
        }
        userNameText.setText(senderName);
    }

	public void onResume() {
        super.onResume();
        registerResultReceiver.setReceiver(this);
    }

    public void onPause() {
        super.onPause();
        registerResultReceiver.setReceiver(null);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    /*
     * Callback for the REGISTER button.
     */
    public void onClick(View v) {
        if (!Settings.isRegistered(this) && helper != null) {

            String userName = null;

            // TODOget userName from UI, and use helper to register
            userNameText=(EditText)findViewById(R.id.chat_name_text);
            userName=userNameText.getText().toString().trim();
            helper.register(userName,registerResultReceiver);
            // End odo
            SharedPreferences prefs = this.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(chat_name, userName);
            editor.commit();
            Log.i(TAG, "Registered: " + userName);

            // finish();

        }
    }

    @Override
    public void onReceiveResult(int resultCode, Bundle data) {
        switch (resultCode) {
            case RESULT_OK:
                // TODOshow a success toast message
                Toast toast=Toast.makeText(this,"Successful to register",Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                break;
            default:
                // TODOshow a failure toast message
                Toast toastFailure=Toast.makeText(this,"Failed to register",Toast.LENGTH_LONG);
                toastFailure.setGravity(Gravity.CENTER, 0, 0);
                toastFailure.show();
                break;
        }
    }

}