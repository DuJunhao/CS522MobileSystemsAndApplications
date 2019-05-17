package edu.stevens.cs522.chat.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.InetAddress;
import java.util.Date;

import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.contracts.PeerContract;
import edu.stevens.cs522.chat.util.DateUtils;
import edu.stevens.cs522.chat.util.InetAddressUtils;

/**
 * Created by dduggan.
 */

public class ChatMessage implements Parcelable {

    // Primary key in the database
    public long id;

    // Global id provided by the server
    public long seqNum;

    public String messageText;

    public String chatRoom;

    // When and where the message was sent
    public Date timestamp;

    public Double longitude;

    public Double latitude;

    // Sender username and FK (in local database)
    public String sender;

    public long senderId;

    public ChatMessage() {
        this.id = 0;
        this.messageText = "";
        this.timestamp = DateUtils.now();
        this.sender = "";
        this.senderId = 0;
        this.seqNum=0;
    }

    protected ChatMessage(Parcel in) {
        id = in.readLong();
        messageText = in.readString();

        timestamp = DateUtils.readDate(in);

        sender = in.readString();
        senderId = in.readLong();
        seqNum=in.readLong();
    }

    public static final Creator<ChatMessage> CREATOR = new Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel in) {
            return new ChatMessage(in);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(messageText);

        DateUtils.writeDate(parcel, timestamp);

        parcel.writeString(sender);
        parcel.writeLong(senderId);
        parcel.writeLong(seqNum);
    }
    // TODOadd operations for parcels (Parcelable), cursors and contentvalues

    public ChatMessage(Cursor cursor) {
        // TODOinit from cursor

        this.id = Long.parseLong(MessageContract.getID(cursor));
        this.messageText = MessageContract.getMessageText(cursor);
        this.timestamp = MessageContract.getTIMESTAMP(cursor);
        this.sender = MessageContract.getSENDER(cursor);
        this.senderId = Long.parseLong(MessageContract.getSENDERID(cursor));
        this.seqNum=Long.parseLong(MessageContract.getSEQUENCE_NUMBER(cursor));
    }

    public ContentValues writeToProvider(ContentValues out) {
        // TOD
        MessageContract.putID(out, Long.toString(this.id));
        MessageContract.putMessageText(out, this.messageText);
        MessageContract.putTIMESTAMP(out, this.timestamp);
        MessageContract.putSENDER(out, this.sender);
        MessageContract.putSENDERID(out, Long.toString(this.senderId));
        MessageContract.putSEQUENCE_NUMBER(out,Long.toString(this.seqNum));
        return out;
    }

}
