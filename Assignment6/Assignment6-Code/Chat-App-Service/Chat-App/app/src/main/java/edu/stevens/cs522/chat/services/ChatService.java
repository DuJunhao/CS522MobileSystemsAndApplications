package edu.stevens.cs522.chat.services;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Messenger;
import android.os.Process;
import android.os.Looper;
import android.os.Message;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.EditText;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.stevens.cs522.chat.R;
import edu.stevens.cs522.chat.activities.SettingsActivity;
import edu.stevens.cs522.chat.async.IContinue;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.MessageManager;
import edu.stevens.cs522.chat.managers.PeerManager;
import edu.stevens.cs522.chat.util.InetAddressUtils;

import static android.app.Activity.RESULT_OK;


public class ChatService extends Service implements IChatService, SharedPreferences.OnSharedPreferenceChangeListener {

    protected static final String TAG = ChatService.class.getCanonicalName();

    protected static final String SEND_TAG = "ChatSendThread";

    protected static final String RECEIVE_TAG = "ChatReceiveThread";

    protected IBinder binder = new ChatBinder();

    protected SendHandler sendHandler;

    protected Thread receiveThread;
    protected HandlerThread messengerThread;
    protected Messenger messenger;


    protected DatagramSocket chatSocket;

    protected boolean socketOK = true;

    protected boolean finished = false;

    PeerManager peerManager;

    MessageManager messageManager;

    protected int chatPort;

    public static int IdCount;

    public Handler getHandle()
    {
        return this.sendHandler;
    }

    @Override
    public void onCreate() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String port =prefs.getString(SettingsActivity.APP_PORT_KEY, Integer.toString(SettingsActivity.DEFAULT_APP_PORT)) ;
        chatPort=Integer.parseInt(port);
        prefs.registerOnSharedPreferenceChangeListener(this);

        peerManager = new PeerManager(this);
        messageManager = new MessageManager(this);

        try {
            chatSocket = new DatagramSocket(chatPort);
           /* chatSocket.getPort();
            chatSocket.getInetAddress();
            chatSocket.getLocalPort();
            chatSocket.getLocalSocketAddress();
            InetAddress ip=InetAddress.getByName("10.0.2.2");
            chatSocket.connect(ip,6666);*/
        } catch (Exception e) {
            IllegalStateException ex = new IllegalStateException("Unable to init client socket.");
            ex.initCause(e);
            throw ex;
        }

        // TODOinitialize the thread that sends messages
        messengerThread = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
        messengerThread.start();
        Looper messengerLooper = messengerThread.getLooper();
        sendHandler = new SendHandler(messengerLooper);
        messenger = new Messenger(sendHandler);
        // end TOD

