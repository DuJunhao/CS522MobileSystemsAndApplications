package edu.stevens.cs522.bookstoredatabase.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import edu.stevens.cs522.bookstoredatabase.contracts.AuthorContract;
import edu.stevens.cs522.bookstoredatabase.contracts.BookContract;
import edu.stevens.cs522.bookstoredatabase.entities.Author;
import edu.stevens.cs522.bookstoredatabase.entities.Book;
import edu.stevens.cs522.bookstoredatabase.util.Utils;

/**
 * Created by dduggan.
 */

public class CartDbAdapter {

    private static final String DATABASE_NAME = "books.db";

    private static final String BOOK_TABLE = "books";

    private static final String AUTHOR_TABLE = "authors";

    private static final String BookForeignKey = "book_fk";

    private static final int DATABASE_VERSION = 8;

    private DatabaseHelper dbHelper;

    private SQLiteDatabase db;
//, GROUP_CONCAT(name,’|’)
    private static final String AuthorGroup="SELECT "+BOOK_TABLE+"._id, "
            +BookContract.TITLE+", "
            +BookContract.PRICE+", "
            +BookContract.ISBN+", GROUP_CONCAT("+AuthorContract.WHOLE_NAME+", '|') as "+AUTHOR_TABLE+" FROM "+BOOK_TABLE+
            " JOIN "+ AUTHOR_TABLE+" ON "+BOOK_TABLE+"._id = "+AUTHOR_TABLE+"."+BookForeignKey+
            " GROUP BY "+BOOK_TABLE+"._id, "+BookContract.TITLE+", "+BookContract.PRICE+", "+BookContract.ISBN;


    public static class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_CREATE ="PRAGMA foreign_keys=ON;"; //TOD


        private static final String BOOK_CREATE =
                "create table if not exists " + BOOK_TABLE
                        +" (_id INTEGER PRIMARY KEY, "
                        + BookContract.Id+" REAL not null, "
                        +BookContract.TITLE+" text not null, "
                       // +BookContract.AUTHORS+"text, "
                        +BookContract.ISBN+" text not null, "
                        +BookContract.PRICE+" text)";

        private static final String AUTHOR_CREATE =
                "create table if not exists " + AUTHOR_TABLE
                        +" (_id INTEGER PRIMARY KEY,"
                        +AuthorContract.FIRST_NAME+" text,"
                        +AuthorContract.MIDDLE_INITIAL+" text,"
                        +AuthorContract.LAST_NAME+" text not null,"
                        +AuthorContract.WHOLE_NAME+" text not null,"
                        +BookForeignKey+" INTEGER NOT NULL,"
                        +"FOREIGN KEY("+BookForeignKey+") REFERENCES "+BOOK_TABLE+"(_id) ON DELETE CASCADE);"
                        +"CREATE INDEX AuthorsBookIndex ON "+AUTHOR_TABLE+"("+BookForeignKey+");";

