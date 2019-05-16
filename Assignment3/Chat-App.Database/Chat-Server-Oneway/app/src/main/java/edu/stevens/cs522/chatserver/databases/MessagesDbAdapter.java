package edu.stevens.cs522.chatserver.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Message;
import edu.stevens.cs522.chatserver.entities.Peer;

/**
 * Created by dduggan.
 */

public class MessagesDbAdapter {

    private static final String DATABASE_NAME = "messages.db";

    private static final String MESSAGE_TABLE = "messages";

    private static final String PEER_TABLE = "view_peers";

    private static final String PeerForeignKey = "peer_fk";

    private static final int DATABASE_VERSION = 5;

    private DatabaseHelper dbHelper;

    private SQLiteDatabase db;


    public static class DatabaseHelper extends SQLiteOpenHelper {

        private static final String PEERS_CREATE =
                "create table if not exists " + PEER_TABLE+"( "
                        +PeerContract.Id+" Integer PRIMARY KEY, "
                        +PeerContract.NAME+" text not null, "
                        +PeerContract.TIMESTAMP+" text not null, "
                        +PeerContract.ADDRESS+" text not null, "
                        +PeerContract.PORT+" integer not null) ";

        private static final String MESSAGES_CREATE =
                "create table if not exists " + MESSAGE_TABLE+"( "
                        +MessageContract.ID+" Integer PRIMARY KEY, "
                        +MessageContract.MESSAGE_TEXT+" text not null, "
                        +MessageContract.TIMESTAMP+" text not null, "
                        +MessageContract.SENDER+" text not null, "
                        +MessageContract.SENDERID+" REAL not null, "
                        +PeerForeignKey+" INTEGER NOT NULL,"
                        +"FOREIGN KEY("+PeerForeignKey+") REFERENCES "+PEER_TABLE+"("+PeerContract.Id+") ON DELETE CASCADE);"
                        +"CREATE INDEX MessagesPeerIndex ON "+MESSAGE_TABLE+"("+PeerForeignKey+");"
                        +"CREATE INDEX PeerNameIndex ON "+PEER_TABLE+"("+PeerContract.NAME+");";

        private static String DATABASE_CREATE = PEERS_CREATE+MESSAGES_CREATE; // TOD
        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TOD
            //db.execSQL(DATABASE_CREATE);
            db.execSQL(PEERS_CREATE);
            db.execSQL(MESSAGES_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TOD
            Log.w("TaskDBAdapter",
                    "Upgrading from version " + oldVersion
                            + " to " + newVersion);
// Upgrade: drop the old table and create a new one.
            db.execSQL("DROP TABLE IF EXISTS " + PEER_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + MESSAGE_TABLE);
            db.execSQL(PEERS_CREATE);
            db.execSQL(MESSAGES_CREATE);
            onCreate(db);
        }
    }


    public MessagesDbAdapter(Context _context) {
        dbHelper = new DatabaseHelper(_context, DATABASE_NAME, null, DATABASE_VERSION);

    }

    public MessagesDbAdapter open() throws SQLException {
        // TOD
        db=dbHelper.getWritableDatabase();
        return this;
    }

    public Cursor fetchAllMessages() {
        // TOD
        Cursor cursor1 =db.rawQuery("select * from messages",null);
        Cursor cursor = db.query(MESSAGE_TABLE,
                new String[]{MessageContract.ID,MessageContract.MESSAGE_TEXT,MessageContract.TIMESTAMP,MessageContract.SENDER,MessageContract.SENDERID}
                ,null,null,null,null,null);
        return cursor;
    }

    public Cursor fetchAllPeers() {
        // TOD
        Cursor cursor = db.query(PEER_TABLE,
                new String[]{PeerContract.Id,PeerContract.NAME,PeerContract.TIMESTAMP,PeerContract.ADDRESS,PeerContract.PORT}
                ,null,null,null,null,null);
        return cursor;
    }

    public Peer fetchPeer(long peerId) {
        // TOD
        String selection=PeerContract.Id+"="+ Long.toString(peerId);
        Cursor cursor=db.query(PEER_TABLE,
                new String[]{PeerContract.Id,PeerContract.NAME,PeerContract.TIMESTAMP,PeerContract.ADDRESS,PeerContract.PORT}
                ,selection,null,null,null,null);
       // cursor.moveToPosition(rowId-1);
        Peer peer=new Peer(cursor);
        return peer;
    }

    public Cursor fetchMessagesFromPeer(Peer peer) {
        // TOD
        String selection=PeerForeignKey+"="+ Long.toString(peer.id);
        Cursor cursor=db.query(MESSAGE_TABLE,
                new String[]{MessageContract.ID,MessageContract.MESSAGE_TEXT,MessageContract.TIMESTAMP,MessageContract.SENDER,MessageContract.SENDERID}
                ,selection,null,null,null,null);
        return cursor;
    }

    public void persist(Message message,long senderId) throws SQLException {
        // TOD
        ContentValues messagesContentValues = new ContentValues();
        messagesContentValues=message.writeToProvider(messagesContentValues);
        messagesContentValues.put(PeerForeignKey,senderId);
        try{
            db.insert(MESSAGE_TABLE,null,messagesContentValues);
        }
        catch (Exception e)
        {
            Log.w("PersistMessage","cannot persist this message: " + e.getMessage());
        }
        //throw new SQLException("Failed to add message "+message.messageText);
    }

    /**
     * Add a peer record if it does not already exist; update information if it is already defined.
     * @param peer
     * @return The database key of the (inserted or updated) peer record
     * @throws SQLException
     */
    public long persist(Peer peer) throws SQLException {
        // TOD 1518925255000
        ContentValues peersContentValues = new ContentValues();
        peersContentValues=peer.writeToProvider(peersContentValues);
        long i=0;
        try{
            i=db.insert(PEER_TABLE,null,peersContentValues);
            return i;
        }
        catch (Exception e)
        {
            Log.w("PersistPeer","cannot persist this peer: " + e.getMessage());
        }
      //  throw new SQLException("Failed to add peer "+peer.name);
        return i;
    }

    public void close() {
        // TOD
        db.close();
    }
}