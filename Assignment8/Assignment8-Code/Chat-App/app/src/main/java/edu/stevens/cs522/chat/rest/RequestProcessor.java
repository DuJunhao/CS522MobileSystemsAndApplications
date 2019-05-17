package edu.stevens.cs522.chat.rest;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.ResultReceiver;
import android.util.JsonReader;
import android.util.JsonWriter;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;

import edu.stevens.cs522.chat.async.IContinue;
import edu.stevens.cs522.chat.entities.ChatMessage;
import edu.stevens.cs522.chat.entities.Peer;
import edu.stevens.cs522.chat.managers.MessageManager;
import edu.stevens.cs522.chat.managers.PeerManager;
import edu.stevens.cs522.chat.managers.RequestManager;
import edu.stevens.cs522.chat.managers.TypedCursor;
import edu.stevens.cs522.chat.settings.Settings;
import edu.stevens.cs522.chat.util.DateUtils;
import edu.stevens.cs522.chat.util.InetAddressUtils;
import edu.stevens.cs522.chat.util.StringUtils;

import static edu.stevens.cs522.chat.settings.Settings.SETTINGS;

/**
 * Created by dduggan.
 */

public class RequestProcessor {

    private Context context;

    private RestMethod restMethod;

    private PeerManager peerManager;
    private MessageManager messageManager;

    private RequestManager requestManager;

    public RequestProcessor(Context context) {
        this.context = context;
        this.restMethod = new RestMethod(context);
        // Used for SYNC
        this.requestManager = new RequestManager(context);

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
        if (!Settings.SYNC) {
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
        } else {
            /*
             * We will just insert the message into the database, and rely on background
             * sync to upload.
             */
            final ChatMessage chatMessage = new ChatMessage();
            // TODOfill the fields with values from the request message
            chatMessage.sender=Settings.getChatName(this.context);
            chatMessage.chatRoom=request.chatRoom;
            chatMessage.senderId=request.senderId;
            //chatMessage.id=1;
            chatMessage.timestamp=request.timestamp;
            chatMessage.chatRoom=request.chatRoom;
            chatMessage.messageText=request.message;
            chatMessage.latitude=request.latitude;
            chatMessage.longitude=request.longitude;
            chatMessage.seqNum=requestManager.getLastSequenceNumber()+1;
            chatMessage.id=chatMessage.seqNum;

            Peer sender=new Peer();
            sender.name = chatMessage.sender;
            sender.timestamp = chatMessage.timestamp;
            sender.address = InetAddressUtils.toIpAddress("192.168.1.63");
            sender.port = 8080;
            sender.id=chatMessage.senderId;
            TypedCursor<ChatMessage> cursor=requestManager.getUnsentMessages();

            requestManager.persist(chatMessage);
            requestManager.persist(sender);
           // requestManager.updateSeqNum(chatMessage.id,chatMessage.seqNum);
           /* peerManager.persistAsync(sender, new IContinue<Long>() {
                @Override
                public void kontinue(Long id) {
                   messageManager.persistAsync(chatMessage);
                }
            });*/

            return request.getDummyResponse();
        }
    }

