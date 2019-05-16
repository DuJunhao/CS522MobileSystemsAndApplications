package edu.stevens.cs522.chatserver.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import edu.stevens.cs522.chatserver.contracts.BaseContract;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;

public class ChatProvider extends ContentProvider {

    public ChatProvider() {
    }


    private static final String AUTHORITY = BaseContract.AUTHORITY;

    private static final String MESSAGE_CONTENT_PATH = MessageContract.CONTENT_PATH;

    private static final String MESSAGE_CONTENT_PATH_ITEM = MessageContract.CONTENT_PATH_ITEM;

    private static final String PEER_CONTENT_PATH = PeerContract.CONTENT_PATH;

    private static final String PEER_CONTENT_PATH_ITEM = PeerContract.CONTENT_PATH_ITEM;


    private static final String DATABASE_NAME = "chat.db";

    private static final int DATABASE_VERSION = 1;

    private static final String MESSAGE_TABLE = "messages";

    private static final String PEER_TABLE = "peers";

    public static final String PeerForeignKey = "peer_fk";
    // Create the constants used to differentiate between the different URI  requests.
    private static final int MESSAGES_ALL_ROWS = 1;
    private static final int MESSAGES_SINGLE_ROW = 2;
    private static final int PEERS_ALL_ROWS = 3;
    private static final int PEERS_SINGLE_ROW = 4;

    public static class DbHelper extends SQLiteOpenHelper {

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

        public DbHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODOinitialize database tables
            db.execSQL(PEERS_CREATE);
            db.execSQL(MESSAGES_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODOupgrade database if necessary
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

    private DbHelper dbHelper;

    @Override
    public boolean onCreate() {
        // Initialize your content provider on startup.
        dbHelper = new DbHelper(getContext(), DATABASE_NAME, null, DATABASE_VERSION);
        return true;
    }

    // Used to dispatch operation based on URI
    private static final UriMatcher uriMatcher;

    // uriMatcher.addURI(AUTHORITY, CONTENT_PATH, OPCODE)
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH, MESSAGES_ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, MESSAGE_CONTENT_PATH_ITEM, MESSAGES_SINGLE_ROW);
        uriMatcher.addURI(AUTHORITY, PEER_CONTENT_PATH, PEERS_ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, PEER_CONTENT_PATH_ITEM, PEERS_SINGLE_ROW);
    }

