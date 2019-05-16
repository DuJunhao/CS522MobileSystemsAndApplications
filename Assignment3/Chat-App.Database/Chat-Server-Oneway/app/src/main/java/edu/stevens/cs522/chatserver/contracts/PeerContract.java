package edu.stevens.cs522.chatserver.contracts;

import android.content.ContentValues;
import android.database.Cursor;
import android.provider.BaseColumns;

import java.net.InetAddress;
import java.util.Date;

import edu.stevens.cs522.chatserver.util.DateUtils;
import edu.stevens.cs522.chatserver.util.InetAddressUtils;

/**
 * Created by dduggan.
 */

public class PeerContract implements BaseColumns {

    // TODOdefine column names, getters for cursors, setters for contentvalues
    public static final String Id = "_id";

    public static final String NAME = "name";

    public static final String TIMESTAMP = "timestamp";

    public static final String ADDRESS = "address";

    public static final String PORT = "port";

    private static int IdColumn = -1;
    private static int nameColumn = -1;
    private static int timeStampColumn = -1;
    private static int addressColumn = -1;
    private static int portColumn = -1;

    public static String getId(Cursor cursor) {
        if (IdColumn < 0) {
            IdColumn =  cursor.getColumnIndexOrThrow(Id);;
        }
        return cursor.getString(IdColumn);
    }

    public static void putId(ContentValues values, String id) {
        values.put(Id, id);
    }

    public static String getName(Cursor cursor) {
        if (nameColumn < 0) {
            nameColumn =  cursor.getColumnIndexOrThrow(NAME);;
        }
        return cursor.getString(nameColumn);
    }

    public static void putName(ContentValues values, String name) {
        values.put(NAME, name);
    }

    public static Date getTimestamp(Cursor cursor) {
        if (timeStampColumn < 0) {
            timeStampColumn =  cursor.getColumnIndexOrThrow(TIMESTAMP);;
        }
        return DateUtils.getDate(cursor,timeStampColumn);
    }

    public static void putTimestamp(ContentValues values, Date timestamp) {
        DateUtils.putDate(values,TIMESTAMP,timestamp);
}

    public static InetAddress getAddress(Cursor cursor) {
        if (addressColumn < 0) {
            addressColumn =  cursor.getColumnIndexOrThrow(ADDRESS);;
        }
        return InetAddressUtils.getAddress(cursor,addressColumn);
    }

    public static void putAddress(ContentValues values, InetAddress address) {
        InetAddressUtils.putAddress(values,ADDRESS,address);
    }

    public static String getPort(Cursor cursor) {
        if (portColumn < 0) {
            portColumn =  cursor.getColumnIndexOrThrow(PORT);;
        }
        return cursor.getString(portColumn);
    }

    public static void putPort(ContentValues values, String port) {
        values.put(PORT, port);
    }
}
