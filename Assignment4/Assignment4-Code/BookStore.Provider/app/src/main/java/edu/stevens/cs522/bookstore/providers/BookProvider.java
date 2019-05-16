package edu.stevens.cs522.bookstore.providers;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import edu.stevens.cs522.bookstore.contracts.AuthorContract;
import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.entities.Author;
import edu.stevens.cs522.bookstore.util.Utils;

import static edu.stevens.cs522.bookstore.contracts.BookContract.CONTENT_PATH;
import static edu.stevens.cs522.bookstore.contracts.BookContract.CONTENT_PATH_ITEM;

public class BookProvider extends ContentProvider {
    public BookProvider() {
    }

    private static final String AUTHORITY = BookContract.AUTHORITY;

    public static final String CONTENT_PATH = BookContract.CONTENT_PATH;

    public static final String CONTENT_PATH_ITEM = BookContract.CONTENT_PATH_ITEM;


    private static final String DATABASE_NAME = "books.db";

    private static final int DATABASE_VERSION = 17;

    private static final String BOOK_TABLE = "books";

    private static final String AUTHOR_TABLE = "authors";

    private static final String BookForeignKey = "book_fk";
    // Create the constants used to differentiate between the different URI  requests.
    private static final int ALL_ROWS = 1;
    private static final int SINGLE_ROW = 2;

    private static final String AuthorGroup="SELECT "+BOOK_TABLE+"."+BookContract.ID+", "
            +BookContract.TITLE+", "
            +BookContract.PRICE+", "
            +BookContract.ISBN+", GROUP_CONCAT("+AuthorContract.WHOLE_NAME+", '|') as "+AUTHOR_TABLE+" FROM "+BOOK_TABLE+
            " JOIN "+ AUTHOR_TABLE+" ON "+BOOK_TABLE+"."+BookContract.ID+" = "+AUTHOR_TABLE+"."+BookForeignKey+
            " GROUP BY "+BOOK_TABLE+"._id, "+BookContract.TITLE+", "+BookContract.PRICE+", "+BookContract.ISBN;

    public static class DbHelper extends SQLiteOpenHelper {

        private static final String DATABASE_CREATE ="PRAGMA foreign_keys=ON;"; //TOD


        private static final String BOOK_CREATE =
                "create table if not exists " + BOOK_TABLE+"("
                        + BookContract.ID+" REAL not null, "
                        +BookContract.TITLE+" text not null, "
                        // +BookContract.AUTHORS+"text, "
                        +BookContract.ISBN+" text not null, "
                        +BookContract.PRICE+" text)";

        private static final String AUTHOR_CREATE =
                "create table if not exists " + AUTHOR_TABLE
                        +" (_id INTEGER PRIMARY KEY,"
                        + AuthorContract.FIRST_NAME+" text,"
                        +AuthorContract.MIDDLE_INITIAL+" text,"
                        +AuthorContract.LAST_NAME+" text not null,"
                        +AuthorContract.WHOLE_NAME+" text not null,"
                        +BookForeignKey+" INTEGER NOT NULL,"
                        +"FOREIGN KEY("+BookForeignKey+") REFERENCES "+BOOK_TABLE+"(_id) ON DELETE CASCADE);"
                        +"CREATE INDEX AuthorsBookIndex ON "+AUTHOR_TABLE+"("+BookForeignKey+");";


        public DbHelper(Context context, String name, CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODOinitialize database tables
            db.execSQL(BOOK_CREATE);
            db.execSQL(AUTHOR_CREATE);
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODOupgrade database if necessary
            // Upgrade: drop the old table and create a new one.
            db.execSQL("DROP TABLE IF EXISTS " + BOOK_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + AUTHOR_TABLE);
            db.execSQL(BOOK_CREATE);
            db.execSQL(AUTHOR_CREATE);
            db.execSQL(DATABASE_CREATE);
// Create a new one.
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
        uriMatcher.addURI(AUTHORITY, CONTENT_PATH, ALL_ROWS);
        uriMatcher.addURI(AUTHORITY, CONTENT_PATH_ITEM, SINGLE_ROW);
    }

