package edu.stevens.cs522.chat.async;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.net.Uri;

import edu.stevens.cs522.chat.activities.ChatActivity;
import edu.stevens.cs522.chat.contracts.MessageContract;
import edu.stevens.cs522.chat.contracts.PeerContract;
import edu.stevens.cs522.chat.managers.TypedCursor;

/**
 * Created by dduggan.
 */

public class QueryBuilder<T>  implements LoaderManager.LoaderCallbacks<Cursor> {

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
        if (id == ChatActivity.MESSAGES_LOADER_ID) {
            this.cursorLoader=new CursorLoader(CONTEXT,
                    MessageContract.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
            return cursorLoader;
        }
        else if(id==ChatActivity.PEERS_LOADER_ID)
        {
            this.cursorLoader=new CursorLoader(CONTEXT,
                    PeerContract.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);
            return cursorLoader;
        }
        else
        {
            return null;
        }
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
