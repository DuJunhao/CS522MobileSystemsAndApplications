package edu.stevens.cs522.chat.rest;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.ResultReceiver;

import edu.stevens.cs522.chat.async.IContinue;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.MessageManager;
import edu.stevens.cs522.chat.managers.PeerManager;
import edu.stevens.cs522.chat.settings.Settings;
import edu.stevens.cs522.chat.util.InetAddressUtils;

import static edu.stevens.cs522.chat.settings.Settings.SETTINGS;

/**
 * Created by dduggan.
 */

public class RequestProcessor {

    private Context context;

    private RestMethod restMethod;

    private PeerManager peerManager;
    private MessageManager messageManager;

    public RequestProcessor(Context context) {
        this.context = context;
        this.restMethod = new RestMethod(context);
        this.messageManager=new MessageManager(context);
        this.peerManager=new PeerManager(context);
    }

    public Response process(Request request) {
        return request.process(this);
    }

    public Response perform(RegisterRequest request) {
        Response response = restMethod.perform(request);
        if (response instanceof RegisterResponse) {
            // TODOupdate the sender senderId in settings, updated peer record PK
            Settings.saveSenderId(this.context,((RegisterResponse) response).getSenderId());
            Settings.saveChatName(this.context,request.chatname);
            SharedPreferences prefs = context.getSharedPreferences(SETTINGS, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("sender-id", Long.toString(((RegisterResponse) response).getSenderId()));
            editor.commit();
        }
        return response;
    }

    public Response perform(PostMessageRequest request) {
        // TODOinsert the message into the local database
        final ChatMessage chatMessage = new ChatMessage();
        chatMessage.sender=Settings.getChatName(this.context);
        chatMessage.chatRoom=request.chatRoom;
        chatMessage.senderId=request.senderId;
        //chatMessage.id=1;
        chatMessage.timestamp=request.timestamp;
        chatMessage.chatRoom=request.chatRoom;
        chatMessage.messageText=request.message;
        chatMessage.latitude=request.latitude;
        chatMessage.longitude=request.longitude;

        Peer sender=new Peer();
        sender.name = chatMessage.sender;
        sender.timestamp = chatMessage.timestamp;
        sender.address = InetAddressUtils.toIpAddress("192.168.1.63");
        sender.port = 8080;
        sender.id=chatMessage.senderId;
        Response response = restMethod.perform(request);
        if (response instanceof PostMessageResponse) {
            // TODOupdate the message in the database with the sequence number
            chatMessage.seqNum=((PostMessageResponse) response).getMessageId();
            chatMessage.id=chatMessage.seqNum;
            peerManager.persistAsync(sender, new IContinue<Long>() {
                @Override
                public void kontinue(Long id) {
                    messageManager.persistAsync(chatMessage);
                }
            });

        }
        return response;
    }

}