    @Override
    public String getType(Uri uri) {
        // TODOImplement this to handle requests for the MIME type of the data
        // at the given URI.
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                // TODOImplement this to handle query of all books.
                return MESSAGE_TABLE;
            case MESSAGES_SINGLE_ROW:
                return  MESSAGE_TABLE+PEER_TABLE;
            case PEERS_ALL_ROWS:
                return PEER_TABLE;
            case PEERS_SINGLE_ROW:
                return PEER_TABLE+MESSAGE_TABLE;
            default:
                return "default";
        }
       // throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                // TODOImplement this to handle requests to insert a new message.
                // Make sure to notify any observers
                long Message_id=0;
                try{
                    Message_id=db.insert(MESSAGE_TABLE,null,values);
                    Uri returnUri=MessageContract.CONTENT_URI(Message_id);
                    if(Message_id>0)
                    {
                        returnUri=MessageContract.CONTENT_URI(Message_id);
                        ContentResolver contentResolver=getContext().getContentResolver();
                        contentResolver.notifyChange(returnUri,null);
                    }
                    return returnUri;
                }
                catch (Exception e)
                {
                    Log.w("PersistMessage","cannot persist this message: " + e.getMessage());
                }
                break;
            case PEERS_ALL_ROWS:
                // TODOImplement this to handle requests to insert a new peer.
                // Make sure to notify any observers
                long Peer_id=0;
                try{
                    //values.put(PeerForeignKey,senderId);
                    Peer_id=db.insert(PEER_TABLE,null,values);
                    Uri returnUri=PeerContract.CONTENT_URI(Peer_id);
                    if(Peer_id>0)
                    {
                        returnUri=PeerContract.CONTENT_URI(Peer_id);
                        ContentResolver contentResolver=getContext().getContentResolver();
                        contentResolver.notifyChange(returnUri,null);
                    }
                    return returnUri;
                }
                catch (Exception e)
                {
                    Log.w("PersistPeer","cannot persist this peer: " + e.getMessage());
                }
                break;
            case MESSAGES_SINGLE_ROW:
                throw new IllegalArgumentException("insert expects a whole-table URI");
            default:
                throw new IllegalStateException("insert: bad case");
        }
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                // TODOImplement this to handle query of all messages.
                return db.query(MESSAGE_TABLE,
                        new String[]{MessageContract.ID,MessageContract.MESSAGE_TEXT,MessageContract.TIMESTAMP,MessageContract.SENDER,MessageContract.SENDERID},
                        selection, selectionArgs, null, null, sortOrder);
            case PEERS_ALL_ROWS:
                // TODOImplement this to handle query of all peers.
                return db.query(PEER_TABLE,
                        new String[]{PeerContract.Id,PeerContract.NAME,PeerContract.TIMESTAMP,PeerContract.ADDRESS,PeerContract.PORT},
                        selection, selectionArgs, null, null, sortOrder);
            case MESSAGES_SINGLE_ROW:
                // TODOImplement this to handle query of a specific message.
                selection=MessageContract.ID+"="+ Long.toString(MessageContract.getId(uri));
                return db.query(MESSAGE_TABLE,
                        new String[]{MessageContract.ID,MessageContract.MESSAGE_TEXT,MessageContract.TIMESTAMP,MessageContract.SENDER,MessageContract.SENDERID},
                        selection,null,null,null,null);
            case PEERS_SINGLE_ROW:
                // TODOImplement this to handle query of a specific peer.
                selection=PeerContract.Id+"="+ Long.toString(PeerContract.getId(uri));
                return db.query(PEER_TABLE,
                        new String[]{PeerContract.Id,PeerContract.NAME,PeerContract.TIMESTAMP,PeerContract.ADDRESS,PeerContract.PORT},
                        selection,null,null,null,null);
            default:
                throw new IllegalStateException("insert: bad case");
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODOImplement this to handle requests to update one or more rows.
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                return db.update(MESSAGE_TABLE,values,selection,selectionArgs);
            case PEERS_ALL_ROWS:
                //db.delete(BOOK_TABLE,AuthorContract.BOOK_FK+"="+Long.toString(BookContract.getId(uri)),null);
                return db.update(PEER_TABLE,values,selection,selectionArgs);
            case MESSAGES_SINGLE_ROW:
                return db.update(MESSAGE_TABLE,values,MessageContract.ID+"="+Long.toString(MessageContract.getId(uri)),null);
            case PEERS_SINGLE_ROW:
                return db.update(PEER_TABLE,values,PeerContract.Id+"="+Long.toString(PeerContract.getId(uri)),null);
            default:
                throw new IllegalArgumentException("Unsupported URI:" + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODOImplement this to handle requests to delete one or more rows.
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            case MESSAGES_ALL_ROWS:
                return db.delete(MESSAGE_TABLE,selection,selectionArgs);
            case PEERS_ALL_ROWS:
                //db.delete(BOOK_TABLE,AuthorContract.BOOK_FK+"="+Long.toString(BookContract.getId(uri)),null);
                return db.delete(PEER_TABLE,selection,selectionArgs);
            case MESSAGES_SINGLE_ROW:
                return db.delete(MESSAGE_TABLE,MessageContract.ID+"="+Long.toString(MessageContract.getId(uri)),null);
            case PEERS_SINGLE_ROW:
                return db.delete(PEER_TABLE,PeerContract.Id+"="+Long.toString(PeerContract.getId(uri)),null);
            default:
                throw new IllegalArgumentException("Unsupported URI:" + uri);
        }
    }

}
