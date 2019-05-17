package edu.stevens.cs522.chat.async;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dduggan.
 */

public class SimpleQueryBuilder<T> implements IContinue<Cursor>{

    public interface ISimpleQueryListener<T> {

        public void handleResults(List<T> results);

    }

    // TODOComplete the implementation of this

    private IEntityCreator<T> helper;
    private ISimpleQueryListener<T> listener;

    public SimpleQueryBuilder(
            IEntityCreator<T> helper,
            ISimpleQueryListener<T> listener) {
        this.helper = helper;
        this.listener = listener;
    }

    @Override
    public void kontinue(Cursor cursor) {
        // TODOcomplete this
        List<T> instances = new ArrayList<T>();
        if (cursor.moveToFirst()) {
            do {
                T instance = helper.create(cursor);
                instances.add(instance);
            } while (cursor.moveToNext());
        }
        cursor.close();
        listener.handleResults(instances);
    }

    public static <T> void executeQuery(Activity context,
                                        Uri uri,
                                        String[] projection,
                                        String selection,
                                        String[] selectionArgs,
                                        IEntityCreator<T> helper,
                                        ISimpleQueryListener<T> listener) {
        SimpleQueryBuilder<T> qb = new SimpleQueryBuilder<T>(helper, listener);
        AsyncContentResolver resolver = new
                AsyncContentResolver(context.getContentResolver());
        resolver.queryAsync(uri, null, null, null, null,qb );
    }

}