    /**
     * For SYNC: perform a sync using a request manager
     * @param request
     * @return
     */
    public Response perform(SynchronizeRequest request) {
        final ArrayList<ChatMessage> messagesList = new ArrayList<>();
        RestMethod.StreamingResponse response = null;
        final TypedCursor<ChatMessage> messages = requestManager.getUnsentMessages();
        try {
            /*
             * This is the callback from streaming new local messages to the server.
             */
            RestMethod.StreamingOutput out = new RestMethod.StreamingOutput() {
                @Override
                public void write(final OutputStream os) throws IOException {
                    try {
                        JsonWriter wr = new JsonWriter(new OutputStreamWriter(new BufferedOutputStream(os)));
                        wr.beginArray();
                        /*
                         * TODOstream unread messages to the server:
                         * {
                         *   chatroom : ...,
                         *   timestamp : ...,
                         *   latitude : ...,
                         *   longitude : ....,
                         *   text : ...
                         * }
                         */
                        if (messages.getCursor().moveToFirst()) {
                            do {
                                ChatMessage msg = messages.getEntity();
                                messagesList.add(msg);
                                wr.beginObject();
                                wr.name("chatroom").value(msg.chatRoom);
                                wr.name("timestamp").value(msg.timestamp.getTime());
                                wr.name("latitude").value(msg.latitude);
                                wr.name("longitude").value(msg.longitude);
                                wr.name("text").value(msg.messageText);
                                wr.endObject();
                            } while (messages.getCursor().moveToNext());
                        }
                        wr.endArray();
                        wr.flush();
                    } finally {
                        messages.close();
                    }
                }
            };
            /*
             * Connect to the server and upload messages not yet shared.
             */

            request.lastSequenceNumber = requestManager.getLastSequenceNumber();
            response = restMethod.perform(request, out);

            /*
             * Stream downloaded peer and message information, and update the database.
             * The connection is closed in the finally block below.
             */
            JsonReader rd = new JsonReader(new InputStreamReader(new BufferedInputStream(response.getInputStream()), StringUtils.CHARSET));
            // TODOparse data from server (messages and peers) and update database
            // See RequestManager for operations to help with this.

            rd.beginObject();
            while (rd.hasNext()) {
                String name = rd.nextName();
                if (name.equals("clients")) {
                    readClients(rd);
                } else {
                    readMessages(rd, messagesList);
                }
            }
            rd.endObject();

            /*
             *
             */
            return response.getResponse();

        } catch (IOException e) {
            return new ErrorResponse(0, ErrorResponse.Status.SERVER_ERROR, e.getMessage());

        } finally {
            if (response != null) {
                response.disconnect();
            }
        }
    }

    public void readClients(JsonReader reader) throws IOException {
        reader.beginArray();
        while (reader.hasNext()){
            Peer peer = new Peer();

            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("id")) {
                    peer.id = reader.nextLong();
                } else if (name.equals("username")) {
                    peer.name = reader.nextString();
                } else if (name.equals("timestamp")) {
                    peer.timestamp = new Date(reader.nextLong());
                } else if (name.equals("latitude")) {
                    peer.latitude = reader.nextDouble();
                } else if (name.equals("longitude")) {
                    peer.longitude = reader.nextDouble();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
            requestManager.persist(peer);
        }
        reader.endArray();
    }

    public void readMessages(JsonReader reader, ArrayList<ChatMessage> messages) throws IOException {
        reader.beginArray();
        int index = 0;
        while (reader.hasNext()) {
            final ChatMessage receivedmsg = new ChatMessage();
            reader.beginObject();
            while (reader.hasNext()) {
                String name = reader.nextName();
                if (name.equals("seqnum")) {
                    receivedmsg.seqNum = reader.nextLong();
                } else if (name.equals("timestamp")) {
                    receivedmsg.timestamp = new Date(reader.nextLong());
                } else if (name.equals("text")) {
                    receivedmsg.messageText = reader.nextString();
                } else if (name.equals("chatroom")) {
                    receivedmsg.chatRoom = reader.nextString();
                } else if (name.equals("latitude")) {
                    receivedmsg.latitude = reader.nextDouble();
                } else if (name.equals("longitude")) {
                    receivedmsg.longitude = reader.nextDouble();
                } else if (name.equals("sender")) {
                    receivedmsg.senderId = reader.nextLong();
                } else {
                    reader.skipValue();
                }
            }
            reader.endObject();
            ChatMessage msg = searchMsg(messages, receivedmsg.timestamp.getTime(), receivedmsg.messageText);
            if (msg != null) {
                requestManager.updateSeqNum(msg.id, receivedmsg.seqNum);
            } else {
                requestManager.persistDownloadedMsg(receivedmsg);
            }
        }
        reader.endArray();
    }

    public ChatMessage searchMsg(ArrayList<ChatMessage> list, long timestamp, String text) {
        for(ChatMessage msg : list){
            if (msg.timestamp.getTime() == timestamp && msg.messageText.equals(text)) {
                return msg;
            }
        }
        return null;
    }

}