        /*
         * This is the thread that receives messages.
         */
        receiveThread = new Thread(new ReceiverThread());
        receiveThread.start();
    }

    @Override
    public void onDestroy() {
        finished = true;
        sendHandler.getLooper().getThread().interrupt();  // No-op?
        sendHandler.getLooper().quit();
        receiveThread.interrupt();
        chatSocket.close();
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
       return binder;
        //return  messenger.getBinder();
    }

    public final class ChatBinder extends Binder {

        public IChatService getService() {
            return ChatService.this;
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals(SettingsActivity.APP_PORT_KEY)) {
            try {
                chatSocket.close();
                chatPort = prefs.getInt(SettingsActivity.APP_PORT_KEY, SettingsActivity.DEFAULT_APP_PORT);
                chatSocket = new DatagramSocket(chatPort);
            } catch (IOException e) {
                IllegalStateException ex = new IllegalStateException("Unable to change client socket.");
                ex.initCause(e);
                throw ex;
            }
        }
    }

    @Override
    public void send(InetAddress destAddress, int destPort, String sender, String messageText, ResultReceiver receiver,int Count) {
        Message message = sendHandler.obtainMessage();
        // TODOsend the message to the sending thread
        sendHandler.destinationPort=destPort;
        sendHandler.senderName=sender;
        sendHandler.messageText=messageText;
        sendHandler.destinationHost=destAddress;
        sendHandler.resultReceiver=receiver;
        IdCount=Count;
        sendHandler.handleMessage(message);
    }


    private final class SendHandler extends Handler {

        public static final String CHAT_NAME = "edu.stevens.cs522.chat.services.extra.CHAT_NAME";
        public static final String CHAT_MESSAGE = "edu.stevens.cs522.chat.services.extra.CHAT_MESSAGE";
        public static final String DEST_ADDRESS = "edu.stevens.cs522.chat.services.extra.DEST_ADDRESS";
        public static final String DEST_PORT = "edu.stevens.cs522.chat.services.extra.DEST_PORT";
        public static final String RECEIVER = "edu.stevens.cs522.chat.services.extra.RECEIVER";

        public SendHandler(Looper looper) {
            super(looper);
        }

        public InetAddress destinationHost;

        public String senderName;

        public int destinationPort;

        public String messageText;

        public ResultReceiver resultReceiver;


        @Override
        public void handleMessage(Message message) {

            try {
                InetAddress destAddr = null;

                int destPort = -1;

                byte[] sendData = null;  // Combine sender and message text; default encoding is UTF-8

                ResultReceiver receiver = null;

                // TODOget data from message (including result receiver)
                /*destinationHost=(EditText) destinationHost.findViewById(R.id.destination_host);
                destinationPort=(EditText) destinationPort.findViewById(R.id.destination_port);
                messageText=(EditText) messageText.findViewById(R.id.message_text);*/

               /* destAddr=InetAddress.getByName(destinationHost.getText().toString());
                destPort=Integer.parseInt(destinationPort.getText().toString());*/
                destAddr=destinationHost;
                destPort=destinationPort;
                receiver=resultReceiver;
                Date currentTime=new Date();
                DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateString = formatter.format(currentTime);
               // String content=name+"|"+dateString+"|"+messageText.getText().toString();
                String content=senderName+"|"+dateString+"|"+messageText;
                //String content=messageText.getText().toString();
                sendData=content.getBytes("UTF-8");
                // End tod

                DatagramPacket sendPacket = new DatagramPacket(sendData,
                        sendData.length, destAddr, destPort);

                chatSocket.send(sendPacket);

                Log.i(TAG, "Sent packet: " + new String(sendData));

                receiver.send(RESULT_OK, null);


            } catch (UnknownHostException e) {
                Log.e(TAG, "Unknown host exception: " + e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, "IO exception: " + e.getMessage());
            }

        }
    }

    private final class ReceiverThread implements Runnable {

        public void run() {

            byte[] receiveData = new byte[1024];
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

            while (!finished && socketOK) {
                Log.i(TAG, "received data" );
                try {

                    chatSocket.receive(receivePacket);
                    Log.i(TAG, "Received a packet");

                    InetAddress sourceIPAddress = receivePacket.getAddress();
                    Log.i(TAG, "Source IP Address: " + sourceIPAddress);

                    String msgContents[] = new String(receivePacket.getData(), 0, receivePacket.getLength()).split("\\|");

                    final ChatMessage message = new ChatMessage();
                    message.sender = msgContents[0];
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date= formatter.parse(msgContents[1]);
                    Log.i(TAG, "The time is: " + date);
                    message.timestamp =date;
                    message.messageText = msgContents[2];
                    Log.i(TAG, "Received from " + message.sender + ": " + message.messageText);

                    Peer sender = new Peer();
                    sender.name = message.sender;
                    sender.timestamp = message.timestamp;
                    sender.address = receivePacket.getAddress();
                    sender.port = receivePacket.getPort();
                    sender.id=date.getTime();

                    peerManager.persistAsync(sender, new IContinue<Long>() {
                        @Override
                        public void kontinue(Long id) {
                            message.senderId = id;
                            IdCount++;
                            message.id=IdCount;
                            messageManager.persistAsync(message);
                        }
                    });

                } catch (Exception e) {

                    Log.e(TAG, "Problems receiving packet.", e);
                    socketOK = false;
                }

            }

        }

    }

}