    @Override
    public String getType(Uri uri) {
        // TODOImplement this to handle requests for the MIME type of the data
        // at the given URI.
        switch (uriMatcher.match(uri)) {
            case ALL_ROWS:
                // TODOImplement this to handle query of all books.
                return BOOK_TABLE;
            case SINGLE_ROW:
                return  BOOK_TABLE+AUTHOR_TABLE;
            default:
                return "default";
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        switch (uriMatcher.match(uri)) {
            case ALL_ROWS:
                // TODOImplement this to handle requests to insert a new row.
                // Make sure to notify any observers
                String authrosName= values.getAsString(BookContract.AUTHORS);
                Author[]authros= Utils.parseAuthors(authrosName);
                values.remove(BookContract.AUTHORS);
                Uri returnUri=null;
                long row=db.insert(BOOK_TABLE,null,values);
                if(row>=0)
                {
                    long author_id=-1;
                    for(int i=0;i<authros.length;i++)
                    {
                        ContentValues authorContentValues=new ContentValues();
                        AuthorContract.putWhole_Name(authorContentValues,authros[i].toString());
                        AuthorContract.putFirstName(authorContentValues,authros[i].firstName);
                        AuthorContract.putMiddleInitial(authorContentValues,authros[i].middleInitial);
                        AuthorContract.putLastName(authorContentValues,authros[i].lastName);
                        AuthorContract.putBook_fk(authorContentValues,values.getAsString(BookContract.ID));
                        author_id=db.insert(AUTHOR_TABLE,null,authorContentValues);
                    }
                    if(author_id>0)
                    {
                        returnUri=BookContract.CONTENT_URI(row);
                        ContentResolver contentResolver=getContext().getContentResolver();
                        contentResolver.notifyChange(returnUri,null);
                    }
                }
                return returnUri;
            case SINGLE_ROW:
                throw new IllegalArgumentException("insert expects a whole-table URI");
            default:
                throw new IllegalStateException("insert: bad case");
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            case ALL_ROWS:
                // TODOImplement this to handle query of all books.
                return db.rawQuery(AuthorGroup,null);
            case SINGLE_ROW:
                // TODOImplement this to handle query of a specific book.
                selection = BookContract._ID + "=?";
                String []selectionArg={Long.toString(BookContract.getId(uri))  };
                int id=(int)BookContract.getId(uri);
                Cursor cursor=db.rawQuery(AuthorGroup,null);
                cursor.moveToPosition(id-1);
                return cursor;
                //selectionArgs = { BookContract.getId(uri) };
               // return db.query(BOOK_TABLE, projection, AuthorGroup, selectionArg, null, null, sortOrder);
                //throw new UnsupportedOperationException("Something wrong when we need to get the book");
        }
       // ContentResolver cr = getContext().getContentResolver();
        //Cursor cursor=null;
        //cursor.setNotificationUri(cr, uri);
        //return cursor;
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        // TODOImplement this to handle requests to update values.
        switch (uriMatcher.match(uri)) {
            case ALL_ROWS:
            case SINGLE_ROW:
            default:
                throw new IllegalArgumentException("Unsupported URI:" + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODOImplement this to handle requests to delete one or more rows.
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        switch (uriMatcher.match(uri)) {
            case ALL_ROWS:
                return db.delete(BOOK_TABLE,selection,selectionArgs);
            case SINGLE_ROW:
                //db.delete(BOOK_TABLE,AuthorContract.BOOK_FK+"="+Long.toString(BookContract.getId(uri)),null);
                return db.delete(BOOK_TABLE,BookContract._ID + "="+Long.toString(BookContract.getId(uri)),null);
            default:
                throw new IllegalArgumentException("Unsupported URI:" + uri);
        }
    }

}