        public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TOD
            db.execSQL(BOOK_CREATE);
            db.execSQL(AUTHOR_CREATE);
            db.execSQL(DATABASE_CREATE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TOD
            // Log the version upgrade.
            Log.w("TaskDBAdapter",
                    "Upgrading from version " + oldVersion
                            + " to " + newVersion);
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


    public CartDbAdapter(Context _context) {
        dbHelper = new DatabaseHelper(_context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public CartDbAdapter open() throws SQLException {
        // TOD
        db = dbHelper.getWritableDatabase();
        return this;
    }

    public Cursor fetchAllBooks() {
        // TODO
       // Cursor cursor1 =db.rawQuery("select * from books",null);
       // Cursor cursor2 =db.rawQuery("select * from authors",null);
        String selectQuery=AuthorGroup;
        //db.rawQuery(selectQuery, null);
        Cursor cursor = db.rawQuery(selectQuery, null);
      /*  int BookIdIdx = cursor.getColumnIndexOrThrow(BookContract._ID);
        int titleIdx = cursor.getColumnIndexOrThrow(BookContract.TITLE);
        int PriceIdx = cursor.getColumnIndexOrThrow(BookContract.PRICE);
        int ISBNIdx=cursor.getColumnIndexOrThrow(BookContract.ISBN);
        int AuthorIdx = cursor.getColumnIndexOrThrow("authors");

        int size=cursor.getCount();
        String BookId=cursor.getString(BookIdIdx);
        String title = cursor.getString(titleIdx);
        String Price=cursor.getString(PriceIdx);
        String ISBN=cursor.getString(ISBNIdx);
        String authors=cursor.getString(AuthorIdx);*/
        return cursor;
       /* return db.query(BOOK_TABLE,
                new String[] {"_id",BookContract.Id, BookContract.TITLE, BookContract.ISBN,BookContract.PRICE,AuthorContract.FIRST_NAME,AuthorContract.MIDDLE_INITIAL,AuthorContract.LAST_NAME},
                null, null, null, null, null);*/
    }

    public Book fetchBook(int rowId) {
        // TOD
        //String selection="rowid ="+ Integer.toString(rowId);
        Cursor cursor=this.fetchAllBooks();
        cursor.moveToPosition(rowId-1);
        Book book=new Book(cursor);
     /*  //it's right too
        int BookIdIdx = cursor.getColumnIndexOrThrow(BookContract._ID);
        int titleIdx = cursor.getColumnIndexOrThrow(BookContract.TITLE);
        int ISBNIdx=cursor.getColumnIndexOrThrow(BookContract.ISBN);
        int PriceIdx = cursor.getColumnIndexOrThrow(BookContract.PRICE);
        int AuthorIdx=cursor.getColumnIndexOrThrow("authors");

            // Extract the title and author.
                String BookId=cursor.getString(BookIdIdx); //String BookId=BookContract.getId(cursor);
                String title = cursor.getString(titleIdx);//String title=BookContract.getId(cursor);
                String ISBN=cursor.getString(ISBNIdx);//String ISBN=BookContract.getId(cursor);
                String Price=cursor.getString(PriceIdx);//String Price=BookContract.getId(cursor);
                String wholeName=cursor.getString(AuthorIdx);//String wholeName=AuthorContract.getWhole_Name(cursor);


                book.id=Long.parseLong(BookId);
                book.title=title;
                book.isbn=ISBN;
                book.price=Price;
                book.authors=Utils.parseAuthors(wholeName);*/

        return book;
    }

    public long persist(Book book) throws SQLException {
        // TOD
        ContentValues BookContentValues = new ContentValues();
        //contentValues.put("BookId", book.id); // etc
        BookContentValues.put("_id",Long.toString(book.id));
        BookContract.putId(BookContentValues,Long.toString(book.id));
       // contentValues.put("title", book.title);
        BookContract.putTitle(BookContentValues,book.title);
       /* String authorsName="";
        for(int i=0;i<book.authors.length;i++)
        {
            authorsName+=book.authors[i].toString();
        }*/
        BookContract.putISBN(BookContentValues,book.isbn);
        BookContract.putPRICE(BookContentValues,book.price);
      /*  contentValues.put("authors", authorsName);
        contentValues.put("ISBN", book.isbn);
        contentValues.put("price", book.price);*/
        long Book_id= db.insert(BOOK_TABLE, null, BookContentValues);

        for(int i=0; i<book.authors.length;i++)
        {
            ContentValues AuthorsContentValues = new ContentValues();
            AuthorContract.putFirstName(AuthorsContentValues,book.authors[i].firstName);
            AuthorContract.putMiddleInitial(AuthorsContentValues,book.authors[i].middleInitial);
            AuthorContract.putLastName(AuthorsContentValues,book.authors[i].lastName);
            AuthorContract.putBook_fk(AuthorsContentValues,Long.toString(Book_id));
            AuthorContract.putWhole_Name(AuthorsContentValues,book.authors[i].name);
            db.insert(AUTHOR_TABLE,null,AuthorsContentValues);
        }
       // BookContract.putAuthors(contentValues,book.authors);
        return Book_id;
    }

    public boolean delete(Book book) {
        // TOD
        return db.delete(BOOK_TABLE, BookContract.Id+ "=" + book.id, null)>0;
    }

    public boolean delete(int rowId)
    {
        /*String selection="rowid ="+ Integer.toString(rowId);// These codes are used to check what I need to delete in the database
        Cursor cursor=this.fetchAllBooks();
        cursor.moveToPosition(rowId-1);
        Book book=new Book(cursor);*/
        return db.delete(BOOK_TABLE, BookContract._ID+ "=" + rowId, null)>0;
    }
    public boolean deleteAll() {
        // TOD
        int doneDelete = 0;
        try {
            doneDelete = db.delete(BOOK_TABLE, null, null);
            Log.w("Deleting", Integer.toString(doneDelete));
            Log.e("doneDelete", doneDelete + "");
        } catch (Exception e) {
            // TOD: handle exception
            e.printStackTrace();
        }
        return doneDelete > 0;
    }

    public void close() {
        // TOD
        db.close();
    }

}
