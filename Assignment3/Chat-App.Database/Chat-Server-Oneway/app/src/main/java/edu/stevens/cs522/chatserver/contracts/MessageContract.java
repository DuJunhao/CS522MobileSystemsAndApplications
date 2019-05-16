package edu.stevens.cs522.chatserver.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import java.util.Date;

import edu.stevens.cs522.chatserver.util.DateUtils;

/**
 * Created by dduggan.
 */

public class MessageContract implements BaseColumns {

    public static final String MESSAGE_TEXT = "message_text";

    public static final String TIMESTAMP = "timestamp";

    public static final String SENDER = "sender";

    public static final String SENDERID = "senderId";

    public static final String ID = "_id";
    // TODOremaining columns in Messages table

    private static int messageTextColumn = -1;
    public static String getMessageText(Cursor cursor) {
        if (messageTextColumn < 0) {
            messageTextColumn = cursor.getColumnIndexOrThrow(MESSAGE_TEXT);
        }
        return cursor.getString(messageTextColumn);
    }

    public static void putMessageText(ContentValues out, String messageText) {
        out.put(MESSAGE_TEXT, messageText);
    }

    // TODOremaining getter and putter operations for other columns
    private static int TIMESTAMPColumn = -1;
    public static Date getTIMESTAMP(Cursor cursor) {
        if (TIMESTAMPColumn < 0) {
            TIMESTAMPColumn = cursor.getColumnIndexOrThrow(TIMESTAMP);
        }
        return DateUtils.getDate(cursor,TIMESTAMPColumn);
    }

    public static void putTIMESTAMP(ContentValues out, Date timestamp) {
        DateUtils.putDate(out,TIMESTAMP,timestamp);
    }

    private static int SENDERColumn = -1;
    public static String getSENDER(Cursor cursor) {
        if (SENDERColumn < 0) {
            SENDERColumn = cursor.getColumnIndexOrThrow(SENDER);
        }
        return cursor.getString(SENDERColumn);
    }

    public static void putSENDER(ContentValues out, String sender) {
        out.put(SENDER, sender);
    }

    private static int SENDERIDColumn = -1;
    public static String getSENDERID(Cursor cursor) {
        if (SENDERIDColumn < 0) {
            SENDERIDColumn = cursor.getColumnIndexOrThrow(SENDERID);
        }
        return cursor.getString(SENDERIDColumn);
    }

    public static void putSENDERID(ContentValues out, String senderId) {
        out.put(SENDERID, senderId);
    }

    private static int IDColumn = -1;
    public static String getID(Cursor cursor) {
        if (IDColumn < 0) {
            IDColumn = cursor.getColumnIndexOrThrow(ID);
        }
        return cursor.getString(IDColumn);
    }

    public static void putID(ContentValues out, String id) {
        out.put(ID, id);
    }
}
