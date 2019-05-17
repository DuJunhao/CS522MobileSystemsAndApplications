package edu.stevens.cs522.bookstore.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.Set;

import edu.stevens.cs522.bookstore.async.AsyncContentResolver;
import edu.stevens.cs522.bookstore.async.IContinue;
import edu.stevens.cs522.bookstore.async.IEntityCreator;
import edu.stevens.cs522.bookstore.async.QueryBuilder;
import edu.stevens.cs522.bookstore.async.QueryBuilder.IQueryListener;
import edu.stevens.cs522.bookstore.contracts.AuthorContract;
import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.entities.Book;

/**
 * Created by dduggan.
 */

public class BookManager extends Manager<Book> {

    private static final int LOADER_ID = 1;

    private static final IEntityCreator<Book> creator = new IEntityCreator<Book>() {
        @Override
        public Book create(Cursor cursor) {
            return new Book(cursor);
        }
    };

    private AsyncContentResolver contentResolver;
    private Context CONTEXT;
    public BookManager(Context context) {
        super(context, creator, LOADER_ID);
        contentResolver = new AsyncContentResolver(context.getContentResolver());
        this.CONTEXT=context;
    }


    public void getAllBooksAsync(IQueryListener<Book> listener) {
        // TODOuse QueryBuilder to complete this
        executeQuery(BookContract.CONTENT_URI,listener);
    }

    public void getBookAsync(long id, final IContinue<Book> callback) {
        // TOD
        Uri BookUri=BookContract.CONTENT_URI(id);
        contentResolver.queryAsync(BookUri, new String[]{BookContract.ID, BookContract.TITLE, BookContract.AUTHORS, BookContract.ISBN, BookContract.PRICE}, null, null, null, new IContinue<Cursor>() {
            @Override
            public Cursor kontinue(Cursor cursor) {
                Book book=new Book(cursor);
                callback.kontinue(book);
                return cursor;
            }
        });
        //contentResolver.startQuery(0,callback,BookUri,null,null,null,null);
    }

    public void persistAsync(final Book book) {
        // TOD
        ContentValues values = new ContentValues();
        book.writeToProvider(values);
        contentResolver.insertAsync(BookContract.CONTENT_URI, values,
                new IContinue<Uri>() {
                    public Uri kontinue(Uri uri) {
                        book.id = BookContract.getId(uri);
                        return uri;
                    }
                });

    }

    public void deleteBooksAsync(Set<Long> toBeDeleted) {
        Long[] ids = new Long[toBeDeleted.size()];
        toBeDeleted.toArray(ids);
        String[] args = new String[ids.length];

        StringBuilder sb = new StringBuilder();
        if (ids.length > 0) {
            sb.append(AuthorContract.ID);
            sb.append("=?");
            args[0] = ids[0].toString();
            for (int ix=1; ix<ids.length; ix++) {
                sb.append(" or ");
                sb.append(AuthorContract.ID);
                sb.append("=?");
                args[ix] = ids[ix].toString();
            }
        }
        String select = sb.toString();
        contentResolver.deleteAsync(BookContract.CONTENT_URI, select, args);
    }
}
