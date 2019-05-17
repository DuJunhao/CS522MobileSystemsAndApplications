package edu.stevens.cs522.chat.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.util.DateUtils;

/**
 * Created by dduggan.
 */

public class ChatMessage implements Parcelable {

    public long id;

    public String messageText;

    public Date timestamp;

    public String sender;

    public long senderId;

    public ChatMessage() {
        this.id = 0;
        this.messageText = "";
        this.timestamp = null;
        this.sender = "";
        this.senderId = 0;
    }

    protected ChatMessage(Parcel in) {
        id = in.readLong();
        messageText = in.readString();

        timestamp = DateUtils.readDate(in);

        sender = in.readString();
        senderId = in.readLong();
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
    }

    // TODOadd operations for parcels (Parcelable), cursors and contentvalues
    public ChatMessage(Cursor cursor) {
        // TODOinit from cursor

        this.id = Long.parseLong(MessageContract.getID(cursor));
        this.messageText = MessageContract.getMessageText(cursor);
        this.timestamp = MessageContract.getTIMESTAMP(cursor);
        this.sender = MessageContract.getSENDER(cursor);
        this.senderId = Long.parseLong(MessageContract.getSENDERID(cursor));
    }

    public ContentValues writeToProvider(ContentValues out) {
        // TODOwrite to ContentValues

        /*out.put(MessageContract.ID,this.id);
        out.put(MessageContract.MESSAGE_TEXT,this.messageText);
        out.put(MessageContract.TIMESTAMP,this.timestamp.getTime());
        out.put(MessageContract.SENDER,this.sender);
        out.put(MessageContract.SENDERID,this.senderId);*/
        MessageContract.putID(out, Long.toString(this.id));
        MessageContract.putMessageText(out, this.messageText);
        MessageContract.putTIMESTAMP(out, this.timestamp);
        MessageContract.putSENDER(out, this.sender);
        MessageContract.putSENDERID(out, Long.toString(this.senderId));
        return out;
    }

}
