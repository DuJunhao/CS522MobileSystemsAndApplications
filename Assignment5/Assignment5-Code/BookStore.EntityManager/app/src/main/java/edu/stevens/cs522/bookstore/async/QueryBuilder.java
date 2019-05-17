package edu.stevens.cs522.bookstore.async;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import java.net.URI;

import edu.stevens.cs522.bookstore.activities.MainActivity;
import edu.stevens.cs522.bookstore.contracts.AuthorContract;
import edu.stevens.cs522.bookstore.contracts.BookContract;
import edu.stevens.cs522.bookstore.entities.Author;
import edu.stevens.cs522.bookstore.entities.Book;
import edu.stevens.cs522.bookstore.managers.TypedCursor;

/**
 * Created by dduggan.
 */

public class QueryBuilder<T> implements LoaderManager.LoaderCallbacks<Cursor>{

    public static interface IQueryListener<T> {

        public TypedCursor<T> handleResults(TypedCursor<T> results);

        public void closeResults();

    }

    // TODOcomplete the implementation of this


    IQueryListener<T> LISTENER;
    private int LOADERID;
    private IEntityCreator<T> CREATOR;
    private Context CONTEXT;
    private android.net.Uri Uri;
    private Cursor cursor;
    private TypedCursor typedCursor;
    private CursorLoader cursorLoader;
    public QueryBuilder(String tag,
                         Context context,
                         Uri uri,
                         int loaderID,
                         IEntityCreator<T> creator,
                         IQueryListener<T> listener) {
         this.LOADERID=loaderID;
         this.CREATOR=creator;
         this.LISTENER=listener;
         this.CONTEXT=context;
         this.Uri=uri;
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        /*if (id == MainActivity.BOOKS_LOADER_ID) {
            this.bookCursorLoader=new CursorLoader(CONTEXT,
                    BookContract.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
            return bookCursorLoader;
        }
        else if(id==MainActivity.AUTHORS_LOADER_ID){
            this.authorCursorLoader=new CursorLoader(CONTEXT,
                    AuthorContract.CONTENT_URI,
                    null,null,null,null);
            return  authorCursorLoader;
        }*/
        if (id == LOADERID) {
            this.cursorLoader=new CursorLoader(CONTEXT,
                    BookContract.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
            return cursorLoader;
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        if (loader.getId() == LOADERID) {
            this.typedCursor=LISTENER.handleResults(new TypedCursor<T>(cursor, CREATOR));
        } else
         {
            throw new IllegalStateException("Unexpected loader callback");
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        if (loader.getId() == LOADERID) {
            LISTENER.closeResults();
        } else {
            throw new IllegalStateException("Unexpected loader callback");
        }
    }


    public static <T> void executeQuery(String tag,
                                        Activity context, Uri uri,
                                        int loaderID,
                                        IEntityCreator<T> creator,
                                        IQueryListener<T> listener)
    {
        QueryBuilder<T> qb = new QueryBuilder<T>(tag, context,
                uri, loaderID,
                creator, listener);
        LoaderManager lm = context.getLoaderManager();
        lm.initLoader(loaderID, null, qb);
       // Cursor cursor=null;
       // int i= qb.bookCursorLoader.getId();
     //   qb.onLoadFinished(qb.bookCursorLoader, cursor );

    }

}
