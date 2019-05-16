package edu.stevens.cs522.chatserver.activities;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import edu.stevens.cs522.chatserver.R;
import edu.stevens.cs522.chatserver.contracts.MessageContract;
import edu.stevens.cs522.chatserver.contracts.PeerContract;
import edu.stevens.cs522.chatserver.entities.Peer;


public class ViewPeersActivity extends Activity implements LoaderManager.LoaderCallbacks<Cursor> {

    // TODOadd loader callbacks

    /*
     * TODOSee ChatServer for example of what to do, query peers database instead of messages database.
     */


    private SimpleCursorAdapter peerAdapter;
    private int Count;
    public  static LoaderManager lm;
    private static final int PEERS_LOADER_ID = 1;
    private static final int MESSAGES_LOADER_ID=2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_peers);
        Count=0;
        String[] from = new String[] { PeerContract.Id, PeerContract.NAME  };
        int[] to = new int[] { android.R.id.text1, android.R.id.text2 };
        // TODOinitialize peerAdapter with empty cursor (null)
        peerAdapter=new SimpleCursorAdapter(this,
                android.R.layout.simple_list_item_2,
                null, from, to,
                SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
        );
        lm = getLoaderManager();
        lm.initLoader(MESSAGES_LOADER_ID, null, this);
        lm.initLoader(PEERS_LOADER_ID, null, this);

        Cursor cursor=getContentResolver().query(PeerContract.CONTENT_URI,null,null,null,null);

        if(cursor!=null){
            Count=cursor.getCount();
            peerAdapter=new SimpleCursorAdapter(this,
                    android.R.layout.simple_list_item_2,
                    cursor, from, to,
                    SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER
            );
        }
        ListView list = (ListView)findViewById(R.id.peerList);
        list.setAdapter(peerAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            Cursor cursor = peerAdapter.getCursor();
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (cursor.moveToPosition(position)) {
                    Intent intent = new Intent(ViewPeersActivity.this, ViewPeerActivity.class);
                    Peer peer = new Peer(cursor);
                    intent.putExtra(ViewPeerActivity.PEER_KEY, peer);
                    startActivity(intent);
                } else {
                    throw new IllegalStateException("Unable to move to position in cursor: "+position);
                }
            }
        });
    }


    /*@Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        *//*
         * Clicking on a peer brings up details
         *//*
        Cursor cursor = peerAdapter.getCursor();
        if (cursor.moveToPosition(position)) {
            Intent intent = new Intent(this, ViewPeerActivity.class);
            Peer peer = new Peer(cursor);
            intent.putExtra(ViewPeerActivity.PEER_KEY, peer);
            startActivity(intent);
        } else {
            throw new IllegalStateException("Unable to move to position in cursor: "+position);
        }
    }*/

    @Override
    public Loader onCreateLoader(int id, Bundle bundle) {
        switch (id) {
            case MESSAGES_LOADER_ID:
                return new CursorLoader(this, MessageContract.CONTENT_URI, null, null, null, null);
            case PEERS_LOADER_ID:
                return new CursorLoader(this, PeerContract.CONTENT_URI, null, null, null, null);
            default:
                return null; // An invalid id was passed in
        }
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor cursor) {
        switch(loader.getId()) {
            case MESSAGES_LOADER_ID:
                //this.peerAdapter.swapCursor(cursor);
                break;
            case PEERS_LOADER_ID:
               this.peerAdapter.swapCursor(cursor);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        this.peerAdapter.swapCursor(null);
    }
}
