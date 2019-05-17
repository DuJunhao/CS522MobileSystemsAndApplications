package edu.stevens.cs522.chatserver.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.widget.CursorAdapter;

import edu.stevens.cs522.chatserver.async.AsyncContentResolver;
import edu.stevens.cs522.chatserver.async.IContinue;
import edu.stevens.cs522.chatserver.async.IEntityCreator;
import edu.stevens.cs522.chatserver.async.QueryBuilder.IQueryListener;
import edu.stevens.cs522.chatserver.contracts.BaseContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Peer;


/**
 * Created by dduggan.
 */

public class PeerManager extends Manager<Peer> {

    private static final int LOADER_ID = 2;

    private static final IEntityCreator<Peer> creator = new IEntityCreator<Peer>() {
        @Override
        public Peer create(Cursor cursor) {
            return new Peer(cursor);
        }
    };

    private AsyncContentResolver contentResolver;
    private Context CONTEXT;
    public PeerManager(Context context) {
        super(context, creator, LOADER_ID);
        contentResolver = new AsyncContentResolver(context.getContentResolver());
        this.CONTEXT=context;
    }

    public void getAllPeersAsync(IQueryListener<Peer> listener) {
        // TODOuse QueryBuilder to complete this
        executeQuery(PeerContract.CONTENT_URI,listener);
    }

    public void getPeerAsync(long id,final IContinue<Peer> callback) {
        // TODOneed to check that peer is not null (not in database)
        Uri BookUri=PeerContract.CONTENT_URI(id);
        contentResolver.queryAsync(BookUri, new String[]{PeerContract.Id,PeerContract.NAME,PeerContract.TIMESTAMP,PeerContract.ADDRESS,PeerContract.PORT}, null, null, null, new IContinue<Cursor>() {
            @Override
            public void kontinue(Cursor cursor) {
                   int i= cursor.getCount();
                    Peer peer=new Peer(cursor);
                    callback.kontinue(peer);
            }
        });
    }

    public void persistAsync(final Peer peer, final IContinue<Long> callback) {
        // TODOneed to ensure the peer is not already in the database
        ContentValues values = new ContentValues();
        peer.writeToProvider(values);
        contentResolver.insertAsync(PeerContract.CONTENT_URI, values,
                new IContinue<Uri>() {
                    public void kontinue(Uri uri) {
                        long id = BaseContract.getId(uri);
                        callback.kontinue(id);
                    }
                });
    }

}
