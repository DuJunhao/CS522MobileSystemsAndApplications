package edu.stevens.cs522.bookstore.async;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import edu.stevens.cs522.bookstore.activities.MainActivity;

/**
 * Created by dduggan.
 */

public class AsyncContentResolver extends AsyncQueryHandler {

    public AsyncContentResolver(ContentResolver cr) {
        super(cr);
    }

    public void insertAsync(Uri uri,
                            ContentValues values,
                            IContinue<Uri> callback) {
        this.startInsert(0, callback, uri, values);
    }

    @Override
    public void onInsertComplete(int token, Object cookie, Uri uri) {
        if (cookie != null) {
            @SuppressWarnings("unchecked")
            IContinue<Uri> callback = (IContinue<Uri>) cookie;
            callback.kontinue(uri);
        }
    }

    public void queryAsync(Uri uri, String[] columns, String select, String[] selectArgs, String order, IContinue<Cursor> callback) {
        // TOD
        this.startQuery(0, callback, uri, columns, select, selectArgs, order);
    }


    @Override
    protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
        super.onQueryComplete(token, cookie, cursor);
        // TOD
        @SuppressWarnings("unchecked")
        IContinue<Cursor> callback = (IContinue<Cursor>) cookie;
        callback.kontinue(cursor);
    }

    public void deleteAsync(Uri uri, String select, String[] selectArgs) {
        // TOD
        this.startDelete(0,null,uri,select,selectArgs);
    }

    @Override
    protected void onDeleteComplete(int token, Object cookie, int result) {
        super.onDeleteComplete(token, cookie, result);
        // TOD
        @SuppressWarnings("unchecked")
       // IContinue<Integer> callback = (IContinue<Integer>) cookie;
        IContinue<Integer> callback =new IContinue<Integer>() {
            @Override
            public Integer kontinue(Integer value) {
                return value;
            }
        };
        callback.kontinue(result);
    }

    public void updateAsync(Uri uri, IContinue<Uri> callback, ContentValues values,String select, String[] selectArgs) {
        // TOD
        this.startUpdate(0,callback,uri,values,select,selectArgs);
    }

    @Override
    protected void onUpdateComplete(int token, Object cookie, int result) {
        super.onUpdateComplete(token, cookie, result);
        // TOD
        @SuppressWarnings("unchecked")
        IContinue<Integer> callback = (IContinue<Integer>) cookie;
        callback.kontinue(result);
    }

}
